/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *
 * @author Thibault
 */
public class FenetrePrincipale extends JFrame {
    private JMenuBar menuBar = new JMenuBar();

    private JMenu file    = new JMenu("File");
    private JMenu options = new JMenu("Options");
    private JMenu about   = new JMenu("About");

    public FenetrePrincipale() {
        this.setSize(500,400);
        this.setLocationRelativeTo(null);
        this.setTitle("Fenêtre principale");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.menuBar.add(file);
        this.menuBar.add(options);
        this.menuBar.add(about);
        this.setJMenuBar(menuBar);

        this.setVisible(true);
    }
}
