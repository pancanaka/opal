/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author razaina
 */
public class MainJTabbedPane extends JPanel {

    public MainJTabbedPane()
    {
        AuthenticationPanel p1 = new AuthenticationPanel();
        AppletPanel p2 = new AppletPanel();
        DeletePanel p3 = new DeletePanel();
        SelectPanel p4 = new SelectPanel();
        SendAPDUPanel p5 = new SendAPDUPanel();

        JTabbedPane jtb = new JTabbedPane();
        jtb.addTab("plop", p5);
    }
}
