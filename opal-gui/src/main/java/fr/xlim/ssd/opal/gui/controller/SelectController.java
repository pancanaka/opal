/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.SelectPanel;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Main controller of the select panel view
 * 
 * @author Tiana Razafindralambo
 * @author Estelle Blandinières
 */
public class SelectController {

    private static final CustomLogger logger= new CustomLogger();
    private SelectPanel selectPanel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication; 
    public SelectController(HomeView homeView, CardReaderModel cardReaderModel, CommunicationController communication)
    {
        this.selectPanel = homeView.getHomePanel().getSelectPanel();
        this.selectPanel.setController(this);
        this.cardReaderModel = cardReaderModel;
        this.selectPanel = homeView.getHomePanel().getSelectPanel();
        this.communication = communication;
    }
    
    /**
     * Call the applet selection command
     * 
     * @author Tiana Razafindralambo
     * 
     * @param APPLET_ID 
     */
    public void selectApplet(byte[] APPLET_ID) {
        logger.info("Selecting applet");
        this.communication.selectApplet(APPLET_ID);
    }

    /**
     * Check the fields of SelectPanel and call selectApplet function
     * @author Estelle Blandinieres
     * 
     * @param objectAID
     * @throws ConfigFieldsException
     */
    public void checkForm (String objectAID) throws ConfigFieldsException {
        checkObjectAID(objectAID);

        byte[] oAID = Conversion.hexToArray(objectAID);
        selectApplet(oAID);
    }

    /**
     * Check the object aid field
     * The field can be null, has to be an hexadecimal string,
     * and has to have a minimum size of 10
     *
     * @param objectAID, the object AID
     * @throws ConfigFieldsException
     */
    private void checkObjectAID(String objectAID) throws ConfigFieldsException {
        if (objectAID.length() > 0) {
            objectAID = objectAID.replaceAll(":", "");
            objectAID = objectAID.replaceAll(" ", "");

            if (objectAID.length() % 2 == 0 && objectAID.length() >= 10 && objectAID.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(objectAID);

                if (m.find()) {
                    throw new ConfigFieldsException("The AID has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Object AID is "
                        + "invalid.It must contain between 10 and 32 characters.\n");
            }
        } else {
            throw new ConfigFieldsException("Issuer Security DomainObject AID can't be empty.\n");
        }
    }

    public boolean isAuthenticated () {
        return communication.isAuthenticated();
    }
}
