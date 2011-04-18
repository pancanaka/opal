package fr.xlim.ssd.opal.gui.view.profiles;

import fr.xlim.ssd.opal.gui.controller.ConfigFieldsException;
import fr.xlim.ssd.opal.gui.controller.ProfileController;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


/**
 * Show saved profiles
 *
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 */
public class ShowProfileView extends JPanel implements ActionListener {
    private HomeView f = null;

    private JTable tableau = null;

    private JButton btModify    = new JButton("Modify");
    private JButton btDelete    = new JButton("Delete");
    private JButton btAdd       = new JButton("Add");
    private JButton btOK        = new JButton("OK");

    private ProfileController profileController;

    public ShowProfileView(HomeView f) {
        try {
            this.f = f;
            // Data for the profile tab
            String[] title = {"Profile name", "Description", "Implementation"};
            profileController = new ProfileController();
            /**/
            /**/
            Object[][] data = profileController.getAllProfiles();
            tableau = new JTable(data, title) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JScrollPane spTab = new JScrollPane(tableau); /**/
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
        } catch (CardConfigNotFoundException ex) {
            new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();

        if(o instanceof JButton) {
            JButton bt  = (JButton) o;
            String name = bt.getText();

            /* If we click on the Quit menu */
            if(name.equals("Modify")) {
                int row = tableau.getSelectedRow();
                if(row<0) {
                    JOptionPane.showMessageDialog(null, "No profile selected!", "Caution", JOptionPane.WARNING_MESSAGE);
                }
            }
            else if(name.equals("Delete")) {
                int row = tableau.getSelectedRow();
                if(row<0) {
                    JOptionPane.showMessageDialog(null, "No profile selected!", "Caution", JOptionPane.WARNING_MESSAGE);
                }
                else {
                    int option = JOptionPane.showConfirmDialog(null, "Do you really want to remove the profile?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
                        try {
                                /*
                                boolean res = profileController.deleteProfile(0);
                                if(res) {
                                new JOptionPane().showMessageDialog(null, "Card deleted!", "Caution", JOptionPane.WARNING_MESSAGE);
                                } else {
                                new JOptionPane().showMessageDialog(null, "No card found!", "Caution", JOptionPane.WARNING_MESSAGE);
                                }
                                /**/
                                profileController.addProfile();
                        } catch (ConfigFieldsException ex) {
                            new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                        } catch (CardConfigNotFoundException ex) {
                            new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
            else if(name.equals("Add")) {
                this.f.showPanel("add update");
            }
            else if(name.equals("OK")) {
                this.f.showPanel("home");
            }
        }
    }
}
