/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>       *
 *           Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tiana Razafindralambo
 * @author Thibault Desmoulins
 */
public class CardXMLManager {
    private CardConfig cardDesired = null;
    private String ISDAID = null;
    private String ScpMode = null;
    private String TransmissionProtocol = null;
    private String implementation = null;

    public CardXMLManager(String cardName) {
        try {
            cardDesired = CardConfigFactory.getCardConfig(cardName);
            ISDAID = Conversion.arrayToHex(cardDesired.getIssuerSecurityDomainAID());
            ScpMode = cardDesired.getScpMode().toString();
            TransmissionProtocol = cardDesired.getTransmissionProtocol();
            implementation = cardDesired.getImplementation().substring(34);
        } catch (CardConfigNotFoundException e) {
            Logger.getLogger("CardXMLManager").log(Level.SEVERE, null, e);
        }
    }

    public String getISDAID() {
        return ISDAID;
    }

    public String getScpMode() {
        return ScpMode;
    }

    public String getTransmissionProtocol() {
        return TransmissionProtocol;
    }

    public String getImplementation() {
        return implementation;
    }
}