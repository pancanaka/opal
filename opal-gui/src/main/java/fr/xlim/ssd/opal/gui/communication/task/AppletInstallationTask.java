/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.communication.task;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.controller.CommunicationController;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import org.jdesktop.application.Task;

/**
 *
 * @author Tiana Razafindralambo
 */
public class AppletInstallationTask extends Task<Void, Void> implements TaskInterface{
    private static final CustomLogger logger= new CustomLogger();
    private CommunicationController communication = null;
    byte[] PACKAGE_ID = null;
    byte[] APPLET_ID = null;
    byte[] securityDomainAID = null;
    byte[] params = null;
    byte[] privileges = null;
    String ressource = "";
    
    public AppletInstallationTask(byte[] PACKAGE_ID, byte[] APPLET_ID, String ressource, byte[] securityDomainAID, byte[] params, byte[] privileges, CommunicationController communication)
    {
        super(App.instance);
        this.communication = communication;
        this.PACKAGE_ID = PACKAGE_ID;
        this.APPLET_ID = APPLET_ID;
        this.ressource = ressource;
    }
    @Override
    protected Void doInBackground(){
        logger.info("Applet installation task...");
        this.communication.installApplet(this.PACKAGE_ID, this.APPLET_ID, this.ressource, this.securityDomainAID, this.params, this.privileges);
        return null;
    }

    @Override
    protected void succeeded(Void nothing)
    {
        logger.info("Applet installation task thread end");
    }

    @Override
    public void doThen(Task task) {
    }

    @Override
    public void nextTask(Task task) { 
    } 
}
