package fr.xlim.ssd.opal.gui.view.components.tab;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * Applet vue
 *
 * @author Thibault
 * @author razaina
 * @author Estelle
 */
public class AppletPanel extends JPanel{

    public String title = "Applet";

    private JLabel jlAppletFile;
    private JTextField tfAppletFile;
    private JButton bFile;

    private JLabel jlPackageAID;
    private JTextField tfPackageAID;

    private JLabel jlSDAID;
    private JTextField tfSDAID;

    private JLabel jlParam;
    private JTextField tfParam;

    private JLabel jlConversion;
    private JCheckBox cbConversion;

    private JLabel jlMaxDataLength;
    private JTextField tfMaxDataLength;

    private JLabel jlAppletAID;
    private JTextField tfAppletAID;

    private JLabel jlInstanceAID;
    private JTextField tfInstanceAID;

    private JLabel jlPrivileges;
    private JTextField tfPrivileges;

    private JButton jbLoad;

    private short lineHeight  = 20;

    public AppletPanel() {
        JPanel jplPanel = new JPanel();
        add(jplPanel);

        Box verticalBox = Box.createVerticalBox();
        Box ligne = Box.createHorizontalBox();
        ligne.setPreferredSize(new Dimension(500, 20));

        // Applet file
        jlAppletFile = createLabel("Applet file", 170, lineHeight);
        tfAppletFile = new JTextField();
        bFile = new JButton("File");
        ligne.add(jlAppletFile);
        ligne.add(tfAppletFile);
        ligne.add(bFile);
        verticalBox.add(ligne);

        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Install for load panel
        JPanel jplInstForLoad = new JPanel();
        jplInstForLoad.setPreferredSize(new Dimension(500, 120));
        TitledBorder tbInstForLoad = new TitledBorder("Install for load");
        jplInstForLoad.setBorder(tbInstForLoad);
        verticalBox.add(jplInstForLoad);

        Box verticalBoxInstForLoad = Box.createVerticalBox();

        // Package AID
        ligne = Box.createHorizontalBox();
        jlPackageAID = createLabel("Package AID", 160, lineHeight);
        tfPackageAID = new JTextField();
        ligne.add(jlPackageAID);
        ligne.add(tfPackageAID);
        verticalBoxInstForLoad.add(ligne);

        verticalBoxInstForLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Security domain AID
        ligne = Box.createHorizontalBox();
        jlSDAID = createLabel("Security domain AID", 160, lineHeight);
        tfSDAID = new JTextField();
        ligne.add(jlSDAID);
        ligne.add(tfSDAID);
        verticalBoxInstForLoad.add(ligne);

        verticalBoxInstForLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Parameters
        ligne = Box.createHorizontalBox();
        jlParam = createLabel("Parameters", 160, lineHeight);
        tfParam = new JTextField();
        ligne.add(jlParam);
        ligne.add(tfParam);
        verticalBoxInstForLoad.add(ligne);

        jplInstForLoad.add(verticalBoxInstForLoad);

        // Load panel
        JPanel jplLoad = new JPanel();
        jplLoad.setPreferredSize(new Dimension(500, 90));
        TitledBorder tbLoad = new TitledBorder("Install for load");
        jplLoad.setBorder(tbLoad);
        verticalBox.add(jplLoad);

        Box verticalBoxLoad = Box.createVerticalBox();

        // Reorder cap file components
        ligne = Box.createHorizontalBox();
        cbConversion = new JCheckBox("Reorder cap file components");
        ligne.add(cbConversion);
        verticalBoxLoad.add(ligne);

        verticalBoxLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Max Data Length
        ligne = Box.createHorizontalBox();
        jlMaxDataLength = createLabel("Maximum data length", 160, lineHeight);
        tfMaxDataLength = new JTextField();
        ligne.add(jlMaxDataLength);
        ligne.add(tfMaxDataLength);
        verticalBoxLoad.add(ligne);

        jplLoad.add(verticalBoxLoad);

        // Install for install and make selectable
        JPanel jplInstForInst = new JPanel();
        jplInstForLoad.setPreferredSize(new Dimension(500, 120));
        TitledBorder tbInstForInst = new TitledBorder("Install for install and make selectable");
        jplInstForInst.setBorder(tbInstForInst);
        verticalBox.add(jplInstForInst);

        Box verticalBoxInstForInst = Box.createVerticalBox();

        // Applet AID
        ligne = Box.createHorizontalBox();
        jlAppletAID = createLabel("Applet AID", 160, lineHeight);
        tfAppletAID = new JTextField();
        ligne.add(jlAppletAID);
        ligne.add(tfAppletAID);
        verticalBoxInstForInst.add(ligne);

        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        // Instance AID
        ligne = Box.createHorizontalBox();
        jlInstanceAID = createLabel("Instance AID", 160, lineHeight);
        tfInstanceAID = new JTextField();
        ligne.add(jlInstanceAID);
        ligne.add(tfInstanceAID);
        verticalBoxInstForInst.add(ligne);

        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        // Parameters
        ligne = Box.createHorizontalBox();
        ligne.add(jlParam);
        ligne.add(tfParam);
        verticalBoxInstForInst.add(ligne);

        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        //Privileges
        ligne = Box.createHorizontalBox();
        jlPrivileges = createLabel("Privileges", 160, lineHeight);
        tfPrivileges = new JTextField();
        ligne.add(jlPrivileges);
        ligne.add(tfPrivileges);
        verticalBoxInstForInst.add(ligne);

        jplInstForInst.add(verticalBoxInstForInst);

        verticalBox.add(Box.createRigidArea(new Dimension(480, 10)));

        // Load applet
        ligne = Box.createHorizontalBox();
        jbLoad = new JButton("Load applet");
        ligne.add(jbLoad);
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

