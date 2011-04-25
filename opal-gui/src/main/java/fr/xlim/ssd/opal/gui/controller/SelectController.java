 
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.SelectPanel;

/**
 *
 * @author razaina
 */
public class SelectController {

    private SelectPanel selectPanel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    public SelectController(HomeView homeView, CardReaderModel cardRederModel, CommunicationController communication)
    {
        this.cardReaderModel = cardReaderModel;
        this.selectPanel = homeView.getHomePanel().getSelectPanel();
        this.communication = communication;
    }
}
