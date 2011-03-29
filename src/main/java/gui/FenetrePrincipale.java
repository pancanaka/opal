/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

/**
 *
 * @author Thibault
 */
public class FenetrePrincipale extends JFrame {
    private JMenuBar menuBar = new JMenuBar();
    public JTabbedPane myPanel = new JTabbedPane();
    
    private JMenu file    = new JMenu("File");
    private JMenu options = new JMenu("Options");
    private JMenu about   = new JMenu("About");

    private JMenuItem itemQuit      = new JMenuItem("Quit");
    private JMenuItem itemMProfiles = new JMenuItem("Manage profiles");
    private JScrollPane scrollPane;

    public FenetrePrincipale() {
        this.setSize(500,400);
        this.setLocationRelativeTo(null);
        this.setTitle("OPAL - GUI");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.menuBar.add(file);
        this.menuBar.add(options);
        this.menuBar.add(about);

        this.file.add(itemMProfiles);
        this.file.add(itemQuit);
        
        itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.SHIFT_DOWN_MASK));
        itemQuit.addActionListener(new btQuit());

        this.setJMenuBar(menuBar);
        this.setVisible(true);

        initPanels();
    }
    private void initPanels()
    {
        AuthenticationPanel p1 = new AuthenticationPanel();
        AppletPanel p2 = new AppletPanel();
        DeletePanel p3 = new DeletePanel();
        SelectPanel p4 = new SelectPanel();
        SendAPDUPanel p5 = new SendAPDUPanel();

        myPanel.addTab(p1.title, p1);
        myPanel.addTab(p2.title, p2);
        myPanel.addTab(p3.title, p3);
        myPanel.addTab(p4.title, p4);
        myPanel.addTab(p5.title, p5);

        //scrollPane = new JScrollPane(myPanel);
       // myPanel.setSize(800,800);

        this.add(myPanel, BorderLayout.CENTER);
    }
}

class btQuit implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent arg0) {
        int option = JOptionPane.showConfirmDialog(null, "Do you really want to quit ?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
    }
}