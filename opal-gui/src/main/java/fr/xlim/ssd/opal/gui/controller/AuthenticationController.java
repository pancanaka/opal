/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.Authentication.AuthenticationModel;
import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel; 
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.HomePanel;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.gui.view.components.tab.AppletPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.AuthenticationPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.DeletePanel;
import fr.xlim.ssd.opal.gui.view.components.tab.SelectPanel;
import fr.xlim.ssd.opal.library.params.CardConfig; 
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import javax.smartcardio.ResponseAPDU;
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
    private CommunicationController communication;
    private ProfileController profileController; 
    private AuthenticationPanel authenticationPanel; 
    private CardReaderStateListener cardReaderStateListener;

    public AuthenticationController(CardReaderModel cardReaderModel, CommunicationController communication, ProfileController profileController, HomeView homeView)
    {
        this.cardReaderModel        = cardReaderModel;
        this.communication          = communication;
        this.profileController      = profileController;
        this.authenticationPanel    = homeView.getHomePanel().getAuthenticationPanel();  
        this.authModel              = new AuthenticationModel(this.cardReaderModel, this.communication.getModel(), this.profileController);
    }

    public void authenticateCard(CardConfig cardConfig)
    { 
        //this.authModel.setCommunication(cardConfig);
        final CardConfig _cf = cardConfig;

        logger.info("Waiting for card reader state change event");
        cardReaderStateListener = new CardReaderStateListener() {
            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) {

                if(cardReaderModel.hasSelectedCardReaderItem())
                {
                   communication.authenticate(_cf);
                   communication.getModel().setSecurityDomain(_cf, cardReaderModel.getCardChannel());
               
                }else logger.error("No card found.");
                cardReaderModel.removeCardReaderStateListener(cardReaderStateListener);
            }
        };
        this.cardReaderModel.addCardReaderStateListener(cardReaderStateListener);
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
    public void test()
    {
        logger.info("Test launched");
        cardReaderStateListener = new CardReaderStateListener() {
            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
                if(cardReaderModel.hasSelectedCardReaderItem())
                {
                    
                    logger.info("Card Name : " +  cardReaderModel.getSelectedCardName());
                    logger.info("Card ATR : " + Conversion.arrayToHex(cardReaderModel.getSelectedCardATR().getValue()));

                    try
                    {
                        CardConfig cardConfig = null;
                        cardConfig = authModel.getCardConfigByATR(cardReaderModel.getSelectedCardATR());
                        authenticateCard(cardConfig);
                    }
                    catch(CardConfigNotFoundException ex) { logger.error(ex.getMessage()); }
                }else logger.info("No card found");
            }
        };
       this.cardReaderModel.addCardReaderStateListener(cardReaderStateListener);
    }
}
