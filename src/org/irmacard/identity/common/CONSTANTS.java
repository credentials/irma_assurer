package org.irmacard.identity.common;


/**
 * Class featuring all static definitions and constants used in other classes
 *
 * @author Geert Smelt
 */
public class CONSTANTS {

    /** The buffer size. Given a random value for now. */
    public static final int BUFFER_SIZE = 256;

    /** The tablet is not connected to the verification server. */
    public static final byte CONNECTION_STATUS_DISCONNECTED = 0x00;
    /** The tablet is connected to the verification server. */
    public static final byte CONNECTION_STATUS_CONNECTED = 0x01;


    public static final String INS_VERIFY_ID = "INS_VERIFY_ID";
    public static final String INS_SIGN_CREDENTIALS = "INS_SIGN_CREDENTIALS";

    public static final String EXIT = "EXIT";
    public static final String ACK = "ACK";
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";



    // Delimiters for the various Data Groups on the Passport chip
    public static final int PASSPORT_DG1_OFFSET = 5;
    public static final int PASSPORT_DG1_LE = 250;
}
