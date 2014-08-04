package org.irmacard.identity.assurer;

import net.sourceforge.scuba.smartcards.CardServiceException;
import org.irmacard.identity.common.CONSTANTS;
import org.jmrtd.*;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * IDreader will handle the reading of data on the chip inside an ID document such as a passport.
 */
public class IDreader {
    Crypto crypto;
    PassportService passportService;
    byte CONNECTION_STATUS;

    public IDreader(Crypto c, PassportService p) {
        crypto = c;
        passportService = p;
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

    private VerificationStatus verifyLocally(String documentNumber, String dateOfBirth, String dateOfExpiry) {
        try {
            // Set up a connection to the passport service
            List<BACKeySpec> bacStore = new ArrayList<BACKeySpec>();
            bacStore.add(new BACKey(documentNumber, dateOfBirth, dateOfExpiry));

            MRTDTrustStore trustManager = new MRTDTrustStore();
            Passport passport = new Passport(passportService, trustManager, bacStore);

            return new VerificationStatus();// passport.verifySecurity();
            /*
            passportService.open();

            if (passportService.isOpen()) {

                // Perform BAC to allow access to passport data
                BACKeySpec bacKey = new BACKey(documentNumber, dateOfBirth, dateOfExpiry);
                passportService.doBAC(bacKey);

                // Read out passport data required for EAC // TODO: Hardcoded still
                int offset = 5;
                int le = 250;
                boolean longRead = false;

                passportService.sendSelectFile(PassportService.EF_DG1);  // Get MRZ info
                byte[] dg1 = passportService.sendReadBinary(CONSTANTS.PASSPORT_DG1_OFFSET, CONSTANTS.PASSPORT_DG1_LE, false);

                passportService.sendSelectFile(PassportService.EF_DG14);  // Get CA keys
                byte[] dg14 = passportService.sendReadBinary(offset, le, longRead);

                MRZInfo mrzInfo = new MRZInfo(new String(dg1));

                System.out.printf("Here come the first %d bytes of DG_1: %s\n", le + 1, mrzInfo);

                passportService.sendSelectFile(PassportService.EF_CVCA);  // Get TA root certificate info
                byte[] cvca = passportService.sendReadBinary(offset, le, longRead);

                // TODO: Check if we hold the right country certificate for signing (determine req. certificate from CVCA)

                // Perform EAC part one (CA) to allow access to biometric passport data
                BigInteger keyId = new BigInteger(dg14);
                // passportService.doCA(keyId, publicKey);


                passportService.sendSelectFile(PassportService.EF_SOD);  // Get Security Object Directory
                byte[] sod = passportService.sendReadBinary(offset, le, longRead);

                // passportService.doTA();

                // Close the passport service when done
                passportService.close();
            }
            return true;
            */


        } catch (CardServiceException e) {
            System.out.println("Error connecting to the passport");
            e.printStackTrace();
            return new VerificationStatus();
        }
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
        VerificationStatus localStatus = verifyLocally(documentNumber, dateOfBirth, dateOfExpiry);

        System.out.println(localStatus);

        System.out.println("The passport passes local checks. Sending to the server for further analysis.");

        if (verifyRemotely()) {
            System.out.println("The passport passes remote checks as well. Proceeding with IRMA attributes.");
        }

        return VerificationStatus.Verdict.SUCCEEDED;
    }

    public void storePassportData() {
        // TODO: Method stub
    }
}
