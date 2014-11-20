package org.irmacard.identity.common;


/**
 * Class featuring all static definitions and constants used in other classes
 *
 * @author Geert Smelt
 */
public class CONSTANTS {

    // TODO: Sanity check on values

    /** The buffer size. Given a random value for now. */
    public static final int BUFFER_SIZE = 256;

    public static final short CONNECTION_STATUS_CONNECTED = 0xCC;
    public static final short CONNECTION_STATUS_DISCONNECTED = 0xCD;

    public static final short INS_VERIFY_ID = 0x20;
    public static final short INS_SIGN_CREDENTIALS = 0x21;

    // Delimiters for the various Data Groups on the Passport chip
    public static final int PASSPORT_DG1_OFFSET = 5;
    public static final int PASSPORT_DG1_LE = 250;

    public static final short AUTH_REQUEST = 0xA1;
    public static final short DATA_LENGTH_OFFSET = 0x02;

    public static final short ACK = 0x01;
    public static final short OK = 0x02;
    public static final short ERROR = 0xFF;
}
