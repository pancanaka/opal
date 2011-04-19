/*
 * This class is used to display The Logs and APDUs exchanges.
 */


package fr.xlim.ssd.opal.view.dataExchanges;

import fr.xlim.ssd.opal.gui.model.dataExchanges.DataExchangesModel;
import fr.xlim.ssd.opal.gui.model.dataExchanges.Observer;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author EL KHALDI Omar
 * @author CHANAA Anas
 */
public class DataExchangesVue extends JDialog implements ActionListener{

    JTabbedPane jtp_main;
    ALL txt_all;
    APDU txt_apdu;
    Logging txt_logging;
    JPanel test_pnl;
    JButton test_APDU,Test_Logging;///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    JLabel test_lbl;///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    DataExchangesModel mdl_dte;

    public DataExchangesVue(){

        //Default JDialog Settings
        this.setSize(450,500);
        this.setTitle("Data Exchanges");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        //Instanciating the Components of the JDialog
        jtp_main=new JTabbedPane();
        test_pnl=new JPanel(new BorderLayout());
        test_APDU=new JButton("APDU");
        Test_Logging=new JButton("Logging");
        test_lbl=new JLabel("These buttons are used only for tests: ");

        mdl_dte=new DataExchangesModel();
        mdl_dte.addObserver(new Observer(){
        	public void updateALL(String change_text){

        		txt_all.setText(txt_all.getText().concat(change_text));
        	}
        	public void updateAPDU(String change_text){

                txt_apdu.setText(txt_apdu.getText().concat(change_text));
        	}
        	public void updateLog(String change_text){

                txt_logging.setText(txt_logging.getText().concat(change_text));
        	}
        	public void clearALL(String change_text){

        		txt_all.setText(change_text);
        	}
        	public void clearAPDU(String change_text){

        		txt_apdu.setText(change_text);
        	}
        	public void clearLog(String change_text){

                txt_logging.setText(change_text);
        	}

        });

        //Instanciating the components of the JTextPane
        txt_all=new ALL();
        txt_apdu=new APDU();
        txt_logging=new Logging();

        //Adding listener to the Test Button!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        test_APDU.addActionListener(this);
        Test_Logging.addActionListener(this);
        txt_all.btn_clrall.addActionListener(this);
        txt_apdu.btn_clrapdu.addActionListener(this);
        txt_logging.btn_clrlog.addActionListener(this);

        //Adding JTextPanes to the JTabbedPane
        jtp_main.add("ALL",txt_all.scr_all);
        jtp_main.add("APDU",txt_apdu.scr_apdu);
        jtp_main.add("Logging",txt_logging.scr_log);

        //Adding Components to the JDialog
        this.add(jtp_main,BorderLayout.CENTER);
        test_pnl.add(test_APDU,BorderLayout.EAST);
        test_pnl.add(Test_Logging,BorderLayout.CENTER);
        test_pnl.add(test_lbl,BorderLayout.WEST);
        this.add(test_pnl,BorderLayout.SOUTH);

    }

    public void actionPerformed(ActionEvent ae){

        JButton btn_ae=(JButton)ae.getSource();
        if(btn_ae.equals(test_APDU)){
        	mdl_dte.displayAllAPDUSendings();
        	mdl_dte.displayApduSending();
        }

        if(btn_ae.equals(Test_Logging)){
        	mdl_dte.displayAllLogSendings();
        	mdl_dte.displayLogSending();
        }

        if(btn_ae.equals(txt_all.btn_clrall))	mdl_dte.clearAll();
        if(btn_ae.equals(txt_apdu.btn_clrapdu))	mdl_dte.clearAPDU();
        if(btn_ae.equals(txt_logging.btn_clrlog))	mdl_dte.clearLogging();

    }


    //this JTextPane is used to displays all of the APDU commands and the Logs
    class ALL extends JTextPane{

        JButton btn_clrall;
        JPanel pnl_bas;
        JScrollPane scr_all;

        public ALL(){

            //Settings of the JTextPane
            this.setLayout(new BorderLayout());
            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            btn_clrall=new JButton("Clear");
            scr_all=new JScrollPane(this);
            scr_all.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            pnl_bas=new JPanel(new BorderLayout());

            //Adding the components in this JTextPane
            this.add(pnl_bas,BorderLayout.SOUTH);
            pnl_bas.add(btn_clrall,BorderLayout.EAST);

        }

    }

    //This JTextPane is used only to display the APDU commands
    class APDU extends JTextPane{

        JButton btn_clrapdu;
        JPanel pnl_bas;
        JScrollPane scr_apdu;

        public APDU(){

            //Settings of the JTextPane
            this.setLayout(new BorderLayout());
            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            btn_clrapdu=new JButton("Clear");
            pnl_bas=new JPanel(new BorderLayout());
            scr_apdu=new JScrollPane(this);
            scr_apdu.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            //Adding the components in this JTextPane
            this.add(pnl_bas,BorderLayout.SOUTH);
            pnl_bas.add(btn_clrapdu,BorderLayout.EAST);

        }

    }


    //This JTextPane is used only to display the Logs
    class Logging extends JTextPane{

        JButton btn_clrlog;
        JPanel pnl_bas;
        JScrollPane scr_log;

        public Logging(){

            //Settings of the JTextPane
            this.setLayout(new BorderLayout());
            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            btn_clrlog=new JButton("Clear");
            pnl_bas=new JPanel(new BorderLayout());
            scr_log=new JScrollPane(this);
            scr_log.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            //Adding the components in this JTextPane
            this.add(pnl_bas,BorderLayout.SOUTH);
            pnl_bas.add(btn_clrlog,BorderLayout.EAST);

        }

    }

    //provisional Main used for tests!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


}
