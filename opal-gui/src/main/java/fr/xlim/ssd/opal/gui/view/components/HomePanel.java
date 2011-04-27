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
 * @author Thibault
 * @author Tiana Razafindralambo
 */

public class HomePanel extends JPanel {
    private AuthenticationPanel authentication;
    private AppletPanel applet;
    private DeletePanel delete;
    private SelectPanel select;

    public HomePanel(MainController controller) {
        authentication = new AuthenticationPanel();
        applet         = new AppletPanel();
        delete         = new DeletePanel();
        select         = new SelectPanel();
        SendAPDUPanel p5       = new SendAPDUPanel();
        //DataExchangesVue dev = new DataExchangesVue();
        //dev.setVisible(true);
        JTabbedPane myPanel    = new JTabbedPane();

        myPanel.addTab(authentication.title, authentication);
        myPanel.addTab(applet.title, applet);
        myPanel.addTab(delete.title, delete);
        myPanel.addTab(select.title, select);
        myPanel.addTab(p5.title, p5);
        //   myPanel.addTab(p6.title, p6);

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

}
