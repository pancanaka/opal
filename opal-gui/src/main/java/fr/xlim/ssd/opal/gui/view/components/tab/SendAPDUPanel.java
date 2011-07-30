/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Chanaa Anas <anas.chanaa@etu.unilim.fr>                          *
 *           Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>       *
 *           Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.send.SendApduController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;


/**
 * View that display the form in order to send APDU
 *
 * @author Chanaa Anas
 * @author Tiana Razafindralambo
 * @author Thibault Desmoulins
 *         This class represents the GUI to sends APDU
 */
public class SendAPDUPanel extends JPanel {

    // The title of the <code>JTabbedPane</code>
    public String title = "Send APDU";

    static public JTextField fld_cla, fld_ins, fld_p1, fld_p2, fld_lc, fld_le;

    public static JTextArea txt_area;

    // The controller of this class
    public SendApduController sendApduController;

    /**
     * Send Apdu Panel Constructor
     *
     * @param sac Constructor that display the form
     * @param sac the controller of this view
     */

    public SendAPDUPanel(SendApduController sac) {
        this.sendApduController = sac;
        setLayout(new BorderLayout());
        JPanel jplPanel = new JPanel();
        add(jplPanel, BorderLayout.NORTH);
        jplPanel.setLayout(new FlowLayout());
        JLabel lbl_cla = new JLabel("CLA");
        JLabel lbl_ins = new JLabel("INS");
        JLabel lbl_p1 = new JLabel("P1");
        JLabel lbl_p2 = new JLabel("P2");
        JLabel lbl_lc = new JLabel("LC");
        JLabel lbl_le = new JLabel("LE");
        JLabel lbl_data = new JLabel("Data :");

        fld_cla = new JTextField(2);
        fld_cla.setDocument(sendApduController.createDefaultModel());
        fld_ins = new JTextField(2);
        fld_ins.setDocument(sendApduController.createDefaultModel());
        fld_p1 = new JTextField(2);
        // fld_lc.setDocument(SendApduController.getinstance().createDefaultModel());
        fld_p2 = new JTextField(2);
        fld_p1.setDocument(sendApduController.createDefaultModel());
        fld_lc = new JTextField(2);
        fld_p2.setDocument(sendApduController.createDefaultModel());
        // fld_lc.setText(""+SendApduController.nb_bytes);
        fld_lc.setEnabled(false);
        fld_le = new JTextField(2);
        fld_le.setDocument(sendApduController.createDefaultModel());

        // Events
        fld_cla.addKeyListener(sendApduController);
        fld_ins.addKeyListener(sendApduController);
        fld_lc.addKeyListener(sendApduController);
        fld_le.addKeyListener(sendApduController);
        fld_p1.addKeyListener(sendApduController);
        fld_p2.addKeyListener(sendApduController);

        jplPanel.add(lbl_cla);
        jplPanel.add(fld_cla);
        jplPanel.add(lbl_ins);
        jplPanel.add(fld_ins);
        jplPanel.add(lbl_p1);
        jplPanel.add(fld_p1);
        jplPanel.add(lbl_p2);
        jplPanel.add(fld_p2);
        jplPanel.add(lbl_lc);
        jplPanel.add(fld_lc);
        jplPanel.add(lbl_le);
        jplPanel.add(fld_le);

        TitledBorder tb = new TitledBorder("");
        txt_area = new JTextArea(20, 40);
        txt_area.setDocument(sendApduController.createDefaultModel());
        //txt_area.setDocument(SendApduController.getinstance().createDefaultModel());
        txt_area.setBackground(Color.white);
        txt_area.setBorder(tb);
        txt_area.addKeyListener(sac);

        JPanel jpdata = new JPanel(new BorderLayout());
        jpdata.add(lbl_data, BorderLayout.NORTH);
        jpdata.add(txt_area, BorderLayout.CENTER);
        add(jpdata);

        JButton but_snd = new JButton("Send");
        but_snd.addActionListener(sendApduController);

        JPanel pnl_snd = new JPanel();
        pnl_snd.setLayout(new BorderLayout());
        pnl_snd.add(but_snd, BorderLayout.EAST);
        add(pnl_snd, BorderLayout.SOUTH);
    }


    /**
     * the method settext counts the number of bytes input by the user
     *
     * @see SendApduController
     *      The text field LC is disabled and is filled automatically by the
     *      controller with this function
     */
    public static void settxt() {
        if (SendApduController.nb_bytes / 2 < 10) {
            fld_lc.setText(String.valueOf("0" + SendApduController.nb_bytes / 2));
        } else {
            fld_lc.setText(String.valueOf(SendApduController.nb_bytes / 2));
        }
    }


    /**
     * This method deletes the characters entered by the user if it exceeds the number of characters allowed
     *
     * @param jt
     * @param jt the text field to clear
     * @see SendApduController
     *      Clears the text field given in parameter
     */
    public static void clear(JTextField jt) {
        jt.setText("");
    }

}