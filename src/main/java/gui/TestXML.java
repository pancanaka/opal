/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miaouPlop
 */
public class TestXML {
    private static CardConfig profils[];

    public TestXML() {
        try {
            profils = CardConfigFactory.getAllCardConfigs();
        } catch (CardConfigNotFoundException ex) {
            Logger.getLogger(TestXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String[][] getProfils() {
        String allProfils[][] = new String[profils.length][3];

        for(int i = 0; i < profils.length; i++) {
            allProfils[i][0] = Conversion.arrayToHex(profils[i].getIssuerSecurityDomainAID());
            allProfils[i][1] = profils[i].getTransmissionProtocol();
            allProfils[i][2] = profils[i].getImplementation();
        }

        
        return allProfils;
    }
}
