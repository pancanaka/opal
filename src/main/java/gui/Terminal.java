/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Thibault
 * @author razaina
 */
public class Terminal extends JPanel implements ActionListener {
    private FenetrePrincipale f = null;


    public Terminal(FenetrePrincipale f) {
        this.f = f;
        Box       sousMenu  = Box.createHorizontalBox();
        JComboBox terminaux = new JComboBox();

        JLabel lMenu = new JLabel("Selected terminal :");
        sousMenu.add(lMenu);
        sousMenu.add(terminaux);

        terminaux.addItem("Option 1");
        terminaux.addItem("Option 2");
        terminaux.addItem("Option 3");
        terminaux.addItem("Option 4");

        AuthenticationPanel p1 = new AuthenticationPanel();
        AppletPanel p2         = new AppletPanel();
        DeletePanel p3         = new DeletePanel();
        SelectPanel p4         = new SelectPanel();
        SendAPDUPanel p5       = new SendAPDUPanel();
        DataExchanges p6       = new DataExchanges();
        JTabbedPane myPanel    = new JTabbedPane();
 
        myPanel.addTab(p1.title, p1);
        myPanel.addTab(p2.title, p2);
        myPanel.addTab(p3.title, p3);
        myPanel.addTab(p4.title, p4);
        myPanel.addTab(p5.title, p5);
        myPanel.addTab(p6.title, p6);

        //scrollPane = new JScrollPane(myPanel);
        //myPanel.setSize(800,800);

        Box frame = Box.createVerticalBox();
        frame.add(sousMenu, BorderLayout.NORTH);
        frame.add(myPanel, BorderLayout.SOUTH);
        this.add(frame);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }
}
