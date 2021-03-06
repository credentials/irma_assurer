
package org.irmacard.identity.assurer;

import net.sourceforge.scuba.smartcards.*;
import org.irmacard.identity.common.CONSTANTS;
import org.irmacard.identity.common.Formatter;
import org.jmrtd.PassportService;

import javax.smartcardio.*;
import java.io.*;
import java.net.*;
import java.security.interfaces.RSAPublicKey;

import java.util.Scanner;


/**
 * This is the app that gets installed on the tablets. It has limited resources.
 * @author Geert Smelt
 */
public class Tablet {
    PassportReader passportReader;
    TerminalCardService terminalCardService;
    Crypto crypto;

    PrintWriter out;
    BufferedReader in;

    final int CHIP_TYPE_IRMA = 1;
    final int CHIP_TYPE_EID = 2;

    /**
     * Create a new Tablet and initialize variables
     */
    public Tablet() {
        crypto = new Crypto();

        CardTerminals terminalList = TerminalFactory.getDefault().terminals();
        try {
            terminalCardService = new TerminalCardService(terminalList.list().get(0));
            CardService cs = CardService.getInstance(terminalList.list().get(0));
            passportReader = new PassportReader(new PassportService(cs));
        } catch (CardException e) {
            System.out.println("Could not select the correct terminal.");
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No terminals were detected. Are you sure it is connected?");
            e.printStackTrace();
        } catch (CardServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is the backbone of the tablet's functionality. This method determines which actions to perform.
     */
    private void start() {

        // FIXME: Determine whether this is obsolete in the new client-server setup
        System.out.printf("Using terminal '%s'\n", terminalCardService.getTerminal());
        System.out.println("Please keep your card or identity document against the terminal.");
        System.out.printf("Is this an IRMA card (%d) or an identity document (%d)? ", CHIP_TYPE_IRMA, CHIP_TYPE_EID);
        if (awaitCard()) {
            switch (new Scanner(System.in).nextInt()) {
                case CHIP_TYPE_IRMA:
                    generateSessionKey();
                    System.out.print(Formatter.toHexString(terminalCardService.getATR()));
                    break;
                case CHIP_TYPE_EID:
                    // TODO: Temporarily store this here
                    String documentNumber = "NSK8DCPJ4";
                    String dateOfBirth = "880810";
                    String dateOfExpiry = "180321";

                    passportReader.verifyIntegrity(documentNumber, dateOfBirth, dateOfExpiry);
                    ConnectionManager connectionManager = ConnectionManager.getInstance();
                    connectionManager.connectToServer();
                    break;
                default:
                    System.out.println("Unsupported operation, please try again.");
            }
        }

        // Properly close the terminal service before shutting down
        terminalCardService.close();
    }

    /**
     * @see #awaitCard(long)
     */
    private boolean awaitCard() {
        return awaitCard(0);
    }

    /**
     * Waits for the user to insert a smart card into the terminal.
     *
     * @param timeout The number of milliseconds to wait.
     * @return true if a card was detected, false otherwise.
     */
    private boolean awaitCard(long timeout) {
        try {
            if (!terminalCardService.getTerminal().waitForCardPresent(timeout)) {
                System.out.println("No IRMA card was detected in the allotted time.");
                return false;
            } else {
                terminalCardService.open();
                return terminalCardService.isOpen();
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
     * Attempts to set up a connection to the verification server.
     * @param host The hostname of the server.
     * @param port The port number of the server.
     * @return true if successful, false otherwise.
     */
    private boolean connectToServer(String host, int port) {
        try {
            System.out.println("Opening a new socket.");

            Socket s = new Socket(host, port);

            System.out.println("Socket opened. Creating input and output streams.");

            out = new PrintWriter(s.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            return s.isConnected();
        } catch (ConnectException e) {
            System.out.printf("Could not connect to server: \"%s\"\n", e.getMessage());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendData() {
        out.println(CONSTANTS.INS_VERIFY_ID);
    }

    private void receiveData() throws SocketException {
        String responseLine;
        try {
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
