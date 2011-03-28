/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author Thibault
 */
public class FenetrePrincipale extends JFrame {
    private JMenuBar menuBar = new JMenuBar();

    private JMenu file    = new JMenu("File");
    private JMenu options = new JMenu("Options");
    private JMenu about   = new JMenu("About");

    private JMenuItem itemQuit      = new JMenuItem("Quit");
    private JMenuItem itemMProfiles = new JMenuItem("Manage profiles");

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