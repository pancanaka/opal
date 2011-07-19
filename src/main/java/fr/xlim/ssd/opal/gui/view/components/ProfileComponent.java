/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Yorick Lesecque <yorick.lesecque@etu.unilim.fr>                  *
 *           Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 *           Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>       *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.model.Key.KeyModel;
import fr.xlim.ssd.opal.library.KeyType;
import fr.xlim.ssd.opal.library.SCGPKey;
import fr.xlim.ssd.opal.library.SCGemVisa;
import fr.xlim.ssd.opal.library.SCGemVisa2;
import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.util.ArrayList;

/**
 * Container for one profile
 *
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 * @author Tiana Razafindralambo
 */
public class ProfileComponent implements Comparable {
    private String name, description, AID, SCPmode, TP, implementation;
    private String[] ATR;
    private ArrayList<KeyComponent> Keylist = new ArrayList<KeyComponent>();
    private ArrayList<KeyModel> keys = new ArrayList<KeyModel>();


    /**
     * This constructor initializes attributes with values ​​given in parameters
     *
     * @param name the name of the profile
     * @param description a description of the profile
     * @param AID the Issuer Security Domain AID of the profile
     * @param SCPmode the SCP Mode of the profile
     * @param TP the transmission protocol of the profile
     * @param ATR the list of ATR of the profile
     * @param implementation the value of the profile
     */
    public ProfileComponent(String name, String description, String AID, String SCPmode, String TP, String[] ATR, String implementation) {
        this.name = name;
        this.description = description;
        this.AID = AID;
        this.SCPmode = SCPmode;
        this.TP = TP;
        this.ATR = ATR;
        this.implementation = implementation;
    }

    /**
     * @return the name of the profile
     */
    public String getName() { return this.name; }

    /**
     * @return the description of the profile
     */
    public String getDescription() { return this.description; }

    /**
     * @return the Issuer Security Domain AID of the profile
     */
    public String getAID() { return this.AID; }

    /**
     * @return the SCP Mode of the profile
     */
    public String getSCPmode() { return this.SCPmode; }

    /**
     * @return the Transmission Protocol of the profile
     */
    public String getTP() { return this.TP; }

    /**
     * @return the implementation of the profile
     */
    public String getImplementation() { return this.implementation; }

    /**
     * @return a list of ATR
     */
    public String[] getATR() { return this.ATR; }

    /**
     * @return an ArrayList of KeyComponentPanel
     */
    public ArrayList getKeylist() { return this.Keylist; }


    /**
     * Add a new key to the list (and its associated model)
     * @param type the type of the key
     * @param version the version of the key
     * @param key the key value
     */
    public void addKey(String type, String version, String id, String key) {
        Keylist.add(new KeyComponent(type, version, id, key));
        keys.add(new KeyModel(type, version, id, key));
    }

    /**
     * @return an ArrayList of KeyModel
     */
    public ArrayList<KeyModel> getKeys() {
        return this.keys;
    }

    /**
     * @param keys ArrayList of KeyModel
     */
    public void setKeys(ArrayList<KeyModel> keys) {
        this.keys = keys;
    }


    /**
     * Overrides the compareTo function in order to compare the ProfileComponent
     * given in parameter to this instance
     * @param o the <code>ProfileComponent</code> to compare to this class
     * @return -1 if this class is before the parameter, 1 if it is after, 0 if both are equals
     */
    @Override
    public int compareTo(Object o) {
        ProfileComponent p2 = (ProfileComponent) o;
        return this.name.compareToIgnoreCase(p2.getName());
    }


    /**
     * Converts all fields of this class into a <code>CardConfig</code> object
     * @return an instance of <code>CardConfig</code>
     * @see fr.xlim.ssd.opal.library.params.CardConfig
     */
    public CardConfig convertToCardConfig() {
        //convert this.ATR into ATR[]
        int atrLength = this.ATR.length;
        ATR[] atrs = new ATR[atrLength];        
        for(int i = 0; i < atrLength; i++)
            atrs[i] = new ATR(Conversion.hexToArray(this.ATR[i]));
        //-----------------------------------------------------------

        //convert this.keys into SCKey[]
        int keysLength = this.keys.size();
        SCKey[] keys = new SCKey[keysLength];
        KeyModel currentKey = null;

        for(int i = 0; i < keysLength; i++) {
            currentKey = this.keys.get(i);
            String keyType = currentKey.type;
            String keyVersionNumber = currentKey.version;
            String keyDatas = currentKey.key;

            System.out.println(keyType);

            if (keyType.equals("DES_ECB") || keyType.equals("83")) {
                String keyId = currentKey.keyID;
                keys[i] = new SCGPKey((byte) Integer.parseInt(keyVersionNumber), (byte) Integer.parseInt(keyId),
                        KeyType.DES_ECB, Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("DES_CBC") || keyType.equals("84")) {
                String keyId = currentKey.keyID;
                keys[i] = new SCGPKey((byte) Integer.parseInt(keyVersionNumber), (byte) Integer.parseInt(keyId),
                        KeyType.DES_CBC, Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("SCGemVisa2") || keyType.equals("1")) {
                keys[i] = new SCGemVisa2((byte) Integer.parseInt(keyVersionNumber), Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("SCGemVisa") || keyType.equals("0")) {
                keys[i] = new SCGemVisa((byte) Integer.parseInt(keyVersionNumber), Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("AES") || keyType.equals("AES_CBC") || keyType.equals("88")) {
                String keyId = currentKey.keyID;
                keys[i] = new SCGPKey((byte) Integer.parseInt(keyVersionNumber), (byte) Integer.parseInt(keyId),
                        KeyType.AES_CBC, Conversion.hexToArray(keyDatas));
            }
        }
        
        //---------------------------------------------------------------------------------------------------------
        
        return new CardConfig(  this.name,
                                this.description,
                                atrs,
                                Conversion.hexToArray(this.AID),
                                this.getSCPMode(this.SCPmode.replace("SCP_", "")),
                                this.TP,
                                keys,
                                this.implementation
                             );

    }


    /**
     * Static function that convert a <code>CardConfig</code> object into a <code>ProfileComponent</code> object
     * @param card the <code>CardConfig</code> object to convert
     * @return an instance of <code>CardConfig</code>
     * @see fr.xlim.ssd.opal.library.params.CardConfig
     */
    public static ProfileComponent convertToProfileComponent(CardConfig card) {
        //convert ATR[] into String[]
        ATR[] at = card.getAtrs();
        int atrLength = at.length;
        String[] atrs = new String[atrLength];
        for(int i = 0; i < atrLength; i++)
            atrs[i] = Conversion.arrayToHex(at[i].getValue());
        //-----------------------------------------------------------

        //---------------------------------------------------------------------------------------------------------
        ProfileComponent p = new ProfileComponent(  card.getName(),
                                                    card.getDescription(),
                                                    Conversion.arrayToHex(card.getIssuerSecurityDomainAID()),
                                                    card.getScpMode().name(),
                                                    card.getTransmissionProtocol(),
                                                    atrs,
                                                    card.getImplementation());
        //---------------------------------------------------------------------------------------------------------


        //convert SCKey[] into ArrayList<KeyComponentPanel/KeyModel>
        SCKey[] sc = card.getSCKeys();
        int keysLength = sc.length;
        ArrayList<KeyComponent> kc = new ArrayList<KeyComponent>();
        ArrayList<KeyModel> km = new ArrayList<KeyModel>();
        SCKey currentKey = null;

        for(int i = 0; i < keysLength; i++) {
            currentKey = sc[i];

            String keyType = null;
            if(currentKey instanceof SCGemVisa2) {
                keyType = "1";
            }
            else if(currentKey instanceof SCGemVisa) {
                keyType = "0";
            }
            else if(currentKey instanceof SCGPKey) {
                keyType = Integer.toHexString(currentKey.getType().getValue() & 0xFF).toUpperCase();
            }

            String keyVersionNumber = Integer.toHexString(currentKey.getSetVersion() & 0xFF).toUpperCase();
            String keyData = Conversion.arrayToHex(currentKey.getData());
            String keyID = Integer.toHexString(currentKey.getId() & 0xFF).toUpperCase();
            p.addKey(keyType, keyVersionNumber, keyID, keyData);
        }

        return p;
    }
    

    /**
     * Function that return a SCPMode object corresponding to the string given in parameter.
     * Notice that SCPMode is an enumeration!
     * @param scp the SCP wanted
     * @return the SCPMode corresponding to the scp given in parameter
     * @see fr.xlim.ssd.opal.library.SCPMode
     */
    public SCPMode getSCPMode(String scp) {
        SCPMode res = null;

        if (scp.equals("01_05")) {
            res = SCPMode.SCP_01_05;
        } else if (scp.equals("01_15")) {
            res = SCPMode.SCP_01_15;
        } else if (scp.equals("02_15")) {
            res = SCPMode.SCP_02_15;
        } else if (scp.equals("02_04")) {
            res = SCPMode.SCP_02_04;
        } else if (scp.equals("02_05")) {
            res = SCPMode.SCP_02_05;
        } else if (scp.equals("02_14")) {
            res = SCPMode.SCP_02_14;
        } else if (scp.equals("02_0A")) {
            res = SCPMode.SCP_02_0A;
        } else if (scp.equals("02_45")) {
            res = SCPMode.SCP_02_45;
        } else if (scp.equals("02_55")) {
            res = SCPMode.SCP_02_55;
        } else if (scp.equals("03_65")) {
            res = SCPMode.SCP_03_65;
        } else if (scp.equals("03_6D")) {
            res = SCPMode.SCP_03_6D;
        } else if (scp.equals("03_05")) {
            res = SCPMode.SCP_03_05;
        } else if (scp.equals("03_0D")) {
            res = SCPMode.SCP_03_0D;
        } else if (scp.equals("03_2D")) {
            res = SCPMode.SCP_03_2D;
        } else if (scp.equals("03_25")) {
            res = SCPMode.SCP_03_25;
        }

        return res;
    }


    /**
     * Function that return the KeyComponentPanel corresponding to the index
     * @param i the index of the KeyComponentPanel in the ArrayList
     * @return a KeyComponentPanel object
     */
    public KeyComponent getKey(int i) {
        return Keylist.get(i);
    }
}
