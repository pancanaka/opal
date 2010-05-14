package fr.xlim.ssd.opal.library.utilities;

import java.util.regex.Pattern;

/**
 * This class provides differents, but usefull, conversion functions
 * @author University of Limoges - Anthony DESSIATNIKOFF, Damien ARCUSET, &Eacute;milie FAUGERON, &Eacute;ric LINKE
 */
public class Conversion {

    /**
     * To convert a byte array into an hexadecimal string
     * @param data the byte array to be converted
     * @return the output string
     */
    public static String arrayToHex(byte[] data) {
        StringBuffer sb = new StringBuffer();
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
     * To convert an hexadecimal string into a byte array
     * @param s the string to be converted, each byte being separated by spaces or not (eg. s = "65 A0 12" <=> "65A012")
     * @return the corresponding byte array
     */
    public static byte[] hexToArray(String s) {

        // check the entry
        Pattern p = Pattern.compile("([a-fA-F0-9]{2}[ ]*)*");
        boolean valid = p.matcher(s).matches();

        if(!valid)
            throw new IllegalArgumentException("not a valid string representation of a byte array");

        String hex = s.replaceAll(" ", "");
        byte[] tab = new byte[hex.length() / 2];
        for (int i = 0; i < tab.length; i++) {
            tab[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return tab;
    }
}