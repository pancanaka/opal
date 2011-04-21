package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.model.Key.KeyModel;
import fr.xlim.ssd.opal.library.KeyType;
import fr.xlim.ssd.opal.library.SCGPKey;
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

    public ProfileComponent(String name, String description, String AID, String SCPmode, String TP, String[] ATR, String implementation) {
        this.name = name;
        this.description = description;
        this.AID = AID;
        this.SCPmode = SCPmode;
        this.TP = TP;
        this.ATR = ATR;
        this.implementation = implementation;
    }

    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getAID() { return this.AID; }
    public String getSCPmode() { return this.SCPmode; }
    public String getTP() { return this.TP; }
    public String getImplementation() { return this.implementation; }
    public String[] getATR() { return this.ATR; }
    public ArrayList getKeylist() { return this.Keylist; } 
    public void addKey(String type, String version, String id, String key) {
        Keylist.add(new KeyComponent(type, version, id, key));
        keys.add(new KeyModel(type, version, id, key));
    }

    public ArrayList<KeyModel> getKeys()
    {
        return this.keys;
    }
    public void setKeys(ArrayList<KeyModel> keys)
    {
        this.keys = keys;
    }
    @Override
    public int compareTo(Object o) {
        ProfileComponent p2 = (ProfileComponent) o;
        return this.name.compareToIgnoreCase(p2.getName());
    }
    public CardConfig convertToCardConfig()
    {
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
        for(int i = 0; i < keysLength; i++)
        {
            currentKey = this.keys.get(i);
            String keyType = currentKey.type;
            String keyVersionNumber = currentKey.version;
            String keyDatas = currentKey.key;
            if (keyType.equals("DES_ECB")) {
                String keyId = currentKey.keyID;
                keys[i] = new SCGPKey((byte) Integer.parseInt(keyVersionNumber), (byte) Integer.parseInt(keyId),
                        KeyType.DES_ECB, Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("DES_CBC")) {
                String keyId = currentKey.keyID;
                keys[i] = new SCGPKey((byte) Integer.parseInt(keyVersionNumber), (byte) Integer.parseInt(keyId),
                        KeyType.DES_CBC, Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("SCGemVisa2")) {
                keys[i] = new SCGemVisa2((byte) Integer.parseInt(keyVersionNumber), Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("SCGemVisa")) {
                keys[i] = new SCGemVisa2((byte) Integer.parseInt(keyVersionNumber), Conversion.hexToArray(keyDatas));
            }
            if (keyType.equals("AES")) {
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
                                this.getSCPMode(this.SCPmode),
                                this.TP,
                                keys,
                                this.implementation
                             );

    }

    public SCPMode getSCPMode(String scp)
    {
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
}
