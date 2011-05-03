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
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class TaskFactory {
    private static final Logger logger = LoggerFactory.getLogger(TaskFactory.class);

    public static ApplicationContext currentContext;
    public static TaskMonitor monitor = null;
    public static TaskService service = null; 

    public TaskFactory(){};
    public static TaskFactory run(Task task)
    {
        TaskFactory.currentContext = App.instance.getContext();
        TaskFactory.monitor = TaskFactory.currentContext.getTaskMonitor();
        TaskFactory.service = TaskFactory.currentContext.getTaskService();
        TaskFactory.service.execute(task);
        
        TaskFactory.monitor.setForegroundTask(task); 
        return new TaskFactory();
    }
    public static void test()
    {

    }
}
