 

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.AppletPanel;

/**
 *
 * @author razaina
 */
public class AppletController {

    private AppletPanel appletPanel;
    public AppletController(HomeView homeView)
    {
        this.appletPanel = homeView.getHomePanel().getAppletPanel();
    }
}
