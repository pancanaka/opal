 
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel; 
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

    public CommunicationController(){ this.model = new CommunicationModel();}
    public CommunicationController(SecLevel secLevel)
    {
        this.model = new CommunicationModel(secLevel); 
    }
    public void setSecurityLevel(SecLevel secLevel)
    {
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
    public void deleteApplet(byte[] PACKAGE_ID , byte[] APPLET_ID)
    {
        // Deleting Applet  
        logger.info("Deleting applet");
        try
        {
            securityDomain.deleteOnCardObj(APPLET_ID, false);
        }catch(CardException ex){ logger.error(ex.getMessage());}
    } 
    public void deletePackage(byte[] PACKAGE_ID, byte[] APPLET_ID)
    {
        // Deleting package if existed
        logger.info("Deleting package");
        try
        {
            securityDomain.deleteOnCardObj(PACKAGE_ID, false);
        }catch(CardException ex){ logger.error(ex.getMessage());}
    }
    public void fullDelete(byte[] PACKAGE_ID , byte[] APPLET_ID)
    { 
        deleteApplet(PACKAGE_ID, APPLET_ID);
        deletePackage(PACKAGE_ID, APPLET_ID);
    }
    private void install4install(byte[] PACKAGE_ID, byte[] APPLET_ID)
    {
        try
         {
            logger.info("* Install for install");
            securityDomain.installForInstallAndMakeSelectable(
                        PACKAGE_ID,
                        APPLET_ID,
                        APPLET_ID,
                        Conversion.hexToArray("00"), null);
         }catch(CardException ex)
         {
             logger.error(ex.getMessage());
         }
    }
    private void install4load(byte[] PACKAGE_ID, byte[] APPLET_ID)
    {
        try
        {
            logger.info("* Install For Load");
            securityDomain.installForLoad(PACKAGE_ID, null, null);
        }catch(CardException ex)
        {
            logger.error(ex.getMessage());
            logger.info("Deleting previous applet install and package install");
            deleteApplet(PACKAGE_ID, APPLET_ID);
            deletePackage(PACKAGE_ID, APPLET_ID);
            install4load(PACKAGE_ID, APPLET_ID);
        }
    }
    public void installApplet(byte[] PACKAGE_ID, byte[] APPLET_ID, String ressource)
    {
        if(this.canCommunicate())
        {
             install4load(PACKAGE_ID, APPLET_ID);

             InputStream is = ClassLoader.getSystemClassLoader().getClass().getResourceAsStream(ressource);
             byte[] convertedBuffer = CapConverter.convert(is);

             try
             {
                logger.info("* Loading file");
                securityDomain.load(convertedBuffer, (byte) 0x10);
             }catch(CardException ex)
             {
                 logger.error(ex.getMessage());
             }

             install4install(PACKAGE_ID, APPLET_ID);
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
                    }
                    catch(CardException ex) { logger.error(ex.getMessage()); }
                }else logger.error("No security domain set yet.");
                model.getSecurityDomainModel().removeSecurityDomainStateListener(this);
            }
        });
    } 
}
