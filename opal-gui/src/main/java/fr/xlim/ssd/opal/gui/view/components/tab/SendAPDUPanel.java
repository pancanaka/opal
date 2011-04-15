package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.send.SendApduController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * @author chanaa
 * @author razaina
 * @author Thibault
 */
public class SendAPDUPanel extends JPanel{


    public String title = "Send APDU";
    public JLabel lbl_cla,lbl_ins,lbl_p1,lbl_p2,lbl_lc,lbl_le,lbl_data;
    static public JTextField fld_cla,fld_ins,fld_p1,fld_p2,fld_lc,fld_le;
    public JTextArea txt_area;

     public static SendAPDUPanel SAP;
    public static SendAPDUPanel getinstance(){
        if(SAP == null) SAP = new SendAPDUPanel();
        return SAP;
    }

    public SendAPDUPanel()
    {
       
        setLayout(new BorderLayout());
        JPanel jplPanel = new JPanel();


        add(jplPanel,BorderLayout.NORTH);
        jplPanel.setLayout(new FlowLayout());
        lbl_cla  = new JLabel("CLA");
        
        lbl_ins  = new JLabel("INS");
        lbl_p1   = new JLabel("P1");
        lbl_p2   = new JLabel("P2");
        lbl_lc   = new JLabel("LC");
        lbl_le   = new JLabel("LE");
        lbl_data = new JLabel("Data :");

        fld_cla  = new JTextField(2);
        fld_ins = new JTextField(2);
        fld_p1  = new JTextField(2);
        fld_p2  = new JTextField(2);
        fld_lc  = new JTextField(2);
        fld_lc.setText(""+SendApduController.nb_bytes);
        fld_lc.setEnabled(false);
        fld_le  = new JTextField(2);

        fld_cla.addKeyListener(SendApduController.getinstance());
        fld_ins.addKeyListener(SendApduController.getinstance());
        fld_lc.addKeyListener(SendApduController.getinstance());
        fld_le.addKeyListener(SendApduController.getinstance());
        fld_p1.addKeyListener(SendApduController.getinstance());
        fld_p2.addKeyListener(SendApduController.getinstance());

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
        txt_area.setBackground(Color.white);
        txt_area.setBorder(tb);
        txt_area.addKeyListener(SendApduController.getinstance());
        JPanel jpdata = new JPanel(new BorderLayout());
        jpdata.add(lbl_data,BorderLayout.NORTH);

        jpdata.add(txt_area,BorderLayout.CENTER);
        add(jpdata);

        JButton but_snd = new JButton("Send");
        but_snd.addActionListener(new SendApduController());
        JPanel pnl_snd = new JPanel();
        pnl_snd.setLayout(new BorderLayout());
        pnl_snd.add(but_snd,BorderLayout.EAST);
        add(pnl_snd,BorderLayout.SOUTH);



    }
    public static void settxt(){
        fld_lc.setText(String.valueOf(SendApduController.nb_bytes/2));
       // SendAPDUPanel.getinstance().fld_lc.repaint();
    }
   
}