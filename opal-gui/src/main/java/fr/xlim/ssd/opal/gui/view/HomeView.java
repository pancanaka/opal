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
package fr.xlim.ssd.opal.gui.view;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.components.CardReaderMonitorToolbar;
import fr.xlim.ssd.opal.gui.view.components.HomePanel;
import fr.xlim.ssd.opal.gui.view.profiles.AddUpdateProfileView;
import fr.xlim.ssd.opal.gui.view.profiles.ShowProfileView;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Graphical user interface home view.
 * <p/>
 * Displays the application main view to interact with users.
 *
 * @author David Pequegnot
 * @author Thibault Desmoulins
 */
public class HomeView extends FrameView implements ActionListener {

    private MainController controller;

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
     *
     * @return the controller
     */
    public MainController getController() {
        return this.controller;
    }

    /**
     * Return the main panel of the application
     *
     * @return the instance of HomePanel
     */
    public HomePanel getHomePanel() {
        return homePanel;
    }

    /**
     * Contains all instructions to draw components in the <code>HomeView</code>
     */
    public void drawComponents() {
        initializeMenu();

        initializeToolbar();

        showPanel("home");

        this.getFrame().setSize(500, 500);
        this.getFrame().setLocationRelativeTo(null);
    }

    /**
     * Draw the menu on the window and add listener on every item
     */
    private void initializeMenu() {
        // Menus
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu options = new JMenu("Options");
        JMenu about = new JMenu("About");
        JMenuItem itemQuit = new JMenuItem("Quit");
        JMenuItem itemMProfiles = new JMenuItem("Manage profiles");
        JMenuItem itemDataExchanges = new JMenuItem("Open data exchanges");
        JMenuItem itemAboutOpal = new JMenuItem("Opal");

        menuBar.add(file);
        file.add(itemQuit);
        menuBar.add(options);
        options.add(itemMProfiles);
        options.add(itemDataExchanges);
        menuBar.add(about);
        about.add(itemAboutOpal);

        this.setMenuBar(menuBar);

        // Events
        itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        itemMProfiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
        itemQuit.addActionListener(this);
        itemMProfiles.addActionListener(this);
        itemDataExchanges.addActionListener(this);
        itemAboutOpal.addActionListener(this);
    }

    /**
     * Initialize the application toolbar.
     */
    private void initializeToolbar() {
        this.cardReaderMonitorToolbar = new CardReaderMonitorToolbar(this.controller);
    }


    /**
     * Show the panel (in the <code>AppJScrollPan</code> class) depending on its type
     *
     * @param type the string corresponding of the view to be shown
     */
    public void showPanel(String type) {
        if (type.equals("home")) {
            if (!homePanelIsSet) {
                homePanelIsSet = true;
                homePanel = new HomePanel(this.controller, this);
            }
            scrollPan = new AppJScrollPan(homePanel);
        } else if (type.equals("show profiles")) {
            scrollPan = new AppJScrollPan(new ShowProfileView(this));
        } else if (type.equals("add update")) {
            scrollPan = new AppJScrollPan(new AddUpdateProfileView(this));
        }

        // Set te application toolbar on the vue
        scrollPan.setColumnHeaderView(this.cardReaderMonitorToolbar);

        this.getFrame().setContentPane(scrollPan);
        this.getFrame().setVisible(true);
    }

    /**
     * Show the panel given to the function (in the <code>AppJScrollPan</code> class)
     *
     * @param pan the <code>JPanel</code> to be shown
     */
    public void showPanel(JPanel pan) {
        scrollPan = new AppJScrollPan(pan);
        this.getFrame().setContentPane(scrollPan);

        // Set te application toolbar on the vue
        scrollPan.setColumnHeaderView(this.cardReaderMonitorToolbar);

        this.getFrame().setVisible(true);
    }


    /**
     * Function called when an action is performed on the menu
     *
     * @param ae the action performed
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();

        if (o instanceof JMenuItem) {
            JMenuItem menu = (JMenuItem) o;
            String name = menu.getText();

            /* If we click on the Quit menu */
            if (name.equals("Quit")) {
                int option = JOptionPane.showConfirmDialog(null, "Do you really want to quit ?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
                    System.exit(0);
                }
            } else if (name.equals("Manage profiles")) {
                showPanel("show profiles");
            } else if (name.equals("Open data exchanges")) {
                App.showDataExchangesVue();
            } else if (name.equals("Opal")) {
                String message = ""
                        + "OPAL is a Java 6 library that implements Global Platform 2.x \n"
                        + "specification. It is able to upload and manage applet life cycle \n"
                        + "on Java Card. It is also able to manage different implementations \n"
                        + "of the specification via a pluggable interface.";
                new JOptionPane().showMessageDialog(null, message, "About opal", JOptionPane.OK_OPTION);
            }
        }
    }
}
