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

import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.SelectPanel;

/**
 *
 * @author Tiana Razafindralambo
 */
public class SelectController {

    private static final CustomLogger logger= new CustomLogger();
    private SelectPanel selectPanel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    public SelectController(HomeView homeView, CardReaderModel cardRederModel, CommunicationController communication)
    {
        this.cardReaderModel = cardReaderModel;
        this.selectPanel = homeView.getHomePanel().getSelectPanel();
        this.communication = communication;
    }
    public void selectApplet(byte[] APPLET_ID)
    {
        logger.info("Selecting applet");
        this.communication.selectApplet(APPLET_ID);
    }
}
