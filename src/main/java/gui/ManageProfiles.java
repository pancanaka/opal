/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Thibault
 */
public class ManageProfiles extends JPanel {
    JButton btModify = new JButton("Modify");
    JButton btDelete = new JButton("Delete");
    JButton btAdd    = new JButton("Add");
    JButton btOK     = new JButton("OK");

    public ManageProfiles() {
        //Les données du tableau
	Object[][] data = {	{"Profile 1", "Description 1", "Value 1"},
				{"Profile 2", "Description 2", "Value 1"},
				{"Profile 3", "Description 3", "Value 2"},
				{"Profile 4", "Description 4", "Value 2"},
				{"Profile 5", "Description 5", "Value 2"}
        };
        String  title[] = {"Profile name", "Description", "ATR"};
        JTable tableau = new JTable(data, title);
        JScrollPane spTab = new JScrollPane(tableau);

        // Create left column of radio buttons
        Box left = Box.createVerticalBox();
        left.add(spTab);
        
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
    }
}
