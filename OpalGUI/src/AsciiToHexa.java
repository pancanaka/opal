/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
            System.out.println(hex);
            tab[i] = (byte)Integer.parseInt(hex,16);
        }

        return tab;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        byte[] hexa = stringToHex("plopinou");
        for(byte b : hexa) {
            System.out.println(b);
        }
    }

}

