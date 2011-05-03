/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.model.Authentication;

import fr.xlim.ssd.opal.gui.controller.ProfileController;
import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.ProfileModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The model containing informations needed to authenticate the card
 *
 * The security domain used to communicate with the card is set using the
 * communication model.
 * 
 * @author Tiana Razafindralambo
 */
public class AuthenticationModel {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationModel.class);
    private CardReaderModel cardReaderModel;
    private CommunicationModel communication;
    private CardConfig cardConfig;
    private CardReaderStateListener cardReaderStateListener;
    private ProfileModel profile;
    private String[][] profiles; 
    
    public AuthenticationModel(){}

    public AuthenticationModel(CardReaderModel crm, CommunicationModel communication, ProfileController profileController)
    {
        this.cardReaderModel = crm;
        this.communication = communication;
        
        this.profile = profileController.getProfileModel();
        loadAllProfile(); 
    } 
 
    /**
     *  Load all profiles (cf. config.xml)
     */
    private void loadAllProfile()
    {
        logger.info("Getting all profiles ...");

        this.profiles = this.profile.getAllProfiles();
        int profilesLength = this.profiles.length;

        logger.info(profilesLength + " profiles has been loaded.");

        /*for(int i = 0; i < profilesLength; i++)
        {
            logger.debug(profiles[i][0]);
        }*/
    }

    public String[] getAllProfileNames()
    {
        String[] res = new String[this.profiles.length];

        for(int i = 0; i < this.profiles.length; i++)
            res[i] = this.profiles[i][0];
        return res;
    }
    /**
     * Get all profiles
     * @return all profiles loaded.
     */
    public String[][] getAllProfiles()
    {
        return this.profiles;
    }

    public ProfileComponent getProfileByName(String name)
    {
        return this.profile.getProfileByName(name);
    }
    /**
     * Get the card configuration
     *
     * It can be null.
     * 
     * @return CardConfig
     */
    public CardConfig getDefaultCardConfig()
    {  
        return this.cardConfig;
    }
    public void setDefaultCardConfig(CardConfig cardConfig)
    {
        this.cardConfig = cardConfig;
    }

    /**
     * Get the card configuration using its ATR's value
     * 
     * @param atr
     * @return CardConfig
     * @throws CardConfigNotFoundException
     */
    public CardConfig getCardConfigByATR(ATR atr) throws CardConfigNotFoundException
    {
        return CardConfigFactory.getCardConfig(atr.getValue());
    }

    /**
     * Set the card reader model
     * 
     * @param crm
     */
    public void setCardReaderModel(CardReaderModel crm)
    {
        this.cardReaderModel = crm;
    }
}
