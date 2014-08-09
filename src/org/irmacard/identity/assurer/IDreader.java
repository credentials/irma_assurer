package org.irmacard.identity.assurer;

import net.sourceforge.scuba.smartcards.CardServiceException;
import org.irmacard.identity.common.CONSTANTS;
import org.jmrtd.*;
import org.jmrtd.lds.FaceImageInfo;
import org.jmrtd.lds.FaceInfo;
import org.jmrtd.lds.LDS;

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
    Passport passport;
    Crypto crypto;
    PassportService passportService;
    byte CONNECTION_STATUS;
    private MRTDTrustStore trustManager;
    private List<BACKeySpec> bacStore;

    public IDreader(Crypto c, PassportService ps) {
        crypto = c;
        passportService = ps;
        CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_DISCONNECTED;

        try {
            String keyStorePath = "file:/" + System.getProperty("user.dir").replace("\\", "/") + "/irma_assurer/csca.ks";

            trustManager = new MRTDTrustStore();
            trustManager.addCSCAStore(new URI(keyStorePath)); // Only for BAC, EAC needs CVCA store

            bacStore = new ArrayList<BACKeySpec>();
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
            bacStore.add(new BACKey(documentNumber, dateOfBirth, dateOfExpiry));

            // This generates an exception by default on BAC enabled passports. Don't worry!
            passport = new Passport(passportService, trustManager, bacStore);

            System.out.println("====================================");
            System.out.println("Supported features of this passport:");
            System.out.println("AA : " + passport.getFeatures().hasAA());
            System.out.println("BAC: " + passport.getFeatures().hasBAC());
            System.out.println("EAC: " + passport.getFeatures().hasEAC());
            System.out.println("====================================");

            vs = passport.verifySecurity();
        } catch (IllegalArgumentException e) {
            System.out.println("You have entered an incorrect value: " + e.getMessage());
        } catch (CardServiceException e) {
            // TODO: Also thrown when a non-passport chip is attempted, switch to other mode dynamically
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
        System.out.println("AA : " + localStatus.getAA()  + "\t : " + localStatus.getAAReason());
        System.out.println("BAC: " + localStatus.getBAC() + "\t : " + localStatus.getBACReason());
        System.out.println("CS : " + localStatus.getCS()  + "\t : " + localStatus.getCSReason());
        System.out.println("DS : " + localStatus.getDS()  + "\t : " + localStatus.getDSReason());
        System.out.println("EAC: " + localStatus.getEAC() + "\t : " + localStatus.getEACReason());
        System.out.println("HT : " + localStatus.getHT()  + "\t : " + localStatus.getHTReason());
        System.out.println("====================================");

        // TODO: Determine what local checks suffice for integrity
        System.out.println("The passport passes local checks. Sending to the server for further analysis.");

        if (verifyRemotely()) {
            System.out.println("The passport passes remote checks as well. Proceeding with IRMA attributes.");
        }

        storePassportData();

        return VerificationStatus.Verdict.SUCCEEDED;
    }

    private void storePassportData() {
        try {
            LDS lds = passport.getLDS();

            List<Short> availableDataGroups = lds.getDataGroupList();
            StringBuilder sb = new StringBuilder("Supported Data Groups in this passport: ");
            for (Short dataGroupNumber : availableDataGroups) {
                sb.append(dataGroupNumber.byteValue()).append(" ");
            }
            System.out.println(sb);

            System.out.println(lds.getDG1File());

            List<FaceInfo> availableFaces = lds.getDG2File().getFaceInfos();

            for (FaceInfo face : availableFaces) {
                List<FaceImageInfo> details = face.getFaceImageInfos();
                System.out.println("There are " + details.size() + " infos in this face.");
                for (FaceImageInfo f : details) {
                    if (f.getSourceType() == FaceImageInfo.SOURCE_TYPE_STATIC_PHOTO_SCANNER) {
                        // TODO: Do something more useful with this
                        System.out.println("This picture was added from a photo scanner.");
                    }
                }
            }

            // System.out.println(lds.getDG3File()); // TODO: Inaccessible until after EAC is performed

            // System.out.println(lds.getDG14File());

            // System.out.println(lds.getDG15File());

        } catch (NullPointerException ignored) {
            System.out.println("The passport data has not been initialized yet.");
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void reset() {
        passport = null;
        bacStore.clear();
        trustManager.clear();
        // passportService.close();
    }
}
