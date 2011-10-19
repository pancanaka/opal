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

import fr.xlim.ssd.opal.gui.communication.task.AuthenticationTask;
import fr.xlim.ssd.opal.gui.communication.task.TaskFactory;
import fr.xlim.ssd.opal.gui.model.Authentication.AuthenticationModel;
import fr.xlim.ssd.opal.gui.model.Key.KeyModel;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.gui.view.components.tab.AuthenticationPanel;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main controller for the authentication view
 *
 * @author Tiana Razafindralambo
 * @author Estelle Blandinières
 */
public class AuthenticationController {

    private static final CustomLogger logger = new CustomLogger();
    private AuthenticationModel authModel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    private ProfileController profileController;
    private AuthenticationPanel authenticationPanel;
    private boolean defaultCardConfigIsSet = false;

    /**
     * Default constructor
     *
     * @param cardReaderModel
     * @param communication
     * @param profileController
     * @param homeView
     * @author Tiana Razafindralambo
     */
    public AuthenticationController(CardReaderModel cardReaderModel, CommunicationController communication, ProfileController profileController, HomeView homeView) {
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
        this.profileController = profileController;
        this.authenticationPanel = homeView.getHomePanel().getAuthenticationPanel();
        this.authenticationPanel.setController(this);
        this.authModel = new AuthenticationModel(this.cardReaderModel, this.communication.getModel(), this.profileController);
        if (!defaultCardConfigIsSet) {
            this.setDefaultCardConfig();
        }
    }

    /**
     * Default card config setter
     *
     * @author Tiana Razafindralambo
     */
    private void setDefaultCardConfig() {
        this.cardReaderModel.addCardReaderStateListener(new CardReaderStateListener() {

            @Override
            public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
                if (cardReaderModel.hasSelectedCardReaderItem()) {
                    logger.info("Default Card selected Name : " + cardReaderModel.getSelectedCardName());
                    logger.info("Default Card selected ATR : " + Conversion.arrayToHex(cardReaderModel.getSelectedCardATR().getValue()));

                    try {
                        CardConfig cardConfig = null;
                        cardConfig = authModel.getCardConfigByATR(cardReaderModel.getSelectedCardATR());

                        logger.info("Setting default card config");
                        authModel.setDefaultCardConfig(cardConfig);
                    } catch (CardConfigNotFoundException ex) {
                        logger.error(ex.getMessage());
                    }
                } else {
                    logger.info("No card found");
                }
                cardReaderModel.removeCardReaderStateListener(this);
            }
        });
    }

    /**
     * Launch the authentication task
     *
     * @param cardConfig
     * @param secLevel
     * @author Tiana Razafindralambo
     */
    public void authenticateCard(CardConfig cardConfig, SecLevel secLevel) {
        logger.info("Proceed to authentication");
        AuthenticationTask authenticationTask = new AuthenticationTask(cardConfig, this.cardReaderModel, this.communication, secLevel);
        TaskFactory taskFactory = TaskFactory.run(authenticationTask);
    }

    /**
     * Get all profiles
     *
     * @return all profiles loaded.
     * @author Tiana Razafindralambo
     */
    public String[][] getAllProfiles() {
        return this.authModel.getAllProfiles();
    }

    /**
     * Get all profile names
     *
     * @return all the profiles names
     * @author Tiana Razafindralambo
     */
    public String[] getAllProfileNames() {
        return this.authModel.getAllProfileNames();
    }

    /**
     * Get the current default card profile name
     *
     * @return name of the current default card profile name
     * @author Tiana Razafindralambo
     */
    public String getCurrentCardDefaultProfileName() {
        return this.authModel.getDefaultCardConfig().getName();
    }

    /**
     * Get the profile by name
     *
     * @param name
     * @return <code>Object</code> ProfileComponent
     * @author Tiana Razafindralambo
     */
    public ProfileComponent getProfileByName(String name) {
        ProfileComponent currProfile = this.authModel.getProfileByName(name);

        //Create a new profile component that you can use as you want
        //without to modify the current selected profile
        ProfileComponent profile = new ProfileComponent(currProfile.getName(),
                currProfile.getDescription(),
                currProfile.getAID(),
                currProfile.getSCPmode(),
                currProfile.getTP(),
                currProfile.getATR(),
                currProfile.getImplementation());
        profile.setKeys(currProfile.getKeys());
        return profile;
    }

    /**
     * Get the card config of a profile
     *
     * @param profile
     * @return <code>Object</code> CardConfig
     * @author Tiana Razafindralambo
     */
    public CardConfig getCardConfigOf(ProfileComponent profile) {
        return profile.convertToCardConfig();
    }

    /**
     * Proceed to the card authentication
     *
     * @param p             profile
     * @param securityLevel
     * @throws CardConfigNotFoundException
     * @throws ConfigFieldsException
     * @author Tiana Razafindralambo
     */
    public void authenticate(ProfileComponent p, String securityLevel)
            throws CardConfigNotFoundException, ConfigFieldsException {
        checkForm(p, securityLevel);

        CardConfig cardConfig = getCardConfigOf(p);

        SecLevel secLevel = SecLevel.NO_SECURITY_LEVEL;
        if (securityLevel.compareTo(SecLevel.C_MAC.toString()) == 0) {
            secLevel = SecLevel.C_MAC;
        } else if (securityLevel.compareTo(SecLevel.C_ENC_AND_MAC.toString()) == 0) {
            secLevel = SecLevel.C_ENC_AND_MAC;
        } else if (securityLevel.compareTo(SecLevel.R_MAC.toString()) == 0) {
            secLevel = SecLevel.R_MAC;
        } else if (securityLevel.compareTo(SecLevel.C_MAC_AND_R_MAC.toString()) == 0) {
            secLevel = SecLevel.C_MAC_AND_R_MAC;
        } else if (securityLevel.compareTo(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC.toString()) == 0) {
            secLevel = SecLevel.C_ENC_AND_C_MAC_AND_R_MAC;
        } else if (securityLevel.compareTo(SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC.toString()) == 0) {
            secLevel = SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC;
        }
        authenticateCard(cardConfig, secLevel);
    }

    /**
     * @param p
     * @param securityLevel
     * @throws ConfigFieldsException
     * @author Estelle Blandinières
     */
    private void checkForm(ProfileComponent p, String securityLevel)
            throws ConfigFieldsException {

        checkAID(p.getAID());
        checkSCP(p.getSCPmode());
        checkTP(p.getTP());
        checkImpl(p.getImplementation());
        checkKeys(p.getKeys());
        checkSecurityLevel(securityLevel);
    }

    /**
     * @param aid
     * @throws ConfigFieldsException
     * @author Estelle Blandinières
     */
    private void checkAID(String aid) throws ConfigFieldsException {
        if (aid.length() > 0) {
            aid = aid.replaceAll(":", "");
            aid = aid.replaceAll(" ", "");

            if (aid.length() % 2 == 0 && aid.length() >= 10 && aid.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(aid);

                if (m.find()) {
                    throw new ConfigFieldsException("The AID has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Issuer Security Domain AID is "
                        + "invalid.It must contain between 10 and 32 characters.\n");
            }
        } else {
            throw new ConfigFieldsException("Issuer Security Domain AID can't be empty.\n");
        }
    }

    /**
     * @param scp
     * @throws ConfigFieldsException
     * @author Estelle Blandinières
     */
    private void checkSCP(String scp) throws ConfigFieldsException {
        System.out.println(scp);
        if (scp.length() > 0) {
            if (scp.compareToIgnoreCase(SCPMode.SCP_01_05.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_01_05.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_01_15.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_04.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_05.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_0A.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_0B.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_14.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_15.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_1A.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_1B.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_55.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_45.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_02_54.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_65.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_6D.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_05.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_0D.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_2D.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_03_25.toString()) != 0
                    && scp.compareToIgnoreCase(SCPMode.SCP_10.toString()) != 0) {

                throw new ConfigFieldsException("The SCPMode is unknown.\n");
            }
        } else {
            throw new ConfigFieldsException("SCPMode can't be empty.\n");
        }
    }

    /**
     * @param tp
     * @throws ConfigFieldsException
     * @author Estelle Blandinières
     */
    private void checkTP(String tp) throws ConfigFieldsException {
        System.out.println(tp);
        if (tp.length() > 0) {
            if (tp.compareToIgnoreCase("T=0") != 0
                    && tp.compareToIgnoreCase("T=1") != 0
                    && tp.compareToIgnoreCase("*") != 0) {
                throw new ConfigFieldsException("The Transmission Protocol is unknown.\n");
            }
        } else {
            throw new ConfigFieldsException("Transmission Protocol can't be empty.\n");
        }
    }

    /**
     * @param impl
     * @throws ConfigFieldsException
     * @author Estelle Blandinières
     */
    private void checkImpl(String impl) throws ConfigFieldsException {
        System.out.println(impl);
        if (impl.length() > 0) {
            if (impl.compareToIgnoreCase("fr.xlim.ssd.opal.library.commands." + "GemXpresso211Commands") != 0
                    && impl.compareToIgnoreCase("fr.xlim.ssd.opal.library.commands." + "GP2xCommands") != 0
                /* If new implementations are allowed : && impl.compareToIgnoreCase("*Implementation*") != 0  */) {
                throw new ConfigFieldsException("The Implementation is unknown.\n");
            }
        } else {
            throw new ConfigFieldsException("Implementation can't be empty.\n");
        }
    }

    /**
     * @param keys
     * @throws ConfigFieldsException
     * @author Estelle Blandinières
     */
    private void checkKeys(ArrayList<KeyModel> keys) throws ConfigFieldsException {
        int n = keys.size();

        for (int i = 0; i < n; i++) {
            checkKey(keys.get(i), ++i);
        }
    }

    /**
     * @param key
     * @param index
     * @throws ConfigFieldsException
     * @author Estelle Blandinières
     */
    private void checkKey(KeyModel key, int index) throws ConfigFieldsException {
        Pattern p1 = Pattern.compile("[^0-9]+");
        Matcher m = p1.matcher(key.keyID);

        if (!m.find()) {
            String id = key.keyID;
            if (Integer.valueOf(id) < Integer.MAX_VALUE) {
                String version = key.version;
                m = p1.matcher(version);

                if (!m.find()) {
                    if (Integer.valueOf(version) >= 0 && Integer.valueOf(version) < 256) {
                        String value = key.key;
                        value = value.replaceAll(":", "");
                        value = value.replaceAll(" ", "");

                        if (value.length() % 2 == 0 && value.length() < 64) {
                            p1 = Pattern.compile("[^0-9A-F]+", Pattern.CASE_INSENSITIVE);
                            m = p1.matcher(value);

                            if (!m.find()) {
                                if (key.type.compareTo("83") == 0
                                        || key.type.compareTo("84") == 0
                                        || key.type.compareTo("0") == 0
                                        || key.type.compareTo("1") == 0
                                        || key.type.compareTo("88") == 0) {

                                    if ((key.type.compareTo("83") == 0
                                            || key.type.compareTo("0") == 0
                                            || key.type.compareTo("1") == 0)
                                            && value.length() != 48) {

                                        throw new ConfigFieldsException("Key value at index " + index + " must contain 48 hex characters (192 bits).");
                                    }

                                    if ((key.type.compareTo("84") == 0
                                            || key.type.compareTo("88") == 0)
                                            && value.length() != 32) {

                                        throw new ConfigFieldsException("Key value at index " + index + " must contain 32 hex characters (128 bits).");
                                    }
                                } else {
                                    throw new ConfigFieldsException("Invalid key type at index " + index + ".");
                                }
                            } else {
                                throw new ConfigFieldsException("All key values have to be an hexadecimal string. You can write it in different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                            }
                        } else {
                            throw new ConfigFieldsException("Key value at index " + index + " is invalid (maximum length: 192 bytes - 48 hex characters).");
                        }

                    } else {
                        throw new ConfigFieldsException("Key versions at index " + index + " must be between 0 and 255.");
                    }
                } else {
                    throw new ConfigFieldsException("Key versions at index " + index + " must be numeric.");
                }
            } else {
                throw new ConfigFieldsException("All key IDs must be numeric.");
            }
        } else {
            throw new ConfigFieldsException("All key IDs must be numeric.");
        }
    }

    /**
     * @param sl
     * @throws ConfigFieldsException
     * @author Estelle Blandinières
     */
    private void checkSecurityLevel(String sl) throws ConfigFieldsException {
        System.out.println(sl);
        if (sl.length() > 0) {
            if (sl.compareToIgnoreCase(SecLevel.NO_SECURITY_LEVEL.toString()) != 0
                    && sl.compareToIgnoreCase(SecLevel.C_MAC.toString()) != 0
                    && sl.compareToIgnoreCase(SecLevel.C_ENC_AND_MAC.toString()) != 0
                    && sl.compareToIgnoreCase(SecLevel.R_MAC.toString()) != 0
                    && sl.compareToIgnoreCase(SecLevel.C_MAC_AND_R_MAC.toString()) != 0
                    && sl.compareToIgnoreCase(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC.toString()) != 0
                    && sl.compareToIgnoreCase(SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC.toString()) != 0) {

                throw new ConfigFieldsException("The security level is unknown.\n");
            }
        } else {
            throw new ConfigFieldsException("Security level can't be empty.\n");
        }
    }
}
