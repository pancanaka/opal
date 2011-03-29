package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Panel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;



/**
 *
 * @author razaina
 */
public class SendAPDUPanel extends JPanel{

    public String title = "Send APDU";
    public SendAPDUPanel()
    {
        JLabel lbl_cla,lbl_ins,lbl_p1,lbl_p2,lbl_lc,lbl_le,lbl_data;
        JTextField fld_cla,fld_ins,fld_p1,fld_p2,fld_lc,fld_le;
        setLayout(new BorderLayout());
        JPanel jplPanel = new JPanel();


        add(jplPanel,BorderLayout.NORTH);
        jplPanel.setLayout(new FlowLayout());
        lbl_cla = new JLabel("CLA");
        fld_cla = new JTextField(2);
        lbl_ins = new JLabel("INS");
        lbl_p1= new JLabel("P1");
        lbl_p2 = new JLabel("P2");
        lbl_lc = new JLabel("LC");
        lbl_le = new JLabel("LE");
        lbl_data = new JLabel("Data :");

        fld_ins = new JTextField(2);
        fld_p1 = new JTextField(2);
        fld_p2 = new JTextField(2);
        fld_lc= new JTextField(2);
        fld_le = new JTextField(2);

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
        JTextArea txt_area = new JTextArea(20, 40);
        txt_area.setBackground(Color.white);
        txt_area.setBorder(tb);
        JPanel jpdata = new JPanel(new BorderLayout());
        jpdata.add(lbl_data,BorderLayout.NORTH);
        
        jpdata.add(txt_area,BorderLayout.CENTER);
        add(jpdata);
        
        JButton but_snd = new JButton("Send");
        JPanel pnl_snd = new JPanel();
        pnl_snd.setLayout(new BorderLayout());
        pnl_snd.add(but_snd,BorderLayout.EAST);
        add(pnl_snd,BorderLayout.SOUTH);



    }
}
