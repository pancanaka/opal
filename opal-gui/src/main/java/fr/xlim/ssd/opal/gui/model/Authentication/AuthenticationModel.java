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
package fr.xlim.ssd.opal.gui.model.Authentication;

import fr.xlim.ssd.opal.gui.controller.ProfileController;
import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.ProfileModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;

/**
 * The model containing informations needed to authenticate the card
 * <p/>
 * The security domain used to communicate with the card is set using the
 * communication model.
 *
 * @author Tiana Razafindralambo
 */
public class AuthenticationModel {

    private static final CustomLogger logger = new CustomLogger();
    private CardReaderModel cardReaderModel;
    private CommunicationModel communication;
    private CardConfig cardConfig;
    private CardReaderStateListener cardReaderStateListener;
    private ProfileModel profile;
    private String[][] profiles;

    public AuthenticationModel() {
    }

    public AuthenticationModel(CardReaderModel crm, CommunicationModel communication, ProfileController profileController) {
        this.cardReaderModel = crm;
        this.communication = communication;

        this.profile = profileController.getProfileModel();
        loadAllProfile();
    }

    /**
     * Load all profiles (cf. config.xml)
     */
    private void loadAllProfile() {
        logger.info("Getting all profiles ...");

        this.profiles = this.profile.getAllProfiles();
        int profilesLength = this.profiles.length;

        logger.info(profilesLength + " profiles has been loaded.");

        /*for(int i = 0; i < profilesLength; i++)
        {
            logger.debug(profiles[i][0]);
        }*/
    }

    public String[] getAllProfileNames() {
        String[] res = new String[this.profiles.length];

        for (int i = 0; i < this.profiles.length; i++)
            res[i] = this.profiles[i][0];
        return res;
    }

    /**
     * Get all profiles
     *
     * @return all profiles loaded.
     */
    public String[][] getAllProfiles() {
        return this.profiles;
    }

    public ProfileComponent getProfileByName(String name) {
        return this.profile.getProfileByName(name);
    }

    /**
     * Get the card configuration
     * <p/>
     * It can be null.
     *
     * @return CardConfig
     */
    public CardConfig getDefaultCardConfig() {
        return this.cardConfig;
    }

    public void setDefaultCardConfig(CardConfig cardConfig) {
        this.cardConfig = cardConfig;
    }

    /**
     * Get the card configuration using its ATR's value
     *
     * @param atr
     * @return CardConfig
     * @throws CardConfigNotFoundException
     */
    public CardConfig getCardConfigByATR(ATR atr) throws CardConfigNotFoundException {
        return CardConfigFactory.getCardConfig(atr.getValue());
    }

    /**
     * Set the card reader model
     *
     * @param crm
     */
    public void setCardReaderModel(CardReaderModel crm) {
        this.cardReaderModel = crm;
    }
}
