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
package fr.xlim.ssd.opal.gui.view.profiles;

import fr.xlim.ssd.opal.gui.controller.ProfileController;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Show saved profiles
 *
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 */
public class ShowProfileView extends JPanel implements ActionListener {
    // The parent view
    private HomeView f = null;


    // The table full of data to display
    private JTable tableau = null;


    // All buttons of this view
    private JButton btModify = new JButton("Modify");
    private JButton btDelete = new JButton("Delete");
    private JButton btAdd = new JButton("Add");
    private JButton btOK = new JButton("OK");


    // The controller of this class
    private ProfileController profileController;


    /**
     * Single constructor of the class which initializes and display data thanks
     * to the controller contained in the parent view (<code>HomeView</code>).
     *
     * @param f the parent view
     */
    public ShowProfileView(HomeView f) {
        this.f = f;

        // Data for the profile tab
        String[] title = {"Profile name", "Description", "Implementation"};
        profileController = f.getController().getProfileController();

        Object[][] data = profileController.getAllProfiles();
        tableau = new JTable(data, title) {
            /* This function prevents the cells edition */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane spTab = new JScrollPane(tableau);

        // Create left column and put the tab inside
        Box left = Box.createVerticalBox();
        left.add(spTab);

        // Create right column and put buttons inside
        Box right = Box.createVerticalBox();
        right.add(btModify);
        right.add(btDelete);
        right.add(btAdd);
        right.add(btOK);

        Box top = Box.createHorizontalBox();
        top.add(left);
        top.add(right);

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.CENTER);

        // Events
        btModify.addActionListener(this);
        btDelete.addActionListener(this);
        btAdd.addActionListener(this);
        btOK.addActionListener(this);
    }


    /**
     * This function is called when an action is performed on a button in the form.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();

        if (o instanceof JButton) {
            JButton bt = (JButton) o;
            String name = bt.getText();

            /* If we click on the modify button */
            if (name.equals("Modify")) {
                int row = tableau.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(null, "No profile selected!", "Caution", JOptionPane.WARNING_MESSAGE);
                } else {
                    ProfileComponent profile = profileController.getProfile(row);
                    this.f.showPanel(new AddUpdateProfileView(f, profile));
                }
            } else if (name.equals("Delete")) {
                int row = tableau.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(null, "No profile selected!", "Caution", JOptionPane.WARNING_MESSAGE);
                } else {
                    int option = JOptionPane.showConfirmDialog(null, "Do you really want to remove the profile?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
                        boolean res;
                        try {
                            res = profileController.deleteProfile(row);
                            this.f.showPanel("show profiles");
                        } catch (CardConfigNotFoundException ex) {
                            new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } else if (name.equals("Add")) {
                this.f.showPanel("add update");
            } else if (name.equals("OK")) {
                this.f.showPanel("home");
            }
        }
    }
}
