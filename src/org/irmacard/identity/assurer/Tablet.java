
package org.irmacard.identity.assurer;

import net.sourceforge.scuba.smartcards.*;
import org.irmacard.identity.common.Formatter;

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

    final int CHIP_TYPE_IRMA = 1;
    final int CHIP_TYPE_ID = 2;

    /**
     * Create a new Tablet and initialize variables
     */
    public Tablet() {
        this.crypto = new Crypto();
        this.id = new IDreader(crypto);
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

    /**
     * This is the backbone of the tablet's functionality. This method determines which actions to perform.
     */
    private void start() {
        System.out.printf("Using terminal '%s'\n", this.terminalService.getTerminal());
        System.out.println("Please keep your card or identity document against the terminal.");
        System.out.printf("Is this an IRMA card (%d) or an identity document (%d)? ", CHIP_TYPE_IRMA, CHIP_TYPE_ID);
        if (awaitConnection()) {
            switch (new Scanner(System.in).nextInt()) {
                case CHIP_TYPE_IRMA:
                    generateSessionKey();
                    System.out.print(Formatter.toHexString(terminalService.getATR()));
                    break;
                case CHIP_TYPE_ID:
                    try {
                        id.verifyIntegrity();
                        id.storePassportData();
                    } catch (IDVerificationException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Unsupported operation, please try again.");
            }
        }

        // Properly close the terminal service before shutting down
        terminalService.close();
    }

    /**
     * @see #awaitConnection(long)
     */
    private boolean awaitConnection() {
        return awaitConnection(0);
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

    /**
     * @see Crypto#generateSessionKey()
     */
    private void generateSessionKey() {
        crypto.generateSessionKey();
    }

    /**
     * @see Crypto#reset()
     */
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
