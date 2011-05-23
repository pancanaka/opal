/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Chanaa Anas <anas.chanaa@etu.unilim.fr>                          *
 *           El Khaldi Omar <omar.el-khaldi@etu.unilim.fr>                    *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.controller.send;
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
            byte[] Apdu = Conversion.hexToArray(apdu);
            boolean isAuthenticated = communication.isAuthenticated();
            if(isAuthenticated){
                sendApdu(Apdu);
            }else{
                logger.info("the card is not authenticated yet !");
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