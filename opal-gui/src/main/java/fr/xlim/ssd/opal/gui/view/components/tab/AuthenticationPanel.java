package fr.xlim.ssd.opal.gui.view.components.tab;


import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;



/**
 * @author Thibault
 * @author razaina
 */
public class AuthenticationPanel extends JPanel{

    public String title = "Authentication";
    
    private JLabel jlISDAID;
    public JTextField tfISDAID;

    private JLabel jlSCPMode;
    private JComboBox cbSCPMode;
    private String[] SCPMode = {"SCP_UNDEFINED", "SCP_01_05", "SCP_01_15"};

    private JLabel jlSecurityLevel;
    private JComboBox cbSecurityLevel;
    private String[] SecurityLevel = {"NO SECURITY LEVEL", "C_MAC",
    "C_ENC_AND_MAC"};

    private JLabel jlTransProto;
    private JComboBox cbTransProto;
    private String[] TransProto = {"T=0", "T=1", "*"};

    private JLabel jlType;
    private JComboBox cbType;
    private String[] Type = {"DES_CBC", "DES_ECB", "SCGemVisa", "SCGemVisa2",
    "AES"};

    private JLabel jlKeyVersion;
    private JTextField tfKeyVersion;

    private JLabel jlKeyID;
    private JTextField tfKeyID;

    private JLabel jlKey;
    private JTextField tfKey;

    private JLabel jlImplementation;
    private JComboBox cbImplementation;
    private String[] Implementation = {"GP2xCommands", "GemXpresso211Commands"};

    private JButton jbAdd;
    private JButton jbRemove;
    private JButton jbAuthenticate;

    private short lineHeight  = 20;

    public AuthenticationPanel()
    {
        JPanel jplPanel = new JPanel();
        add(jplPanel);

        Box verticalBox = Box.createVerticalBox();
        Box ligne = Box.createHorizontalBox();
        ligne.setPreferredSize(new Dimension(500, 20));

        // Issuer Security Domain AID
        jlISDAID = createLabel("Issuer Security Domain AID", 160, lineHeight);
        tfISDAID = new JTextField();
        ligne.add(jlISDAID);
        ligne.add(tfISDAID);
        verticalBox.add(ligne);

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // SCP PMode
        ligne = Box.createHorizontalBox();
        jlSCPMode = createLabel("SCP Mode", 160, lineHeight);
        cbSCPMode = new JComboBox(SCPMode);
        cbSCPMode.setPreferredSize(new Dimension(100,20));
        ligne.add(jlSCPMode);
        ligne.add(cbSCPMode);
        verticalBox.add(ligne);

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Security Level
        ligne = Box.createHorizontalBox();
        jlSecurityLevel = createLabel("Security Level", 160, lineHeight);
        cbSecurityLevel = new JComboBox(SecurityLevel);
        cbSecurityLevel.setPreferredSize(new Dimension(120,20));
        ligne.add(jlSecurityLevel);
        ligne.add(cbSecurityLevel);
        verticalBox.add(ligne);

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Transmission Protocol
        ligne = Box.createHorizontalBox();
        jlTransProto = createLabel("Transmission Protocol", 160, lineHeight);
        cbTransProto = new JComboBox(TransProto);
        cbTransProto.setPreferredSize(new Dimension(100,20));
        ligne.add(jlTransProto);
        ligne.add(cbTransProto);
        verticalBox.add(ligne);

        // Key Panel
        JPanel jpKeys = new JPanel();
        jpKeys.setPreferredSize(new Dimension(500, 180));
        TitledBorder tbKeys = new TitledBorder("Keys");
        jpKeys.setBorder(tbKeys);
        verticalBox.add(jpKeys);

        Box verticalBoxKey = Box.createVerticalBox();

        // Type
        ligne = Box.createHorizontalBox();
        jlType = createLabel("Type", 50, lineHeight);
        cbType = new JComboBox(Type);
        cbType.setPreferredSize(new Dimension(100,20));
        ligne.add(jlType);
        ligne.add(cbType);

        // Key Version Number
        jlKeyVersion = createLabel("Key Version Number", 120, lineHeight);
        tfKeyVersion = new JTextField();
        ligne.add(jlKeyVersion);
        ligne.add(tfKeyVersion);

        // Key ID
        jlKeyID = createLabel("Key ID", 50, lineHeight);
        tfKeyID = new JTextField();
        ligne.add(jlKeyID);
        ligne.add(tfKeyID);
        verticalBoxKey.add(ligne);

        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));

        // Key
        ligne = Box.createHorizontalBox();
        jlKey = createLabel("Key", 50, lineHeight);
        tfKey = new JTextField(48);
        ligne.add(jlKey);
        ligne.add(tfKey);
        verticalBoxKey.add(ligne);

        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));

        //Remove field
        ligne = Box.createHorizontalBox();
        jbRemove = new JButton("Remove field");
        ligne.add(jbRemove);
        verticalBoxKey.add(ligne);

        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));

        // Separator
        verticalBoxKey.add(new JSeparator(SwingConstants.HORIZONTAL));

        verticalBoxKey.add(Box.createRigidArea(new Dimension(300, 10)));

        // Add field
        ligne = Box.createHorizontalBox();
        jbAdd = new JButton("Add field");
        ligne.add(jbAdd);
        verticalBoxKey.add(ligne);

        jpKeys.add(verticalBoxKey);

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Implementation
        ligne = Box.createHorizontalBox();
        jlImplementation = createLabel("Implementation", 160, lineHeight);
        cbImplementation = new JComboBox(Implementation);
        cbImplementation.setPreferredSize(new Dimension(100,20));
        ligne.add(jlImplementation);
        ligne.add(cbImplementation);
        verticalBox.add(ligne);

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Authenticate
        ligne = Box.createHorizontalBox();
        jbAuthenticate = new JButton("Authenticate");
        ligne.add(jbAuthenticate);
        verticalBox.add(ligne);

        jplPanel.add(verticalBox);
    }

    /**
     * Create a new label
     * @param name
     * @param width
     * @param height
     * @return the label
     */
    public JLabel createLabel(String name, int width, int height) {
        JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(width,height));
        //label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        //label.addMouseListener((MouseListener) this);
        return label;
    }
}
