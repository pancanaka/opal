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
import fr.xlim.ssd.opal.gui.view.components.tab.AppletPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.AuthenticationPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.DeletePanel;
import fr.xlim.ssd.opal.gui.view.components.tab.SelectPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.SendAPDUPanel;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
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



    public HomePanel(MainController controller) {
        authentication = new AuthenticationPanel(controller);
        applet         = new AppletPanel();
        delete         = new DeletePanel();
        select         = new SelectPanel();
        send      = new SendAPDUPanel(controller.sendApduController);
        JTabbedPane myPanel    = new JTabbedPane();

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
    public AuthenticationPanel getAuthenticationPanel(){ return authentication;}
    public AppletPanel getAppletPanel(){ return applet;}
    public DeletePanel getDeletePanel(){ return delete;}
    public SelectPanel getSelectPanel(){ return select;}
    public SendAPDUPanel getSendApduPanel(){ return send;}

}
