package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.reader.ProfileModel;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;

/**
 *
 * @author Yorick Lesecque
 */
public class ProfileController {
    private ProfileModel profileModel;
    public static String mode = "modify";

    public ProfileController()
            throws CardConfigNotFoundException {
        profileModel = new ProfileModel();
    }

    public String[][] getAllProfiles() {
        return profileModel.getAllProfiles();
    }

    public boolean deleteProfile(int id)
            throws CardConfigNotFoundException {
        return profileModel.deleteProfile(id);
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