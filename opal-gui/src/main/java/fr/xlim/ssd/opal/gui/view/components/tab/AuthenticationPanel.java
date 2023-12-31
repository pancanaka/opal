/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.AuthenticationController;
import fr.xlim.ssd.opal.gui.controller.ConfigFieldsException;
import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.model.Key.KeyModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.HomePanel;
import fr.xlim.ssd.opal.gui.view.components.KeyComponentApplet;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 * <<<<<<< local
 *
 * @author Thibault
 *         =======
 * @author Thibault Desmoulins
 *         >>>>>>> other
 * @author Tiana Razafindralambo
 * @author Estelle Blandinieres
 */
public class AuthenticationPanel extends JPanel implements ActionListener {
    // The parent view
    private HomeView f = null;

    private HomePanel p = null;

    public String title = "Authentication";

    private AuthenticationController controller;

    private JPanel jplPanel;

    private JButton jbLoadConf;

    private JTextField tfISDAID;

    private JComboBox cbSCPMode;
    private String[] SCPMode = {"SCP_UNDEFINED", "SCP_01_05", "SCP_01_15", "SCP_02_04",
            "SCP_02_05", "SCP_02_0A", "SCP_02_0B", "SCP_02_14", "SCP_02_15", "SCP_02_1A",
            "SCP_02_1B", "SCP_02_55", "SCP_02_45", "SCP_02_54", "SCP_10", "SCP_03_65",
            "SCP_03_6D", "SCP_03_05", "SCP_03_0D", "SCP_03_2D", "SCP_03_25"};

    private JComboBox cbSecurityLevel;
    private String[] SecurityLevel = {"NO_SECURITY_LEVEL", "C_MAC",
            "C_ENC_AND_MAC", "R_MAC", "C_MAC_AND_R_MAC", "C_ENC_AND_C_MAC_AND_R_MAC",
            "C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC"};

    private JComboBox cbTransProto;
    private String[] TransProto = {"T=0", "T=1", "*"};

    private ArrayList<KeyComponentApplet> Keylist = new ArrayList<KeyComponentApplet>();

    private JComboBox cbImplementation;
    private String[] Implementation = {"GP2xCommands", "GemXpresso211Commands"};

    private JButton jbAdd;
    private JButton jbRemove;
    private JButton jbAuthenticate;

    private short lineHeight = 20;

    //
    private String name;
    private String description;
    private String[] ATR;

    //String name, String description, String AID, String SCPmode, String TP, String[] ATR, String implementation
    public AuthenticationPanel(MainController mainController, HomeView f, HomePanel p) {
        this.f = f;
        this.p = p;

        jbLoadConf = new JButton("Load Configuration");
        jbLoadConf.addActionListener(this);
        tfISDAID = new JTextField();
        cbSCPMode = new JComboBox(SCPMode);
        cbSecurityLevel = new JComboBox(SecurityLevel);
        cbTransProto = new JComboBox(TransProto);
        Keylist.add(new KeyComponentApplet());
        cbImplementation = new JComboBox(Implementation);
        jbAuthenticate = new JButton("Authenticate");
        jbAuthenticate.addActionListener(this);

        drawWindow();
    }

    public void setController(AuthenticationController controller) {
        this.controller = controller;
    }

    private void drawWindow() {
        this.removeAll();

        jplPanel = new JPanel();
        add(jplPanel);

        //jplPanel.repaint();

        Box verticalBox = Box.createVerticalBox();
        Box ligne = Box.createHorizontalBox();
        ligne.setPreferredSize(new Dimension(500, 20));

        // Load Configuration
        verticalBox.add(createFormLine("", jbLoadConf));
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Issuer Security Domain AID
        verticalBox.add(createFormLine("Issuer Security Domain AID", tfISDAID));
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // SCP PMode
        verticalBox.add(createFormLine("SCP Mode", cbSCPMode));
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Security Level
        verticalBox.add(createFormLine("Security Level", cbSecurityLevel));
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Transmission Protocol               
        verticalBox.add(createFormLine("Transmission Protocol", cbTransProto));

        // Key Panel
        drawKeysLines(verticalBox);
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Implementation
        verticalBox.add(createFormLine("Implementation", cbImplementation));
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Authenticate        
        ligne.add(jbAuthenticate);
        verticalBox.add(ligne);

        jplPanel.add(verticalBox);

        /*
         * To fix a problem due to Windows and Mac we have to refresh the content pane
         * calling setContentPane of the main frame
         */
        this.f.showPanel(p);
    }

    /**
     * Create a box
     *
     * @param label
     * @param field
     * @return a box with the label and the field
     */
    public Box createFormLine(String label, Component field) {
        Box ligne = Box.createHorizontalBox();
        JLabel lbl = new JLabel(label);

        lbl.setPreferredSize(new Dimension(150, lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));

        ligne.add(lbl);
        ligne.add(field);

        return ligne;
    }

    /**
     * @param v
     */
    public void drawKeysLines(Box v) {

        Box ligne = Box.createHorizontalBox();
        ligne.setPreferredSize(new Dimension(500, 20));

        int n = Keylist.size();
        JPanel jpKeys = new JPanel();
        TitledBorder tbKeys = new TitledBorder("Keys");
        jpKeys.setBorder(tbKeys);

        Box verticalBoxKey = Box.createVerticalBox();
        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 5)));

        for (int i = 0; i < n; i++) {
            Box b = Keylist.get(i).createLineForm();
            verticalBoxKey.add(b);
            verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));
            //Remove field
            ligne = Box.createHorizontalBox();
            jbRemove = new JButton("Remove field");
            jbRemove.setName(Integer.toString(i));
            jbRemove.addActionListener(this);
            ligne.add(jbRemove);
            verticalBoxKey.add(ligne);
            verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));
        }
        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));

        // Separator
        verticalBoxKey.add(new JSeparator(SwingConstants.HORIZONTAL));

        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));

        // Add field
        ligne = Box.createHorizontalBox();
        jbAdd = new JButton("Add field");
        jbAdd.addActionListener(this);
        ligne.add(jbAdd);
        verticalBoxKey.add(ligne);

        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));
        jpKeys.add(verticalBoxKey);
        v.add(jpKeys);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof JButton) {
            JButton b = (JButton) o;

            if (b.equals(jbAdd)) {
                Keylist.add(new KeyComponentApplet());
                drawWindow();

            } else if (b.getText().equals("Remove field")) {
                // The index of the field we want to remove ("Keys" fields)
                int iRemove = Integer.parseInt(b.getName());

                if (iRemove >= 0 && iRemove < Keylist.size() && Keylist.size() > 1) {
                    Keylist.remove(iRemove);
                    drawWindow();
                }
            } else if (b.equals(jbLoadConf)) {
                String[] configurations = controller.getAllProfileNames();

                if (configurations == null) {
                    System.out.println("No configuration found.");
                } else {
                    // name of the default configuration
                    String defaultConfig = controller.getCurrentCardDefaultProfileName();
                    //String defaultConfig = "";

                    int indexDefaultConfig = 0;

                    // default configuration card research
                    for (int i = 0; i < configurations.length; i++) {
                        if (configurations[i].compareTo(defaultConfig) == 0) {
                            indexDefaultConfig = i;
                        }
                    }

                    // name of the choosen configuration
                    String configName = (String) JOptionPane.showInputDialog(null, "Choose a configuration",
                            "Configuration choice", JOptionPane.DEFAULT_OPTION,
                            null, configurations, configurations[indexDefaultConfig]);

                    if (configName != null) {
                        // the choosen configuration
                        ProfileComponent config = controller.getProfileByName(configName);

                        // keeping informations
                        name = config.getName();
                        description = config.getDescription();
                        ATR = config.getATR();

                        // filling the view with all the informations
                        tfISDAID.setText(config.getAID());
                        cbSCPMode.setSelectedItem("SCP_" + config.getSCPmode());
                        cbTransProto.setSelectedItem(config.getTP());

                        // getting all keys
                        ArrayList<KeyModel> listK = config.getKeys();
                        if (listK.size() > 0) {
                            Keylist.clear();
                            for (KeyModel k : listK) {
                                String v = null;
                                if (k.version.compareToIgnoreCase("ff") == 0) {
                                    v = String.valueOf(Integer.parseInt(k.version, 16));
                                } else {
                                    v = String.valueOf(Integer.parseInt(Integer.toHexString(Integer.valueOf(k.version) & 0xFF).toUpperCase(), 16));
                                }

                                Keylist.add(new KeyComponentApplet(k.type, v, k.keyID, k.key));
                            }
                        }
                        cbImplementation.setSelectedItem("fr.xlim.ssd.opal.library.commands." + config.getImplementation());

                        drawWindow();
                    }
                }
            } else if (b.equals(jbAuthenticate)) {
                // ProfileComponent created with user's choices 
                if (tfISDAID.getText().compareTo("") == 0) {
                    JOptionPane.showMessageDialog(null, "You have to load a configuration.", "Caution", JOptionPane.WARNING_MESSAGE);
                } else {
                    ProfileComponent authentication = new ProfileComponent(name,
                            description, tfISDAID.getText(), (String) cbSCPMode.getSelectedItem(),
                            (String) cbTransProto.getSelectedItem(), ATR,
                            "fr.xlim.ssd.opal.library.commands." + (String) cbImplementation.getSelectedItem());

                    ArrayList<KeyModel> keyModels = new ArrayList<KeyModel>();
                    for (KeyComponentApplet kc : Keylist) {
                        authentication.addKey(kc.type, kc.keyVersion, kc.keyId, kc.key);
                    }

                    try {
                        controller.authenticate(authentication,
                                (String) cbSecurityLevel.getSelectedItem());
                    } catch (CardConfigNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                    } catch (ConfigFieldsException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }
}
