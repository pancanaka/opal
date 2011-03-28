/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import gui.UITools;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
/**
 *
 * @author razaina
 */
public class TestJPanel extends JPanel {

    public TestJPanel()
    {
        JTabbedPane jtp1 = new JTabbedPane();
        JPanel jpInner1 = UITools.createInnerPanel("plpo");

        ImageIcon icon = new ImageIcon();
        jtp1.addTab("Tab one", icon, jpInner1, "Tab1");
        jtp1.setSelectedIndex(0);
        setLayout(new GridLayout(1,1));

        add(jtp1);
    }
}
