/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Estelle Blandinieres <estelle.blandinieres@etu.unilim.fr>        *
 *           Thibault Desmoulins  <thibault.desmoulins@etu.unilim.fr>         *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.ConfigFieldsException;
import fr.xlim.ssd.opal.gui.controller.DeleteController;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * @author Estelle Blandinieres
 * @author Thibault Desmoulins
 *
 * the DeletePanel class serves to instantiate the deletion tab
 */
public class DeletePanel extends JPanel implements ActionListener, KeyListener{

    public String title = "Delete";
    private DeleteController controller;

    private JComboBox cbAID;
    private JCheckBox cascade;
    private JButton jbDelete ;

    private short lineHeight = 20;
    
    /**
     * DeletePanel constructor
     */
    public DeletePanel() {

        // Object AID
        cbAID = new JComboBox();
        cbAID.addKeyListener(this);
        cbAID.setEditable(true);

        // Cascade
        cascade = new JCheckBox("Cascade", false);

        // Delete
        jbDelete = new JButton("Delete");
        jbDelete.addActionListener((ActionListener) this);

        drawWindow();
    }

    /**
     * Draw the elements of the panel
     * @author Estelle Blandinieres
     *
     */
    private void drawWindow() {

        setLayout(new BorderLayout());

        JPanel jplPanel = new JPanel();
        JPanel jplButton = new JPanel();

        add(jplPanel, BorderLayout.NORTH);
        add(jplButton, BorderLayout.SOUTH);

        jplPanel.setLayout(new FlowLayout());
        jplButton.setLayout(new FlowLayout());

        jplPanel.add(createFormLine("Objet AID", cbAID));
        jplPanel.add(cascade);
        jplButton.add(jbDelete);
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

        lbl.setPreferredSize(new Dimension(100,lineHeight));
        ligne.setPreferredSize(new Dimension(500, lineHeight));

        ligne.add(lbl);
        ligne.add(field);

        return ligne;
    }


    public void setController(DeleteController controller) {
        this.controller = controller;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource(); 

        if(o instanceof JButton) {
            JButton b = (JButton) o;
            if (b.equals(jbDelete)) {
                if (controller.isAuthenticated()) {
                    try {
                        boolean trouve = false;
                        String aid = (String)cbAID.getSelectedItem();
                        aid = (aid == null) ? "" : aid;

                        controller.checkForm(aid, cascade.isSelected());

                        // if the aid is not in the comboBox yet, we add it to it
                        for (int i=0; i<cbAID.getItemCount() && !trouve; i++) {
                            if (aid.compareTo((String)cbAID.getItemAt(i))==0) {
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}