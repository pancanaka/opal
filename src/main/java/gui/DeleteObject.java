/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author chanaa
 */
public class DeleteObject implements ActionListener {


    

    @Override
    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");

        String aid = DeletePanel.gettext();
        if(aid != null){
            final byte[] APPLET_ID = AsciiToHexa.stringToHex(aid);

       }else{
            JOptionPane.showMessageDialog(null, "Entrez le AID !");
       }



    }

}
