 

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.gui.model.securityDomain.SecurityDomainModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.AppletPanel;  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class AppletController {

    private static final Logger logger = LoggerFactory.getLogger(AppletController.class);
    private AppletPanel appletPanel;
    private CardReaderStateListener cardReaderStateListener;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    
    public AppletController(HomeView homeView, CardReaderModel cardReaderModel, CommunicationController communication)
    {
        this.appletPanel = homeView.getHomePanel().getAppletPanel();
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
    }

    public void installApplet(byte[] PACKAGE_ID, byte[] APPLET_ID, String ressource)
    {
        logger.info("Installing Applet");
        
        SecurityDomainModel securityDomainModel = this.communication.getModel().getSecurityDomainModel(); 

        if(this.communication.hasDomain())
            if(this.communication.isAuthenticated())
                this.communication.installApplet(PACKAGE_ID, APPLET_ID, ressource);
            else logger.error("Card isn't authenticated");
        else logger.error("Security domain isn't set yet"); 
    }
    
    public void testAppletInstallationProcess()
    {
        logger.info("Test Applet Installation Process launched");

        /// applet ID of hello world CAP
        final byte[] APPLET_ID = {
            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x62,
            (byte) 0x03, (byte) 0x01, (byte) 0x0C, (byte) 0x01, (byte) 0x01
        };

        /// package ID of hello world CAP
        final byte[] PACKAGE_ID = {
            (byte) 0xA0, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x62, (byte) 0x03,
            (byte) 0x01, (byte) 0x0C, (byte) 0x01
        };
 
        installApplet(PACKAGE_ID, APPLET_ID, "/cap/HelloWorld-2_1_2.cap"); 
    }
}
