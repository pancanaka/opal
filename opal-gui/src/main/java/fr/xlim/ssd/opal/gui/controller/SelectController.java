 
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.SelectPanel;

/**
 *
 * @author razaina
 */
public class SelectController {

    private SelectPanel selectPanel;
    public SelectController(HomeView homeView)
    {
        this.selectPanel = homeView.getHomePanel().getSelectPanel();
    }
}
