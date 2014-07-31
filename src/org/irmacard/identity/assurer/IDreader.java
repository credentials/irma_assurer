package org.irmacard.identity.assurer;

import net.sourceforge.scuba.smartcards.CardServiceException;
import org.irmacard.identity.common.CONSTANTS;
import org.jmrtd.BACKey;
import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.VerificationStatus;
import org.jmrtd.lds.MRZInfo;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * IDreader will handle the reading of data on the chip inside an ID document such as a passport.
 */
public class IDreader {
    Crypto crypto;
    PassportService ps;
    byte CONNECTION_STATUS;

    public IDreader(Crypto c, PassportService p) {
        crypto = c;
        ps = p;
        CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_DISCONNECTED;
    }

    private boolean isConnected() {
        return CONNECTION_STATUS == CONSTANTS.CONNECTION_STATUS_CONNECTED;
    }

    private Socket connectToServer(String hostname, int port) {
        System.out.println("Connecting to the verification server...");
        SSLSocket s = null;

        // TODO: Set up connection to the server

        try {
            s = (SSLSocket) SSLSocketFactory.getDefault().createSocket(hostname, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_CONNECTED;
        return s;
    }

    private boolean verifyLocally(String documentNumber, String dateOfBirth, String dateOfExpiry) {
        try {
            // Set up a connection to the passport service
            ps.open();

            if (ps.isOpen()) {

                // Perform BAC to allow access to passport data
                BACKeySpec bacKey = new BACKey(documentNumber, dateOfBirth, dateOfExpiry);
                ps.doBAC(bacKey);

                // Read out passport data required for EAC // TODO: Hardcoded still
                int offset = 5;
                int le = 250;
                boolean longRead = false;

                ps.sendSelectFile(PassportService.EF_DG1);  // Get MRZ info
                byte[] dg1 = ps.sendReadBinary(CONSTANTS.PASSPORT_DG1_OFFSET, CONSTANTS.PASSPORT_DG1_LE, false);

                ps.sendSelectFile(PassportService.EF_DG14);  // Get CA keys
                byte[] dg14 = ps.sendReadBinary(offset, le, longRead);

                MRZInfo mrzInfo = new MRZInfo(new String(dg1));

                System.out.printf("Here come the first %d bytes of DG_1: %s\n", le + 1, mrzInfo);

                ps.sendSelectFile(PassportService.EF_CVCA);  // Get TA root certificate info
                byte[] cvca = ps.sendReadBinary(offset, le, longRead);

                // TODO: Check if we hold the right country certificate for signing (determine req. certificate from CVCA)

                // Perform EAC part one (CA) to allow access to biometric passport data
                BigInteger keyId = new BigInteger(dg14);
                // ps.doCA(keyId, publicKey);


                ps.sendSelectFile(PassportService.EF_SOD);  // Get Security Object Directory
                byte[] sod = ps.sendReadBinary(offset, le, longRead);

                // ps.doTA();

                // Close the passport service when done
                ps.close();
            }
            return true;
        } catch (CardServiceException e) {
            System.out.println("Error connecting to the passport");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method attempts to set up a link to the ID verification server.
     */
    private boolean verifyRemotely() {
        String hostname = "localhost";
        int port = 9999;
        Socket s = connectToServer(hostname, port);

        if (isConnected()) {
            // TODO: Perform remote verification
            SecretKey kTS = crypto.getSessionKey();
        }
        return false;
    }

    public VerificationStatus.Verdict verifyIntegrity(String documentNumber, String dateOfBirth, String dateOfExpiry) throws IDVerificationException {
        VerificationStatus.Verdict localVerdict = VerificationStatus.Verdict.NOT_CHECKED;
        VerificationStatus.Verdict remoteVerdict = VerificationStatus.Verdict.NOT_CHECKED;

        if (verifyLocally(documentNumber, dateOfBirth, dateOfExpiry)) {
            System.out.println("The passport passes local checks. Sending to the server for further analysis.");
            localVerdict = VerificationStatus.Verdict.SUCCEEDED;
        }
        if (verifyRemotely()) {
            System.out.println("The passport passes remote checks as well. Proceeding with IRMA attributes.");
        }

        if (localVerdict == VerificationStatus.Verdict.SUCCEEDED && remoteVerdict == VerificationStatus.Verdict.SUCCEEDED) {
            System.out.println("Everything checks out. Your ID is genuine.");
            return VerificationStatus.Verdict.SUCCEEDED;
        } else {
            throw new IDVerificationException("Your ID couldn't be verified. It might have been tampered with.");
        }
    }

    public void storePassportData() {
        // TODO: Method stub
    }
}
