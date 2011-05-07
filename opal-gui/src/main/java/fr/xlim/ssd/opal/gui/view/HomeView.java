/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : David Pequegnot <david.pequegnot@etu.unilim.fr>                  *
 *           Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.view.components.HomePanel;
import fr.xlim.ssd.opal.gui.controller.MainController;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import fr.xlim.ssd.opal.gui.view.components.CardReaderMonitorToolbar;
import fr.xlim.ssd.opal.gui.view.profiles.AddUpdateProfileView;
import fr.xlim.ssd.opal.gui.view.profiles.ShowProfileView;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

    private AppJScrollPan scrollPan;

    private HomePanel homePanel;
    
    private boolean homePanelIsSet = false;
    

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

    public MainController getController() {
        return this.controller;
    }

    /**
     * Contains all instructions to draw components in the "Home view".
     */
    public void drawComponents() {
        initializeMenu();

        initializeToolbar();

        this.getFrame().setSize(500, 500);

        showPanel("home");
    }

    /**
     * Draw the menu on the window
     */
    private void initializeMenu() {
        // Menus
        JMenuBar  menuBar           = new JMenuBar();
        JMenu     file              = new JMenu("File");
        JMenu     options           = new JMenu("Options");
        JMenu     about             = new JMenu("About");
        JMenuItem itemQuit          = new JMenuItem("Quit");
        JMenuItem itemMProfiles     = new JMenuItem("Manage profiles");
        JMenuItem itemDataExchanges = new JMenuItem("Open data exchanges");

        menuBar.add(file);
            file.add(itemQuit);
        menuBar.add(options);
            options.add(itemMProfiles);
            options.add(itemDataExchanges);
        menuBar.add(about);
       
        this.setMenuBar(menuBar);

        // Events
        itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        itemMProfiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
        itemQuit.addActionListener(this);
        itemMProfiles.addActionListener(this);
        itemDataExchanges.addActionListener(this);
    }

    /**
     * Initialize the application toolbar.
     */
    private void initializeToolbar() {
        this.cardReaderMonitorToolbar = new CardReaderMonitorToolbar(this.controller);
    }


    public void showPanel(String type) { 
        if(type.equals("home")) {
            if(!homePanelIsSet) {
                homePanelIsSet = true;
                homePanel = new HomePanel(this.controller);
            }
            scrollPan = new AppJScrollPan(homePanel);
        }
        else if(type.equals("show profiles")) {
            scrollPan = new AppJScrollPan(new ShowProfileView(this));
        }
        else if(type.equals("add update")) {
            scrollPan = new AppJScrollPan(new AddUpdateProfileView(this));
        }

        // Set te application toolbar on the vue
        scrollPan.setColumnHeaderView(this.cardReaderMonitorToolbar);

        this.getFrame().setContentPane(scrollPan);
        this.getFrame().setVisible(true);
    }

    public void showPanel(JPanel pan) { 
        scrollPan = new AppJScrollPan(pan);
        this.getFrame().setContentPane(scrollPan);

        // Set te application toolbar on the vue
        scrollPan.setColumnHeaderView(this.cardReaderMonitorToolbar);
        
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
            else if(name.equals("Open data exchanges")) {
                App.showDataExchangesVue();
            }
        }
    }

    public HomePanel getHomePanel() {
        return homePanel;
    }
}
