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

import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.DeletePanel;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main controller of the Delete (panel) view
 *
 * @author Tiana Razafindralambo
 * @author Estelle Blandinières
 */
public class DeleteController {

    private static final CustomLogger logger = new CustomLogger();
    private DeletePanel deletePanel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;

    public DeleteController(HomeView homeView, CardReaderModel cardReaderModel, CommunicationController communication) {
        this.deletePanel = homeView.getHomePanel().getDeletePanel();
        this.deletePanel.setController(this);
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
    }

    /**
     * Call the delete command
     *
     * @param AID
     * @param cascade
     * @author Tiana Razafindralambo
     */
    public void delete(byte[] AID, boolean cascade) {
        logger.info("Deleting Applet");
        this.communication.delete(AID, cascade);
    }

    /**
     * Check the fields of DeletePanel and call delete function
     *
     * @param AID,     the object aid
     * @param cascade, boolean wich indicate if cascade mode is choosen
     * @throws ConfigFieldsException
     */
    public void checkForm(String AID, boolean cascade) throws ConfigFieldsException {
        checkAID(AID);
        byte[] ObjectAID = Conversion.hexToArray(AID);

        delete(ObjectAID, cascade);
    }

    /**
     * Check the object aid field
     * The field can be null, has to be an hexadecimal string,
     * and has to have a minimum size of 10
     *
     * @param AID, the object AID
     * @throws ConfigFieldsException
     */
    public void checkAID(String AID) throws ConfigFieldsException {
        if (AID.length() > 0) {
            AID = AID.replaceAll(":", "");
            AID = AID.replaceAll(" ", "");

            if (AID.length() % 2 == 0 && AID.length() >= 10 && AID.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(AID);

                if (m.find()) {
                    throw new ConfigFieldsException("The Object AID has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Object AID is "
                        + "invalid.It must contain between 10 and 32 characters.\n");
            }
        } else {
            throw new ConfigFieldsException("Object AID can't be empty.\n");
        }
    }

    public boolean isAuthenticated() {
        return communication.isAuthenticated();
    }
}
