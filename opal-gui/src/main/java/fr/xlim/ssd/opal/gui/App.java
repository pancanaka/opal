/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.gui;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.dataExchanges.DataExchangesView;
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