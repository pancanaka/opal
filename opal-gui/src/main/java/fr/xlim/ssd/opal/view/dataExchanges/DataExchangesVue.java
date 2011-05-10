
/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : El Khaldi Omar <omar.el-khaldi@etu.unilim.fr>                    *
 *           Chanaa Anas <anas.chanaa@etu.unilim.fr>                          *
 *           Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/


/*
 * This class is used to display The Loggers for the Project in this Vue.
 */


package fr.xlim.ssd.opal.view.dataExchanges;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.model.dataExchanges.DataExchangesModel;
import fr.xlim.ssd.opal.gui.model.dataExchanges.Observer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class DataExchangesVue extends JDialog implements ActionListener{


    //DataExchangesView Attributes//////////////////////////////////////////////////////////////////////////////////////////////////////
    JTabbedPane jtp_main;//Contains the "ALL", "APDU" and the "Logging" JTextPanes
    ALL txt_all;
    APDU txt_apdu;
    Logging txt_logging;
    JPanel btm_pnl;//This Panel is in the bottom of the JDialog
    JButton BtnClear;
    DataExchangesModel mdl_dte;
    StyledDocument doc;
    Style def;
    Style regular;
    Style rouge;

    //DataExchanges Vue Constructor//////////////////////////////////////////////////////////////////////////////////////////////////////
    public DataExchangesVue(){

        //Default DataExchanges Settings
        this.setSize(600,500);
        this.setTitle("Data Exchanges");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        // When this window is closed, the App class must be informed
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                App.dataExchangesVueOpened = false;
            }
        });

        //Instanciating the JTextPanes of this View, and setting their background in white color
        txt_all=new ALL();
        txt_all.setBackground(Color.BLACK);
        txt_apdu=new APDU();
        txt_apdu.setBackground(Color.BLACK);
        txt_logging=new Logging();
        txt_logging.setBackground(Color.BLACK);


        //Defining styles used by each Logging Level:///////////////////////////////////////////
        //  1-Error  (Red, Bold)
        //  2-Warning (Orange, Bold)
        //  3-Info    (Blue, Bold, Italic)
        //  4-Debug   (Green, Bold)

        //Styles for the "ALL" JTextPane
        doc = txt_all.getStyledDocument();
        Style stl_dflt = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            //*Default style
            Style rgl_stl = doc.addStyle("regular", stl_dflt);
            //*Underlined style
            Style under= doc.addStyle("underline", rgl_stl);
            StyleConstants.setUnderline(under, true);
            //*Bold style
            Style bold= doc.addStyle("bold", rgl_stl);
            StyleConstants.setBold(bold, true);
            //*Italic style
            Style italic= doc.addStyle("italic", rgl_stl);
            StyleConstants.setItalic(italic, true);

            //1-Error Level style
            Style error=doc.addStyle("error", bold);
            StyleConstants.setForeground(error, Color.red);
            //2-Warning Level  style
            Style warning=doc.addStyle("warning", bold);
            StyleConstants.setForeground(warning, Color.orange);
            //3-Info Level style
            Style sev1=doc.addStyle("info", bold);
            StyleConstants.setItalic(sev1, true);
            Style info=doc.addStyle("info", sev1);
            StyleConstants.setForeground(info, Color.blue);
            //4-Debug Level style
            Style debug=doc.addStyle("debug", bold);
            StyleConstants.setForeground(debug, Color.green);
            

        //Instanciating the Components of the JDialog
        jtp_main=new JTabbedPane();
        btm_pnl=new JPanel(new BorderLayout());
        BtnClear=new JButton("Clear");

        //This is the instance of the Singleton DataExchangesModel
        mdl_dte=DataExchangesModel.getInstance();

        mdl_dte.addObserver(new Observer(){
            @Override
            public void updateALL(String change_text, String level){

                    try {
                        
                        if(level.equals("ERROR"))           doc.insertString(doc.getLength(), change_text, doc.getStyle("error"));
                        else if(level.equals("WARNING"))    doc.insertString(doc.getLength(), change_text, doc.getStyle("warning"));
                        else if(level.equals("INFO"))       doc.insertString(doc.getLength(), change_text , doc.getStyle("info"));
                        else if(level.equals("DEBUG"))      doc.insertString(doc.getLength(), change_text , doc.getStyle("debug"));

                        //The lines of code below autoscrolls "jtp_main" to the bottom of the JTextPane
                        Document d = txt_all.getDocument();
                        txt_all.select(d.getLength(), d.getLength());


                    }
                    catch (BadLocationException ex) {
                        //don't forget to fill this!!!!!!!!!!!!!!!!!!!
                    }
                
            }
            @Override
            public void updateAPDU(String change_text){
                txt_apdu.setText(txt_apdu.getText().concat(change_text));

                //The lines of code below autoscrolls "jtp_main" to the bottom of the JTextPane
                Document d = txt_apdu.getDocument();
                txt_apdu.select(d.getLength(), d.getLength());
                
            }
            @Override
            public void updateLog(String change_text){
                txt_logging.setText(txt_logging.getText().concat(change_text));

                //The lines of code below autoscrolls "jtp_main" to the bottom of the JTextPane
                Document d = txt_apdu.getDocument();
                txt_apdu.select(d.getLength(), d.getLength());

            }
            @Override
            public void clearALL(String change_text){
                txt_all.setText(change_text);
            }
            @Override
            public void clearAPDU(String change_text){
                txt_apdu.setText(change_text);
            }
            @Override
            public void clearLog(String change_text){
                txt_logging.setText(change_text);
            }
        });

        //Adding listener to the Test Button!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        BtnClear.addActionListener(this);

        //Adding JTextPanes to the JTabbedPane
        jtp_main.add("ALL",txt_all.scr_all);
        jtp_main.add("APDU",txt_apdu.scr_apdu);
        jtp_main.add("Logging",txt_logging.scr_log);

        //Adding Components to the JDialog
        this.add(jtp_main,BorderLayout.CENTER);
        btm_pnl.add(BtnClear,BorderLayout.EAST);
        this.add(btm_pnl,BorderLayout.SOUTH);

    }


    //DataExchanges Methods
    @Override
    public void actionPerformed(ActionEvent ae){

        JButton btn_ae=(JButton)ae.getSource();
        if(btn_ae.equals(BtnClear)){
            
            if(jtp_main.getSelectedIndex() == 0) mdl_dte.clearAll();
            if(jtp_main.getSelectedIndex() == 1) mdl_dte.clearAPDU();
            if(jtp_main.getSelectedIndex() == 2) mdl_dte.clearLogging();

        }

    }


    //this JTextPane is used to displays all of the APDU commands and Logs
    class ALL extends JTextPane{

        JScrollPane scr_all;

        public ALL(){

            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            scr_all=new JScrollPane(this);
            scr_all.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
     
        }

    }

    //This JTextPane is used only to display the APDU commands
    class APDU extends JTextPane{

         JScrollPane scr_apdu;

        public APDU(){

            //Settings of the JTextPane
            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            scr_apdu=new JScrollPane(this);
            scr_apdu.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        }

    }


    //This JTextPane is used only to display the Logs
    class Logging extends JTextPane{

        JScrollPane scr_log;

        public Logging(){

            //Settings of the JTextPane
            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            scr_log=new JScrollPane(this);
            scr_log.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        }

    }

}
