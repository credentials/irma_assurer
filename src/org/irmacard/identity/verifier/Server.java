package org.irmacard.identity.verifier;

import java.security.interfaces.RSAPrivateKey;

/**
 * This class represents the backend of the IRMA card assurer protocol. It verifies the credentials read and sent by
 * the tablet to ensure their validity. To do this, it uses the JMRTD toolset. If the credentials are valid, it will
 * create and sign an IRMA attribute to be issued by the assurer.
 */
public class Server {
    RSAPrivateKey skT;

    public Server() {
        // TODO: Method stub
    }

    private void generateSessionKey() {
        // TODO: Method stub
    }

    private void verifyCredential() {
        // TODO: Method stub
    }

    private void createAttribute() {
        // TODO: Method stub
    }

    private void signAttribute() {
        // TODO: Method stub
    }

    public static void main(String[] args) {
        new Server();
    }
}
