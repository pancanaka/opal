package fr.xlim.ssd.opal.gui;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.tools.SmartCardListParser;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.view.dataExchanges.DataExchangesVue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

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
        DataExchangesVue dev = new DataExchangesVue();
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
