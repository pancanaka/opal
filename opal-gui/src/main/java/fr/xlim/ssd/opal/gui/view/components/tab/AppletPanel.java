package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.gui.controller.AsciiToHexa;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;
import javax.swing.KeyStroke;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * Applet vue
 *
 * @author Thibault
 * @author razaina
 * @author Estelle Blandinieres
 */
public class AppletPanel extends JPanel implements ActionListener, KeyListener{

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

    private JLabel jlParam2;
    private JTextField tfParam2;

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
        bFile.addActionListener((ActionListener) this);

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
        tfPackageAID.addKeyListener(this);
        ligne.add(jlPackageAID);
        ligne.add(tfPackageAID);
        verticalBoxInstForLoad.add(ligne);

        verticalBoxInstForLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Security domain AID
        ligne = Box.createHorizontalBox();
        jlSDAID = createLabel("Security domain AID", 160, lineHeight);
        tfSDAID = new JTextField();
        tfSDAID.addKeyListener(this);
        ligne.add(jlSDAID);
        ligne.add(tfSDAID);
        verticalBoxInstForLoad.add(ligne);

        verticalBoxInstForLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Parameters
        ligne = Box.createHorizontalBox();
        jlParam = createLabel("Parameters", 160, lineHeight);
        tfParam = new JTextField();
        tfParam.addKeyListener(this);
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
        cbConversion = new JCheckBox("Reorder cap file components", true);
        ligne.add(cbConversion);
        verticalBoxLoad.add(ligne);

        verticalBoxLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Max Data Length
        ligne = Box.createHorizontalBox();
        jlMaxDataLength = createLabel("Maximum data length", 160, lineHeight);
        tfMaxDataLength = new JTextField("255");
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
        tfAppletAID.addKeyListener(this);
        ligne.add(jlAppletAID);
        ligne.add(tfAppletAID);
        verticalBoxInstForInst.add(ligne);

        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        // Instance AID
        ligne = Box.createHorizontalBox();
        jlInstanceAID = createLabel("Instance AID", 160, lineHeight);
        tfInstanceAID = new JTextField();
        tfInstanceAID.addKeyListener(this);
        ligne.add(jlInstanceAID);
        ligne.add(tfInstanceAID);
        verticalBoxInstForInst.add(ligne);

        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        // Parameters
        ligne = Box.createHorizontalBox();
        jlParam2 = createLabel("Parameters", 160, lineHeight);
        tfParam2 = new JTextField();
        tfParam2.addKeyListener(this);
        ligne.add(jlParam2);
        ligne.add(tfParam2);
        verticalBoxInstForInst.add(ligne);

        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        //Privileges
        ligne = Box.createHorizontalBox();
        jlPrivileges = createLabel("Privileges", 160, lineHeight);
        tfPrivileges = new JTextField("00");
        tfPrivileges.addKeyListener(this);
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
    private JLabel createLabel(String name, int width, int height) {
        JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(width,height));
        //label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        //label.addMouseListener((MouseListener) this);
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        KeyStroke hexa;

        if(o instanceof JButton) {
            JButton b = (JButton) o;

            if(b.equals(bFile)) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CAP Files", "cap");
                //chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    tfAppletFile.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        } 

    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {

        Object o = e.getSource();
        // if the user press CTRL+H, the text is converted to hexa
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_H) {
            if (o instanceof JTextField) {
                JTextField field = (JTextField) o;
                if (field.equals(tfAppletAID)) {
                    tfAppletAID.setText(Conversion.arrayToHex(tfAppletAID.getText().getBytes()));
                } else if (field.equals(tfInstanceAID)) {
                    tfInstanceAID.setText(Conversion.arrayToHex(tfInstanceAID.getText().getBytes()));
                } else if (field.equals(tfPackageAID)) {
                    tfPackageAID.setText(Conversion.arrayToHex(tfPackageAID.getText().getBytes()));
                } else if (field.equals(tfParam)) {
                    tfParam.setText(Conversion.arrayToHex(tfParam.getText().getBytes()));
                } else if (field.equals(tfSDAID)) {
                    tfSDAID.setText(Conversion.arrayToHex(tfSDAID.getText().getBytes()));
                } else if (field.equals(tfParam2)) {
                    tfParam2.setText(Conversion.arrayToHex(tfParam2.getText().getBytes()));
                }
            }
        // if the user press CTRL+N, the text is converted to ASCII
        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
            if (o instanceof JTextField) {
                JTextField field = (JTextField) o;
                if (field.equals(tfAppletAID)) {
                    tfAppletAID.setText("ascii");
                    //tfAppletAID.setText(Conversion.hexToArray(tfAppletAID.getText()));
                } else if (field.equals(tfInstanceAID)) {
                    tfInstanceAID.setText("ascii");
                } else if (field.equals(tfPackageAID)) {
                    tfPackageAID.setText("ascii");
                } else if (field.equals(tfParam)) {
                    tfParam.setText("ascii");
                } else if (field.equals(tfSDAID)) {
                    tfSDAID.setText("ascii");
                } else if (field.equals(tfParam2)) {
                    tfParam2.setText("ascii");
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}

