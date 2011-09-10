/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 *          Estelle Blandinières  <estelle.blandinieres@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.communication.task.AppletInstallationTask;
import fr.xlim.ssd.opal.gui.communication.task.TaskFactory;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.AppletPanel;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Main controller for the applet installation view
 *
 * @author Tiana Razafindralambo
 * @author Estelle Blandinières
 */
public class AppletController {


    private static final CustomLogger logger = new CustomLogger();
    private AppletPanel appletPanel;
    private CardReaderStateListener cardReaderStateListener;
    private CardReaderModel cardReaderModel;
    private CommunicationController communication;

    /**
     * Default constructor
     *
     * @param homeView        main view
     * @param cardReaderModel
     * @param communication   controller that allows to communicate with the card
     * @author Tiana Razafindralambo
     */
    public AppletController(HomeView homeView, CardReaderModel cardReaderModel, CommunicationController communication) {
        this.appletPanel = homeView.getHomePanel().getAppletPanel();
        this.appletPanel.setController(this);
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
    }

    /**
     * Launch the applet installation task in a thread
     *
     * @param PACKAGE_ID
     * @param MODULE_AID
     * @param APPLET_ID
     * @param ressource
     * @param securityDomainAID
     * @param params4Install4load
     * @param maxDataLength
     * @param privileges
     * @param paramsInstall4Install
     * @param reorderCapFileComponents
     * @author Tiana Razafindralambo
     */
    public void installApplet(byte[] PACKAGE_ID, byte[] MODULE_AID, byte[] APPLET_ID, String ressource, byte[] securityDomainAID, byte[] params4Install4load, byte maxDataLength, byte[] privileges, byte[] paramsInstall4Install, boolean reorderCapFileComponents) {
        logger.info("Installing Applet");
        AppletInstallationTask appletInstallationTask = new AppletInstallationTask(PACKAGE_ID, MODULE_AID, APPLET_ID, ressource, securityDomainAID, params4Install4load, maxDataLength, privileges, paramsInstall4Install, this.communication, reorderCapFileComponents);
        TaskFactory taskFactory = TaskFactory.run(appletInstallationTask);
    }

    /**
     * Check all fields of the applet panel and call
     * the installApplet function
     *
     * @param packageAID
     * @param instanceAID
     * @param appletAID
     * @param ressource
     * @param securityDomainAID
     * @param params4Install4load
     * @param maxDataLength
     * @param privileges
     * @param paramsInstall4Install
     * @param reorderCapFileComponents
     */
    public void checkForm(String packageAID, String instanceAID, String appletAID,
                          String ressource, String securityDomainAID, String params4Install4load,
                          String maxDataLength, String privileges, String paramsInstall4Install,
                          boolean reorderCapFileComponents) throws ConfigFieldsException {

        checkRessource(ressource);
        checkPackageAID(packageAID);
        checkSecurityDomain(securityDomainAID);
        checkParam(params4Install4load);
        checkMaxDataLength(maxDataLength);

        checkAppletAID(appletAID);
        checkInstanceAID(instanceAID);
        checkParam(paramsInstall4Install);
        checkPrivileges(privileges);


        byte[] pAID = Conversion.hexToArray(packageAID);
        byte[] aAID = Conversion.hexToArray(appletAID);
        byte[] iAID = Conversion.hexToArray(instanceAID);
        ressource = (ressource.equals("")) ? null : ressource;
        byte[] secuDomainAID = (("".equals(securityDomainAID))) ? null : Conversion.hexToArray(securityDomainAID);
        byte[] par4Install4load = (("".equals(params4Install4load))) ? null : Conversion.hexToArray(params4Install4load);
        String length = Integer.toHexString(Integer.parseInt(maxDataLength)).toUpperCase();
        if (length.length() < 2) {
            length = "0" + length;
        }
        byte maxLength = (byte) (Conversion.hexToArray(length)[0]);
        byte[] parInstall4Install = (("".equals(paramsInstall4Install))) ? null : Conversion.hexToArray(paramsInstall4Install);
        byte[] privilege = Conversion.hexToArray(privileges);

        installApplet(pAID, aAID, iAID, ressource, secuDomainAID,
                par4Install4load, maxLength, privilege, parInstall4Install,
                reorderCapFileComponents);
    }

    private void checkPackageAID(String packageAID) throws ConfigFieldsException {
        if (packageAID.length() > 0) {
            packageAID = packageAID.replaceAll(":", "");
            packageAID = packageAID.replaceAll(" ", "");

            if (packageAID.length() % 2 == 0 && packageAID.length() >= 10 && packageAID.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(packageAID);

                if (m.find()) {
                    throw new ConfigFieldsException("The Package AID has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Package AID is "
                        + "invalid. It must contain between 10 and 32 characters.\n");
            }
        } else {
            throw new ConfigFieldsException("Package AID can't be empty.\n");
        }
    }

    private void checkInstanceAID(String instanceAID) throws ConfigFieldsException {
        if (instanceAID.length() > 0) {
            instanceAID = instanceAID.replaceAll(":", "");
            instanceAID = instanceAID.replaceAll(" ", "");

            if (instanceAID.length() % 2 == 0 && instanceAID.length() >= 10 && instanceAID.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(instanceAID);

                if (m.find()) {
                    throw new ConfigFieldsException("The Instance AID has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Instance AID is "
                        + "invalid.It must contain between 10 and 32 characters.\n");
            }
        } else {
            throw new ConfigFieldsException("Instance AID can't be empty.\n");
        }
    }

    private void checkAppletAID(String appletAID) throws ConfigFieldsException {
        if (appletAID.length() > 0) {
            appletAID = appletAID.replaceAll(":", "");
            appletAID = appletAID.replaceAll(" ", "");

            if (appletAID.length() % 2 == 0 && appletAID.length() >= 10 && appletAID.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(appletAID);

                if (m.find()) {
                    throw new ConfigFieldsException("The Applet AID has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Applet AID is "
                        + "invalid. It must contain between 10 and 32 characters.\n");
            }
        } else {
            throw new ConfigFieldsException("Applet AID can't be empty.\n");
        }
    }

    private void checkRessource(String ressource) throws ConfigFieldsException {
        if (ressource.length() > 0) {
            Pattern p1 = Pattern.compile(".*\\.cap", Pattern.CASE_INSENSITIVE);
            Matcher m = p1.matcher(ressource);

            if (!m.matches()) {
                throw new ConfigFieldsException("The Applet File has to be a "
                        + "cap file.\n");
            }

            File file = new File(ressource);
            if (!file.exists()) {
                throw new ConfigFieldsException("Applet File doesn't exist.\n");
            }
        } else {
            throw new ConfigFieldsException("Applet File can't be empty.\n");
        }
    }

    private void checkSecurityDomain(String securityDomainAID) throws ConfigFieldsException {
        if (securityDomainAID.length() > 0) {
            securityDomainAID = securityDomainAID.replaceAll(":", "");
            securityDomainAID = securityDomainAID.replaceAll(" ", "");

            if (securityDomainAID.length() % 2 == 0 && securityDomainAID.length() >= 10 && securityDomainAID.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(securityDomainAID);

                if (m.find()) {
                    throw new ConfigFieldsException("The Security Domain AID has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Security Domain AID is "
                        + "invalid. It must contain between 10 and 32 characters.\n");
            }
        }
    }

    /**
     * Check the parameters field
     * The field can be null, has to be an hexadecimal string, and
     *
     * @param params4Install4load
     * @throws ConfigFieldsException
     */
    private void checkParam(String params4Install4load) throws ConfigFieldsException {
        if (params4Install4load.length() > 0) {
            params4Install4load = params4Install4load.replaceAll(":", "");
            params4Install4load = params4Install4load.replaceAll(" ", "");

            if (params4Install4load.length() % 2 == 0 && params4Install4load.length() >= 10 && params4Install4load.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(params4Install4load);

                if (m.find()) {
                    throw new ConfigFieldsException("Parameters has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Parameters is "
                        + "invalid.It must contain between 10 and 32 characters.\n");
            }
        }
    }

    private void checkMaxDataLength(String maxDataLength) throws ConfigFieldsException {
        if (maxDataLength.length() != 0) {

            if ((Integer.parseInt(maxDataLength)) < 0 || (Integer.parseInt(maxDataLength) > 255)) {
                throw new ConfigFieldsException("Max Data Length is "
                        + "invalid.It must be between 0 and 255.\n");
            }
        } else {
            throw new ConfigFieldsException("Max Data Length can't be null. "
                    + "\n");
        }
    }

    private void checkPrivileges(String privilege) throws ConfigFieldsException {
        if (privilege.length() > 0) {
            privilege = privilege.replaceAll(":", "");
            privilege = privilege.replaceAll(" ", "");

            if (privilege.length() % 2 == 0 && privilege.length() >= 2 && privilege.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(privilege);

                if (m.find()) {
                    throw new ConfigFieldsException("Privileges has to be an "
                            + "hexadecimal string. You can write it in "
                            + "different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Privileges is "
                        + "invalid. It must contain between 2 and 32 characters.\n");
            }
        }
    }

    public boolean isAuthenticated() {
        return communication.isAuthenticated();
    }
}
