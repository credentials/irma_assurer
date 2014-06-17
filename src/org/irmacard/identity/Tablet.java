
package org.irmacard.identity;

import org.irmacard.idemix.*;

import javax.smartcardio.*;
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
    Crypto crypto;

    /**
     * Create a new Tablet and initialize variables
     */
    public Tablet() {
        this.crypto = new Crypto();
        this.id = new IDreader();
        CardTerminals terminalList = TerminalFactory.getDefault().terminals();
        try {
            this.t = terminalList.list().get(0);
        } catch (CardException e) {
            System.out.println("Could not select the correct terminal.");
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No terminals were detected. Are you sure it is connected?");
            e.printStackTrace();
        }
    }

    private void start() {
        Scanner scan = new Scanner(System.in);
        System.out.printf("Available terminals: %s\n", t);
        System.out.print("Are we connecting to an ID (1) or an IRMA card (2)? ");
        int type = scan.nextInt();

        switch (type) {
            case 1:
                System.out.println("Please keep your passport or ID card against the terminal.");
                if (id.verifyIntegrity())
                    System.out.println("Everything checks out. Your ID is genuine.");
                else
                    System.out.println("Your ID couldn't be verified. It might have been tampered with.");
                break;
            case 2:
                System.out.println("Please keep your IRMA card against the terminal, or insert it.");
                if (awaitConnection(30000)) {
                    generateSessionKey();
                }
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
                return false;
            } else {
                System.out.println("Card detected, continuing.");
                return true;
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

    private void reset() {
        crypto.reset();
    }

    public RSAPublicKey getPublicKey() {
        return crypto.getPublicKey();
    }

    public static void main(String[] args) {
        Tablet t = new Tablet();
        t.start();
    }
}
