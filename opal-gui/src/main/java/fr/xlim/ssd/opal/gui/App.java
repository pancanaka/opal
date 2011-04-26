package fr.xlim.ssd.opal.gui;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.view.dataExchanges.DataExchangesVue;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import java.io.UnsupportedEncodingException;

/**
 * Application life cycle management.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public class App extends SingleFrameApplication {
    public static short nbDataExchangesVueOpened = 0;
    public static App instance = null;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        this.mainController = new MainController(this);
        
        this.instance = this;
        
        this.mainController.startTerminalTask();

        show(this.mainController.getHomeView());

        showDataExchangesVue();
    }

    /**
     * Open the Data Exchanges window (only if it is not already opened)
     */
    public static void showDataExchangesVue() {
        // If there is no DataExchangesVue opened at this moment
        if(App.nbDataExchangesVueOpened==0) {
            App.nbDataExchangesVueOpened++;
            new DataExchangesVue();
        }
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
    public static void main(String[] args) throws UnsupportedEncodingException {
        Application.launch(App.class, args);
    }

    private MainController mainController;
}
