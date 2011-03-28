/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author razaina
 */
public class UITools {
    public static JPanel createInnerPanel(String text)
    {
        JPanel jplPanel = new JPanel();
	JLabel jlbDisplay = new JLabel(text);
	jlbDisplay.setHorizontalAlignment(JLabel.LEFT);
	jplPanel.setLayout(new GridLayout(1, 1));
	jplPanel.add(jlbDisplay);
	return jplPanel;

    }
}
