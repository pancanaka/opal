/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Estelle Blandinieres <estelle.blandinieres@etu.unilim.fr>        *
 *           Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 *           Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>       *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.ConfigFieldsException;
import fr.xlim.ssd.opal.gui.controller.SelectController;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;


/**
 * @author Estelle Blandinieres
 * @author Thibault Desmoulins
 * @author Tiana Razafindralambo
 */
public class SelectPanel extends JPanel implements ActionListener, KeyListener {

    // The title of the <code>JTabbedPane</code>
    public String title = "Select";

    private JComboBox cbAID;
    private JButton jbSelect;
    private SelectController controller;

    private short lineHeight = 20;

    public SelectPanel() {
        // Object AID
        cbAID = new JComboBox();
        cbAID.addKeyListener(this);
        cbAID.setEditable(true);

        // Select
        jbSelect = new JButton("Select");
        jbSelect.addActionListener((ActionListener) this);

        drawWindow();
    }

    /**
     * Set the controller
     *
     * @param controller, new controller
     */
    public void setController(SelectController controller) {
        this.controller = controller;
    }

    /**
     * Draw the elements of the panel
     *
     * @author Estelle Blandinieres
     */
    private void drawWindow() {

        setLayout(new BorderLayout());

        JPanel jplPanel = new JPanel();
        JPanel jplButton = new JPanel();

        add(jplPanel, BorderLayout.NORTH);
        add(jplButton, BorderLayout.SOUTH);

        jplPanel.setLayout(new FlowLayout());
        jplButton.setLayout(new FlowLayout());

        jplPanel.add(createFormLine("Object AID", cbAID));
        jplButton.add(jbSelect);
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

        lbl.setPreferredSize(new Dimension(100, lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));

        ligne.add(lbl);
        ligne.add(field);

        return ligne;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o instanceof JButton) {
            JButton b = (JButton) o;
            if (b.equals(jbSelect)) {
                if (controller.isAuthenticated()) {
                    try {
                        boolean trouve = false;
                        String aid = (String) cbAID.getSelectedItem();
                        aid = (aid == null) ? "" : aid;
                        controller.checkForm(aid);

                        // if the aid is not in the comboBox yet, we add it to it
                        for (int i = 0; i < cbAID.getItemCount() && !trouve; i++) {
                            if (aid.compareTo((String) cbAID.getItemAt(i)) == 0) {
                                trouve = true;
                            }
                        }
                        if (!trouve) {
                            cbAID.addItem(cbAID.getSelectedItem());
                        }
                    } catch (ConfigFieldsException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(),
                                "Caution", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You have to be authenticated.", "Caution", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("test");
        Object o = e.getSource();

        // if the user press CTRL+B, the text is converted into hexa
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_B) {
            if (o instanceof JComboBox) {
                JComboBox field = (JComboBox) o;
                if (field.equals(cbAID)) {
                    String aid = (String) cbAID.getSelectedItem();
                    System.out.println(aid);
                    cbAID.setSelectedItem(Conversion.arrayToHex(aid.getBytes()));
                }
            }
            // if the user press CTRL+N, the text is converted into ASCII
        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
            if (o instanceof JComboBox) {
                JComboBox field = (JComboBox) o;
                if (field.equals(cbAID)) {
                    String aid = (String) cbAID.getSelectedItem();
                    cbAID.setSelectedItem(byteToString(Conversion.hexToArray(aid)));
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String byteToString(byte[] data) {
        String ascii = "";
        for (int i = 0; i < data.length; i++) {
            ascii += (char) data[i];
        }
        return ascii;
    }
}

