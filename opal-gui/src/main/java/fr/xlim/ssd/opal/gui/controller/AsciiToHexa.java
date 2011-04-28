package fr.xlim.ssd.opal.gui.controller;

/**
 * 
 * @author Thibault
 */

public class AsciiToHexa {

    public static byte[] stringToHex(String chaine) {
        int longueur = chaine.length();
        String hex;
        byte[] tab = new byte[longueur];

        for(int i=0 ; i<longueur ; i++) {
            hex = Integer.toHexString((int)chaine.charAt(i));
            tab[i] = (byte)Integer.parseInt(hex,16);
        }

        return tab;
    }
}