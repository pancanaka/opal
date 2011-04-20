package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.communication.task.CardReaderTask;
import fr.xlim.ssd.opal.gui.model.Authentication.AuthenticationModel;
import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
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

    private Application application;
    private CardReaderModel cardReaderModel;
    private AuthenticationController authController;
    private AuthenticationModel authModel;
    private CommunicationModel communication;
    private HomeView homeView;
    private CardReaderTask cardReaderTask;

    

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

        this.cardReaderModel = new CardReaderModel();

        this.communication = new CommunicationModel(SecLevel.C_MAC);

        this.authController = new AuthenticationController(this.cardReaderModel, this.communication);
        
        this.startTerminalTask();
        
        this.homeView = new HomeView(this.application, this);
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
     * Get the card reader model.
     *
     * @return the card reader model
     */
    public CardReaderModel getCardReaderModel() {
        return this.cardReaderModel;
    }

    /**
     * Start the terminal task.
     *
     * This <code>Task</code> instance will monitor terminals connected to the computer.
     * It updates the terminal list every time when a new terminal is plugged or removed.
     */
    public void startTerminalTask() {
        this.cardReaderTask = new CardReaderTask(this.application,  this.cardReaderModel);

        ApplicationContext context = this.application.getContext();

        TaskMonitor monitor = context.getTaskMonitor();
        TaskService service = context.getTaskService();
        service.execute(this.cardReaderTask);
        monitor.setForegroundTask(this.cardReaderTask);
    }

    /**
     * Stop the terminal task.
     *
     * It is the way to stop the thread which listen for plugged terminals.
     */
    public void stopTerminalTask() {
        this.cardReaderTask.cancel(true);
    }
}
