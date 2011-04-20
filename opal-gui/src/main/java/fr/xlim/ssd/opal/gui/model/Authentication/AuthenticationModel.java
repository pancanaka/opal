/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.model.Authentication;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
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
    
    public AuthenticationModel(){}

    public AuthenticationModel(CardReaderModel crm, CommunicationModel communication)
    {
        this.cardReaderModel = crm;
        this.communication = communication;
    }

    /**
     * Set the card configuration.
     *
     * We need to use a CardReaderStateListener here, in order
     * to catch the event dispatched by the CardReaderModel
     *
     * Note that the securityDomain is set at the same time when the card
     * configuration has been found.
     */
    public void setConfiguration()
    {
        cardReaderStateListener = new CardReaderStateListener() {
            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) {

                logger.info("Card Name : " +  cardReaderModel.getSelectedCardName());
                logger.info("Card ATR : " + Conversion.arrayToHex(cardReaderModel.getSelectedCardATR().getValue()));
                
                 //The selected card is completly loaded, then we can use it
                //getting the card configuration and set the security domain
                try
                {
                    cardConfig = getCardConfigByATR(cardReaderModel.getSelectedCardATR());
                    communication.setSecurityDomain(cardConfig, cardReaderModel.getCardChannel());
                    cardReaderModel.removeCardReaderStateListener(cardReaderStateListener);
                }
                catch(CardConfigNotFoundException ex)
                {
                    logger.error(ex.getMessage());
                } 
            }
        };

       this.cardReaderModel.addCardReaderStateListener(cardReaderStateListener);
    }

    /**
     * Get the card configuration
     *
     * It can be null.
     * 
     * @return CardConfig
     */
    public CardConfig getCardConfig()
    {
        return cardConfig;
    }

    /**
     * Get the card configuration using its ATR's value
     * 
     * @param atr
     * @return CardConfig
     * @throws CardConfigNotFoundException
     */
    private CardConfig getCardConfigByATR(ATR atr) throws CardConfigNotFoundException
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
