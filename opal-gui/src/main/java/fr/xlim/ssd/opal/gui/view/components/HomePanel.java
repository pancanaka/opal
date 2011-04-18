package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.components.tab.AppletPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.AuthenticationPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.DeletePanel;
import fr.xlim.ssd.opal.gui.view.components.tab.SelectPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.SendAPDUPanel;
import fr.xlim.ssd.opal.view.dataExcahnges.DataExchangesVue;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Thibault
 * @author razaina
 */
public class HomePanel extends JPanel {
    public HomePanel(MainController controller) {
        AuthenticationPanel p1 = new AuthenticationPanel();
        AppletPanel p2         = new AppletPanel();
        DeletePanel p3         = new DeletePanel();
        SelectPanel p4         = new SelectPanel();
        SendAPDUPanel p5       = new SendAPDUPanel();
        //DataExchangesVue dev = new DataExchangesVue();
        //dev.setVisible(true);
        JTabbedPane myPanel    = new JTabbedPane();

        myPanel.addTab(p1.title, p1);
        myPanel.addTab(p2.title, p2);
        myPanel.addTab(p3.title, p3);
        myPanel.addTab(p4.title, p4);
        myPanel.addTab(p5.title, p5);
        //   myPanel.addTab(p6.title, p6);

        //scrollPane = new JScrollPane(myPanel);
        //myPanel.setSize(800,800);

        Box frame = Box.createVerticalBox();
        frame.add(myPanel, BorderLayout.SOUTH);
        this.add(frame);
    }
}
