/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author chanaa
 */
public class DataExchanges extends JPanel {

    public String title = "Data Exchanges";
    public JTabbedPane sub_panel = new JTabbedPane();
    public DataExchanges() {
        // setting layout
        setLayout(new BorderLayout());
        //
        // tabs initialisation
        All All= new All();
        Apdu Apdu = new Apdu();
        Logging Logging = new Logging();

        //adding tabs to panel
        sub_panel.addTab(All.title, All);
        sub_panel.addTab(Apdu.title, Apdu);
        sub_panel.addTab(Logging.title, Logging);
        add(sub_panel,BorderLayout.CENTER);




    }

    public class All extends JPanel{
        public String title = "All";
    }

    public class Apdu extends JPanel{
        public String title = "APDU";
    }

    public class Logging extends JPanel{
        public String title = "Loggings";
    }
    

}
