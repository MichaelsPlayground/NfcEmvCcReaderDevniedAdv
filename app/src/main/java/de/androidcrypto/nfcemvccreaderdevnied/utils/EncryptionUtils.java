package de.androidcrypto.nfcemvccreaderdevnied.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardAids;
import de.androidcrypto.nfcemvccreaderdevnied.model.SessionKey;
import fr.devnied.bitlib.BytesUtils;

public class EncryptionUtils {

    /**
     * this class handles the internal storage of the the session key and the
     * encrypted storing and load of the model files
     * As we need a context the class needs to get initialized on app's startup
     */

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static long EXPIRATION_IN_SECONDS = 120;
    private static String sessionKeyFilename = "sessionkey.dat";
    private static int PBKDF2_ITERATIONS = 10000;

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
        try {
            // run a key derivation using PBKDF2 SHA-1
            SecureRandom secureRandom = new SecureRandom();
            byte[] passphraseNonce = new byte[12];
            secureRandom.nextBytes(passphraseNonce);

            byte[] salt = new byte[32];
            secureRandom.nextBytes(salt);
            byte[] nonce = new byte[12];
            secureRandom.nextBytes(nonce);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(passphrase, salt, iterations, 32 * 8);
            byte[] key = secretKeyFactory.generateSecret(keySpec).getEncoded();

            // todo encrypt the passphrase before storing in sessionKey

            // get the SessionKeyModel
            SessionKey sessionKey = new SessionKey(passphrase, passphraseNonce, key, salt, nonce, iterations);
            writeSessionKeyToInternalStorage(sessionKey);
            return sessionKey;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
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

    public static SessionKey loadSessionKey() {
        // todo implement this
        return loadSessionKeyFromInternalStorage();
    }

    public static boolean deleteSessionKey() {
        try {
            context.deleteFile(sessionKeyFilename);
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
            fos = context.openFileOutput(sessionKeyFilename, Context.MODE_PRIVATE);
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
            fis = context.openFileInput(sessionKeyFilename);
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
        sessionKey = loadSessionKeyFromInternalStorage();
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
            // todo run the key derivation here

            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[32];
            secureRandom.nextBytes(salt);
            byte[] nonce = new byte[12];
            secureRandom.nextBytes(nonce);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(sessionKey.getPassphrase(), salt, PBKDF2_ITERATIONS, 32 * 8);
            byte[] key = secretKeyFactory.generateSecret(keySpec).getEncoded();

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

            System.out.println("ENC key " + BytesUtils.bytesToString(key) + " salt " +
                    BytesUtils.bytesToString(salt) + " nonce: " + BytesUtils.bytesToString(nonce) +
                    " passphrase: " + Arrays.toString(sessionKey.getPassphrase()));

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
        sessionKey = loadSessionKeyFromInternalStorage();
        if (sessionKey == null) {
            System.out.println("*** writeEncryptedModelToUri sessionKey is NULL");
            return null;
        } else {
            isSessionKey = true;
        }
        // check if sessionKey is expired
        boolean sessionKeyIsExpired = isSessionKeyExpired(sessionKey, 60l);
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
            KeySpec keySpec = new PBEKeySpec(sessionKey.getPassphrase(), salt, PBKDF2_ITERATIONS, 32 * 8); // 128 - 192 - 256
            byte[] key = secretKeyFactory.generateSecret(keySpec).getEncoded();

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, nonce);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);

            System.out.println("DEC key " + BytesUtils.bytesToString(key) + " salt " +
                    BytesUtils.bytesToString(salt) + " nonce: " + BytesUtils.bytesToString(nonce) +
                    " passphrase: " + Arrays.toString(sessionKey.getPassphrase()));

/*
ENC key 1E 11 29 F8 6F 8F EA 88 30 00 45 59 C3 31 E0 AF 41 CA 0F 5A A0 6A 38 DE 30 97 E4 3D 2E 7A F9 E1
salt 97 03 2C C8 EB 17 F5 29 C1 89 8E 81 6D BF 7A 59 2B 24 90 6F E6 C4 B3 FA B3 F6 59 D9 F6 31 46 EE
nonce: A7 4F 0B 2B 52 89 B6 F7 55 33 13 8C
passphrase: [1, 2, 3]
DEC key C4 CF F6 9B 0D F2 0F 1C CC C5 76 02 60 BD AF 30 E3 ED 30 71 B8 D0 68 24 9F 1D 75 85 FD 71 C2 DA
salt 4D 86 F5 33 BF 43 58 0B 32 6C F0 B4 BC E4 CF A6 33 D7 4E 5E 9E 4B 2B 78 7F E2 F9 31 1B CA 68 36
nonce: 18 C4 6A 85 9F 1A 2C 19 FA CC D6 F9
passphrase: [1, 2, 3]


 */

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emvCardAidsImport;
    }
}
