/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import javax.swing.JFrame;

/**
 *
 * @author Thibault
 */
public class FenetrePrincipale extends JFrame {
    public FenetrePrincipale() {
        this.setSize(200,200);
        this.setLocationRelativeTo(null);
        this.setTitle("Fenêtre principale");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
