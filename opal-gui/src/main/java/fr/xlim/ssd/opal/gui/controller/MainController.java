/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.communication.task.CardReaderTask;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView; 
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;


/**
 * Application main controller.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 * @author Tiana Razafindralambo
 */
public class MainController {

    private static final CustomLogger logger= new CustomLogger();
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

        try {
            this.profileController = new ProfileController();
        } catch (CardConfigNotFoundException ex) {
           new JOptionPane().showMessageDialog(null, ex.getMessage(), "Caution", JOptionPane.WARNING_MESSAGE);
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
