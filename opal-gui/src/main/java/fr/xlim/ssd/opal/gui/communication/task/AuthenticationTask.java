/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.communication.task;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.controller.CommunicationController;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.params.CardConfig;
import org.jdesktop.application.Task;

/**
 * @author Tiana Razafindralambo
 */
public class AuthenticationTask extends Task<Void, Void> implements TaskInterface {

    private static final CustomLogger logger = new CustomLogger();
    private CardConfig cardConfig;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    private Task nextTask = null;

    /**
     * Default constructor
     *
     * @param cardConfig
     * @param cardReaderModel
     * @param communication   controller that allows to communicate with the card
     * @param secLevel        security level
     */
    public AuthenticationTask(CardConfig cardConfig, CardReaderModel cardReaderModel, CommunicationController communication, SecLevel secLevel) {
        super(App.instance);
        this.cardConfig = cardConfig;
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
        this.communication.setSecurityLevel(secLevel);
    }

    /**
     * The <code>Task</code> operation
     */
    @Override
    protected Void doInBackground() {
        logger.info("Authenticating card...");
        if (cardReaderModel.hasSelectedCardReaderItem()) {
            communication.authenticate(cardConfig);
            communication.getModel().setSecurityDomain(cardConfig, cardReaderModel.getCardChannel());
        } else logger.error("No card found");
        return null;
    }

    /**
     * End of the authentication task
     * If nextTask is set, it will be launched then
     *
     * @param nothing
     */
    @Override
    protected void succeeded(Void nothing) {
        logger.info("Authentication task thread end");
        if (nextTask != null) {
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
