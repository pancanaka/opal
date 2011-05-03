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

import fr.xlim.ssd.opal.gui.model.securityDomain.SecurityDomainModel;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.params.CardConfig; 
import org.slf4j.Logger;
import javax.smartcardio.CardChannel;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class CommunicationModel {

    private static final Logger logger = LoggerFactory.getLogger(CommunicationModel.class);
    private SecurityDomainModel securityModel; 
    private SecLevel securityLevel;

    public CommunicationModel(){ this.securityModel = new SecurityDomainModel(); }
    
    public CommunicationModel(SecLevel securityLevel)
    {
        this.securityModel = new SecurityDomainModel();
        this.securityLevel = securityLevel;
    }
    public SecurityDomain getSecurityDomain() { return this.securityModel.getDomain(); }
    public SecLevel getSecurityLevel(){ return this.securityLevel;}
    public SecurityDomainModel getSecurityDomainModel() { return this.securityModel;}
    public void setSecurityDomain(CardConfig cardConfig, CardChannel channel)
    { 
        //  select the security domain
        this.securityModel.setSecurityDomain(cardConfig, channel); 
    }

    public void setSecurityLevel(SecLevel securityLevel)
    {
        this.securityLevel = securityLevel;
    }
}
