package de.androidcrypto.nfcemvccreaderdevnied.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SessionKey implements Serializable {

    private byte[] passphraseByte;
    private byte[] passphraseNonce;
    int iterations;
    private Date date;

    public SessionKey(byte[] passphraseByte, byte[] passphraseNonce, int iterations) {
        this.passphraseByte = passphraseByte;
        this.passphraseNonce = passphraseNonce;
        this.iterations = iterations;
        this.date = Calendar.getInstance().getTime();
    }

    public byte[] getPassphraseByte() {
        return passphraseByte;
    }

    public byte[] getPassphraseNonce() {
        return passphraseNonce;
    }

    public int getIterations() {
        return iterations;
    }

    public Date getDate() {
        return date;
    }

    public String dumpData() {
        String output = "SessionKeyClass" +
                "\npassphraseByte: " + Arrays.toString(passphraseByte) +
                "\npassphraseNonce: " + Arrays.toString(passphraseNonce) +
                "\niterations: " + iterations +
                "\ncreation date: " + date.toString();
        return output;
    }
}
