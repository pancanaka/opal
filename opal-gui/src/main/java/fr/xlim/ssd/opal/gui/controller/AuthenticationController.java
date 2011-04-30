
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.communication.task.AppletInstallationTask;
import fr.xlim.ssd.opal.gui.communication.task.AuthenticationTask;
import fr.xlim.ssd.opal.gui.communication.task.TaskFactory;
import fr.xlim.ssd.opal.gui.model.Authentication.AuthenticationModel;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel; 
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.gui.view.components.tab.AuthenticationPanel;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.params.CardConfig; 
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
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
    private CommunicationController communication;
    private ProfileController profileController; 
    private AuthenticationPanel authenticationPanel;  
    private boolean defaultCardConfigIsSet = false;
    
    public AuthenticationController(CardReaderModel cardReaderModel, CommunicationController communication, ProfileController profileController, HomeView homeView)
    {
        this.cardReaderModel        = cardReaderModel;
        this.communication          = communication;
        this.profileController      = profileController;
        this.authenticationPanel    = homeView.getHomePanel().getAuthenticationPanel();  
        this.authenticationPanel.setController(this);
        this.authModel              = new AuthenticationModel(this.cardReaderModel, this.communication.getModel(), this.profileController);
        if(!defaultCardConfigIsSet)
            this.setDefaultCardConfig();
    }
    private void setDefaultCardConfig()
    {
        this.cardReaderModel.addCardReaderStateListener( new CardReaderStateListener() {
            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
                if(cardReaderModel.hasSelectedCardReaderItem())
                {
                    logger.info("Default Card selected Name : " +  cardReaderModel.getSelectedCardName());
                    logger.info("Default Card selected ATR : " + Conversion.arrayToHex(cardReaderModel.getSelectedCardATR().getValue()));

                    try
                    {
                        CardConfig cardConfig = null;
                        cardConfig = authModel.getCardConfigByATR(cardReaderModel.getSelectedCardATR()); 
                        authModel.setDefaultCardConfig(cardConfig);
                    }
                    catch(CardConfigNotFoundException ex) { logger.error(ex.getMessage()); }
                }else logger.info("No card found");
                cardReaderModel.removeCardReaderStateListener(this);
            }
        });
    }
    public void authenticateCard(CardConfig cardConfig, SecLevel secLevel)
    {
        AuthenticationTask authenticationTask = new AuthenticationTask(cardConfig, this.cardReaderModel, this.communication, secLevel); 
        TaskFactory taskFactory = TaskFactory.run(authenticationTask);
        
        //this.authModel.setCommunication(cardConfig);
        /*final CardConfig _cf = cardConfig;

        logger.info("Authenticating card...");
        this.cardReaderModel.addCardReaderStateListener(new CardReaderStateListener() {
            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) { 
                if(cardReaderModel.hasSelectedCardReaderItem())
                {
                   communication.authenticate(_cf);
                   communication.getModel().setSecurityDomain(_cf, cardReaderModel.getCardChannel());
               
                }else logger.error("No card found.");
                cardReaderModel.removeCardReaderStateListener(this);
            }
        });*/
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
        return this.authModel.getDefaultCardConfig().getName();
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

    /*public void testAuthenticationProcessAndAppletInstallationThreadMode(final AppletController _appletController)
    {
        logger.info("Test Authentication Process launched");
        /// applet ID of hello world CAP

        this.cardReaderModel.addCardReaderStateListener( new CardReaderStateListener() {
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
                        //appletController.installApplet(PACKAGE_ID, APPLET_ID, ressource);
                    }
                    catch(CardConfigNotFoundException ex) { logger.error(ex.getMessage()); }
                }else logger.info("No card found");
                cardReaderModel.removeCardReaderStateListener(this);
            }
        });
    }*/
    /*public void testAuthenticationProcess()
    {
        logger.info("Test Authentication Process launched");
        this.cardReaderModel.addCardReaderStateListener( new CardReaderStateListener() {
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
                cardReaderModel.removeCardReaderStateListener(this);
            }
        });
    } */
}
