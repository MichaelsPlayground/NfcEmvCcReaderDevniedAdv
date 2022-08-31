package de.androidcrypto.nfcemvccreaderdevnied.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.androidcrypto.nfcemvccreaderdevnied.Utils;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardAids;
import de.androidcrypto.nfcemvccreaderdevnied.model.SessionKey;

public class EncryptionUtils {

    /**
     * this class handles the internal storage of the the session key and the
     * encrypted storing and load of the model files
     * As we need a context the class needs to get initialized on app's startup
     */

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static final long EXPIRATION_IN_SECONDS = 300; // 5 minutes
    // important: if you change the duration do it also change in activity_set_session_key.xml
    private static final String SESSION_KEY_FILENAME = "sessionkey.dat";
    private static final int PBKDF2_ITERATIONS = 10000;
    private static byte[] temporaryEncryptionKey = new byte[32]; // used for encryption e.g. of the sessionKey

    public static void init(Context context) {
        EncryptionUtils.context = context;
    }

    public static SessionKey setSessionKey(char[] passphrase, int iterations) {
        // sanity checks
        if (passphrase == null) return null;
        // check for passphrase
        // todo minimum length check ?
        if (passphrase.length < 1) return null;
        if (iterations < 10000) iterations = 10000; // minimum iterations
        SecureRandom secureRandom = new SecureRandom();
        temporaryEncryptionKey = new byte[32];
        secureRandom.nextBytes(temporaryEncryptionKey);
        byte[] passphraseNonce = new byte[12];
        secureRandom.nextBytes(passphraseNonce);
        byte[] encryptedPassphraseByte = runAesGcmEncryption(temporaryEncryptionKey, passphraseNonce, Utils.fromCharToByteArray(passphrase)) ;
        // get the SessionKeyModel
        SessionKey sessionKey = new SessionKey(encryptedPassphraseByte, passphraseNonce, iterations);
        writeSessionKeyToInternalStorage(sessionKey);
        return sessionKey;
    }

    public static boolean isSessionKeyAvailable() {
        // checks that a session key is stored and not expired
        SessionKey sessionKey = loadSessionKeyFromInternalStorage();
        if (sessionKey == null) {
            return false;
        }
        if (isSessionKeyExpired(sessionKey, EXPIRATION_IN_SECONDS)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isSessionKeyExpired(SessionKey sessionKey, long seconds) {
        boolean result = true;
        Date dateNow = Calendar.getInstance().getTime();
        long durationInSeconds = (dateNow.getTime() - sessionKey.getDate().getTime()) / 1000;
        if (durationInSeconds < seconds) {
            result = false;
        }
        return result;
    }

    // this method decrypts the session key
    public static SessionKey loadSessionKey() {
        SessionKey encryptedSessionKey;
        encryptedSessionKey = loadSessionKeyFromInternalStorage();
        if (encryptedSessionKey != null) {
            byte[] encryptedPassphrase = encryptedSessionKey.getPassphraseByte();
            byte[] passphraseNonce = encryptedSessionKey.getPassphraseNonce();
            int iterations = encryptedSessionKey.getIterations();
            byte[] passphraseByte = runAesGcmDecryption(temporaryEncryptionKey, passphraseNonce, encryptedPassphrase);
            return new SessionKey(passphraseByte, passphraseNonce, iterations);
        } else {
            return null;
        }
    }

    public static boolean deleteSessionKey() {
        try {
            context.deleteFile(SESSION_KEY_FILENAME);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean writeSessionKeyToInternalStorage(SessionKey sessionKey) {
        // store the data in the internal storage
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(SESSION_KEY_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sessionKey);
            oos.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static SessionKey loadSessionKeyFromInternalStorage() {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(SESSION_KEY_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            return (SessionKey) object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeEncryptedModelToUri(Uri uri, EmvCardAids emvCardAids)  {
        SessionKey sessionKey;
        boolean isSessionKey = false;
        sessionKey = loadSessionKey();
        if (sessionKey == null) {
            System.out.println("*** writeEncryptedModelToUri sessionKey is NULL");
            return false;
        } else {
            isSessionKey = true;
        }
        // check if sessionKey is expired
        boolean sessionKeyIsExpired = isSessionKeyExpired(sessionKey, 60l);
        if (sessionKeyIsExpired) {
            System.out.println("*** writeEncryptedModelToUri sessionKey is expired");
            return false;
        }

        OutputStream outputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);

            Cipher cipher = Cipher.getInstance("AES/GCM/NOPadding");
            CipherOutputStream encryptedOutputStream = new CipherOutputStream(outputStream, cipher);
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[32];
            secureRandom.nextBytes(salt);
            byte[] nonce = new byte[12];
            secureRandom.nextBytes(nonce);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            char[] passphrase = Utils.fromByteToCharArrayConverter(sessionKey.getPassphraseByte());
            KeySpec keySpec = new PBEKeySpec(passphrase, salt, PBKDF2_ITERATIONS, 32 * 8);
            byte[] key = secretKeyFactory.generateSecret(keySpec).getEncoded();

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

            outputStream.write(salt);
            outputStream.write(nonce);

            // Wrapping our file stream.
            //ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            ObjectOutputStream oos = new ObjectOutputStream(encryptedOutputStream);
            // Writing the serializable object to the file
            oos.writeObject(emvCardAids);
            // Closing our object stream which also closes the wrapped stream.
            oos.close();

            System.out.println("*** writeEncryptedModelToUri model is written");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static EmvCardAids readEncryptedModelFromUri(Uri uri) throws IOException {
        SessionKey sessionKey;
        boolean isSessionKey = false;
        //sessionKey = loadSessionKeyFromInternalStorage();
        sessionKey = loadSessionKey();
        if (sessionKey == null) {
            System.out.println("*** writeEncryptedModelToUri sessionKey is NULL");
            return null;
        } else {
            isSessionKey = true;
        }
        // check if sessionKey is expired
        boolean sessionKeyIsExpired = isSessionKeyExpired(sessionKey, 300l);
        if (sessionKeyIsExpired) {
            System.out.println("*** writeEncryptedModelToUri sessionKey is expired");
            return null;
        }

        InputStream inputStream = null;
        EmvCardAids emvCardAidsImport = new EmvCardAids();
        try {
            byte[] salt = new byte[32];
            byte[] nonce = new byte[12];
            Cipher cipher = Cipher.getInstance("AES/GCM/NOPadding");

            // this is the encrypted version
            inputStream = context.getContentResolver().openInputStream(uri);
            CipherInputStream encryptedInputStream = new CipherInputStream(inputStream, cipher);
            // todo run the key derivation here
            inputStream.read(salt);
            inputStream.read(nonce);

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            char[] passphrase = Utils.fromByteToCharArrayConverter(sessionKey.getPassphraseByte());

            KeySpec keySpec = new PBEKeySpec(passphrase, salt, PBKDF2_ITERATIONS, 32 * 8); // 128 - 192 - 256
            byte[] key = secretKeyFactory.generateSecret(keySpec).getEncoded();

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, nonce);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);

            // Wrapping our file stream.
            ObjectInputStream ois = new ObjectInputStream(encryptedInputStream);
            // Writing the serializable object to the file
            emvCardAidsImport = (EmvCardAids) ois.readObject();

            /*
            // this is the unencrypted version
            inputStream = context.getContentResolver().openInputStream(uri);
            // Wrapping our file stream.
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            // Writing the serializable object to the file
            emvCardAidsImport = (EmvCardAids) ois.readObject();
             */
            // Closing our object stream which also closes the wrapped stream.
            ois.close();
            return emvCardAidsImport;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] runAesGcmEncryption(byte[] key, byte[] nonce, byte[] plaintext) {
        Cipher cipher = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, nonce);
            cipher = Cipher.getInstance("AES/GCM/NOPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
            return cipher.doFinal(plaintext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] runAesGcmDecryption(byte[] key, byte[] nonce, byte[] ciphertext) {
        Cipher cipher = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            System.out.println("*** nonce: " + Arrays.toString(nonce));
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, nonce);
            cipher = Cipher.getInstance("AES/GCM/NOPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
            return cipher.doFinal(ciphertext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
