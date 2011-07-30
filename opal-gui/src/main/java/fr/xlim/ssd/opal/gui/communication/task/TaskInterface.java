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

import org.jdesktop.application.Task;

/**
 * @author Tiana Razafindralambo
 */
public interface TaskInterface {
    public void doThen(Task task);

    public void nextTask(Task task);
}
