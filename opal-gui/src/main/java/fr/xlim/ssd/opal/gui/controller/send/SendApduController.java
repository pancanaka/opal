/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.controller.send;

import fr.xlim.ssd.opal.gui.view.components.tab.SendAPDUPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author chanaa
 */
public class SendApduController implements KeyListener,ActionListener {
    // array of authorized caracters
    final char exa [] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    // singleton
    public static SendApduController SAC;
    public static SendApduController getinstance(){
        if(SAC == null) SAC = new SendApduController();
        return SAC;
    }
     


    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getSource() instanceof JTextField){
        JTextField tf = (JTextField)e.getSource();
        boolean t =false;
        for (int i = 0; i < exa.length; i++) {
            if(e.getKeyChar() == exa[i] ) t=true;

        }
        if(t==false){
            JOptionPane.showMessageDialog(null, "the caracters must be Exadecimal and Upper case", null,JOptionPane.ERROR_MESSAGE );
           // tf.setToolTipText(null);
        }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        
    }


    @Override
    public void keyReleased(KeyEvent e) {
       if(e.getSource() instanceof JTextField){
        JTextField tf = (JTextField)e.getSource();
        if( tf.getText().length() > 2 ){
            JOptionPane.showMessageDialog(null, "you exceed the field size Maximum size is two", null,JOptionPane.ERROR_MESSAGE ); 
            tf.setText(null);

        }
        }else if(e.getSource() instanceof JTextArea){
            JTextArea ta = (JTextArea)e.getSource();
            int nb_bytes = 0;
            String text;
            text = ta.getText();
            for(int i = 0 ; i<text.length();i++){
                if(text.charAt(i) != ' '){
                    nb_bytes++;
                    SendAPDUPanel.getinstance().fld_lc.setText(String.valueOf(nb_bytes));
                }
            }
            System.out.println(nb_bytes);
            
        }
      
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(SendAPDUPanel.getinstance().fld_cla.getText().isEmpty()||SendAPDUPanel.getinstance().fld_ins.getText().isEmpty()||SendAPDUPanel.getinstance().fld_lc.getText().isEmpty()||SendAPDUPanel.getinstance().fld_p1.getText().isEmpty()||SendAPDUPanel.getinstance().fld_p2.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Some fields still empty", null,JOptionPane.ERROR_MESSAGE );
        }

    }

}
