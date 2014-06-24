package org.irmacard.identity.verifier;

import java.security.Key;
import java.security.KeyStore;

/**
 * Handles all cryptographic operations required by the verification server
 */
public class Crypto {
    Key kTS;

    Crypto() {

    }

    String encrypt(String plaintext) {
        // TODO: Method stub
        return plaintext;
    }

    String symencrypt(String plaintext) {
        // TODO: Method stub
        return plaintext;
    }

    String decrypt(String ciphertext) {
        // TODO: Method stub
        return ciphertext;
    }

    String symdecrypt(String ciphertext) {
        // TODO: Method stub

        return ciphertext;
    }

    void generateSessionKey() {
        // TODO: Method stub
    }

    /**
     * Stores the client key in a local database. Useful for DHKE with authentication, i.e. the clients reuse their key.
     */
    void storeClientKey() {
        KeyStore keyStore;
    }
}
