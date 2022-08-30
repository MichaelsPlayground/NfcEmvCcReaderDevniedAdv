package de.androidcrypto.nfcemvccreaderdevnied.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SessionKey implements Serializable {

    private byte[] key;
    private byte[] salt;
    private byte[] nonce;
    int iterations;
    private Date date;

    public SessionKey(byte[] key, byte[] salt, byte[] nonce, int iterations) {
        this.key = key;
        this.salt = salt;
        this.nonce = nonce;
        this.iterations = iterations;
        this.date = Calendar.getInstance().getTime();
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public int getIterations() {
        return iterations;
    }

    public Date getDate() {
        return date;
    }

    public String dumpData() {
        String output = "SessionKeyClass" +
                "\nkey: " + Arrays.toString(key) +
                "\nsalt: " + Arrays.toString(salt) +
                "\nnonce: " + Arrays.toString(nonce) +
                "\niterations: " + iterations +
                "\ncreation date: " + date.toString();
        return output;
    }
}
