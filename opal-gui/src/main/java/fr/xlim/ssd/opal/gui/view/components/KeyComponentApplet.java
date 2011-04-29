/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.view.components;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JLabel;

/**
 *
 * @author Estelle Blandinieres
 */
public class KeyComponentApplet extends KeyComponent{
    private short lineHeight  = 20;

    public Box createLineForm() {

        Box line1  = Box.createHorizontalBox();
        line1.setPreferredSize(new Dimension(500, lineHeight));
        line1.add(new JLabel("Type "));
        line1.add(cbImp);
        line1.add(new JLabel("Key version number "));
        line1.add(JkeyVersion);
        line1.add(new JLabel("Key id "));
        line1.add(JkeyId);

        Box line2  = Box.createHorizontalBox();
        line2.setPreferredSize(new Dimension(500, lineHeight));
        line2.add(new JLabel("Key "));
        line2.add(Jkey);

        Box v = Box.createVerticalBox();
        v.add(line1);
        v.add(Box.createRigidArea(new Dimension(500, 10)));
        v.add(line2);

        return v;
    }
}
