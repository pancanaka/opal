/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>           *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.profiles;

import fr.xlim.ssd.opal.gui.controller.ConfigFieldsException;
import fr.xlim.ssd.opal.gui.controller.ProfileController;
import fr.xlim.ssd.opal.gui.model.Key.KeyModel;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.KeyComponent;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    // The parent view
    private HomeView f = null;

    // The controller of this class
    private ProfileController profileController = null;

    private CardReaderModel cardReaderModel;
    private ATR currentATR;

    private short lineHeight  = 25;
    private short lineSpacing = 10;

    private JButton btAction = null,
                btAddATR     = new JButton("Add ATR"),
                btAddField   = new JButton("Add field"),
                btCancel     = new JButton("Cancel");

    private JTextField
                txtName = new JTextField(),
                txtDesc = new JTextField(),
                txtAID  = new JTextField();

    private Box v = Box.createVerticalBox();

    private JComboBox cbSCP = null, cbTP  = null, cbImp = null;

    // Values that initialize the comboboxes
    String[] implementationValues = {"GP2xCommands", "GemXpresso211Commands"};
    String[] tabTP = {"T=0", "T=1", "*"};
    SCPMode[] tabSCP = SCPMode.values(); // All SCPMode values are in this enumeration

    private ArrayList<JTextField>   ATRlist = new ArrayList<JTextField>();
    private ArrayList<KeyComponent> Keylist = new ArrayList<KeyComponent>();


    /**
     * This constructor is called when we want to create a new profile.
     * @param f the parent view
     */
    public AddUpdateProfileView(HomeView f) {
        this.f = f;
        profileController = f.getController().getProfileController();

        btAction = new JButton("Save");

        initializeWindow();

        drawWindow();
    }


    /**
     * This constructor is called when we want to update an existing profile.
     * @param f the parent view
     * @param profile the profile which already exist (all fields will be initialized with its values)
     */
    public AddUpdateProfileView(HomeView f, ProfileComponent profile) {
        this.f = f;
        profileController = f.getController().getProfileController();

        btAction = new JButton("Update");

        initializeWindow();


        txtName.setText(profile.getName());
        txtDesc.setText(profile.getDescription());
        txtAID.setText(profile.getAID());

        // Initialize ATR
        String[] list = profile.getATR();
        if(list.length > 0) {
            ATRlist.clear();
            for(String s : list) {
                ATRlist.add(new JTextField(s));
            }
        }

        // Initialize Keys
        ArrayList<KeyModel> listK = profile.getKeys();
        if(listK.size() > 0) {
            Keylist.clear();
            for(KeyModel k : listK) {
                String v = null;
                if(k.version.compareToIgnoreCase("ff") == 0) {
                    v = String.valueOf(Integer.parseInt(k.version, 16));
                }
                else {
                    v = String.valueOf(Integer.parseInt(Integer.toHexString(Integer.valueOf(k.version) & 0xFF).toUpperCase(), 16));
                }
                
                Keylist.add(new KeyComponent(k.type, v, k.keyID, k.key));
            }
        }

        drawWindow();
        
        cbSCP.setSelectedIndex( getIndexComboBox(cbSCP, "SCP_" + profile.getSCPmode()) );
        cbTP.setSelectedIndex ( getIndexComboBox(cbTP, profile.getTP()) );
        cbImp.setSelectedIndex( getIndexComboBox(cbImp, profile.getImplementation()) );
    }


    /**
     * This function is called in every constructor in order to perform
     * instructions that are necessary for the proper functioning of the class
     */
    public void initializeWindow() {
        // ATRlist must contain one JTextField at least
        ATRlist.add(new JTextField());

        // Keylist must contain one KeyComponentPanel at least
        Keylist.add(new KeyComponent());

        // Events
        btAddATR.addActionListener(this);
        btAddField.addActionListener(this);
        btCancel.addActionListener(this);
        btAction.addActionListener(this);

        cardReaderModel = f.getController().getCardReaderModel();

        currentATR = cardReaderModel.getSelectedCardATR();
        
        cardReaderModel.addCardReaderStateListener(new CardReaderStateListener() {

            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
                if (cardReaderModel.hasSelectedCardReaderItem()) {
                   currentATR = cardReaderModel.getSelectedCardATR();
                }
                cardReaderModel.removeCardReaderStateListener(this);
            }
        });
    }


    /**
     * This generic function return the index of the string to find in the combobox
     * @param cb the combobox
     * @param toFind the string to find in the combobox
     * @return 0 if the string is not found, otherwise its index in the combobox
     */
    public int getIndexComboBox(JComboBox cb, String toFind) {
        int n = cb.getItemCount();
        for(int i=1 ; i<n ; i++) {
            if(String.valueOf(cb.getItemAt(i)).compareToIgnoreCase(toFind) == 0) {
                return i;
            }
        }
        return 0;
    }


    /**
     * This function draw the form on the window. It's often called because ATR
     * fields and Keys can be added or removed at will
     * 
     * /!\ Important /!\ Notice that at the end of the function, the showPanel
     * function of the parent view is called in order to refresh the panel.
     */
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
        v.add(createFormLine("Issuer Security Domain AID : ", txtAID));
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
        v.add(createFormLine("", btCancel, btAction));
        v.add(Box.createRigidArea(new Dimension(300, 80)));
        
        this.add(v);
        
        /*
         * To fix a problem due to Windows and Mac we have to refresh the content pane
         * calling setContentPane of the main frame
         */
        this.f.showPanel(this);
    }


    /**
     * "Template" of ONE line of the form (with a label and a component)
     * @param label the label of the line
     * @param field the component of the line
     * @return an horizontal box containing the label and the component
     */
    public Box createFormLine(String label, Component field) {
        Box    ligne  = Box.createHorizontalBox();
        JLabel lbl    = new JLabel(label);

        lbl.setPreferredSize(new Dimension(200,lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));

        ligne.add(lbl);
        ligne.add(field);

        return ligne;
    }


    /**
     * "Template" of ONE line of the form (with a label and two component)
     * @param label the label of the line
     * @param field the first component of the line
     * @param field the second component of the line
     * @return an horizontal box containing the label and the two fields
     */
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


    /**
     * Draws all keys of the <code>Keylist</code> on the box given in parameter
     * @param v the box where the keys has to be drawn
     */
    public void drawKeysLines(Box v) {
        int n      = Keylist.size();
        JPanel jpl = new JPanel();
        jpl.setBorder(new TitledBorder("Keys"));

        Box vJPL = Box.createVerticalBox();
        vJPL.add(Box.createRigidArea(new Dimension(300, 5)));

        for(int i=0 ; i<n ; i++) {
            // Call function createLineForm in KeyComponentPanel class
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


    /**
     * Draws all ATR lines of the <code>ATRlist</code> on the box given in parameter
     * @param v the box where the keys has to be drawn
     */
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


    /**
     * @return the ATR list
     */
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
             System.out.println(Keylist.get(i).getType());
             p.addKey(Keylist.get(i).getType(), Keylist.get(i).getKeyVersion(), Keylist.get(i).getKeyId(), Keylist.get(i).getKey());
         }
    }


    /**
     * This function is called when an action is performed on a button in the form.
     */
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
            else if(b.equals(btAction)) {
                if(btAction.getText().equalsIgnoreCase("Save")) {
                    // When we add a new profile

                    ProfileComponent p = new ProfileComponent(txtName.getText(), txtDesc.getText(), txtAID.getText(), tabSCP[cbSCP.getSelectedIndex()].name(), tabTP[cbTP.getSelectedIndex()], getATR(), implementationValues[cbImp.getSelectedIndex()]);
                    getKeys(p);
                    
                    try {
                        profileController.addProfile(p);
                        new JOptionPane().showMessageDialog(null, "Profile added!", "Caution", JOptionPane.WARNING_MESSAGE);
                        this.f.showPanel("show profiles");
                    } catch (CardConfigNotFoundException ex) {
                        new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                    } catch (ConfigFieldsException ex) {
                        new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                    }
                }
                else if(btAction.getText().equalsIgnoreCase("Update")) {
                    // When we update an existing profile

                    ProfileComponent p = new ProfileComponent(txtName.getText(), txtDesc.getText(), txtAID.getText(), tabSCP[cbSCP.getSelectedIndex()].name(), tabTP[cbTP.getSelectedIndex()], getATR(), implementationValues[cbImp.getSelectedIndex()]);
                    getKeys(p);

                    try {
                        profileController.updateProfile(p);
                        new JOptionPane().showMessageDialog(null, "Profile updated!", "Caution", JOptionPane.WARNING_MESSAGE);
                        this.f.showPanel("show profiles");
                    } catch (CardConfigNotFoundException ex) {
                        new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                    } catch (ConfigFieldsException ex) {
                        new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            else if(b.equals(btCancel)) {
                int option = JOptionPane.showConfirmDialog(null, "Do you really want to go back?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
                    this.f.showPanel("show profiles");
                }
            }
            else if(b.getText().equals("Current")) {
                int i = Integer.parseInt(b.getName());

                if(currentATR==null) {
                    JOptionPane.showMessageDialog(null, "There is no current ATR to display", "Caution", JOptionPane.WARNING_MESSAGE);
                }
                else {
                    String atr = Conversion.arrayToHex(currentATR.getValue());
                    ATRlist.get(i).setText(atr);
                }
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
