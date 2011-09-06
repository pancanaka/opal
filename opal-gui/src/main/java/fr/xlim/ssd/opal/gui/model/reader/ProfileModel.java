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

package fr.xlim.ssd.opal.gui.model.reader;
 
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.SCGPKey;
import fr.xlim.ssd.opal.library.SCGemVisa;
import fr.xlim.ssd.opal.library.SCGemVisa2;
import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.util.ArrayList;
import java.util.Collections;


/**
 * The model for profiles. Make the link between the application and the XML
 *
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 * @author Tiana Razafindralambo
 */
public class ProfileModel {

    ArrayList<ProfileComponent> profiles = new ArrayList<ProfileComponent>();

    private CardConfig profiles_cf[];

    public ProfileModel()
            throws CardConfigNotFoundException {
        
        CardConfig profiles_cf[] = CardConfigFactory.getAllCardConfigs();
        ProfileComponent profileComponent;
        CardConfig cardConfig;

        for(int i = 0; i < profiles_cf.length; i++) {
            cardConfig = profiles_cf[i];
            ATR[] atrs = cardConfig.getAtrs();
            String[] ret = new String[atrs.length];
            String AID = Conversion.arrayToHex(cardConfig.getIssuerSecurityDomainAID());
            String SCPMode = getSCPMode(cardConfig.getScpMode());
            SCKey scKey[] = cardConfig.getSCKeys();

            for(int j = 0; j < atrs.length; j++) {
                ret[j] = Conversion.arrayToHex(atrs[j].getValue());
            }

            profileComponent = new ProfileComponent(cardConfig.getName(), cardConfig.getDescription(), AID, SCPMode, cardConfig.getTransmissionProtocol(), ret, cardConfig.getImplementation());

            for(int j = 0; j < scKey.length; j++) {
                String type = null;
                if(scKey[j] instanceof SCGemVisa2) {
                    type = "1";
                }
                else if(scKey[j] instanceof SCGemVisa) {
                    type = "0";
                }
                else if(scKey[j] instanceof SCGPKey) {
                    type = Integer.toHexString(scKey[j].getType().getValue() & 0xFF).toUpperCase();
                }
                
                String version = String.valueOf(Integer.parseInt(Integer.toHexString(scKey[j].getSetVersion() & 0xFF).toUpperCase(), 16));
                String id = Integer.toHexString(scKey[j].getId() & 0xFF).toUpperCase();
                String key = Conversion.arrayToHex(scKey[j].getData());
                
                profileComponent.addKey(type, version, id, key);
            }
            
            this.profiles.add(profileComponent);
            Collections.sort(this.profiles);
        }
    }

    public ProfileComponent getProfile(int i) {
        return profiles.get(i);
    }

    public ProfileComponent getProfileByName(String name) {
        int i = 0,  profilesLength = profiles.size();
        ProfileComponent currentProfile = profiles.get(i);

        while(i < profilesLength && !(currentProfile.getName().compareToIgnoreCase(name) == 0))
            currentProfile = profiles.get(++i);
        
        return currentProfile;
    }

    public CardConfig[] getAllCardConfigs() {
        return this.profiles_cf;
    }

    public String[][] getAllProfiles() {
        String allProfiles[][] = new String[profiles.size()][3];

        for(int i = 0; i < profiles.size(); i++) {
            allProfiles[i][0] = profiles.get(i).getName();
            allProfiles[i][1] = profiles.get(i).getDescription();
            allProfiles[i][2] = profiles.get(i).getImplementation().replace("fr.xlim.ssd.opal.library.commands.", "");
        }
        
        return allProfiles;
    }

    public void addProfile(CardConfig card)
            throws CardConfigNotFoundException {

        CardConfigFactory.addCardConfig(card);
        profiles.add(ProfileComponent.convertToProfileComponent(card));
        Collections.sort(this.profiles);
    }

    public void updateProfile(CardConfig card)
            throws CardConfigNotFoundException {

        CardConfigFactory.deleteCardConfig(card.getName());
        CardConfigFactory.addCardConfig(card);

        profiles.remove(getProfileByName(card.getName()));
        profiles.add(ProfileComponent.convertToProfileComponent(card));
        
        Collections.sort(this.profiles);
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

    public boolean deleteProfile(int id)
            throws CardConfigNotFoundException {

        String name = profiles.get(id).getName();
        boolean t = false;

        t = CardConfigFactory.deleteCardConfig(name);

        if(t) {
            profiles.remove(id);
        }

        return t;
    }
}