
package org.irmacard.identity;

import net.sourceforge.scuba.smartcards.*;

import javax.smartcardio.*;
import java.security.interfaces.RSAPublicKey;

import java.util.Scanner;


/**
 * This is the app that gets installed on the tablets. It has limited resources.
 * @author Geert Smelt
 */
public class Tablet {
    IDreader id;
    TerminalCardService terminalService;
    Crypto crypto;

    /**
     * Create a new Tablet and initialize variables
     */
    public Tablet() {
        this.crypto = new Crypto();
        this.id = new IDreader();
        CardTerminals terminalList = TerminalFactory.getDefault().terminals();
        try {
            this.terminalService = new TerminalCardService(terminalList.list().get(0));
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
        System.out.printf("Using terminal '%s'\n", this.terminalService.getTerminal());
        System.out.print("Do you wish to read an ID (1), an IRMA card (2) or abort (3)? ");
        int type = scan.nextInt();

        switch (type) {
            case 1:
                System.out.println("Please keep your passport or ID card against the terminal.");
                if (id.verifyIntegrity()) {
                    System.out.println("Everything checks out. Your ID is genuine.");
                    id.storePassportData();
                }
                else
                    System.out.println("Your ID couldn't be verified. It might have been tampered with.");
                break;
            case 2:
                System.out.println("Please keep your IRMA card against the terminal, or insert it.");
                if (awaitConnection(30000)) {
                    generateSessionKey();
                }
                break;
            case 3:
                System.out.println("Resetting buffers and quitting.");
                reset();
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
            if (!terminalService.getTerminal().waitForCardPresent(timeout)) {
                System.out.println("No IRMA card was detected in the allotted time.");
                return false;
            } else {
                System.out.println("Card detected, continuing.");
                terminalService.open();
                return terminalService.isOpen();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Timeout value must not be negative.");
            e.printStackTrace();
        } catch (CardException e) {
            System.out.println("An unexpected error occurred.");
            e.printStackTrace();
        } catch (CardServiceException e) {
            e.printStackTrace();
            return false;
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
