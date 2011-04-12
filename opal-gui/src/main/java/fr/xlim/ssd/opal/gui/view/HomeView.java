package fr.xlim.ssd.opal.gui.view;

import fr.xlim.ssd.opal.gui.view.components.HomePanel;
import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.components.CardReaderMonitorToolbar;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import fr.xlim.ssd.opal.gui.view.components.CardReaderMonitorToolbar;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

/**
 * Graphical user interface home view.
 *
 * Displays the application main view to interact with users.
 *
 * @author David Pequegnot
 */
public class HomeView extends FrameView {

    private MainController controller;
    private JToolBar       terminalToolBar;
    private HomePanel      homePanel;
    private CardReaderMonitorToolbar cardReaderMonitorToolbar;
    

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

        drawComponents();
    }

    /**
     * Contains all instructions to draw components in the "Home view".
     */
    public void drawComponents() {
        initializeMenu();
        initializeToolbar();

        homePanel = new HomePanel(this.controller);
        setComponent(this.homePanel);
    }

    /**
     * Draw the menu on the window
     */
    private void initializeMenu() {
        // Menus
        JMenuBar  menuBar       = new JMenuBar();
        JMenu     file          = new JMenu("File");
        JMenu     options       = new JMenu("Options");
        JMenu     about         = new JMenu("About");
        JMenuItem itemQuit      = new JMenuItem("Quit");
        JMenuItem itemMProfiles = new JMenuItem("Manage profiles");
        
        menuBar.add(file);
            file.add(itemMProfiles);
            file.add(itemQuit);
        menuBar.add(options);
        menuBar.add(about);
       
        this.setMenuBar(menuBar);

        // Events
        itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        itemMProfiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
        //itemQuit.addActionListener(this);
        //itemMProfiles.addActionListener(this);
    }

    /**
     * Initialize the application toolbar.
     */
    private void initializeToolbar() {
        this.cardReaderMonitorToolbar = new CardReaderMonitorToolbar(this.controller);

        this.setToolBar(this.cardReaderMonitorToolbar);
    }
}
