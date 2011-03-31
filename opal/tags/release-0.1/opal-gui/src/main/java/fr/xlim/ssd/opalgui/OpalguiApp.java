/*
 * OpalguiApp.java
 */

package fr.xlim.ssd.opalgui;

import fr.xlim.ssd.opalgui.io.terminal.TerminalConnection;
import fr.xlim.ssd.opalgui.view.OpalguiView;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class OpalguiApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new OpalguiView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of OpalguiApp
     */
    public static OpalguiApp getApplication() {
        return Application.getInstance(OpalguiApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(OpalguiApp.class, args);
    }
}
