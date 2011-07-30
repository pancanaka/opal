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
 * This class is used to display The Loggers for the Project in this View.
 */


package fr.xlim.ssd.opal.view.dataExchanges;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.model.dataExchanges.DataExchangesModel;
import fr.xlim.ssd.opal.gui.model.dataExchanges.Observer;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DataExchangesVue extends JDialog implements ActionListener {


    //Attributes
    JTabbedPane jtp_main;//Contains the "ALL", "APDU" and the "Logging" JTextPanes
    ALL txt_all;
    APDU txt_apdu;
    Logging txt_logging;
    JPanel btm_pnl;//This Panel is in the bottom of the JDialog
    JButton BtnClear;//This button clears the JTextPane selected
    DataExchangesModel mdl_dte;

    //Styles for the JTextPanes
    StyledDocument doc_all;
    StyledDocument doc_log;
    StyledDocument doc_apdu;


    //DataExchanges Vue Constructor
    public DataExchangesVue() {

        //Default DataExchanges Settings
        this.setSize(600, 500);
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
        txt_all = new ALL();
        txt_all.setBackground(Color.WHITE);
        txt_apdu = new APDU();
        txt_apdu.setBackground(Color.WHITE);
        txt_logging = new Logging();
        txt_logging.setBackground(Color.WHITE);


        //Defining styles used by each Logging Level:///////////////////////////////////////////
        //  1-Error  (Red, Bold)
        //  2-Warning (Orange, Bold)
        //  3-Info    (Blue, Bold, Italic)
        //  4-Debug   (Green, Bold)

        //  5-Defining another style used only by the LE for the APDU Sendings

        //Styles for the JTextPanes
        doc_all = txt_all.getStyledDocument();
        doc_log = txt_logging.getStyledDocument();
        doc_apdu = txt_apdu.getStyledDocument();

        Style stl_dflt = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        //*Regular style
        Style rgl_stl1 = doc_all.addStyle("regular", stl_dflt);
        Style rgl_stl2 = doc_log.addStyle("regular", stl_dflt);
        Style rgl_stl3 = doc_apdu.addStyle("regular", stl_dflt);
        //*Underlined style
        Style under1 = doc_all.addStyle("underline", rgl_stl1);
        Style under2 = doc_log.addStyle("underline", rgl_stl2);
        Style under3 = doc_apdu.addStyle("underline", rgl_stl3);
        StyleConstants.setUnderline(under1, true);
        StyleConstants.setUnderline(under2, true);
        StyleConstants.setUnderline(under3, true);
        //*Bold style
        Style bold1 = doc_all.addStyle("bold", rgl_stl1);
        Style bold2 = doc_log.addStyle("bold", rgl_stl2);
        Style bold3 = doc_apdu.addStyle("bold", rgl_stl3);
        StyleConstants.setBold(bold1, true);
        StyleConstants.setBold(bold2, true);
        StyleConstants.setBold(bold3, true);
        //*Italic style
        Style italic1 = doc_all.addStyle("italic", rgl_stl1);
        Style italic2 = doc_log.addStyle("italic", rgl_stl2);
        Style italic3 = doc_apdu.addStyle("italic", rgl_stl3);
        StyleConstants.setItalic(italic1, true);
        StyleConstants.setItalic(italic2, true);
        StyleConstants.setItalic(italic3, true);

        //1-Error Level style
        Style error1 = doc_all.addStyle("error", bold1);
        Style error2 = doc_log.addStyle("error", bold2);
        Style error3 = doc_apdu.addStyle("error", bold3);
        StyleConstants.setForeground(error1, Color.red);
        StyleConstants.setForeground(error2, Color.red);
        StyleConstants.setForeground(error3, Color.red);
        //2-Warning Level  style
        Style warning1 = doc_all.addStyle("warning", bold1);
        Style warning2 = doc_log.addStyle("warning", bold2);
        Style warning3 = doc_apdu.addStyle("warning", bold3);
        StyleConstants.setForeground(warning1, Color.orange);
        StyleConstants.setForeground(warning2, Color.orange);
        StyleConstants.setForeground(warning3, Color.orange);
        //3-Info Level style
        Style sev1 = doc_all.addStyle("info", bold1);
        Style sev2 = doc_log.addStyle("info", bold2);
        Style sev3 = doc_apdu.addStyle("info", bold3);
        StyleConstants.setItalic(sev1, true);
        StyleConstants.setItalic(sev2, true);
        StyleConstants.setItalic(sev3, true);
        Style info1 = doc_all.addStyle("info", sev1);
        Style info2 = doc_log.addStyle("info", sev2);
        Style info3 = doc_apdu.addStyle("info", sev3);
        StyleConstants.setForeground(info1, Color.blue);
        StyleConstants.setForeground(info2, Color.blue);
        StyleConstants.setForeground(info3, Color.blue);
        //4-Debug Level style
        Style debug1 = doc_all.addStyle("debug", bold1);
        Style debug2 = doc_log.addStyle("debug", bold2);
        Style debug3 = doc_apdu.addStyle("debug", bold3);
        StyleConstants.setForeground(debug1, Color.green);
        StyleConstants.setForeground(debug2, Color.green);
        StyleConstants.setForeground(debug3, Color.green);

        //LE style
        Style le_style = doc_apdu.addStyle("le_style", bold3);
        StyleConstants.setForeground(le_style, Color.MAGENTA);


        //Instanciating the Components of the JDialog
        jtp_main = new JTabbedPane();
        btm_pnl = new JPanel(new BorderLayout());
        BtnClear = new JButton("Clear");

        //This is the instance of the DataExchangesModel Singleton
        mdl_dte = DataExchangesModel.getInstance();

        //Adding an observer to the DataExchangesModel
        mdl_dte.addObserver(new Observer() {
            @Override

            //Update the ALL JTextPane
            public void updateALL(String change_text, String level) {

                try {

                    //Display a log for each level
                    if (level.equals("ERROR"))
                        doc_all.insertString(doc_all.getLength(), change_text, doc_all.getStyle("error"));
                    else if (level.equals("WARNING"))
                        doc_all.insertString(doc_all.getLength(), change_text, doc_all.getStyle("warning"));
                    else if (level.equals("INFO"))
                        doc_all.insertString(doc_all.getLength(), change_text, doc_all.getStyle("info"));
                    else if (level.equals("DEBUG"))
                        doc_all.insertString(doc_all.getLength(), change_text, doc_all.getStyle("debug"));

                    //The lines of code below autoscrolls "jtp_main" to the bottom of the JTextPane

                    Document d = txt_all.getDocument();
                    txt_all.select(d.getLength(), d.getLength());

                } catch (BadLocationException ex) {
                    System.out.println(ex.getMessage());
                }

            }


            @Override
            public void updateAPDU(String head, String req, String params, String le, String data, String response, String res, String level) {

                try {

                    //Title
                    doc_apdu.insertString(doc_apdu.getLength(), head, doc_apdu.getStyle("bold"));
                    //Request
                    doc_apdu.insertString(doc_apdu.getLength(), req, doc_apdu.getStyle("regular"));
                    //Parameters
                    doc_apdu.insertString(doc_apdu.getLength(), params, doc_apdu.getStyle("info"));
                    //LE
                    doc_apdu.insertString(doc_apdu.getLength(), le, doc_apdu.getStyle("le_style"));
                    //Data
                    doc_apdu.insertString(doc_apdu.getLength(), data, doc_apdu.getStyle("warning"));
                    //Response
                    doc_apdu.insertString(doc_apdu.getLength(), response, doc_apdu.getStyle("regular"));
                    //Response status
                    if (level.equals("ERROR"))
                        doc_apdu.insertString(doc_apdu.getLength(), res, doc_apdu.getStyle("error"));
                    else doc_apdu.insertString(doc_apdu.getLength(), res, doc_apdu.getStyle("debug"));

                    //The lines of code below autoscrolls "jtp_main" to the bottom of the JTextPane

                    Document d = txt_apdu.getDocument();
                    txt_apdu.select(d.getLength(), d.getLength());

                } catch (BadLocationException ex) {
                    System.out.println(ex.getMessage());
                }


            }


            @Override
            public void updateLog(String change_text, String level) {

                try {

                    //Display a log for each level
                    if (level.equals("ERROR"))
                        doc_log.insertString(doc_log.getLength(), change_text, doc_log.getStyle("error"));
                    else if (level.equals("WARNING"))
                        doc_log.insertString(doc_log.getLength(), change_text, doc_log.getStyle("warning"));
                    else if (level.equals("INFO"))
                        doc_log.insertString(doc_log.getLength(), change_text, doc_log.getStyle("info"));

                    //The lines of code below autoscrolls "jtp_main" to the bottom of the JTextPane

                    Document doc = txt_logging.getDocument();
                    txt_logging.select(doc.getLength(), doc.getLength());

                } catch (BadLocationException ex) {
                    System.out.println(ex.getMessage());
                }

            }

        });


        //Adding JTextPanes to the JTabbedPane
        jtp_main.add("APDU", txt_apdu.scr_apdu);
        jtp_main.add("ALL", txt_all.scr_all);
        jtp_main.add("Logging", txt_logging.scr_log);

        //Adding Components to the JDialog
        this.add(jtp_main, BorderLayout.CENTER);
        btm_pnl.add(BtnClear, BorderLayout.EAST);
        BtnClear.addActionListener(this);
        this.add(btm_pnl, BorderLayout.SOUTH);
        this.setVisible(true);
    }


    //DataExchanges Methods
    @Override
    public void actionPerformed(ActionEvent ae) {

        Object o = ae.getSource();
        if (o instanceof JButton) {

            if (o.equals(BtnClear)) {

                if (jtp_main.getSelectedIndex() == 1) txt_all.setText("");//Clears the ALL JTextPane
                if (jtp_main.getSelectedIndex() == 0) txt_apdu.setText("");//Clears the APDU JTextPane
                if (jtp_main.getSelectedIndex() == 2) txt_logging.setText("");//Clears the Logging JTextPane

            }

        }

    }


    //this JTextPane is used to displays all of the Logs
    class ALL extends JTextPane {


        JScrollPane scr_all;

        public ALL() {

            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            scr_all = new JScrollPane(this);
            scr_all.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        }


    }

    //This JTextPane is used only to display the APDU commands
    class APDU extends JTextPane {

        JScrollPane scr_apdu;

        public APDU() {


            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            scr_apdu = new JScrollPane(this);
            scr_apdu.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        }

    }


    //This JTextPane is used display Logs which level is INFO, WARN ou ERROR
    class Logging extends JTextPane {

        JScrollPane scr_log;

        public Logging() {


            this.setEditable(false);//The text is used only for displays, so there's ne need to write on it

            //Instanciating the components of this JTextPane
            scr_log = new JScrollPane(this);
            scr_log.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        }

    }

}
