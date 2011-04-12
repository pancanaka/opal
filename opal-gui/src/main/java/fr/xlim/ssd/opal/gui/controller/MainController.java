package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.communication.task.CardReaderTask;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;

/**
 * Application main controller.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public class MainController {

    private Application     application;
    private CardReaderTask  terminalTask;
    private HomeView        homeView;
    private CardReaderModel terminalModel;

    

    /**
     * Constructor.
     *
     * The constructor will:
     * <ul>
     *     <li>create the main view.</li>
     * </ul>
     *
     * @param application the application context
     * @see HomeView
     */
    public MainController(Application application) {
        this.application = application;
        
        this.homeView = new HomeView(application, this);
    }

    /**
     * Get the home view.
     *
     * @return the home view
     */
    public HomeView getHomeView() {
        return this.homeView;
    }

    /**
     * Start the terminal task.
     *
     * This <code>Task</code> instance will monitor terminals connected to the computer.
     * It updates the terminal list every time when a new terminal is plugged or removed.
     */
    public void startTerminalTask() {
        this.terminalTask = new CardReaderTask(this.application, this.terminalModel);

        ApplicationContext context = this.application.getContext();

        TaskMonitor monitor = context.getTaskMonitor();
        TaskService service = context.getTaskService();
        service.execute(this.terminalTask);
        monitor.setForegroundTask(this.terminalTask);
    }

    /**
     * Stop the terminal task.
     *
     * It is the way to stop the thread which listen for plugged terminals.
     */
    public void stopTerminalTask() {
        this.terminalTask.cancel(true);
    }
}
