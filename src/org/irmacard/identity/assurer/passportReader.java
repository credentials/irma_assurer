package org.irmacard.identity.assurer;

import net.sourceforge.scuba.smartcards.CardServiceException;
import org.jmrtd.*;
import org.jmrtd.lds.DisplayedImageInfo;
import org.jmrtd.lds.FaceImageInfo;
import org.jmrtd.lds.FaceInfo;
import org.jmrtd.lds.LDS;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * IDreader will handle the reading of data on the chip inside an ID document such as a passport.
 */
public class PassportReader {
    Passport passport;
    PassportService passportService;
    private MRTDTrustStore trustManager;
    private List<BACKeySpec> bacStore;

    public PassportReader(PassportService ps) {
        passportService = ps;
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

    public VerificationStatus.Verdict verifyIntegrity(String documentNumber, String dateOfBirth, String dateOfExpiry) {
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

        // TODO: Determine what local checks suffice for integrity and maybe return a boolean?
        System.out.println("The passport passes local checks. Sending to the server for further analysis.");
        return VerificationStatus.Verdict.SUCCEEDED;
    }

    // TODO: Probably has to be moved to the server side, so it can sign immediately
    private void storePassportData() {
        try {
            LDS lds = passport.getLDS();

            // TODO: Retrieve Data Groups based on the list instead of hard-coded.
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
                    switch (f.getType()) {
                        case DisplayedImageInfo.TYPE_PORTRAIT: System.out.println("Portrait"); break;
                        case DisplayedImageInfo.TYPE_FINGER: System.out.println("Fingerprint"); break;
                        case DisplayedImageInfo.TYPE_IRIS: System.out.println("Iris"); break;
                        case DisplayedImageInfo.TYPE_SIGNATURE_OR_MARK: System.out.println("Signature or Mark"); break;
                        default:
                            System.out.println("Unrecognized image type."); break;
                    }
                    int imageLength = f.getImageLength();
                    String imageMimeType = f.getMimeType();
                    System.out.println("Image length: " + imageLength);
                    System.out.println("Image Mime type: " + imageMimeType);

                }
            }

            // System.out.println(lds.getDG3File()); // TODO: Inaccessible until after EAC is performed

            System.out.println(lds.getDG14File());

            System.out.println(lds.getDG15File());

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
    }
}
