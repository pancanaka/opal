package fr.xlim.ssd.opal.gui.view;

import fr.xlim.ssd.opal.gui.view.components.HomePanel;
import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.components.CardReaderMonitorToolbar;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import fr.xlim.ssd.opal.gui.view.components.CardReaderMonitorToolbar;
import fr.xlim.ssd.opal.gui.view.profiles.AddUpdateProfileView;
import fr.xlim.ssd.opal.gui.view.profiles.ShowProfileView;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

/**
 * Graphical user interface home view.
 *
 * Displays the application main view to interact with users.
 *
 * @author David Pequegnot
 * @author Thibault Desmoulins
 */
public class HomeView extends FrameView implements ActionListener {

    private MainController           controller;
    
    private CardReaderMonitorToolbar cardReaderMonitorToolbar;

    private JScrollPane scrollPan;
    

    /**
     * Constructor
     *
     * @param application the <code>Application</code> instance which provides methods to manage the application life
     *                    cycle
     * @param controller  the application main controller
     */
    public HomeView(Application application, MainController controller) {
        super(application);//input control on Send Apdu tab (suite)

        this.controller = controller;

        drawComponents();
    }

    /**
     * Contains all instructions to draw components in the "Home view".
     */
    public void drawComponents() {

        initializeMenu();

        initializeToolbar();

        scrollPan = new JScrollPane(new HomePanel(this.controller));

        this.getFrame().setContentPane(scrollPan);

        this.getFrame().setSize(500, 500);
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
            file.add(itemQuit);
        menuBar.add(options);
            options.add(itemMProfiles);
        menuBar.add(about);
       
        this.setMenuBar(menuBar);

        // Events
        itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        itemMProfiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
        itemQuit.addActionListener(this);
        itemMProfiles.addActionListener(this);
    }

    /**
     * Initialize the application toolbar.
     */
    private void initializeToolbar() {
        this.cardReaderMonitorToolbar = new CardReaderMonitorToolbar(this.controller);

        this.setToolBar(this.cardReaderMonitorToolbar);
    }


    public void showPanel(String type) {
        if(type.equals("home")) {
            scrollPan = new JScrollPane(new HomePanel(this.controller));
        }
        else if(type.equals("show profiles")) {
            scrollPan = new JScrollPane(new ShowProfileView(this));
        }
        else if(type.equals("add update")) {
            scrollPan = new JScrollPane(new AddUpdateProfileView(this));
        }
        this.getFrame().setContentPane(scrollPan);
        this.getFrame().setVisible(true);
    }

    public void showPanel(JPanel pan) {
        scrollPan = new JScrollPane(pan);
        this.getFrame().setContentPane(scrollPan);
        this.getFrame().setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();

        if(o instanceof JMenuItem) {
            JMenuItem menu = (JMenuItem) o;
            String name    = menu.getText();

            /* If we click on the Quit menu */
            if(name.equals("Quit")) {
                int option = JOptionPane.showConfirmDialog(null, "Do you really want to quit ?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
                    System.exit(0);
                }
            }
            else if(name.equals("Manage profiles")) {
                showPanel("show profiles");
            }
        }
    }
}
