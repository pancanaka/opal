/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/
 
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel; 
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateListener;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.utilities.CapConverter;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.InputStream;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 *
 * @author razaina
 */
public class CommunicationController {
    
    private static final CustomLogger logger= new CustomLogger();
    private CommunicationModel model;
    private SecurityDomain securityDomain;
    private SecurityDomainStateListener securityDomainStateListener;
    private SecurityDomainStateListener securityDomainStageChangedEvent;

    public CommunicationController(){ this.model = new CommunicationModel();}
    public CommunicationController(SecLevel secLevel)
    {
        this.model = new CommunicationModel(secLevel); 
    }
    public void setSecurityLevel(SecLevel secLevel)
    {
        logger.info("Setting security level to " + secLevel.toString() + " and transmitting to model");
        this.model.setSecurityLevel(secLevel);
    }
    public CommunicationModel getModel()
    {
        return this.model;
    }
    public boolean hasDomain()
    {
        return this.model.getSecurityDomainModel().hasDomain();
    }
    public boolean isAuthenticated()
    {
        return this.model.getSecurityDomainModel().isAuthenticated();
    }
    public boolean canCommunicate()
    {
        if(this.hasDomain())
        {
            if(this.isAuthenticated())
            {
                return true;
            }else
            {
                logger.error("Card isn't authenticated yet");
                return false;
            }
        }
        logger.error("Security domain isn't set yet");
        return false;
    }
    public void useApplet(byte[] DATA)
    {
        // Using Applet
        CommandAPDU TODO = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , DATA // DATA
        );
        ResponseAPDU resp = send(TODO);
    }
    public void selectApplet(byte[] APPLET_ID)
    {
        CommandAPDU select = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0xA4 // INS
                , (byte) 0x04 // P1
                , (byte) 0x00 // P2
                , APPLET_ID   // DATA
        );
        logger.info("Selecting Applet (AID = "+Conversion.arrayToHex(select.getBytes())+")");
        ResponseAPDU resp = send(select);  
    }
    public ResponseAPDU send(CommandAPDU command)
    {
        logger.info("Sending an APDU command");
        if(this.canCommunicate())
        {
            try
            {
                ResponseAPDU resp =  securityDomain.send(command);
                logger.debug("Response to command "
                     + "(-> " + Conversion.arrayToHex(resp.getBytes()) + ") "
                     + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");
                return resp;
            }catch(CardException ex){ logger.info(ex.getMessage());}
        }
        return null;
    }
    public void deleteApplet(byte[] APPLET_ID)
    {
        // Deleting Applet  
        logger.info("Deleting applet");
        try
        {
            ResponseAPDU resp = securityDomain.deleteOnCardObj(APPLET_ID, false);
        }catch(CardException ex){ logger.error(ex.getMessage());}
    } 
    public void deletePackage(byte[] PACKAGE_ID)
    {
        // Deleting package if existed
        logger.info("Deleting package");
        try
        {
            ResponseAPDU resp = securityDomain.deleteOnCardObj(PACKAGE_ID, false);
        }catch(CardException ex){ logger.error(ex.getMessage());}
    }
    public void fullDelete(byte[] PACKAGE_ID , byte[] APPLET_ID)
    { 
        deleteApplet(APPLET_ID);
        deletePackage(PACKAGE_ID);
    }
    private void install4install(byte[] PACKAGE_ID, byte[] APPLET_ID, byte[] privileges, byte[] params)
    {
        try
         {
            logger.info("* Install for install"); 
            ResponseAPDU resp = securityDomain.installForInstallAndMakeSelectable(
                        PACKAGE_ID,
                        APPLET_ID,
                        APPLET_ID,
                        privileges, params);
         }catch(CardException ex)
         {
             logger.error(ex.getMessage());
         }
    }
    private void install4load(byte[] PACKAGE_ID, byte[] APPLET_ID, byte[] securityDomainAID, byte[] params)
    {
        try
        {
            logger.info("* Install For Load");
            ResponseAPDU resp = securityDomain.installForLoad(PACKAGE_ID, securityDomainAID, params);
        }catch(CardException ex)
        {
            logger.error(ex.getMessage());
            logger.info("Deleting previous applet install and package install");
            fullDelete(PACKAGE_ID, APPLET_ID); 
            install4load(PACKAGE_ID, APPLET_ID, securityDomainAID, params);
        }
    }
     
    public void installApplet(byte[] PACKAGE_ID, byte[] APPLET_ID, String ressource, byte[] securityDomainAID, byte[] params4Install4load , byte maxDataLength, byte[] privileges, byte[] paramsInstall4Install)
    {
        if(this.canCommunicate())
        {
             install4load(PACKAGE_ID, APPLET_ID, securityDomainAID, params4Install4load);

             InputStream is = ClassLoader.getSystemClassLoader().getClass().getResourceAsStream(ressource);
             byte[] convertedBuffer = CapConverter.convert(is);

             try
             {
                logger.info("* Loading file");
                ResponseAPDU[] resp = securityDomain.load(convertedBuffer, maxDataLength);
             }catch(CardException ex)
             {
                 logger.error(ex.getMessage());
             }

             install4install(PACKAGE_ID, APPLET_ID, privileges, paramsInstall4Install);
        }
    }
    public void authenticate(CardConfig _cardConfig)
    {
        logger.info("Initialize Update"); 
        final CardConfig cardConfig = _cardConfig; 
        this.model.getSecurityDomainModel().addSecurityDomainStateListener( new SecurityDomainStateListener() {
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

                        model.getSecurityDomainModel().isAuthenticated(true); 
                        logger.warn(Conversion.arrayToHex(cardConfig.getIssuerSecurityDomainAID())+"__");
                    }
                    catch(CardException ex) { logger.error(ex.getMessage()); }
                }else logger.error("No security domain set yet.");
                model.getSecurityDomainModel().removeSecurityDomainStateListener(this);
            }
        });
    } 
}
