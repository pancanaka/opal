/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.Authentication.AuthenticationModel;
import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel; 
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private AuthenticationModel authModel;
    private CardReaderModel cardReaderModel;
    private CommunicationModel communication;
    private ProfileController profileController;

    public AuthenticationController(CardReaderModel cardReaderModel, CommunicationModel communication, ProfileController profileController)
    {
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
        this.profileController = profileController;
        this.authModel = new AuthenticationModel(this.cardReaderModel, this.communication, this.profileController);
    }

    /**
     *  Get all profiles
     * @return all profiles loaded.
     */
    public String[][] getAllProfiles()
    {
        return this.authModel.getAllProfiles();
    }
    public String[] getAllProfileNames()
    { 
        return this.authModel.getAllProfileNames();
    }
    public String getCurrentCardDefaultProfileName()
    {
        return this.authModel.getCardConfig().getName();
    }
    public ProfileComponent getProfileByName(String name)
    { 
        ProfileComponent currProfile = this.authModel.getProfileByName(name);

        //Create a new profile component that you can use as you want
        //without to modify the current selected profile
        ProfileComponent profile = new ProfileComponent(    currProfile.getName(),
                                                            currProfile.getDescription(),
                                                            currProfile.getAID(),
                                                            currProfile.getSCPmode(),
                                                            currProfile.getTP(),
                                                            currProfile.getATR(),
                                                            currProfile.getImplementation()
                                                        );
        profile.setKeys(currProfile.getKeys());
        return profile;
    }
    public CardConfig getCardConfigOf(ProfileComponent profile)
    {
        return profile.convertToCardConfig();
    }
    
}
