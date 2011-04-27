package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.reader.ProfileModel;
import fr.xlim.ssd.opal.gui.view.components.KeyComponent;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Yorick Lesecque
 * @author Tiana Razafindralambo
 */
public class ProfileController {
    private ProfileModel profileModel;
    public static String mode = "modify";

    public ProfileController()
            throws CardConfigNotFoundException {
        profileModel = new ProfileModel();
    }

    public ProfileModel getProfileModel()
    {
        return profileModel;
    }

    public String[][] getAllProfiles() {
        return profileModel.getAllProfiles();
    }

    public boolean deleteProfile(int id)
            throws CardConfigNotFoundException {
        return profileModel.deleteProfile(id);
    }

    public void addProfile() 
            throws CardConfigNotFoundException, ConfigFieldsException {
        //profileModel.addProfile();
        checkForm();
    }

    public void checkForm()
            throws ConfigFieldsException {
        checkName("Adb19778E-_/.");
        checkDesc("Voici la super description de la mort qui tue l'truc.");
        checkSCP("SCP_01_01");
        checkATR("09 AF BC");
    }

    public void checkName(String name)
            throws ConfigFieldsException {
        
        if(name.length() >= 4 && name.length() <= 25) {
            Pattern p1 = Pattern.compile("[^0-9A-Z/_ .-]+", Pattern.CASE_INSENSITIVE);
            Matcher m = p1.matcher(name);

            if(m.find()) {
                throw new ConfigFieldsException("The name contains illegal characters. You can only use figures, letters from A to Z and: /_.-\n");
            }
        }
        else {
            throw new ConfigFieldsException("The name must contain between 4 and 25 characters.\n");
        }
    }

    public void checkDesc(String desc)
            throws ConfigFieldsException {

        if(desc.length() > 0) {
            if(desc.length() < 141) {
                Pattern p1 = Pattern.compile("[^0-9A-Z/_ .'-]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(desc);

                if(m.find()) {
                    throw new ConfigFieldsException("The description contains illegal characters. You can only use figures, letters from A to Z and: /_.-'\n");
                }
            }
            else {
                throw new ConfigFieldsException("The description is optional but can't contain more than 140 characters.\n");
            }
        }
    }

    public void checkATR(String atr)
            throws ConfigFieldsException {

        if(atr.length() > 0) {
            atr = atr.replaceAll(":", "");
            atr = atr.replaceAll(" ", "");

            if(atr.length() % 2 == 0) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(atr);

                if(m.find()) {
                    throw new ConfigFieldsException("The ATR has to be an hexadecimal string. You can write it in different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            }
            else {
                throw new ConfigFieldsException("The ATR is invalid.\n");
            }
        }
        else {
            throw new ConfigFieldsException("The ATR can't be empty.\n");
        }
    }

    public void checkAID(String aid)
            throws ConfigFieldsException {

        if(aid.length() > 0) {
            aid = aid.replaceAll(":", "");
            aid = aid.replaceAll(" ", "");

            if(aid.length() % 2 == 0 && aid.length() >= 10 && aid.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(aid);

                if(m.find()) {
                    throw new ConfigFieldsException("The AID has to be an hexadecimal string. You can write it in different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            }
            else {
                throw new ConfigFieldsException("Issuer Security Domain AID is invalid.It must contain between 10 and 32 characters.\n");
            }
        }
        else {
            throw new ConfigFieldsException("Issuer Security Domain AID can't be empty.\n");
        }
    }

    public void checkSCP(String scp)
            throws ConfigFieldsException {

        if(scp.length() > 0) {
            if(scp.compareToIgnoreCase(SCPMode.SCP_01_05.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_01_05.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_01_15.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_04.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_05.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_0A.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_0B.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_14.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_15.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_1A.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_1B.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_55.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_45.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_54.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_65.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_6D.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_05.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_0D.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_2D.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_25.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_10.toString()) != 0) {
                
                throw new ConfigFieldsException("The SCPMode is unknown.\n");
            }
        }
        else {
            throw new ConfigFieldsException("SCPMode can't be empty.\n");
        }
    }

    public void checkTP(String tp)
            throws ConfigFieldsException {

        if(tp.length() > 0) {
            if(tp.compareToIgnoreCase("T=0") != 0
                    && tp.compareToIgnoreCase("T=1") != 0
                    && tp.compareToIgnoreCase("*") != 0) {
                throw new ConfigFieldsException("The Transmission Protocol is unknown.\n");
            }
        }
        else {
            throw new ConfigFieldsException("Transmission Protocol can't be empty.\n");
        }
    }

    public void checkKeys(KeyComponent[] keys)
            throws ConfigFieldsException {

        for(int i = 0; i < keys.length; i++) {
            Pattern p1 = Pattern.compile("[^0-9]+");
            Matcher m = p1.matcher(keys[i].getKeyId());

            if(!m.find()) {
                String version = keys[i].getKeyVersion();
                m = p1.matcher(version);

                if(!m.find()) {
                    if(Integer.valueOf(version) >= 0 && Integer.valueOf(version) < 256) {
                        //DES_ECB, DES_CBC, SCGemVisa, SCGemVisa2 et d’AES : vérifier la taille de la clé de chaque algo
                    }
                    else {
                        throw new ConfigFieldsException("Key versions must be between 0 and 255.");
                    }
                }
                else {
                    throw new ConfigFieldsException("Key versions must be numeric.");
                }
            }
            else {
                throw new ConfigFieldsException("Key IDs must be numeric.");
            }
        }
    }

    public void checkImpl(String impl)
            throws ConfigFieldsException {

        if(impl.length() > 0) {
            if(impl.compareToIgnoreCase("GemXpresso211Commands") != 0
                    && impl.compareToIgnoreCase("GP2xCommands") != 0
                    /* If new implementations are allowed : && impl.compareToIgnoreCase("*Implementation*") != 0  */) {
                throw new ConfigFieldsException("The Implementation is unknown.\n");
            }
        }
        else {
            throw new ConfigFieldsException("Implementation can't be empty.\n");
        }
    }


    /*public String[] getAtrs(int id) {
        if(mode.compareTo("modify") == 0 || mode.compareTo("view") == 0) {
            ATR[] atrs = profiles[id].getAtrs();
            String[] ret = new String[atrs.length];

            for(int i = 0; i < atrs.length; i++) {
                ret[i] = Conversion.arrayToHex(atrs[i].getValue());
            }

            return ret;
        }
        else {
            String[] ret = new String[1];
            return ret;
        }
    }*/
}