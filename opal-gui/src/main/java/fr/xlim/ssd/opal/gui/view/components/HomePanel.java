/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 *           Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>       *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.*;

import javax.swing.*;
import java.awt.*;

/**
 * The first panel displayed on the application. It shows these different panels :
 * <ul>
 * <li>Authentication;</li>
 * <li>Applet;</li>
 * <li>Delete;</li>
 * <li>Select;</li>
 * <li>Send APDU;</li>
 * </ul>
 *
 * @author Thibault Desmoulins
 * @author Tiana Razafindralambo
 */
public class HomePanel extends JPanel {
    private AuthenticationPanel authentication;
    private AppletPanel applet;
    private DeletePanel delete;
    private SelectPanel select;
    private SendAPDUPanel send;


    /**
     * Constructor
     * Display the <code>JTabbedPane</code> which contains all the panels mentioned in the class description
     *
     * @param controller the main controller of the application
     * @param f          the main view of the application
     * @see fr.xlim.ssd.opal.gui.view.HomeView
     */
    public HomePanel(MainController controller, HomeView f) {
        authentication = new AuthenticationPanel(controller, f, this);
        applet = new AppletPanel();
        delete = new DeletePanel();
        select = new SelectPanel();
        send = new SendAPDUPanel(controller.sendApduController);
        JTabbedPane myPanel = new JTabbedPane();

        myPanel.addTab(authentication.title, authentication);
        myPanel.addTab(applet.title, applet);
        myPanel.addTab(delete.title, delete);
        myPanel.addTab(select.title, select);
        myPanel.addTab(send.title, send);

        //scrollPane = new JScrollPane(myPanel);
        //myPanel.setSize(800,800);

        Box frame = Box.createVerticalBox();
        frame.add(myPanel, BorderLayout.SOUTH);
        this.add(frame);
    }


    /**
     * @return the authentication panel, instance of the <code>AuthenticationPanel</code> class
     */
    public AuthenticationPanel getAuthenticationPanel() {
        return authentication;
    }


    /**
     * @return the applet panel, instance of the <code>AppletPanel</code> class
     */
    public AppletPanel getAppletPanel() {
        return applet;
    }


    /**
     * @return the delete panel, instance of the <code>DeletePanel</code> class
     */
    public DeletePanel getDeletePanel() {
        return delete;
    }


    /**
     * @return the select panel, instance of the <code>SelectPanel</code> class
     */
    public SelectPanel getSelectPanel() {
        return select;
    }


    /**
     * @return the send APDU panel, instance of the <code>SendAPDUPanel</code> class
     */
    public SendAPDUPanel getSendApduPanel() {
        return send;
    }

}
