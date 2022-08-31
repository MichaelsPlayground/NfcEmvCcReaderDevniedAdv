package de.androidcrypto.nfcemvccreaderdevnied.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SessionKeyV1 implements Serializable {

    private char[] passphrase;
    private byte[] passphraseNonce;
    private byte[] key;
    private byte[] salt;
    private byte[] nonce;
    int iterations;
    private Date date;

    public SessionKeyV1(char[] passphrase, byte[] passphraseNonce, byte[] key, byte[] salt, byte[] nonce, int iterations) {
        this.passphrase = passphrase;
        this.passphraseNonce = passphraseNonce;
        this.key = key;
        this.salt = salt;
        this.nonce = nonce;
        this.iterations = iterations;
        this.date = Calendar.getInstance().getTime();
    }

    public char[] getPassphrase() {
        return passphrase;
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
                "\npassphrase: " + Arrays.toString(passphrase) +
                "\nkey: " + Arrays.toString(key) +
                "\nsalt: " + Arrays.toString(salt) +
                "\nnonce: " + Arrays.toString(nonce) +
                "\niterations: " + iterations +
                "\ncreation date: " + date.toString();
        return output;
    }
}
