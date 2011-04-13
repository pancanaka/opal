package fr.xlim.ssd.opal.gui.view.profiles;

import fr.xlim.ssd.opal.gui.view.HomeView;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * @author Thibault Desmoulins
 */
public class AddUpdateProfileVue extends JPanel implements ActionListener {
    private HomeView f = null;

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

    private JCheckBox atr = new JCheckBox("Default");


    public AddUpdateProfileVue(HomeView f) {
        this.f = f;

        Box v = Box.createVerticalBox();
        JLabel l = null;

        v.add(Box.createRigidArea(new Dimension(300, lineSpacing+15)));

        // Line "name"
        Box ligne = Box.createHorizontalBox();


        // Line "name"
        v.add(createLigneForm("Name : ", txtName));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "description"
        v.add(createLigneForm("Description : ", txtDesc));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "ATR"
        v.add(createLigneForm("ATR : ", txtATR, atr));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "Issuer Security Domain AID"
        v.add(createLigneForm("Issuer Security Domain AID : ", txtISD));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "SCP Mode"
        String[] tab = {"SCP01_15"};
        cbSCP = new JComboBox(tab);
        v.add(createLigneForm("SCP Mode : ", cbSCP));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));

        
        // Line "Transmission Protocol"
        String[] tab2 = {"T=0", "T=1", "T=*"};
        cbTP = new JComboBox(tab2);
        v.add(createLigneForm("Transmission Protocol : ", cbTP));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        JPanel jpl2 = new JPanel();
        TitledBorder t1 = new TitledBorder("Keys");
        jpl2.setBorder(t1);
        v.add(jpl2);
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "Implementation"
        String[] tab3 = {"GP2xCommands", "GP2xCommands", "GP2xCommands"};
        cbImp = new JComboBox(tab3);
        v.add(createLigneForm("Implementation : ", cbImp, Box.createRigidArea(new Dimension(220, lineHeight))));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line with the save button
        cbImp = new JComboBox(tab3);
        v.add(createLigneForm("", Box.createRigidArea(new Dimension(300,80)), btSave));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));
        

        this.add(v);
    }


    public Box createLigneForm(String label, Component field) {
        Box    ligne  = Box.createHorizontalBox();
        JLabel lbl    = new JLabel(label);

        lbl.setPreferredSize(new Dimension(180,lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));
        
        ligne.add(lbl);
        ligne.add(field);

        return ligne;
    }

    public Box createLigneForm(String label, Component field, Component field2) {
        Box    ligne  = Box.createHorizontalBox();
        JLabel lbl    = new JLabel(label);

        lbl.setPreferredSize(new Dimension(180,lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));

        ligne.add(lbl);
        ligne.add(field);
        ligne.add(field2);

        return ligne;
    }


    
    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
