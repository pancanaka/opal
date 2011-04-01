package fr.xlim.ssd.opal.gui.view;

import fr.xlim.ssd.opal.gui.controller.MainController;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

/**
 * Graphical user interface home view.
 * <p/>
 * Displays the application main view to interact with users.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public class HomeView extends FrameView {

    /**
     * Constructor
     *
     * @param application the <code>Application</code> instance which provides methods to manage the application life
     *                    cycle
     * @param controller  the application main controller
     */
    public HomeView(Application application, MainController controller) {
        super(application);

        this.controller = controller;
    }

    /**
     * Contains all instructions to draw components in the "Home view".
     */
    public void drawComponents() {

    }

    private MainController controller;
}
