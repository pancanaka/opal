 
package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.securityDomain.SecurityDomainModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.DeletePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author razaina
 */
public class DeleteController {

    private static final Logger logger = LoggerFactory.getLogger(AppletController.class);
    private DeletePanel deletePanel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    public DeleteController(HomeView homeView, CardReaderModel cardReaderModel, CommunicationController communication)
    {
        this.deletePanel = homeView.getHomePanel().getDeletePanel();
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
    }
    public void deleteApplet(byte[] APPLET_ID, byte[] PACKAGE_ID)
    {
        logger.info("Deleting Applet");

        SecurityDomainModel securityDomainModel = this.communication.getModel().getSecurityDomainModel();

        if(this.communication.hasDomain())
            if(this.communication.isAuthenticated())
                this.communication.deleteApplet(APPLET_ID, PACKAGE_ID);
            else logger.error("Card isn't authenticated");
        else logger.error("Security domain isn't set yet");
    }
}
