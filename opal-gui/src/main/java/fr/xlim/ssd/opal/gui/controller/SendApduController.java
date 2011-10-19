/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.gui.controller;
import fr.xlim.ssd.opal.gui.communication.task.CardSenderTask;
import fr.xlim.ssd.opal.gui.communication.task.TaskFactory;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.gui.view.components.tab.SendAPDUPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener; 
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import fr.xlim.ssd.opal.gui.controller.CommunicationController;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;

/**
 * This class controls actions on the sending Apdu Panel
 * 
 * @author CHANAA Anas
 * @author EL KHALDI Omar
 * @see SendAPDUPanel
 */
public class SendApduController implements KeyListener,ActionListener {
    private static final CustomLogger logger= new CustomLogger();
   // private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    String apdu = null;
    int taille_data_text = 0;
    String data_text = null;
    public static int nb_bytes = 0;

    /**
     * Constructor
     * @param cardreadermodel
     * @param communicationcontroller
     */
     
    public SendApduController(CardReaderModel cardreadermodel , CommunicationController communicationcontroller) {
        this.communication = communicationcontroller;
        //this.cardReaderModel = cardreadermodel;

    }


    public Document createDefaultModel(){
        return new FileCaseDocument ();
    }

    /**
     * this class manage the kyes typed by the user
     */

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

            if (text.matches("(([0-9]*)([A-F]*)([0-9]*)([A-F]*))*")){
                super.insertString(offs, str, a);
            }
        }
    }
     
    

    @Override
    public void keyTyped(KeyEvent e) {
    
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        
    }


    @Override
    /**
     *
     */
    public void keyReleased(KeyEvent e) {
       if(e.getSource() instanceof JTextField){
        JTextField tf = (JTextField)e.getSource();
        if( tf.getText().length() > 2 ){
            JOptionPane.showMessageDialog(null, "you exceed the field size Maximum size is two", null,JOptionPane.ERROR_MESSAGE ); 
            tf.setText(null);

        }
        }else if(e.getSource() instanceof JTextArea){
            JTextArea ta = (JTextArea)e.getSource();
            nb_bytes = 0;
            String text;
            text = ta.getText();
            data_text = ta.getText();
            for(int i = 0 ; i<text.length();i++){
                if(text.charAt(i) != ' '){
                    nb_bytes++;
                }
               
            }
            SendAPDUPanel.settxt();
        }
      
    }
    @Override
    /**
     *
     * if the send button are pressed the actionperformed method verify that all the text feald
     * are not empty after that the method execute the method sendApdu;
     *
     */
    public void actionPerformed(ActionEvent e) {
        if(SendAPDUPanel.fld_cla.getText().isEmpty()||SendAPDUPanel.fld_ins.getText().isEmpty()||SendAPDUPanel.fld_lc.getText().isEmpty()||SendAPDUPanel.fld_p1.getText().isEmpty()||SendAPDUPanel.fld_p2.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Some fields still empty", null,JOptionPane.ERROR_MESSAGE );
        }else{
            String lc = Integer.toHexString(Integer.parseInt(SendAPDUPanel.fld_lc.getText())).toUpperCase();
            if(lc.length()<2){
                lc = "0"+lc;
            }
            apdu = SendAPDUPanel.fld_cla.getText()+" "+SendAPDUPanel.fld_ins.getText()+" "+SendAPDUPanel.fld_p1.getText()+" "+SendAPDUPanel.fld_p2.getText()+" "+lc+" "+SendAPDUPanel.fld_le.getText()+" "+SendAPDUPanel.txt_area.getText();
            try{
                byte[] Apdu = Conversion.hexToArray(apdu);
                boolean isAuthenticated = communication.isAuthenticated();
                if(isAuthenticated){
                    sendApdu(Apdu);
                }else{
                    logger.error("the card is not authenticated yet !");
                }
            }catch(IllegalArgumentException ia){
                logger.error("the Data field is not filled correctly !");
            }

        }

    }
    public String getapdu(){
        return this.apdu;
    }
    /**
     * this method intialize the sending task
     * @param apdu
     */

     public void sendApdu( byte [] apdu){
        logger.info("Sending APDU");
        CardSenderTask cst = new CardSenderTask(communication,  apdu);
        TaskFactory tf = new TaskFactory().run(cst);


    }
}