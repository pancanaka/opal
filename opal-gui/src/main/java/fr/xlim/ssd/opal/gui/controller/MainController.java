package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.communication.task.CardReaderTask; 
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.library.SecLevel; 
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import org.slf4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import org.slf4j.LoggerFactory;

/**
 * Application main controller.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 * @author Tiana Razafindralambo
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private Application application;
    private CardReaderModel cardReaderModel;
    private AuthenticationController authController;

    //AppleController is public just for applet installation process TEST
    public AppletController appletController;
    //------------------------------ 
    
    private DeleteController deleteController;
    private SelectController selectController;
    private CommunicationController communication;
    private HomeView homeView;
    private CardReaderTask cardReaderTask;
    private ProfileController profileController;
    

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

        this.homeView = new HomeView(this.application, this);
        
        this.communication = new CommunicationController(SecLevel.C_MAC, this);

        try
        {
            this.profileController = new ProfileController();
        }
        catch (CardConfigNotFoundException ex) {
           logger.error(ex.getMessage());
        }
        
        this.authController = new AuthenticationController(this.cardReaderModel, this.communication, this.profileController, this.homeView);

        this.appletController = new AppletController(this.homeView, this.cardReaderModel, this.communication);

        this.deleteController = new DeleteController(this.homeView, this.cardReaderModel, this.communication);

        this.selectController = new SelectController(this.homeView, this.cardReaderModel, this.communication);
        
        this.startTerminalTask();

        TestMyCard();
    }

    public void TestMyCard()
    {
        logger.info("Testing my card .... ");
        this.authController.testAuthenticationProcess();
    }

    /**
     * Get the home view.
     *
     * @return the home view
     */
    public HomeView getHomeView() {
        return this.homeView;
    }

    public ProfileController getProfileController()
    {
        return this.profileController;
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
