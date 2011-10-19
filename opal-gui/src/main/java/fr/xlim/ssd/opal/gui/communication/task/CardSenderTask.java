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
package fr.xlim.ssd.opal.gui.communication.task;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.controller.CommunicationController;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import javax.smartcardio.CommandAPDU;
import org.jdesktop.application.Task;

/**
 * Send an APDU Task
 * this class Send an APDU to a JavaCard and obtain the response from the javacard
 */
public class CardSenderTask extends Task<Void,Void> implements TaskInterface {

    private static final CustomLogger logger= new CustomLogger();
    private CommunicationController communication = null;
    private byte[] Apdu = null;
 //   private CardReaderModel cardReaderModel;
    
  
   
/**
 * Task Constructor
 * @param app
 * @param main_controller
 * @param to_send
 */

    public CardSenderTask(CommunicationController communicationController  , byte [] apdu) {
        super(App.instance);
     //  this.cardReaderModel = crm;
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
