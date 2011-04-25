 
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

    //Just for Applet installation process TEST
    private MainController mainController;
    //--------------------------------

    
    public CommunicationController(SecLevel secLevel, MainController mainController)
    {
        this.model = new CommunicationModel(secLevel);

        //Just for Applet installation process TEST
        this.mainController = mainController;
        //---------------------
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
        if(hasDomain())
        {
            if(isAuthenticated())
            {
                try
                {
                    ResponseAPDU resp =  securityDomain.send(command);
                    logger.debug("Response to command "
                        + "(-> " + Conversion.arrayToHex(resp.getBytes()) + ") "
                        + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");
                    return resp;
                }catch(CardException ex){ logger.info(ex.getMessage());}
            }else logger.error("Card isn't authenticated yet");
        }else logger.error("The security domain isn't set yet");
        return null;
    }
    public void deleteApplet(byte[] APPLET_ID, byte[] PACKAGE_ID)
    {
        // Deleting Applet  
        logger.info("Deleting applet");
        try
        {
            securityDomain.deleteOnCardObj(APPLET_ID, false);
        }catch(CardException ex){ logger.error(ex.getMessage());}

        // Deleting package if existed
        logger.info("Deleting package");
        try
        {
            securityDomain.deleteOnCardObj(PACKAGE_ID, false);
        }catch(CardException ex){ logger.error(ex.getMessage());}
        
    }
    public void installApplet(byte[] APPLET_ID, byte[] PACKAGE_ID, String ressource)
    {
        // install Applet 
        try
        {
            logger.info("* Install For Load");
            securityDomain.installForLoad(PACKAGE_ID, null, null);
        }catch(CardException ex){ logger.error(ex.getMessage());}

         InputStream is = ClassLoader.getSystemClassLoader().getClass().getResourceAsStream(ressource);
         byte[] convertedBuffer = CapConverter.convert(is);

         try
         {
            logger.info("* Loading file");
            securityDomain.load(convertedBuffer, (byte) 0x10);
         }catch(CardException ex){ logger.error(ex.getMessage());} 

         try
         {
            logger.info("* Install for install");
            securityDomain.installForInstallAndMakeSelectable(
                        PACKAGE_ID,
                        APPLET_ID,
                        APPLET_ID,
                        Conversion.hexToArray("00"), null);
         }catch(CardException ex){ logger.error(ex.getMessage());}
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
                        
                        //TESTING APPLET INSTALLATION PROCESS
                        mainController.appletController.testAppletInstallationProcess();

                    }
                    catch(CardException ex) { logger.error(ex.getMessage()); }
                }else logger.error("No security domain set yet.");
                model.getSecurityDomainModel().removeSecurityDomainStateListener(this);
            }
        });
    } 
}
