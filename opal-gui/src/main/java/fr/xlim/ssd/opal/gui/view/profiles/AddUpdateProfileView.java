package fr.xlim.ssd.opal.gui.view.profiles;

import fr.xlim.ssd.opal.gui.controller.ConfigFieldsException;
import fr.xlim.ssd.opal.gui.controller.ProfileController;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.KeyComponent;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * View used to add, update or use a card profile
 *
 * @author Thibault Desmoulins
 */
public class AddUpdateProfileView extends JPanel implements ActionListener {
    private HomeView f = null;
    private ProfileController profileController = null;

    private short lineHeight  = 25;
    private short lineSpacing = 10;

    private JButton btSave     = new JButton("Save"),
                btAddATR   = new JButton("Add ATR"),
                btAddField = new JButton("Add field"),
                btCancel   = new JButton("Cancel");

    private JTextField
                txtName = new JTextField(),
                txtDesc = new JTextField(),
                txtATR  = new JTextField(),
                txtISD  = new JTextField();

    private Box v = Box.createVerticalBox();

    private JComboBox cbSCP = null, cbTP  = null, cbImp = null;

    String[] implementationValues = {"GP2xCommands", "GemXpresso211Commands"};
    String[] tabTP = {"T=0", "T=1", "*"};
    SCPMode[] tabSCP = SCPMode.values(); // All SCPMode values are in this enumeration

    private ArrayList<JTextField>   ATRlist = new ArrayList<JTextField>();
    private ArrayList<KeyComponent> Keylist = new ArrayList<KeyComponent>();

    public AddUpdateProfileView(HomeView f) {
        this.f = f;
        profileController = f.getController().getProfileController();

        // ATRlist must contain one JTextField at least
        ATRlist.add(new JTextField());

        Keylist.add(new KeyComponent());

        btAddATR.addActionListener(this);
        btAddField.addActionListener(this);
        btCancel.addActionListener(this);
        btSave.addActionListener(this);

        drawWindow();
    }

    public void drawWindow() {
        this.removeAll();

        v.removeAll();
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing+15)));

        
        // Line "name"
        v.add(createFormLine("Name : ", txtName));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "description"
        v.add(createFormLine("Description : ", txtDesc));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "ATR"
        drawATRLines(v);
        v.add(createFormLine("", btAddATR));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing*2)));


        // Line "Issuer Security Domain AID"
        v.add(createFormLine("Issuer Security Domain AID : ", txtISD));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "SCP Mode"
        cbSCP = new JComboBox(tabSCP);
        v.add(createFormLine("SCP Mode : ", cbSCP));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "Transmission Protocol"
        cbTP = new JComboBox(tabTP);
        v.add(createFormLine("Transmission Protocol : ", cbTP));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing)));


        // Line "Keys"
        drawKeysLines(v);
        v.add(createFormLine("", btAddField));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing*2)));


        // Line "Implementation"
        cbImp = new JComboBox(implementationValues);
        v.add(createFormLine("Implementation : ", cbImp, Box.createRigidArea(new Dimension(220, lineHeight))));
        v.add(Box.createRigidArea(new Dimension(300, lineSpacing*3)));


        // Line with the save button
        v.add(createFormLine("", btCancel, btSave));
        v.add(Box.createRigidArea(new Dimension(300, 80)));
        
        this.add(v);
        
        /*
         * To fix a problem due to Windows and Mac we have to refresh the content pane
         * calling setContentPane of the main frame
         */
        this.f.showPanel(this);
    }


    public Box createFormLine(String label, Component field) {
        Box    ligne  = Box.createHorizontalBox();
        JLabel lbl    = new JLabel(label);

        lbl.setPreferredSize(new Dimension(200,lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));

        ligne.add(lbl);
        ligne.add(field);

        return ligne;
    }

    public Box createFormLine(String label, Component field, Component field2) {
        Box    ligne  = Box.createHorizontalBox();
        JLabel lbl    = new JLabel(label);

        lbl.setPreferredSize(new Dimension(200,lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));

        ligne.add(lbl);
        ligne.add(field);
        ligne.add(field2);

        return ligne;
    }

    public void drawKeysLines(Box v) {
        int n      = Keylist.size();
        JPanel jpl = new JPanel();
        jpl.setBorder(new TitledBorder("Keys"));

        Box vJPL = Box.createVerticalBox();
        vJPL.add(Box.createRigidArea(new Dimension(300, 5)));

        for(int i=0 ; i<n ; i++) {
            // Call function createLineForm in KeyComponent class
            Box b = Keylist.get(i).createLineForm();
            vJPL.add(b);

            
            // Add a "remove field" button (with listener)
            Box line3  = Box.createHorizontalBox();
            JButton jbRemove = new JButton("Remove field");
            jbRemove.setName(Integer.toString(i));
            jbRemove.addActionListener(this);
            line3.add(jbRemove);
            vJPL.add(line3);


            vJPL.add(Box.createRigidArea(new Dimension(300, lineSpacing)));
        }

        vJPL.add(Box.createRigidArea(new Dimension(300, 10)));
        jpl.add(vJPL);
        v.add(jpl);
    }

    public void drawATRLines(Box v) {
        int n = ATRlist.size();
        JPanel jpl = new JPanel();
        jpl.setBorder(new TitledBorder("ATR list"));

        Box vJPL = Box.createVerticalBox();
        vJPL.add(Box.createRigidArea(new Dimension(300, 5)));

        for(int i=0 ; i<n ; i++) {
            Box line = Box.createHorizontalBox();

            line.setPreferredSize(new Dimension(500, lineHeight));

            // Add the label
            JLabel lbl = new JLabel("ATR "+(i+1)+" : ");
            lbl.setPreferredSize(new Dimension(100,lineHeight));
            line.add(lbl);

            // Add the text field
            line.add(ATRlist.get(i));

            // Add the button "current" with the listener
            JButton btCurrent = new JButton("Current");
            btCurrent.addActionListener(this);
            btCurrent.setName(Integer.toString(i));
            line.add(btCurrent);

            // Add the button "remove" with the listener
            JButton btRemove = new JButton("Remove");
            btRemove.setName(Integer.toString(i));
            btRemove.addActionListener(this);
            line.add(btRemove);

            // Add this line in the vertical box
            vJPL.add(line);
        }

        vJPL.add(Box.createRigidArea(new Dimension(300, 10)));
        jpl.add(vJPL);
        v.add(jpl);
    }


    private String[] getATR() {
         int n = ATRlist.size();
         String ATRs[] = new String[n];

         for(int i=0 ; i<n ; i++) {
             ATRs[i] = ATRlist.get(i).getText();
         }

    return ATRs;
    }

    private void getKeys(ProfileComponent p) {
         int n = Keylist.size();

         for(int i=0 ; i<n ; i++) {
             p.addKey(Keylist.get(i).getType(), Keylist.get(i).getKeyVersion(), Keylist.get(i).getKeyId(), Keylist.get(i).getKey());
         }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if(o instanceof JButton) {
            JButton b = (JButton) o;
            
            if(b.equals(btAddATR)) {
                ATRlist.add(new JTextField());
                drawWindow();
            }
            else if(b.equals(btAddField)) {
                Keylist.add(new KeyComponent());
                drawWindow();
            }
            else if(b.equals(btSave)) {

                ProfileComponent p = new ProfileComponent(txtName.getText(), txtDesc.getText(), txtISD.getText(), tabSCP[cbSCP.getSelectedIndex()].name(), tabTP[cbTP.getSelectedIndex()], getATR(), implementationValues[cbImp.getSelectedIndex()]);
                getKeys(p);
                
                
                try {
                    profileController.addProfile(p);
                    this.f.showPanel("show profiles");
                } catch (CardConfigNotFoundException ex) {
                    new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                } catch (ConfigFieldsException ex) {
                    new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                }
            }
            else if(b.equals(btCancel)) {
                int option = JOptionPane.showConfirmDialog(null, "Do you really want to go back?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
                    this.f.showPanel("show profiles");
                }
            }
            else if(b.getText().equals("Current")) {
                JOptionPane.showMessageDialog(null, "plop : "+b.getName(), "Caution", JOptionPane.WARNING_MESSAGE);
            }
            else if(b.getText().equals("Remove")) {
                // The index of the field we want to remove ("ATR" field)
                int iRemove = Integer.parseInt(b.getName());

                if(iRemove>=0 && iRemove<ATRlist.size() && ATRlist.size()>1) {
                    ATRlist.remove(iRemove);
                    drawWindow();
                }
            }
            else if(b.getText().equals("Remove field")) {
                // The index of the field we want to remove ("Keys" fields)
                int iRemove = Integer.parseInt(b.getName());

                if(iRemove>=0 && iRemove<Keylist.size() && Keylist.size()>1) {
                    Keylist.remove(iRemove);
                    drawWindow();
                }
            }
        }
    }
}
