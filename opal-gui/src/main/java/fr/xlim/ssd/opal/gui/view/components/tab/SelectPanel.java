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

import fr.xlim.ssd.opal.gui.controller.SelectController;
import fr.xlim.ssd.opal.library.utilities.Conversion;
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
 * @author Thibault Desmoulins
 * @author Tiana Razafindralambo
 */
public class SelectPanel extends JPanel implements ActionListener{

    // The title of the <code>JTabbedPane</code>
    public String title = "Select";
    
    private JComboBox cbAID;
    private JButton jbSelect;
    private SelectController controller;

    private short lineHeight = 20;

    public SelectPanel() { 
        drawWindow();
    }
    public void setController(SelectController controller)
    {
        this.controller = controller;
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
        jbSelect.addActionListener((ActionListener) this);
        
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
        Object o = e.getSource(); 

        if(o instanceof JButton) {
            JButton b = (JButton) o;
            if(b.equals(jbSelect))
            {
                byte[] APPLET_ID = Conversion.hexToArray(cbAID.getSelectedItem().toString());
                controller.selectApplet(APPLET_ID);
            }
        }
    }
}

