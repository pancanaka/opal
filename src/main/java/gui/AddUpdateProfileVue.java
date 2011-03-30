/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 *
 * @author Thibault
 */
class AddUpdateProfileVue extends JPanel implements ActionListener {
    private FenetrePrincipale f = null;

    private JButton btOK        = new JButton("Save");

    private JTextField 
                txtName   = new JTextField(),
                txtDesc   = new JTextField(),
                txtATR    = new JTextField(),
                txtIssuer = new JTextField();

    public AddUpdateProfileVue(FenetrePrincipale f) {
        this.f = f;

        String[] labels = {"Name: ", "Description: ", "ATR: ", "Issuer Security Domain AID: ", "SCP Mode: ", "Transmission Protocol: "};
        int numPairs = labels.length;

        //Create and populate the panel.
        JPanel p = new JPanel(new SpringLayout());
        for (int i = 0; i < numPairs; i++) {
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            p.add(l);
            JTextField textField = new JTextField(10);
            l.setLabelFor(textField);
            p.add(textField);
        }

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(p,
                                        numPairs, 2, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad

        this.add(p);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
