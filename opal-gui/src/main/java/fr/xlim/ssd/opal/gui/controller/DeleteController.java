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

import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel; 
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.DeletePanel;

/**
 * Main controller of the Delete (panel) view
 * 
 * @author Tiana Razafindralambo
 * @author Estelle Blandinières
 */
public class DeleteController {

    private static final CustomLogger logger= new CustomLogger();
    private DeletePanel deletePanel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    public DeleteController(HomeView homeView, CardReaderModel cardReaderModel, CommunicationController communication)
    {
        this.deletePanel = homeView.getHomePanel().getDeletePanel();
        this.deletePanel.setController(this);
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
    }
    
    /**
     * Call the delete command
     * 
     * @author Tiana Razafindralambo
     * 
     * @param AID
     * @param cascade 
     */
    public void delete(byte[] AID, boolean cascade)
    {
        logger.info("Deleting Applet");
        this.communication.delete(AID, cascade);
    } 
}
