package fr.xlim.ssd.opal.gui.model.reader;

import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.util.ArrayList;


/**
 * The model for profiles. Make the link between the application and the XML
 *
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 */
public class ProfileModel {
    ArrayList<ProfileComponent> profiles = new ArrayList<ProfileComponent>();

    public ProfileModel()
            throws CardConfigNotFoundException {
        
        CardConfig profiles[] = CardConfigFactory.getAllCardConfigs();
        ProfileComponent profileComponent;
        CardConfig cardConfig;

        for(int i = 0; i < profiles.length; i++) {
            cardConfig = profiles[i];
            ATR[] atrs = cardConfig.getAtrs();
            String[] ret = new String[atrs.length];
            String AID = Conversion.arrayToHex(cardConfig.getIssuerSecurityDomainAID());
            String SCPMode = getSCPMode(cardConfig.getScpMode());
            SCKey scKey[] = cardConfig.getSCKeys();

            for(int j = 0; j < atrs.length; i++) {
                ret[j] = Conversion.arrayToHex(atrs[j].getValue());
            }

            /*for(int j = 0; j < scKey.length; i++) {
                addKey(scKey[i]);
            }*/

            profileComponent = new ProfileComponent(cardConfig.getName(), cardConfig.getDescription(), AID, SCPMode, cardConfig.getTransmissionProtocol(), ret, cardConfig.getImplementation());
            
            this.profiles.add(profileComponent);
        }
    }

    public ProfileComponent getProfile(int i) {
        return profiles.get(i);
    }

    public String[][] getAllProfiles() {
        String allProfiles[][] = new String[profiles.size()][3];

        for(int i = 0; i < profiles.size(); i++) {
            allProfiles[i][0] = profiles.get(i).getName();
            allProfiles[i][1] = profiles.get(i).getDescription();
            allProfiles[i][2] = profiles.get(i).getImplementation();
        }

        return allProfiles;
    }

    public void addProfile(ProfileComponent p) {
        profiles.add(p);

        // Insert the profile in the xml
        
    }

    private String getSCPMode(SCPMode scp) {
        String res = null;
        
        if (scp.equals(SCPMode.SCP_01_05)) {
            res = "01_05";
        } else if (scp.equals(SCPMode.SCP_01_15)) {
            res = "01_15";
        } else if (scp.equals(SCPMode.SCP_02_15)) {
            res = "02_15";
        } else if (scp.equals(SCPMode.SCP_02_04)) {
            res = "02_04";
        } else if (scp.equals(SCPMode.SCP_02_05)) {
            res = "02_05";
        } else if (scp.equals(SCPMode.SCP_02_14)) {
            res = "02_14";
        } else if (scp.equals(SCPMode.SCP_02_0A)) {
            res = "02_0A";
        } else if (scp.equals(SCPMode.SCP_02_45)) {
            res = "02_45";
        } else if (scp.equals(SCPMode.SCP_02_55)) {
            res = "02_55";
        } else if (scp.equals(SCPMode.SCP_03_65)) {
            res = "03_65";
        } else if (scp.equals(SCPMode.SCP_03_6D)) {
            res = "03_6D";
        } else if (scp.equals(SCPMode.SCP_03_05)) {
            res = "03_05";
        } else if (scp.equals(SCPMode.SCP_03_0D)) {
            res = "03_0D";
        } else if (scp.equals(SCPMode.SCP_03_2D)) {
            res = "03_2D";
        } else if (scp.equals(SCPMode.SCP_03_25)) {
            res = "03_25";
        }
        
        return res;
    }

    public String getName() {
        return getName();
    }

    public String getDescription() {
        return getDescription();
    }

    public String getImplementation() {
        return getImplementation();
    }

    public boolean deleteProfile(int id)
            throws CardConfigNotFoundException {

        String name = profiles.get(id).getName();
        boolean t = false;

        t = CardConfigFactory.deleteCardConfig(name);

        return t;
    }
}
