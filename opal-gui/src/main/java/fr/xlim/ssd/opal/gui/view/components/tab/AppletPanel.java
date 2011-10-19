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

import fr.xlim.ssd.opal.gui.controller.AppletController;
import fr.xlim.ssd.opal.gui.controller.ConfigFieldsException;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * Applet vue
 *
 * @author Thibault Desmoulins
 * @author Estelle Blandinieres
 * @author Tiana Razafindralambo
 */
public class AppletPanel extends JPanel implements ActionListener, KeyListener {

    private AppletController controller;

    public String title = "Applet";

    private JLabel jlAppletFile;
    private JTextField tfAppletFile;
    private JButton bFile;

    private JTextField tfPackageAID;
    private JTextField tfSDAID;
    private JTextField tfParam;
    private JCheckBox cbConversion;
    private JTextField tfMaxDataLength;
    private JTextField tfAppletAID;
    private JTextField tfInstanceAID;
    private JTextField tfParam2;
    private JTextField tfPrivileges;

    private JButton jbLoad;

    private short lineHeight = 20;

    public AppletPanel() {

        // Applet file
        jlAppletFile = createLabel("Applet file", 170, lineHeight);
        tfAppletFile = new JTextField();
        bFile = new JButton("File");
        bFile.addActionListener((ActionListener) this);

        // Package AID
        tfPackageAID = new JTextField();
        tfPackageAID.addKeyListener(this);

        // Security domain AID
        tfSDAID = new JTextField();
        tfSDAID.addKeyListener(this);

        // Parameters
        tfParam = new JTextField();
        tfParam.addKeyListener(this);

        // Reorder cap file components
        cbConversion = new JCheckBox("Reorder cap file components", true);

        // Max Data Length
        tfMaxDataLength = new JTextField("255");

        // Applet AID
        tfAppletAID = new JTextField();
        tfAppletAID.addKeyListener(this);

        // Instance AID
        tfInstanceAID = new JTextField();
        tfInstanceAID.addKeyListener(this);

        // Parameters
        tfParam2 = new JTextField();
        tfParam2.addKeyListener(this);

        //Privileges
        tfPrivileges = new JTextField("00");
        tfPrivileges.addKeyListener(this);

        // Load applet
        jbLoad = new JButton("Load applet");
        jbLoad.addActionListener(this);
        drawWindow();

    }

    private void drawWindow() {
        JPanel jplPanel = new JPanel();
        add(jplPanel);

        Box verticalBox = Box.createVerticalBox();
        Box ligne = Box.createHorizontalBox();
        ligne.setPreferredSize(new Dimension(500, 20));

        // Applet file
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
        verticalBoxInstForLoad.add(createFormLine("Package AID", tfPackageAID));
        verticalBoxInstForLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Security domain AID
        verticalBoxInstForLoad.add(createFormLine("Security domain AID", tfSDAID));
        verticalBoxInstForLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Parameters
        verticalBoxInstForLoad.add(createFormLine("Parameters", tfParam));
        jplInstForLoad.add(verticalBoxInstForLoad);

        // Load panel
        JPanel jplLoad = new JPanel();
        jplLoad.setPreferredSize(new Dimension(500, 90));
        TitledBorder tbLoad = new TitledBorder("Load");
        jplLoad.setBorder(tbLoad);
        verticalBox.add(jplLoad);

        Box verticalBoxLoad = Box.createVerticalBox();

        // Reorder cap file components
        verticalBoxLoad.add(createFormLine("", cbConversion));
        verticalBoxLoad.add(Box.createRigidArea(new Dimension(480, 10)));

        // Max Data Length
        verticalBoxLoad.add(createFormLine("Max Data Length", tfMaxDataLength));

        jplLoad.add(verticalBoxLoad);

        // Install for install and make selectable
        JPanel jplInstForInst = new JPanel();
        jplInstForLoad.setPreferredSize(new Dimension(500, 120));
        TitledBorder tbInstForInst = new TitledBorder("Install for install and make selectable");
        jplInstForInst.setBorder(tbInstForInst);
        verticalBox.add(jplInstForInst);

        Box verticalBoxInstForInst = Box.createVerticalBox();

        // Applet AID
        verticalBoxInstForInst.add(createFormLine("Applet AID", tfAppletAID));
        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        // Instance AID
        verticalBoxInstForInst.add(createFormLine("Instance AID", tfInstanceAID));
        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        // Parameters
        verticalBoxInstForInst.add(createFormLine("Parameters", tfParam2));
        verticalBoxInstForInst.add(Box.createRigidArea(new Dimension(480, 10)));

        //Privileges
        verticalBoxInstForInst.add(createFormLine("Privileges", tfPrivileges));

        jplInstForInst.add(verticalBoxInstForInst);
        verticalBox.add(Box.createRigidArea(new Dimension(480, 10)));

        // Load applet
        ligne = Box.createHorizontalBox();
        ligne.add(jbLoad);
        verticalBox.add(ligne);

        jplPanel.add(verticalBox);
    }

    /**
     * Create a new label
     *
     * @param name
     * @param width
     * @param height
     * @return the label
     */
    private JLabel createLabel(String name, int width, int height) {
        JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(width, height));
        return label;
    }

    public Box createFormLine(String label, Component field) {
        Box ligne = Box.createHorizontalBox();
        JLabel lbl = new JLabel(label);

        lbl.setPreferredSize(new Dimension(150, lineHeight));
        ligne.setPreferredSize(new Dimension(480, lineHeight));

        ligne.add(lbl);
        ligne.add(field);

        return ligne;
    }

    public void setController(AppletController controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof JButton) {
            JButton b = (JButton) o;

            if (b.equals(bFile)) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CAP Files", "cap");
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    tfAppletFile.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            } else if (b.equals(jbLoad)) {
                if (controller.isAuthenticated()) {
                    try {
                        controller.checkForm(
                                tfPackageAID.getText(),
                                tfInstanceAID.getText(),
                                tfAppletAID.getText(),
                                tfAppletFile.getText(),
                                tfSDAID.getText(),
                                tfParam.getText(),
                                tfMaxDataLength.getText(),
                                tfPrivileges.getText(),
                                tfParam2.getText(),
                                cbConversion.isSelected());
                    } catch (ConfigFieldsException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You have to be authenticated.", "Caution", JOptionPane.WARNING_MESSAGE);
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
        // if the user press CTRL+B, the text is converted into hexa
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_B) {
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
            // if the user press CTRL+N, the text is converted into ASCII
        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
            if (o instanceof JTextField) {
                JTextField field = (JTextField) o;
                if (field.equals(tfAppletAID)) {
                    tfAppletAID.setText(byteToString(Conversion.hexToArray(tfAppletAID.getText())));
                } else if (field.equals(tfInstanceAID)) {
                    tfInstanceAID.setText(byteToString(Conversion.hexToArray(tfInstanceAID.getText())));
                } else if (field.equals(tfPackageAID)) {
                    tfPackageAID.setText(byteToString(Conversion.hexToArray(tfPackageAID.getText())));
                } else if (field.equals(tfParam)) {
                    tfParam.setText(byteToString(Conversion.hexToArray(tfParam.getText())));
                } else if (field.equals(tfSDAID)) {
                    tfSDAID.setText(byteToString(Conversion.hexToArray(tfSDAID.getText())));
                } else if (field.equals(tfParam2)) {
                    tfParam2.setText(byteToString(Conversion.hexToArray(tfParam2.getText())));
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private String byteToString(byte[] data) {
        String ascii = "";
        for (int i = 0; i < data.length; i++) {
            ascii += (char) data[i];
        }
        return ascii;
    }
}

