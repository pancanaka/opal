package fr.xlim.ssd.opal.gui.tasks;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Fox
 * Date: 18/07/11
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class MyTask extends Task<Integer, Integer> {

    public MyTask(Application app) {
        super(app);
    }

    @Override
    protected Integer doInBackground() throws Exception {
        Thread.sleep(1000);

        return new Integer(10);
    }

    @Override
    protected void succeeded(Integer value) {
        System.out.println(value);
    }
}
