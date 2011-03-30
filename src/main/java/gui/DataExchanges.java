/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * @author omar
 * @author chanaa
 */
public class DataExchanges extends JFrame {

    public String title = "Data Exchanges";
    public JTabbedPane sub_panel = new JTabbedPane();
    public DataExchanges(String s) {
        // setting layout
        super(s);
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
        JPanel pnl_send=new JPanel();
        pnl_send.setLayout(new BorderLayout());
        JButton btn_send= new JButton("Clear");
        pnl_send.add(btn_send,BorderLayout.EAST);

        add(pnl_send,BorderLayout.SOUTH);
        setVisible(true);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


    }

    public class All extends JPanel{
        public String title = "All";

        public All() {
            setLayout(new BorderLayout());
            JLabel All_info = new JLabel("All Informations :");
            add(All_info,BorderLayout.NORTH);
            JTextArea txt_all=new JTextArea();
            add(txt_all,BorderLayout.CENTER);
        }

    }

    public class Apdu extends JPanel{
        public String title = "APDU";

        public Apdu() {
            setLayout(new BorderLayout());
            JLabel Apdu_info = new JLabel("APDU Informations :");
            add(Apdu_info,BorderLayout.NORTH);
            JTextArea txt_apdu=new JTextArea();
            add(txt_apdu,BorderLayout.CENTER);

        }

    }

    public class Logging extends JPanel{
        public String title = "Loggings";

        public Logging() {
            setLayout(new BorderLayout());
            JLabel Log_info = new JLabel("Loggings Informations :");
            add(Log_info,BorderLayout.NORTH);
            JTextArea txt_log=new JTextArea();
            add(txt_log,BorderLayout.CENTER);
        }

    }
    

}
