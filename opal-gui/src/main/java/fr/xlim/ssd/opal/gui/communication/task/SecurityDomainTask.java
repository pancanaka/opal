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

import fr.xlim.ssd.opal.gui.model.securityDomain.SecurityDomainModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class SecurityDomainTask extends Task<Void, Void> {

    private static final Logger logger = LoggerFactory.getLogger(SecurityDomainTask.class);
    private SecurityDomainModel secModel;
    public SecurityDomainTask(Application application, SecurityDomainModel secModel)
    {
        super(application);
        this.secModel = secModel;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Void doInBackground(){ 
        message("Security domain task started");
        while(!isCancelled())
        {
            if(this.secModel.hasDomain()) logger.debug("_____SecModel has domain.");
            try
            {
                Thread.sleep(2000);
            }
            catch(InterruptedException ie)
            {
                if(!isCancelled()) logger.debug("Error while sleeping", ie);
            }
        }
        message("Security domain task finished");
        return null;
    }
}
