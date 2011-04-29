/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.communication.task;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.controller.CommunicationController;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.library.params.CardConfig;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class AuthenticationTask extends Task<Void, Void> implements TaskInterface{

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationTask.class);
    private CardConfig cardConfig;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    private Task nextTask = null; 
    public AuthenticationTask(CardConfig cardConfig, CardReaderModel cardReaderModel, CommunicationController communication)
    {
        super(App.instance);
        this.cardConfig = cardConfig;
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
    }
    @Override
    protected Void doInBackground(){
       logger.info("Authenticating card...");
        this.cardReaderModel.addCardReaderStateListener(new CardReaderStateListener() {
            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
                if(cardReaderModel.hasSelectedCardReaderItem())
                { 
                   communication.authenticate(cardConfig);
                   communication.getModel().setSecurityDomain(cardConfig, cardReaderModel.getCardChannel());

                }else logger.error("No card found.");
                cardReaderModel.removeCardReaderStateListener(this);  
            }
        }); 
       return null;
    } 
 
    @Override
    protected void succeeded(Void nothing)
    {
        logger.info("Authentication task thread end");
        if(nextTask != null)
        {
            logger.info("Something else to do");
            doThen(nextTask);
        }
    }

    @Override
    public void doThen(Task task) {
        TaskFactory taskFactory = TaskFactory.run(task);
    }

    @Override
    public void nextTask(Task task) {
        this.nextTask = task;
    }
 
}