/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.model.Communication;

import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.securityDomain.SecurityDomainModel;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.params.CardConfig;

import javax.smartcardio.CardChannel;

/**
 * Communication Model
 *
 * @author Tiana Razafindralambo
 */
public class CommunicationModel {

    private static final CustomLogger logger = new CustomLogger();
    private SecurityDomainModel securityModel;
    private SecLevel securityLevel;

    public CommunicationModel() {
        this.securityModel = new SecurityDomainModel();
    }

    public CommunicationModel(SecLevel securityLevel) {
        this.securityModel = new SecurityDomainModel();
        this.securityLevel = securityLevel;
    }

    public SecurityDomain getSecurityDomain() {
        return this.securityModel.getDomain();
    }

    public SecLevel getSecurityLevel() {
        return this.securityLevel;
    }

    public SecurityDomainModel getSecurityDomainModel() {
        return this.securityModel;
    }

    /**
     * Security domain setter
     * affects the security domain model
     *
     * @param cardConfig
     * @param channel
     */
    public void setSecurityDomain(CardConfig cardConfig, CardChannel channel) {
        //  select the security domain
        this.securityModel.setSecurityDomain(cardConfig, channel);
    }

    /**
     * Security level setter
     *
     * @param securityLevel
     */
    public void setSecurityLevel(SecLevel securityLevel) {
        this.securityLevel = securityLevel;
    }
}
