package org.irmacard.identity;

import org.irmacard.credentials.*;
import org.irmacard.credentials.cert.*;
import org.irmacard.credentials.info.*;
import org.irmacard.credentials.spec.*;
import org.irmacard.credentials.keys.*;
import org.irmacard.credentials.util.*;

/**
 * IDreader will handle the reading of data on the chip inside an ID document such as a passport.
 */
public class IDreader {
    String surname = "";
    String lastname = "";
    String street = "";
    String housenumber = "";
    String zipcode = "";
    String state = "";
    String country = "";
    String phone = "";
    String bsn = "";
    String passportIdentifier = "";

    public IDreader() {
        // Init stuff
    }

    public boolean verifyIntegrity() {
        // TODO: Method stub
        return false;
    }

    public void storePassportData() {
        // TODO: Method stub
    }
}
