/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Thibault
 */
public class ManageProfiles extends JPanel implements ActionListener {
    private FenetrePrincipale f = null;

    private JTable tableau = null;

    private JButton btModify    = new JButton("Modify");
    private JButton btDelete    = new JButton("Delete");
    private JButton btAdd       = new JButton("Add");
    private JButton btOK        = new JButton("OK");

    public ManageProfiles(FenetrePrincipale f) {
        this.f = f;

        // Data for the profile tab
        String  title[] = {"Profile name", "Description", "ATR"};
        new TestXML();
        Object[][] data = TestXML.getProfils();
	/*Object[][] data = {	{"Profile 1", "Description 1", "Value 1"},
				{"Profile 2", "Description 2", "Value 1"},
				{"Profile 3", "Description 3", "Value 2"},
				{"Profile 4", "Description 4", "Value 2"},
				{"Profile 5", "Description 5", "Value 2"}
        };/**/
        tableau = new JTable(data, title){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();

        if(o instanceof JButton) {
            JButton bt = (JButton) o;
            String name    = bt.getText();

            /* If we click on the Quit menu */
            if(name.equals("Modify")) {
                int row = tableau.getSelectedRow();
                if(row<0) {
                    new JOptionPane().showMessageDialog(null, "No profile selected!", "Caution", JOptionPane.WARNING_MESSAGE);
                }
            }
            else if(name.equals("Delete")) {
                int row = tableau.getSelectedRow();
                if(row<0) {
                    new JOptionPane().showMessageDialog(null, "No profile selected!", "Caution", JOptionPane.WARNING_MESSAGE);
                }
                else {
                    int option = JOptionPane.showConfirmDialog(null, "Do you really to remove the profile?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
                        
                    }
                }
            }
            else if(name.equals("Add")) {

            }
            else if(name.equals("OK")) {
                this.f.affichePanel("terminal");
            }
        }
    }
}
