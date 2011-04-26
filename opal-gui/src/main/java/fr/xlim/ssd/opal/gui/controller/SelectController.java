 
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.SelectPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class SelectController {

    private static final Logger logger = LoggerFactory.getLogger(SelectController.class);
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
