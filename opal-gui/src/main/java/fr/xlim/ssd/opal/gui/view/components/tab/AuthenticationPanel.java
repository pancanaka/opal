package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.AuthenticationController;
import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.components.KeyComponentApplet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;


/**
 * @author Thibault
 * @author razaina
 * @author Estelle Blandinieres
 */
public class AuthenticationPanel extends JPanel implements ActionListener{

    public String title = "Authentication";

    private AuthenticationController controller;

    //private String[] configurations;

    private JButton jbLoadConf;

    private JTextField tfISDAID;

    private JComboBox cbSCPMode;
    private String[] SCPMode = {"SCP_UNDEFINED", "SCP_01_05", "SCP_01_15"};

    private JComboBox cbSecurityLevel;
    private String[] SecurityLevel = {"NO SECURITY LEVEL", "C_MAC",
    "C_ENC_AND_MAC"};

    private JComboBox cbTransProto;
    private String[] TransProto = {"T=0", "T=1", "*"};

    private ArrayList<KeyComponentApplet> Keylist = new ArrayList<KeyComponentApplet>();

    private JComboBox cbImplementation;
    private String[] Implementation = {"GP2xCommands", "GemXpresso211Commands"};

    private JButton jbAdd;
    private JButton jbRemove;
    private JButton jbAuthenticate;

    private short lineHeight  = 20;

    public AuthenticationPanel(MainController mainController) { 
        drawWindow();
    }

    public void setController(AuthenticationController controller)
    {
        this.controller = controller;
    }
    private void drawWindow() {
        this.removeAll();

        JPanel jplPanel = new JPanel();
        add(jplPanel);

        Box verticalBox = Box.createVerticalBox();
        Box ligne = Box.createHorizontalBox();
        ligne.setPreferredSize(new Dimension(500, 20));

        // Load Configuration
        jbLoadConf = new JButton("Load Configuration");
        jbLoadConf.addActionListener(this);
        verticalBox.add(createFormLine("", jbLoadConf));

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Issuer Security Domain AID
        tfISDAID = new JTextField();
        verticalBox.add(createFormLine("Issuer Security Domain AID", tfISDAID));
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // SCP PMode
        cbSCPMode = new JComboBox(SCPMode);
        verticalBox.add(createFormLine("SCP Mode", cbSCPMode));

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Security Level
        cbSecurityLevel = new JComboBox(SecurityLevel);
        verticalBox.add(createFormLine("Security Level", cbSecurityLevel));

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Transmission Protocol               
        cbTransProto = new JComboBox(TransProto);        
        verticalBox.add(createFormLine("Transmission Protocol", cbTransProto));

        // Key Panel
        Keylist.add(new KeyComponentApplet());
        drawKeysLines(verticalBox);

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Implementation
        cbImplementation = new JComboBox(Implementation);        
        verticalBox.add(createFormLine("Implementation", cbImplementation));

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Authenticate        
        jbAuthenticate = new JButton("Authenticate");
        ligne.add(jbAuthenticate);
        verticalBox.add(ligne);

        jplPanel.add(verticalBox);

    }

    /**
     * Create a box
     * @param label
     * @param field
     * @return a box with the label and the field
     */
    public Box createFormLine(String label, Component field) {
        Box    ligne  = Box.createHorizontalBox();
        JLabel lbl    = new JLabel(label);

        lbl.setPreferredSize(new Dimension(150,lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));

        ligne.add(lbl);
        ligne.add(field);

        return ligne;
    }

    /**
     *
     * @param v
     */
    public void drawKeysLines(Box v) {

        Box ligne = Box.createHorizontalBox();
        ligne.setPreferredSize(new Dimension(500, 20));

        int n      = Keylist.size();
        JPanel jpKeys = new JPanel();
        TitledBorder tbKeys = new TitledBorder("Keys");
        jpKeys.setBorder(tbKeys);

        Box verticalBoxKey = Box.createVerticalBox();
        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 5)));

        for(int i=0 ; i<n ; i++) {
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
        if(o instanceof JButton) {
            JButton b = (JButton) o;

            if(b.equals(jbAdd)) {
                Keylist.add(new KeyComponentApplet());
                drawWindow();

            } else if(b.getText().equals("Remove field")) {
                // The index of the field we want to remove ("Keys" fields)
                int iRemove = Integer.parseInt(b.getName());

                if(iRemove>=0 && iRemove<Keylist.size() && Keylist.size()>1) {
                    Keylist.remove(iRemove);
                    drawWindow();
                }
            } else if(b.equals(jbLoadConf)) {
                System.out.println("________" + (controller==null));
                String[] configurations = controller.getAllProfileNames();
                //String[] possibilities = {"test", "test1", "test2"};
                if (configurations == null) {
                    System.out.println("pas de configurations");
                } else {
                    String s = (String)JOptionPane.showInputDialog(null, "Choose a configuration",
                        "Configuration choice", JOptionPane.DEFAULT_OPTION,
                        null, configurations, configurations[0]);
                tfISDAID.setText(s);
                }
            }
        }
    }
}
