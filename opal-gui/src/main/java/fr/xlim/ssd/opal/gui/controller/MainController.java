package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.view.HomeView;
import org.jdesktop.application.Application;

/**
 * Application main controller.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public class MainController {

    /**
     * Constructor.
     *
     * The constructor will:
     * <ul>
     *     <li>create the main view.</li>
     * </ul>
     *
     * @param application the application context
     * @see HomeView
     */
    public MainController(Application application) {
        this.application = application;
        
        this.homeView = new HomeView(application, this);
    }

    /**
     * Get the home view.
     *
     * @return the home view
     */
    public HomeView getHomeView() {
        return this.homeView;
    }

    /**
     * Start the terminal task.
     *
     * This <code>Task</code> instance will monitor terminals connected to the computer.
     * It updates the terminal list every time when a new terminal is plugged or removed.
     */
    public void startTerminalTask() {
        // TODO Needs the startTerminalTask implementation
    }

    /**
     * Stop the terminal task.
     *
     * It is the way to stop the thread which listen for plugged terminals.
     */
    public void stopTerminalTask() {
        // TODO Needs the stopTerminalTask implementation
    }

    private Application application;
    
    private HomeView homeView;
}
