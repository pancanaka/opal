
package fr.xlim.ssd.opal.gui.communication.task;

import org.jdesktop.application.Task;

/**
 *
 * @author Tiana Razafindralambo
 */
public interface TaskInterface {
    public void doThen(Task task);
    public void nextTask(Task task); 
}
