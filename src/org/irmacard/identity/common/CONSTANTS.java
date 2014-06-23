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
}
