
package org.irmacard.identity;

import org.irmacard.idemix.*;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.Scanner;


/**
 * This is the app that gets installed on the tablets. It has limited resources.
 * @author Geert Smelt
 */
public class Tablet {
    IdemixService service;
    IDreader id;
    CardTerminal t;
    final byte[] serverPublicKey = {};
    byte[] sessionKey;
    Crypto crypto;
    CardTerminals terminalList;

    /**
     * Create a new Tablet and initialize variables
     */
    public Tablet() throws CardException {
        this.crypto = new Crypto();
        this.id = new IDreader();
        TerminalFactory tf = TerminalFactory.getDefault();
        this.terminalList = tf.terminals();
        this.t = terminalList.list().get(0);
        this.sessionKey = new byte[CONSTANTS.BUFFER_SIZE];
    }

    private void start() throws IDVerificationException {
        Scanner scan = new Scanner(System.in);
        try {
            System.out.printf("Terminal List: %s\nSelected Terminal: %s\n", terminalList.list(), t);
        } catch (CardException e) {
            System.out.printf("Could not find a suitable terminal. Please make sure it is connected and try again.");
            e.printStackTrace();
        }
        System.out.print("Are we connecting to an ID (1) or an IRMA card (2)? ");
        int type = scan.nextInt();

        switch (type) {
            case 1:
                System.out.println("Please keep your passport or ID card against the terminal.");
                if (id.verifyIntegrity())
                    System.out.println("Everything checks out. Your ID is genuine.");
                else
                    throw new IDVerificationException("Your ID couldn't be verified. It might have been tampered with.");
                break;
            case 2:
                System.out.println("Please keep your IRMA card against the terminal, or insert it.");
                awaitConnection(30000);
                generateSessionKey();
                break;
            default:
                System.out.println("Unsupported operation, please try again.");
        }
    }

    /**
     * @see #awaitConnection(long)
     */
    private void awaitConnection() {
        awaitConnection(0);
    }

    /**
     * Waits for the user to insert a smart card into the terminal.
     *
     * @param timeout The number of milliseconds to wait.
     * @return true if a card was detected, false otherwise.
     */
    private boolean awaitConnection(long timeout) {
        try {
            if (!t.waitForCardPresent(timeout)) {
                System.out.println("No IRMA card was detected in the allotted time.");
                return true;
            } else {
                System.out.println("Card detected, continuing.");
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Timeout value must not be negative.");
            e.printStackTrace();
        } catch (CardException e) {
            System.out.println("An unexpected error occurred.");
            e.printStackTrace();
        }
        return false;
    }

    private void generateSessionKey() {
        crypto.generateSessionKey();
    }

    private void clearBuffers() {
        java.util.Arrays.fill(sessionKey, (byte) 0);
    }

    private void reset() {
        clearBuffers();
    }

    public RSAPublicKey getPublicKey() {
        return crypto.getPublicKey();
    }

    public static void main(String[] args) {
        try {
            Tablet t = new Tablet();
            t.start();
        } catch (CardException e) {
            e.printStackTrace();
        } catch (IDVerificationException e) {
            e.printStackTrace();
        }
    }
}
