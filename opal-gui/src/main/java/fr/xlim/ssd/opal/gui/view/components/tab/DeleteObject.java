/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Chanaa Anas <anas.chanaa@etu.unilim.fr>                          *
 *           Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.AsciiToHexa;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author Chanaa Anas
 * @author Thibault Desmoulins
 */
public class DeleteObject implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");

        String aid = DeletePanel.gettext();
        if(aid != null){
            final byte[] APPLET_ID = AsciiToHexa.stringToHex(aid);
            // j'atten de voir l'utilité de Securiti Domain pour pouvoir
            // supprimer l'AID

       }else{
            JOptionPane.showMessageDialog(null, "Entrez le AID !");
       }
    }
}
