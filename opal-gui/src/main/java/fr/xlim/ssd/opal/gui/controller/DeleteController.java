 
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.DeletePanel;

/**
 *
 * @author razaina
 */
public class DeleteController {

    private DeletePanel deletePanel;
    public DeleteController(HomeView homeView)
    {
        this.deletePanel = homeView.getHomePanel().getDeletePanel();
    }
}
