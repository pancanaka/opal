/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : David Pequegnot <david.pequegnot@etu.unilim.fr>                  *
 *           Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>       *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.controller.send.SendApduController;
import fr.xlim.ssd.opal.gui.communication.task.CardReaderTask; 
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView; 
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
    private AppletController appletController;
    private DeleteController deleteController;
    private SelectController selectController;
    public SendApduController sendApduController;
    private CommunicationController communication;
    private HomeView homeView;
    private CardReaderTask cardReaderTask;
    //private CardSenderTask cardSenderTask;
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
        this.communication = new CommunicationController();
        this.sendApduController = new SendApduController(cardReaderModel, communication);
        this.homeView = new HomeView(this.application, this);
        
        //this.communication = new CommunicationController(SecLevel.C_MAC);
        

        //this.com = new Communication();

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

        this.selectController = new SelectController(this.homeView,this.cardReaderModel,this.communication);

        this.selectController = new SelectController(this.homeView, this.cardReaderModel, this.communication);

       

        //this.startTerminalTask();
       // TestMyCard();

        
        this.startTerminalTask();  

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
     * Get the authentication controller
     *
     * @return the authentication controller
     */
    public AuthenticationController getAuthenticationController(){
        return this.authController;
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
