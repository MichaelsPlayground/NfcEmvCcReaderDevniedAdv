package de.androidcrypto.nfcemvccreaderdevnied.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
    private static long EXPIRATION_IN_SECONDS = 20;
    private static String sessionKeyFilename = "sessionkey.dat";

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
            byte[] salt = new byte[32];
            secureRandom.nextBytes(salt);
            byte[] nonce = new byte[12];
            secureRandom.nextBytes(nonce);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(passphrase, salt, iterations, 32 * 8);
            byte[] key = secretKeyFactory.generateSecret(keySpec).getEncoded();
            // get the SessionKeyModel
            SessionKey sessionKey = new SessionKey(key, salt, nonce, iterations);
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
            // Wrapping our file stream.
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
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
}
