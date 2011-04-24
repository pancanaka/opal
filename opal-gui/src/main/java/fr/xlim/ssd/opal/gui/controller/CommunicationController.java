/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateListener;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.params.CardConfig;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author razaina
 */
public class CommunicationController {

    private static final Logger logger = LoggerFactory.getLogger(CommunicationController.class);
    private CommunicationModel model;
    private SecurityDomain securityDomain;
    private SecurityDomainStateListener securityDomainStateListener;
    private SecurityDomainStateListener securityDomainStageChangedEvent;
    
    public CommunicationController(SecLevel secLevel)
    {
        this.model = new CommunicationModel(secLevel); 
    }
    public CommunicationModel getModel()
    {
        return this.model;
    }
    public void authenticate(CardConfig _cardConfig)
    {
        logger.info("Initialize Update");
        final CardConfig cardConfig = _cardConfig;
        securityDomainStateListener = new SecurityDomainStateListener() {
            @Override
            public void securityDomainStateChanged(SecurityDomainStateChangedEvent event) {
                ResponseAPDU response = null;
                securityDomain = model.getSecurityDomain();
                if(model.getSecurityDomainModel().hasDomain())
                { 
                    try
                    {
                        response = securityDomain.initializeUpdate( cardConfig.getDefaultInitUpdateP1(),
                                                                    cardConfig.getDefaultInitUpdateP2(),
                                                                    cardConfig.getScpMode()
                                                                  );
                        logger.debug("Initialize update APDU response : " + response);

                        response = securityDomain.externalAuthenticate(model.getSecurityLevel());
                        logger.debug("External Authenticate APDU response : " + response);
                    }
                    catch(CardException ex) { logger.error(ex.getMessage()); }
                }else logger.error("No security domain set yet.");
                model.getSecurityDomainModel().removeSecurityDomainStateListener(securityDomainStateListener);
            }
        }; 
        this.model.getSecurityDomainModel().addSecurityDomainStateListener(securityDomainStateListener);   
    } 
}
