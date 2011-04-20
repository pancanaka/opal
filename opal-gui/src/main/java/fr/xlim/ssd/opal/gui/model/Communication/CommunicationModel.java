/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.model.Communication;

import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;
import java.util.logging.Level;
import org.slf4j.Logger;
import javax.smartcardio.CardChannel;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class CommunicationModel {

    private static final Logger logger = LoggerFactory.getLogger(CommunicationModel.class);
    private SecurityDomain securityDomain;
    private SecLevel securityLevel;

    public CommunicationModel(){}
    
    public CommunicationModel(SecLevel securityLevel)
    {
        this.securityLevel = securityLevel;
    }
    public SecurityDomain getSecurityDomain()
    {
        return this.securityDomain;
    }
    public void setSecurityDomain(CardConfig cardConfig, CardChannel channel)
    {
        //  select the security domain 
        try
        {
            this.securityDomain = new SecurityDomain(   cardConfig.getImplementation(),
                                                        channel,
                                                        cardConfig.getIssuerSecurityDomainAID()
                                                     );
        }
        catch(CommandsImplementationNotFound ex)
        {
            logger.error("Commands Implementation not found");
        }
        catch(ClassNotFoundException ex)
        {
            logger.error("Class not found exception");
        }

        this.securityDomain.setOffCardKeys(cardConfig.getSCKeys());
        
        try {
            logger.info("APDU Response to selection : " + securityDomain.select().toString());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(CommunicationModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setSecurityLevel(SecLevel securityLevel)
    {
        this.securityLevel = securityLevel;
    }
}
