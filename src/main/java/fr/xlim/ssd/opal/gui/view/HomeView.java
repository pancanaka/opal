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
import fr.xlim.ssd.opal.gui.view.components.custom.AIDJTextField;
import fr.xlim.ssd.opal.gui.view.components.HomePanel;
import fr.xlim.ssd.opal.gui.controller.MainController;

import java.awt.event.ActionEvent;
import javax.swing.*;

import fr.xlim.ssd.opal.gui.view.components.menubar.AppMenuBar;
import fr.xlim.ssd.opal.gui.view.components.toolbar.CardReaderMonitorToolbar;

import java.awt.event.ActionListener;

import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

/**
 * Graphical user interface home view.
 *
 * Displays the application main view to interact with users.
 *
 * @author David Pequegnot
 * @author Thibault Desmoulins
 */
public class HomeView extends FrameView implements ActionListener {

    private MainController controller;
    private JPanel mainPanel;
    private ToolWindowManager mainToolWindowManager;

    private CardReaderMonitorToolbar cardReaderMonitorToolbar;

    private AppJScrollPan scrollPan;

    private HomePanel homePanel;
    
    private boolean homePanelIsSet = false;


    /**
     * Constructor
     *
     * @param application the <code>Application</code> instance which provides methods to manage the application life cycle
     * @param controller  the application main controller
     */
    public HomeView(Application application, MainController controller) {
        super(application);//input control on Send Apdu tab (suite)

        this.controller = controller;

        drawComponents();
    }

    /**
     * Return the main controller of the application
     * @return the controller
     */
    public MainController getController() {
        return this.controller;
    }

    /**
     * Return the main panel of the application
     * @return the instance of HomePanel
     */
    public HomePanel getHomePanel() {
        return homePanel;
    }

    /**
     * Contains all instructions to draw components in the <code>HomeView</code>
     */
    public void drawComponents() {
        this.setMenuBar(new AppMenuBar());
        this.setToolBar(new CardReaderMonitorToolbar(this.controller));

        this.drawTabs();
        this.drawLogging();
    }

    private void drawTabs() {
        MyDoggyToolWindowManager tabsMyDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.mainToolWindowManager = tabsMyDoggyToolWindowManager;
        ContentManager tabsContentManager = this.mainToolWindowManager.getContentManager();

        tabsContentManager.addContent("Authentication", "Authentication", null, new AIDJTextField(11, AIDJTextField.HEXADECIMAL_MODE), "Authentication");
        tabsContentManager.addContent("Load Applet", "Load Applet", null, new JButton("Load Applet"), "Load Applet");
        tabsContentManager.addContent("Select", "Select", null, new JButton("Select"), "Select");
        tabsContentManager.addContent("Delete", "Delete", null, new JButton("Delete"), "Delete");
        tabsContentManager.addContent("Send APDU Command", "Send APDU Command", null, new JButton("Send APDU Command"), "Send APDU Command");

        this.setComponent(tabsMyDoggyToolWindowManager);
    }

    private void drawLogging() {
        this.mainToolWindowManager.registerToolWindow("Logging", "Logging", null, new JButton("Logging"), ToolWindowAnchor.BOTTOM);

        for (ToolWindow window : this.mainToolWindowManager.getToolWindows())
            window.setAvailable(true);
    }


    /**
     * Show the panel (in the <code>AppJScrollPan</code> class) depending on its type
     * @param type the string corresponding of the view to be shown
     */
    public void showPanel(String type) {
        /*if(type.equals("home")) {
            if(!homePanelIsSet) {
                homePanelIsSet = true;
                homePanel = new HomePanel(this.controller, this);
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
        //scrollPan.setColumnHeaderView(this.cardReaderMonitorToolbar);

        this.getFrame().setContentPane(scrollPan);
        this.getFrame().setVisible(true);*/
    }

    /**
     * Show the panel given to the function (in the <code>AppJScrollPan</code> class)
     * @param pan the <code>JPanel</code> to be shown
     */
    public void showPanel(JPanel pan) { 
        /*scrollPan = new AppJScrollPan(pan);
        this.getFrame().setContentPane(scrollPan);

        // Set te application toolbar on the vue
        scrollPan.setColumnHeaderView(this.cardReaderMonitorToolbar);
        
        this.getFrame().setVisible(true);*/
    }


    /**
     * Function called when an action is performed on the menu
     * @param ae the action performed
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();

        if(o instanceof JMenuItem) {
            JMenuItem menu = (JMenuItem) o;
            String name    = menu.getText();

            if(name.equals("Manage profiles")) {
                showPanel("show profiles");
            }
            else if(name.equals("Open data exchanges")) {
                App.showDataExchangesVue();
            }
        }
    }
}
