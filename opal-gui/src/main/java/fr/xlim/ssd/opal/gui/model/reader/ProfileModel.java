package fr.xlim.ssd.opal.gui.model.reader;

import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import java.util.ArrayList;


/**
 * The model for profiles. Make the link between the application and the XML
 *
 * @author Thibault Desmoulins
 */
public class ProfileModel {
    ArrayList<ProfileComponent> profiles = new ArrayList<ProfileComponent>();

    public ProfileModel() {
        // Initialize profiles that are in the XML
        
    }

    public ProfileComponent getProfile(int i) {
        return profiles.get(i);
    }

    public ArrayList getProfiles() {
        return profiles;
    }

    public void addProfile(ProfileComponent p) {
        profiles.add(p);

        // Insert the profile in the xml
        
    }
}
