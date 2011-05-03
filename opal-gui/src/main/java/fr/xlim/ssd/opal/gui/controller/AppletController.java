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

import fr.xlim.ssd.opal.gui.communication.task.AppletInstallationTask;
import fr.xlim.ssd.opal.gui.communication.task.TaskFactory;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener; 
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

    public void installApplet(byte[] PACKAGE_ID, byte[] APPLET_ID, String ressource, byte[] securityDomainAID, byte[] params, boolean capConverter, byte maxDataLength, byte[] privileges)
    {
        logger.info("Installing Applet");
        AppletInstallationTask appletInstallationTask = new AppletInstallationTask(PACKAGE_ID, APPLET_ID, ressource, securityDomainAID, params, privileges, communication);
        TaskFactory taskFactory = TaskFactory.run(appletInstallationTask);
        //this.communication.installApplet(PACKAGE_ID, APPLET_ID, ressource);
    } 
   /* public void testAppletInstallationProcess()
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

        final byte[] HELLO_WORLD = { // "HELLO"
            (byte) 'H', (byte) 'E', (byte) 'L', (byte) 'L', (byte) 'O'
        };
        installApplet(PACKAGE_ID, APPLET_ID, "/cap/HelloWorld-2_1_2.cap");
        selectApplet(APPLET_ID);
        useApplet(HELLO_WORLD);
        fullDelete(PACKAGE_ID, APPLET_ID); 
    }*/
}
