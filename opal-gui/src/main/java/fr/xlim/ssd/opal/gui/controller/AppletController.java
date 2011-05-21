/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 *          Estelle Blandinières  <estelle.blandinieres@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.communication.task.AppletInstallationTask;
import fr.xlim.ssd.opal.gui.communication.task.TaskFactory;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener; 
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.AppletPanel;


/**
 * Main controller for the applet installation view
 * @author Tiana Razafindralambo
 * @author Estelle Blandinières
 */
public class AppletController {

    
    private static final CustomLogger logger= new CustomLogger();
    private AppletPanel appletPanel;
    private CardReaderStateListener cardReaderStateListener;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    
    /**
     * Default constructor
     * 
     * @author Tiana Razafindralambo
     * 
     * @param homeView main view
     * @param cardReaderModel
     * @param communication controller that allows to communicate with the card
     */
    public AppletController(HomeView homeView, CardReaderModel cardReaderModel, CommunicationController communication)
    {
        this.appletPanel = homeView.getHomePanel().getAppletPanel();
        this.appletPanel.setController(this);
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
    }
                    
    /**
     * Launch the applet installation task in a thread
     * 
     * @author Tiana Razafindralambo
     * 
     * @param PACKAGE_ID
     * @param MODULE_AID
     * @param APPLET_ID
     * @param ressource
     * @param securityDomainAID
     * @param params4Install4load
     * @param maxDataLength
     * @param privileges
     * @param paramsInstall4Install
     * @param reorderCapFileComponents 
     */
    public void installApplet(byte[] PACKAGE_ID, byte[] MODULE_AID, byte[] APPLET_ID, String ressource, byte[] securityDomainAID, byte[] params4Install4load, byte maxDataLength, byte[] privileges, byte[] paramsInstall4Install, boolean reorderCapFileComponents)
    {
        logger.info("Installing Applet");                                                                   
        AppletInstallationTask appletInstallationTask = new AppletInstallationTask(PACKAGE_ID, MODULE_AID, APPLET_ID, ressource, securityDomainAID, params4Install4load, maxDataLength, privileges, paramsInstall4Install, this.communication, reorderCapFileComponents);
        TaskFactory taskFactory = TaskFactory.run(appletInstallationTask); 
    }  
}
