package com.cryptotpmail.client;

public class ClientSessionKey {
    byte[] sessionID;
    byte[] aesKey;

    public ClientSessionKey(byte[] sessionID, byte[] aesKey) {
        this.sessionID = sessionID;
        this.aesKey = aesKey;
    }

    public byte[] getSessionID() {
        return sessionID;
    }

    public void setSessionID(byte[] sessionID) {
        this.sessionID = sessionID;
    }

    public byte[] getAesKey() {
        return aesKey;
    }

    public void setAesKey(byte[] aesKey) {
        this.aesKey = aesKey;
    }
}
