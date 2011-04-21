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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author CHANAA Anas
 * @author EL KHALDI Omar
 */
public class SendApduController implements KeyListener,ActionListener {
    // array of authorized caracters
   // final char exa [] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    // singleton
    public static SendApduController SAC;
    public static SendApduController getinstance(){
        if(SAC == null) SAC = new SendApduController();
        return SAC;
    }
    ///////////////////////////////////////mehdi////////////////////////////////////
    public Document createDefaultModel(){
        return new FileCaseDocument ();
    }

    static class FileCaseDocument extends PlainDocument{
        String text = null;
        String str1,str2;

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

            if(str == null){
                return;
            }
            text = this.getText(0, this.getLength());
            str1 = text.substring(0, offs);
            str2 = text.substring(offs, this.getLength());

            text = str1+str+str2;

            if (text.matches("([0-9]*)|([A-F]*)")){
                super.insertString(offs, str, a);
            }
        }
    }
     
    public static int nb_bytes = 0;

    @Override
    public void keyTyped(KeyEvent e) {
    
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
        // TEST OF THE EXADECIMAL VALUE
        ////////////////////////////////////
     /*     boolean t =false;
            for (int i = 0; i < exa.length; i++) {
                if(e.getKeyChar() == exa[i] ) t=true;

            }
            if(t==false){
                
                JOptionPane.showMessageDialog(null, "the caracters must be Exadecimal and Upper case", null,JOptionPane.ERROR_MESSAGE );
                 SendAPDUPanel.clear(tf);
            }*/
        ////////////////////////////////////


        }else if(e.getSource() instanceof JTextArea){
            JTextArea ta = (JTextArea)e.getSource();
            nb_bytes = 0;
            String text;
            text = ta.getText();
            for(int i = 0 ; i<text.length();i++){
                if(text.charAt(i) != ' '){
                    nb_bytes++;
                }
               
            }
            SendAPDUPanel.settxt();
        }
      
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(SendAPDUPanel.getinstance().fld_cla.getText().isEmpty()||SendAPDUPanel.getinstance().fld_ins.getText().isEmpty()||SendAPDUPanel.getinstance().fld_lc.getText().isEmpty()||SendAPDUPanel.getinstance().fld_p1.getText().isEmpty()||SendAPDUPanel.getinstance().fld_p2.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Some fields still empty", null,JOptionPane.ERROR_MESSAGE );
        }

    }

    

}
