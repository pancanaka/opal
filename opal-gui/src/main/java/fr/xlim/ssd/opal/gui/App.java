/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : David Pequegnot <david.pequegnot@etu.unilim.fr>                   *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.dataExchanges.DataExchangesView;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/*
 * Application life cycle management.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public class App extends SingleFrameApplication {

    public static boolean dataExchangesVueOpened = false;

    public static App instance = null;

    private MainController mainController;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        try {
            this.mainController = new MainController(this);
        } catch (CardConfigNotFoundException e) {
            throw new IllegalStateException("cannot found card config",e);
        }

        this.instance = this;

        this.mainController.startTerminalTask();

        show(this.mainController.getHomeView());

        showDataExchangesVue();
    }

    /**
     * Open the Data Exchanges window (only if it is not already opened)
     */
    public static void showDataExchangesVue() {
        // If there is no DataExchangesView opened at this moment
        if (!App.dataExchangesVueOpened) {
            App.dataExchangesVueOpened = true;
            new DataExchangesView();
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
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}