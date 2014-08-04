package org.irmacard.identity.assurer;

import net.sourceforge.scuba.smartcards.CardServiceException;
import org.irmacard.identity.common.CONSTANTS;
import org.jmrtd.*;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * IDreader will handle the reading of data on the chip inside an ID document such as a passport.
 */
public class IDreader {
    Crypto crypto;
    PassportService passportService;
    byte CONNECTION_STATUS;
    private URI keyStoreURI;

    public IDreader(Crypto c, PassportService p) {
        crypto = c;
        passportService = p;
        CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_DISCONNECTED;
        try {
            // TODO: Make this path relative
            keyStoreURI = new URI("file:/C:/Users/Geert/Documents/Universiteit/Masterscriptie/irma_assurer/csca.ks");
        } catch (URISyntaxException e) {
            System.out.println("The keystore file path appears malformed: " + e.getMessage());
            e.printStackTrace();
        }
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
        } catch (ConnectException ce) {
            System.out.println("ERROR: " + ce.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_CONNECTED;
        return s;
    }

    private VerificationStatus verifyLocally(String documentNumber, String dateOfBirth, String dateOfExpiry) {
        VerificationStatus vs = new VerificationStatus();
        try {
            List<BACKeySpec> bacStore = new ArrayList<BACKeySpec>();
            bacStore.add(new BACKey(documentNumber, dateOfBirth, dateOfExpiry));

            MRTDTrustStore trustManager = new MRTDTrustStore();
            trustManager.addCSCAStore(keyStoreURI);

            // This generates an exception by default on BAC enabled passports. Don't worry!
            Passport passport = new Passport(passportService, trustManager, bacStore);

            System.out.println("====================================");
            System.out.println("Supported features of this passport:");
            System.out.println("AA : " + passport.getFeatures().hasAA());
            System.out.println("BAC: " + passport.getFeatures().hasBAC());
            System.out.println("EAC: " + passport.getFeatures().hasEAC());
            System.out.println("====================================");

            vs = passport.getVerificationStatus();
        } catch (CardServiceException e) {
            System.out.println("Error connecting to the passport: " + e.getMessage());
            e.printStackTrace();
        }
        return vs;
    }

    /**
     * This method attempts to set up a link to the ID verification server.
     */
    private boolean verifyRemotely() {
        String hostname = "localhost";
        int port = 9999;
        Socket socket = connectToServer(hostname, port);

        if (isConnected()) {
            // TODO: Perform remote verification
            SecretKey kTS = crypto.getSessionKey();
        }
        return false;
    }

    public VerificationStatus.Verdict verifyIntegrity(String documentNumber, String dateOfBirth, String dateOfExpiry) throws IDVerificationException {
        VerificationStatus localStatus = verifyLocally(documentNumber, dateOfBirth, dateOfExpiry);

        System.out.println("====================================");
        System.out.println("Verification Status results:");
        System.out.println("AA : " + localStatus.getAA());
        System.out.println("BAC: " + localStatus.getBAC());
        System.out.println("CS : " + localStatus.getCS());
        System.out.println("DS : " + localStatus.getDS());
        System.out.println("EAC: " + localStatus.getEAC());
        System.out.println("HT : " + localStatus.getHT());
        System.out.println("====================================");

        // TODO: Determine what local checks suffice for integrity
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
