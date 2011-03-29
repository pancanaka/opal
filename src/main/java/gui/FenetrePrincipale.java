/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 *
 * @author Thibault
 */
public class FenetrePrincipale extends JFrame implements ActionListener {
    // Menus
    private JMenuBar  menuBar       = new JMenuBar();
    private JMenu     file          = new JMenu("File");
    private JMenu     options       = new JMenu("Options");
    private JMenu     about         = new JMenu("About");
    private JMenuItem itemQuit      = new JMenuItem("Quit");
    private JMenuItem itemMProfiles = new JMenuItem("Manage profiles");

    // Panels
    private ManageProfiles panMProfiles = new ManageProfiles(this);
    private JPanel         panTerminal  = new JPanel();


    public FenetrePrincipale() {
        // informations of the window
        this.setSize(800,500);
        this.setLocationRelativeTo(null);
        this.setTitle("OPAL - GUI");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Initializes the menu bar
        this.menuBar.add(file);
            this.file.add(itemMProfiles);
            this.file.add(itemQuit);
        this.menuBar.add(options);
        this.menuBar.add(about);
        this.setJMenuBar(menuBar);

        
        // Events
        itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.SHIFT_DOWN_MASK));
        itemMProfiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.SHIFT_DOWN_MASK));
        itemQuit.addActionListener(this);
        itemMProfiles.addActionListener(this);
        

        this.initPanTerminal();

        affichePanel("terminal");
    }

    public void initPanTerminal() {
        Box       sousMenu  = Box.createHorizontalBox();
        JComboBox terminaux = new JComboBox();

        JLabel lMenu = new JLabel("Selected terminal :");
        sousMenu.add(lMenu);
        sousMenu.add(terminaux);
        Dimension dim = new Dimension(25,250);

        terminaux.setSize(dim);
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
        
        panTerminal.add(sousMenu, BorderLayout.NORTH);
        panTerminal.add(myPanel, BorderLayout.SOUTH);
    }

    public void affichePanel(String type) {
        if(type.equals("terminal")) {
            this.setContentPane(panTerminal);
        }
        else if(type.equals("profiles")) {
            this.setContentPane(panMProfiles);
        }
        this.setVisible(true);
    }


    /*
     * Called when an action is performed on the principal window
     * @param ae
     *          the ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();

        if(o instanceof JMenuItem) {
            JMenuItem menu = (JMenuItem) o;
            String name    = menu.getText();
            /* If we click on the Quit menu */
            if(name.equals("Quit")) {
                int option = JOptionPane.showConfirmDialog(null, "Do you really want to quit ?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION && option != JOptionPane.CLOSED_OPTION) {
                    System.exit(0);
                }
            }
            else if(name.equals("Manage profiles")) {
                affichePanel("profiles");
            }
        }
    }
}