package de.androidcrypto.nfcemvccreaderdevnied.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import de.androidcrypto.nfcemvccreaderdevnied.model.SessionKey;

public class EncryptionUtils {

    /**
     * this class handles the internal storage of the the session key and the
     * encrypted storing and load of the model files
     * As we need a context the class needs to get initialized on app's startup
     */

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static String sessionKeyFilename = "sessionkey.dat";

    public static void init(Context context) {
        EncryptionUtils.context = context;
    }

    public static boolean setSessionKey(char[] passphrase, int iterations) {
        // sanity checks
        if (passphrase == null) return false;
        // check for passphrase
        // todo minimum length check ?
        if (passphrase.length < 1) return false;
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
            SessionKey sessionKey = new SessionKey(key, salt, nonce);
            // store the data in the internal storage
            FileOutputStream fos = context.openFileOutput(sessionKeyFilename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sessionKey);
            oos.close();
            fos.close();
            return true;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static SessionKey loadSessionKey() {
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

    public static boolean deleteSessionKey() {
        // todo implement this method
        return true;
    }
}
