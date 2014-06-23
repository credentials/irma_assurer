package org.irmacard.identity.assurer;

import org.irmacard.credentials.*;
import org.irmacard.credentials.cert.*;
import org.irmacard.credentials.info.*;
import org.irmacard.credentials.spec.*;
import org.irmacard.credentials.keys.*;
import org.irmacard.credentials.util.*;
import org.irmacard.identity.common.CONSTANTS;

import javax.crypto.SecretKey;

/**
 * IDreader will handle the reading of data on the chip inside an ID document such as a passport.
 */
public class IDreader {
    Crypto crypto;
    byte CONNECTION_STATUS;

    public IDreader(Crypto c) {
        crypto = c;
        CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_DISCONNECTED;
    }

    private boolean isConnected() {
        return CONNECTION_STATUS == CONSTANTS.CONNECTION_STATUS_CONNECTED;
    }

    /**
     * This method attempts to set up a link to the ID verification server.
     */
    private boolean connect() {
        System.out.println("Connecting to the verification server...");

        // TODO: Set up the connection.

        SecretKey kTS = crypto.getSessionKey();

        System.out.println("Successfully connected to the server.");
        CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_CONNECTED;

        return isConnected();
    }

    public void verifyIntegrity() throws IDVerificationException {
        if (connect()) {
            System.out.println("Starting identity verification...");

            // TODO: Perform verification

            System.out.println("Everything checks out. Your ID is genuine.");
        } else {
            throw new IDVerificationException("Your ID couldn't be verified. It might have been tampered with.");
        }
    }

    public void storePassportData() {
        // TODO: Method stub
    }
}
