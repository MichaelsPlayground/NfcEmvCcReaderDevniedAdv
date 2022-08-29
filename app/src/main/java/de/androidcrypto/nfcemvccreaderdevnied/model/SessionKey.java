package de.androidcrypto.nfcemvccreaderdevnied.model;

import java.io.Serializable;

public class SessionKey implements Serializable {

    private byte[] key;
    private byte[] salt;
    private byte[] nonce;

    public SessionKey(byte[] key, byte[] salt, byte[] nonce) {
        this.key = key;
        this.salt = salt;
        this.nonce = nonce;
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
}
