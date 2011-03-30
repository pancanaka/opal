/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Thibault
 */
class AddUpdateProfileVue extends JPanel implements ActionListener, MouseListener {
    private FenetrePrincipale f = null;

    private JButton btOK        = new JButton("Save");

    private JButton btSave = new JButton("Save");

    private short lineHeight  = 20;
    private short lineSpacing = 10;

    private JTextField
                txtName = new JTextField(),
                txtDesc = new JTextField(),
                txtATR  = new JTextField(),
                txtISD  = new JTextField();

    private JComboBox cbSCP = null, cbTP  = null, cbImp = null;

    public AddUpdateProfileVue(FenetrePrincipale f) {
        this.f = f;
        
        Box v = Box.createVerticalBox();
        JLabel l = null;

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing+15)));

        // Line "name"
        Box ligne = Box.createHorizontalBox();
        ligne.setPreferredSize(new Dimension(500, lineHeight));
        l = createLabel("Name: ", 180, lineHeight);
        ligne.add(l);
        ligne.add(txtName);
        v.add(ligne);

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));

        // Line "description"
        ligne = Box.createHorizontalBox();
        l = createLabel("Description: ", 180, lineHeight);
        ligne.add(l);
        ligne.add(txtDesc);
        v.add(ligne);

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));

        // Line "ATR"
        ligne = Box.createHorizontalBox();
        l = createLabel("ATR: ", 180, lineHeight);
        ligne.add(l);
        ligne.add(txtATR);
        ligne.add(new JCheckBox("Default"));
        v.add(ligne);

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));

        // Line "Issuer Security Domain AID"
        ligne = Box.createHorizontalBox();
        l = createLabel("Issuer Security Domain AID: ", 180, lineHeight);
        ligne.add(l);
        ligne.add(txtISD);
        v.add(ligne);

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));

        // Line "SCP Mode"
        ligne = Box.createHorizontalBox();
        l = createLabel("SCP Mode: ", 180, lineHeight);
        ligne.add(l);
        cbSCP = new JComboBox();
        cbSCP.addItem("SCP01_15");
        ligne.add(cbSCP);
        v.add(ligne);

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));

        // Line "Transmission Protocol"
        ligne = Box.createHorizontalBox();
        l = createLabel("Transmission Protocol: ", 180, lineHeight);
        ligne.add(l);
        String[] tab1 = {"T=0", "T=1", "T=*"};
        cbTP = new JComboBox(tab1);
        ligne.add(cbTP);
        v.add(ligne);

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        JPanel jpl2 = new JPanel();
        TitledBorder t1 = new TitledBorder("Keys");
        jpl2.setBorder(t1);
        v.add(jpl2);

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "Implementation"
        ligne = Box.createHorizontalBox();
        l = createLabel("Implementation: ", 180, lineHeight);
        ligne.add(l);
        String[] tab2 = {"GP2xCommands", "GP2xCommands", "GP2xCommands"};
        cbImp = new JComboBox(tab2);
        ligne.add(cbImp);
        ligne.add(Box.createRigidArea(new Dimension(220, lineHeight)));
        v.add(ligne);

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));

        // Line with the save button
        ligne = Box.createHorizontalBox();
        ligne.add(Box.createRigidArea(new Dimension(300,80)));
        ligne.add(btSave);
        v.add(ligne);
        
        this.add(v);
    }

    public JLabel createLabel(String libelle, int width, int height) {
        JLabel lbl = new JLabel(libelle);
        lbl.setPreferredSize(new Dimension(width,height));
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener((MouseListener) this);
        return lbl;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        Object o = me.getSource();
        if(o instanceof JLabel) {
            JLabel l = (JLabel)o;
            String s = l.getText();
            
            if(s.equals("Name: "))                            {txtName.requestFocus();}
            else if(s.equals("Description: "))                {txtDesc.requestFocus();}
            else if(s.equals("ATR: "))                        {txtATR.requestFocus();}
            else if(s.equals("Issuer Security Domain AID: ")) {txtISD.requestFocus();}
            else if(s.equals("SCP Mode: "))                   {cbSCP.requestFocus();}
            else if(s.equals("Transmission Protocol: "))      {cbTP.requestFocus();}
            else if(s.equals("Implementation: "))             {cbImp.requestFocus();}
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
}
