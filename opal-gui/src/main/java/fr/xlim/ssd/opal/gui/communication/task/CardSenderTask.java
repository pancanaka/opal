/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Chanaa Anas <anas.chanaa@etu.unilim.fr>                           *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.communication.task;



import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.controller.CommunicationController;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import javax.smartcardio.CommandAPDU;
import org.jdesktop.application.Task;

/**
 *
 * @author chanaa
 *
 * Send an APDU Task
 * this class Send an APDU to a JavaCard and obtain the response from the javacard
 */
public class CardSenderTask extends Task<Void,Void> implements TaskInterface {

    private static final CustomLogger logger= new CustomLogger();
    private CommunicationController communication = null;
    private byte[] Apdu = null;
    private CardReaderModel cardReaderModel;
    
  
   
/**
 * Task Constructor
 * @param app
 * @param main_controller
 * @param to_send
 */

    public CardSenderTask(CardReaderModel crm ,CommunicationController communicationController  , byte [] apdu) {
        super(App.instance);
       this.cardReaderModel = crm;
        communication = communicationController;
        Apdu = apdu;
        

    }

   /**
    * 
    * @return
    * @throws Exception
    */

    
    @Override
    protected Void doInBackground() throws Exception {
        logger.info("Sending APDU Task ");
        CommandAPDU command = new CommandAPDU(Apdu);
        this.communication.send(command);
        return null;
     
    }
    @Override
    public void succeeded(Void v){
        logger.info("Sending APDU end !");
    }

    @Override
    public void doThen(Task task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void nextTask(Task task) {
        
    }

    






}
