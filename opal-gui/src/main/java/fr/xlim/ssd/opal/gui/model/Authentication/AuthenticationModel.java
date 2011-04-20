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
 *
 * @author Tiana Razafindralambo
 */
public class AuthenticationModel {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationModel.class);
    private CardReaderModel cardReaderModel;
    private CommunicationModel communication;
    private CardConfig cardConfig;
    
    public AuthenticationModel(){}

    public AuthenticationModel(CardReaderModel crm, CommunicationModel communication)
    {
        this.cardReaderModel = crm;
        this.communication = communication;
    }

    public void setConfiguration()
    {
        this.cardReaderModel.addCardReaderStateListener(new CardReaderStateListener() {
            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) {

                logger.info("Card Name : " +  cardReaderModel.getSelectedCardName());
                logger.info("Card ATR : " + Conversion.arrayToHex(cardReaderModel.getSelectedCardATR().getValue()));
                 //The selected card is completly loaded, then we can use it 
                try
                {
                    cardConfig = getCardConfigByATR(cardReaderModel.getSelectedCardATR());
                    communication.setSecurityDomain(cardConfig, cardReaderModel.getCardChannel());
                }
                catch(CardConfigNotFoundException ex)
                {
                    logger.error(ex.getMessage());
                } 
            }
        });
    }
    public CardConfig getCardConfig()
    {
        return cardConfig;
    }
    private CardConfig getCardConfigByATR(ATR atr) throws CardConfigNotFoundException
    {
        return CardConfigFactory.getCardConfig(atr.getValue());
    }
    public void setCardReaderModel(CardReaderModel crm)
    {
        this.cardReaderModel = crm;
    }

}
