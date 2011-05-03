package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.communication.task.AuthenticationTask;
import fr.xlim.ssd.opal.gui.communication.task.TaskFactory;
import fr.xlim.ssd.opal.gui.model.Authentication.AuthenticationModel;

import fr.xlim.ssd.opal.gui.model.Key.KeyModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tiana Razafindralambo
 */
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private AuthenticationModel authModel;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;
    private ProfileController profileController;
    private AuthenticationPanel authenticationPanel;
    private boolean defaultCardConfigIsSet = false;

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

    public void authenticateCard(CardConfig cardConfig, SecLevel secLevel) {
        logger.info("Proceed to authentication");
        AuthenticationTask authenticationTask = new AuthenticationTask(cardConfig, this.cardReaderModel, this.communication, secLevel);
        TaskFactory taskFactory = TaskFactory.run(authenticationTask);

        //this.authModel.setCommunication(cardConfig);
        /*final CardConfig _cf = cardConfig;

        logger.info("Authenticating card...");
        this.cardReaderModel.addCardReaderStateListener(new CardReaderStateListener() {
        @Override
        public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
        if(cardReaderModel.hasSelectedCardReaderItem())
        {
        communication.authenticate(_cf);
        communication.getModel().setSecurityDomain(_cf, cardReaderModel.getCardChannel());

        }else logger.error("No card found.");
        cardReaderModel.removeCardReaderStateListener(this);
        }
        });*/
    }

    /**
     *  Get all profiles
     * @return all profiles loaded.
     */
    public String[][] getAllProfiles() {
        return this.authModel.getAllProfiles();
    }

    public String[] getAllProfileNames() {
        return this.authModel.getAllProfileNames();
    }

    public String getCurrentCardDefaultProfileName() {
        return this.authModel.getDefaultCardConfig().getName();
    }

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

    public CardConfig getCardConfigOf(ProfileComponent profile) {
        return profile.convertToCardConfig();
    }

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

    private void checkForm(ProfileComponent p, String securityLevel)
            throws ConfigFieldsException {

        checkAID(p.getAID());
        checkSCP(p.getSCPmode());
        checkTP(p.getTP());
        checkImpl(p.getImplementation());
        checkKeys(p.getKeys());
        checkSecurityLevel(securityLevel);
    }

    private void checkAID(String aid) throws ConfigFieldsException {
        System.out.println(aid);
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

    private void checkSCP(String scp) throws ConfigFieldsException {
        System.out.println(scp);
        if(scp.length() > 0) {
            if(scp.compareToIgnoreCase(SCPMode.SCP_01_05.toString()) != 0
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

    private void checkTP(String tp) throws ConfigFieldsException {
        System.out.println(tp);
        if(tp.length() > 0) {
            if(tp.compareToIgnoreCase("T=0") != 0
                    && tp.compareToIgnoreCase("T=1") != 0
                    && tp.compareToIgnoreCase("*") != 0) {
                throw new ConfigFieldsException("The Transmission Protocol is unknown.\n");
            }
        } else {
            throw new ConfigFieldsException("Transmission Protocol can't be empty.\n");
        }
    }

    private void checkImpl(String impl) throws ConfigFieldsException {
        System.out.println(impl);
        if(impl.length() > 0) {
            if(impl.compareToIgnoreCase("fr.xlim.ssd.opal.library.commands."+"GemXpresso211Commands") != 0
                    && impl.compareToIgnoreCase("fr.xlim.ssd.opal.library.commands."+"GP2xCommands") != 0
                    /* If new implementations are allowed : && impl.compareToIgnoreCase("*Implementation*") != 0  */) {
                throw new ConfigFieldsException("The Implementation is unknown.\n");
            }
        }
        else {
            throw new ConfigFieldsException("Implementation can't be empty.\n");
        }
    }

    private void checkKeys(ArrayList<KeyModel> keys) throws ConfigFieldsException {
        int n = keys.size();

        for(int i = 0; i < n; i++) {
            checkKey(keys.get(i));
        }
    }

    private void checkKey(KeyModel key) throws ConfigFieldsException {
        Pattern p1 = Pattern.compile("[^0-9]+");
        Matcher m = p1.matcher(key.keyID);
             //key.put("type", Keylist.get(i).getType());

        if(!m.find()) {
            String id = key.keyID;
            if(Integer.valueOf(id) < Integer.MAX_VALUE) {
                String version = key.version;
                m = p1.matcher(version);

                if(!m.find()) {
                    if(Integer.valueOf(version) >= 0 && Integer.valueOf(version) < 256) {
                        String value = key.key;
                        value = value.replaceAll(":", "");
                        value = value.replaceAll(" ", "");

                        System.out.println(key.type);

                        if(value.length() % 2 == 0 &&  value.length() < 64) {
                            p1 = Pattern.compile("[^0-9A-F]+", Pattern.CASE_INSENSITIVE);
                            m = p1.matcher(value);

                            if(!m.find()) {
                                //DES_ECB, DES_CBC, SCGemVisa, SCGemVisa2 et d’AES : vérifier la taille de la clé de chaque algo
                            }
                            else {
                                throw new ConfigFieldsException("Key values have to be an hexadecimal string. You can write it in different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                            }
                        }
                        else {
                            throw new ConfigFieldsException("Key value is invalid (maximum length: 192 bytes - 24 characters).");
                        }

                    }
                    else {
                        throw new ConfigFieldsException("Key versions must be between 0 and 255.");
                    }
                }
                else {
                    throw new ConfigFieldsException("Key versions must be numeric.");
                }
            }
            else {
                throw new ConfigFieldsException("Key IDs must be numeric.");
            }
        }
        else {
            throw new ConfigFieldsException("Key IDs must be numeric.");
        }
    }

    private void checkSecurityLevel (String sl) throws ConfigFieldsException {
        System.out.println(sl);
        if(sl.length() > 0) {
            if(sl.compareToIgnoreCase(SecLevel.NO_SECURITY_LEVEL.toString()) != 0
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

    /*public void testAuthenticationProcessAndAppletInstallationThreadMode(final AppletController _appletController)
    {
    logger.info("Test Authentication Process launched");
    /// applet ID of hello world CAP

    this.cardReaderModel.addCardReaderStateListener( new CardReaderStateListener() {
    @Override
    public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
    if(cardReaderModel.hasSelectedCardReaderItem())
    {
    logger.info("Card Name : " +  cardReaderModel.getSelectedCardName());
    logger.info("Card ATR : " + Conversion.arrayToHex(cardReaderModel.getSelectedCardATR().getValue()));

    try
    {
    CardConfig cardConfig = null;
    cardConfig = authModel.getCardConfigByATR(cardReaderModel.getSelectedCardATR());
    authenticateCard(cardConfig);
    //appletController.installApplet(PACKAGE_ID, APPLET_ID, ressource);
    }
    catch(CardConfigNotFoundException ex) { logger.error(ex.getMessage()); }
    }else logger.info("No card found");
    cardReaderModel.removeCardReaderStateListener(this);
    }
    });
    }*/
    /*public void testAuthenticationProcess()
    {
    logger.info("Test Authentication Process launched");
    this.cardReaderModel.addCardReaderStateListener( new CardReaderStateListener() {
    @Override
    public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
    if(cardReaderModel.hasSelectedCardReaderItem())
    {
    logger.info("Card Name : " +  cardReaderModel.getSelectedCardName());
    logger.info("Card ATR : " + Conversion.arrayToHex(cardReaderModel.getSelectedCardATR().getValue()));

    try
    {
    CardConfig cardConfig = null;
    cardConfig = authModel.getCardConfigByATR(cardReaderModel.getSelectedCardATR());
    authenticateCard(cardConfig);
    }
    catch(CardConfigNotFoundException ex) { logger.error(ex.getMessage()); }
    }else logger.info("No card found");
    cardReaderModel.removeCardReaderStateListener(this);
    }
    });
    } */
}
