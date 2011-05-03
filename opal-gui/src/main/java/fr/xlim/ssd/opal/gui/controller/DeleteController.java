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

import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel; 
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.DeletePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class DeleteController {

    private static final Logger logger = LoggerFactory.getLogger(DeleteController.class);
    private DeletePanel deletePanel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    public DeleteController(HomeView homeView, CardReaderModel cardReaderModel, CommunicationController communication)
    {
        this.deletePanel = homeView.getHomePanel().getDeletePanel();
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
    }
    public void deleteApplet(byte[] PACKAGE_ID, byte[] APPLET_ID)
    {
        logger.info("Deleting Applet");
        this.communication.deleteApplet(PACKAGE_ID, APPLET_ID);
    }
    public void deletePackage(byte[] PACKAGE_ID, byte[] APPLET_ID)
    {
        logger.info("Deleting package");
        this.communication.deletePackage(PACKAGE_ID, APPLET_ID);
    }
    public void fullDelete(byte[] PACKAGE_ID , byte[] APPLET_ID)
    {
        logger.info("Deleting Applet and Package");
        this.communication.fullDelete(PACKAGE_ID, APPLET_ID);
    }
}
