package fr.xlim.ssd.opal.gui.view.components.tab;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;


/**
 *
 * @author Estelle Blandinieres
 * @author Thibault
 * @author razaina
 */
public class SelectPanel extends JPanel implements ActionListener{

    public String title = "Select";
    
    private JComboBox cbAID;
    private JButton jbSelect;

    private short lineHeight = 20;

    public SelectPanel() {
        drawWindow();
    }

    public void drawWindow() {

        JPanel jplPanel = new JPanel();
        add(jplPanel);

        Box verticalBox = Box.createVerticalBox();

        // Object AID
        cbAID = new JComboBox();
        cbAID.setEditable(true);
        verticalBox.add(createFormLine("Object AID", cbAID));
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        // Select
        jbSelect = new JButton("Select");
        verticalBox.add(createFormLine("", jbSelect));
        verticalBox.add(Box.createRigidArea(new Dimension(300, 10)));

        jplPanel.add(verticalBox);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
}

