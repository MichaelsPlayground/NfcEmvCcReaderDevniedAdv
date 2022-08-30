package de.androidcrypto.nfcemvccreaderdevnied;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedEncryptedPreferences {

    Context context;

    // stores data in encrypted shared preferences file
    private String mainKeyAlias; // for the masterKey
    private String sharedEncryptedPreferencesFilename = "sharedencryprefs.dat";
    private SharedPreferences sharedEncryptedPreferences;

    public SharedEncryptedPreferences(Context context) {
        this.context = context;
        // general initialization
        // Context context = getApplicationContext();
        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        try {
            mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        // create or open EncryptedSharedPreferences
        try {
            sharedEncryptedPreferences = EncryptedSharedPreferences.create(
                    sharedEncryptedPreferencesFilename,
                    mainKeyAlias,
                    this.context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean storeDataString(String key, String data) {
        // store in sharedPreferences
        sharedEncryptedPreferences
                .edit()
                .putString(key, data)
                .apply();
        return true;
    }

}