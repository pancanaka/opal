package fr.xlim.ssd.opal.gui;

import fr.xlim.ssd.opal.gui.controller.MainController;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * Application life cycle management.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public class App extends SingleFrameApplication {
    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        this.mainController = new MainController(this);

        this.mainController.startTerminalTask();

        show(this.mainController.getHomeView());
    }

    /**
     * Function called when the application is shutting down.
     */
    @Override
    protected void shutdown() {
        this.mainController.stopTerminalTask();
        
        super.shutdown();
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of App
     */
    public static App getApplication() {
        return Application.getInstance(App.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }

    private MainController mainController;
}
