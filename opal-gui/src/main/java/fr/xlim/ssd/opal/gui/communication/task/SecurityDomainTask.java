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

import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.securityDomain.SecurityDomainModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 * @author Tiana Razafindralambo
 */
public class SecurityDomainTask extends Task<Void, Void> {

    private static final CustomLogger logger = new CustomLogger();
    private SecurityDomainModel secModel;

    /**
     * Default constructor
     *
     * @param application
     * @param secModel
     */
    public SecurityDomainTask(Application application, SecurityDomainModel secModel) {
        super(application);
        this.secModel = secModel;
    }

    /**
     * The <code>Task</code> operation
     */
    @Override
    protected Void doInBackground() {
        message("Security domain task started");
        while (!isCancelled()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                if (!isCancelled()) logger.debug("Error while sleeping", ie);
            }
        }
        message("Security domain task finished");
        return null;
    }
}
