package org.irmacard.identity.common;

import java.math.BigInteger;

/**
 * Formatting of values as byteArrays and vice versa, and printing byte arrays as hex strings.
 *
 * @author Geert Smelt
 */
public final class Formatter {
    /**
     * Returns a hexadecimal byte string representation fit to be included as value for an array in JavaCard code.
     *
     * @param bytes an array of bytes.
     * @return a string containing a hex representation of <code>bytes</code>.
     */
    public static final String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append("0x");
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v).toUpperCase());
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Returns a hexadecimal byte string representation fit to be included as value for an array in JavaCard code.
     *
     * @see #toHexString(byte[])
     * @param val the value to convert
     * @return the converted value
     */
    public static final String toHexString(short val) {
        return toHexString(toByteArray(val));
    }

    /**
     * Converts a short to a byte array
     *
     * @param value the value to convert
     * @return the converted value
     */
    public static final byte[] toByteArray(short value) {
        return new byte[] { (byte) (value >> 8 & 0xff), (byte) (value & 0xff) };
    }

    /**
     * Converts an int to a byte array
     *
     * @param value the value to convert
     * @return the converted value
     */
    public static final byte[] toByteArray(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >> 16 & 0xff),
                (byte) (value >> 8 & 0xff), (byte) (value & 0xff) };
    }

    /**
     * Converts a byte array to a long, assuming the first byte is most significant
     *
     * @param b the value to convert
     * @return the converted value
     */
    public static final long byteArrayToLong(byte[] b) {
        long value = 0;
        for (byte aB : b) {
            value = (value << 8) + (aB & 0xff);
        }
        return value;
    }

    /**
     * Converts a byte array to an integer
     *
     * @param b the value to convert
     * @return the converted value
     */
    public static final int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }

    /**
     * Converts a byte array to a short
     *
     * @param b the value to convert
     * @return the converted value
     */
    public static final int byteArrayToShort(byte[] b) {
        return ((b[0] & 0xFF) << 8) | (b[1] & 0xFF);
    }

    /**
     * Gets an unsigned byte array representation of <code>big</code>. A leading
     * zero (present only to hold sign bit) is stripped.
     *
     * @param big a big integer.
     * @return a byte array containing a representation of <code>big</code>.
     */
    public static final byte[] getUnsignedBytes(BigInteger big) {
        byte[] data = big.toByteArray();
        if (data[0] == 0) {
            byte[] tmp = data;
            data = new byte[tmp.length - 1];
            System.arraycopy(tmp, 1, data, 0, tmp.length - 1);
        }
        return data;
    }
}
