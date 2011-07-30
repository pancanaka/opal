package fr.xlim.ssd.opal.library.utilities;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * This class provides different but useful conversion functions
 *
 * @author Anthony Dessiatnikoff,
 * @author Damien Arcuset
 * @author Émilie Faugeron
 * @author Éric Linke
 * @author Julien Iguchi-Cartigny
 */
public class Conversion {

    /**
     * Convert a byte array to an hexadecimal string
     *
     * @param data the byte array to be converted
     * @return the output string
     */
    public static String arrayToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String bs = Integer.toHexString(data[i] & 0xFF).toUpperCase();
            if (bs.length() == 1) {
                sb.append(0);
            }
            sb.append(bs);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Convert an hexadecimal string into a byte array. Each byte in ethe hexadecimal string being separated by spaces
     * or not (eg. s = "65 A0 12" <=> "65A012").
     *
     * @param s the string to be converted
     * @return the corresponding byte array
     * @throws IllegalArgumentException if not a valid string representation of a byte array
     */
    public static byte[] hexToArray(String s) {

        // check the entry
        Pattern p = Pattern.compile("([a-fA-F0-9]{2}[ ]*)*");
        boolean valid = p.matcher(s).matches();

        if (!valid) {
            throw new IllegalArgumentException("not a valid string representation of a byte array");
        }

        String hex = s.replaceAll(" ", "");
        byte[] tab = new byte[hex.length() / 2];
        for (int i = 0; i < tab.length; i++) {
            tab[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return tab;
    }

    /**
     * Convert a byte array into ASCII string representation.
     *
     * @param buf The bytes to format.
     * @return ASCII string representation of the specified bytes.
     * @throws UnsupportedEncodingException
     */
    public static String toAsciiString(byte[] buf) throws UnsupportedEncodingException {
        String ascii = null;
        if (buf != null) {
            ascii = new String(buf, "US-ASCII");
            // Check the characters
            char[] charArray = ascii.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                // Show null character as blank space
                if (charArray[i] == (char) 0x00) {
                    charArray[i] = ' ';
                }
            }
            ascii = new String(charArray);
        }
        return ascii;
    }

    /**
     * Convert the byte array to an int starting from the given offset.
     *
     * @param b      The byte array
     * @param offset The array offset
     * @return The integer
     */
    public static int byteArrayToInt(byte[] b) {
        if (b.length == 1) {
            return b[0] & 0xFF;
        } else if (b.length == 2) {
            return ((b[0] & 0xFF) << 8) + (b[1] & 0xFF);
        } else if (b.length == 3) {
            return ((b[0] & 0xFF) << 16) + ((b[1] & 0xFF) << 8) + (b[2] & 0xFF);
        } else if (b.length == 4)
            return (b[0] << 24)
                    + ((b[1] & 0xFF) << 16)
                    + ((b[2] & 0xFF) << 8)
                    + (b[3] & 0xFF);
        else
            throw new IndexOutOfBoundsException();
    }


}
