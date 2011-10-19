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

import fr.xlim.ssd.opal.gui.model.Key.KeyModel;
import fr.xlim.ssd.opal.gui.model.reader.ProfileModel;
import fr.xlim.ssd.opal.gui.view.components.ProfileComponent;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The <code>ProfileController</code> class is the controlleur used for profile management.
 * <p>It communicates with the <code>ProfileModel</code> to set/get all profiles information
 * and to and call core functions</p>
 * <p>It also contains all checks needed to verify that what the user sends to the app is legal or not</p>
 *
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 * @author Tiana Razafindralambo
 * @version 0.1
 * @see fr.xlim.ssd.opal.gui.model.reader.ProfileModel
 * @see fr.xlim.ssd.opal.library.params.CardConfigNotFoundException
 */

public class ProfileController {
    /**
     * Contains the model
     */
    private ProfileModel profileModel;

    /**
     * Initialises a new {@code ProfileController} containing the model.
     *
     * @throws CardConfigNotFoundException If an error occured while reading the XML config file
     */
    public ProfileController()
            throws CardConfigNotFoundException {
        profileModel = new ProfileModel();
    }

    /**
     * Used to get tu profile model.
     *
     * @return A {@code ProfileModel}
     */
    public ProfileModel getProfileModel() {
        return profileModel;
    }

    /**
     * Used to get all profiles loaded from the XML config file.
     *
     * @return A String matrix that contains needed profile information
     *         sorted by names in order to display them in the view.
     *         It can be used this way: <br />
     *         <ul>
     *         <li>info[x][0] contains the name of the profile</li>
     *         <li>info[x][1] contains the description of the profile</li>
     *         <li>info[x][2] contains the default implementation of the profile</li>
     *         </ul>
     */
    public String[][] getAllProfiles() {
        return profileModel.getAllProfiles();
    }

    /**
     * Used to delete a profile from the XML config file
     *
     * @param id is the id of the profile to delete
     *           It's meant to correspond with the line clicked by the user on the display panel
     * @return A boolean
     *         {@code True} for config found {@code False} if not
     * @throws CardConfigNotFoundException If an error occured while reading the XML config file or if the profile was not found
     */
    public boolean deleteProfile(int id)
            throws CardConfigNotFoundException {

        return profileModel.deleteProfile(id);
    }

    /**
     * Used to get a precise {@code ProfileComponent} from the model.
     *
     * @param i is the id of the profile to delete
     * @return A {@code ProfileComponent}
     * @see fr.xlim.ssd.opal.gui.view.components.ProfileComponent
     */
    public ProfileComponent getProfile(int i) {
        return profileModel.getProfile(i);
    }


    /**
     * Used add a profile to the XML config file
     *
     * @param p is the profile to add
     * @throws CardConfigNotFoundException If an error occured while reading the XML config file
     *                                     ConfigFieldsException If the data entered by the user are incorrect
     */
    public void addProfile(ProfileComponent p)
            throws CardConfigNotFoundException, ConfigFieldsException {

        checkForm(p);
        profileModel.addProfile(p.convertToCardConfig());
    }

    /**
     * Used to update the profile selected by the user.
     *
     * @param p is the profile to update
     * @throws CardConfigNotFoundException If an error occured while reading the XML config file or if the profile was not found
     *                                     ConfigFieldsException If the data entered by the user are incorrect
     */
    public void updateProfile(ProfileComponent p)
            throws CardConfigNotFoundException, ConfigFieldsException {

        checkForm(p);
        profileModel.updateProfile(p.convertToCardConfig());
    }


    private void checkForm(ProfileComponent p)
            throws ConfigFieldsException {

        checkName(p.getName());
        checkDesc(p.getDescription());
        checkAID(p.getAID());
        checkSCP(p.getSCPmode());
        checkTP(p.getTP());
        checkImpl(p.getImplementation());
        checkATRs(p.getATR());
        checkKeys(p.getKeys());
    }

    private void checkName(String name)
            throws ConfigFieldsException {

        if (name.length() >= 4 && name.length() <= 25) {
            Pattern p1 = Pattern.compile("[^0-9A-Z/_ .-]+", Pattern.CASE_INSENSITIVE);
            Matcher m = p1.matcher(name);

            if (m.find()) {
                throw new ConfigFieldsException("The name contains illegal characters. You can only use figures, letters from A to Z and: /_.-\n");
            }
        } else {
            throw new ConfigFieldsException("The name must contain between 4 and 25 characters.\n");
        }
    }

    private void checkDesc(String desc)
            throws ConfigFieldsException {

        if (desc.length() > 0) {
            if (desc.length() < 141) {
                Pattern p1 = Pattern.compile("[^0-9A-Z/_ .'-]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(desc);

                if (m.find()) {
                    throw new ConfigFieldsException("The description contains illegal characters. You can only use figures, letters from A to Z and: /_.-'\n");
                }
            } else {
                throw new ConfigFieldsException("The description is optional but can't contain more than 140 characters.\n");
            }
        }
    }

    private void checkATRs(String[] atrs)
            throws ConfigFieldsException {

        for (int i = 0; i < atrs.length; i++) {
            checkATR(atrs[i]);
        }
    }

    private void checkATR(String atr)
            throws ConfigFieldsException {

        atr = atr.replaceAll(":", "");
        atr = atr.replaceAll(" ", "");

        if (atr.length() % 2 == 0) {
            Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
            Matcher m = p1.matcher(atr);

            if (m.find()) {
                throw new ConfigFieldsException("The ATR has to be an hexadecimal string. You can write it in different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
            }
        } else {
            throw new ConfigFieldsException("The ATR is invalid.\n");
        }
    }

    private void checkAID(String aid)
            throws ConfigFieldsException {

        if (aid.length() > 0) {
            aid = aid.replaceAll(":", "");
            aid = aid.replaceAll(" ", "");

            if (aid.length() % 2 == 0 && aid.length() >= 10 && aid.length() <= 32) {
                Pattern p1 = Pattern.compile("[^A-F0-9]+", Pattern.CASE_INSENSITIVE);
                Matcher m = p1.matcher(aid);

                if (m.find()) {
                    throw new ConfigFieldsException("The AID has to be an hexadecimal string. You can write it in different ways like:\n -AD0F98\n -AD:0F:98\n -AD 0F 98");
                }
            } else {
                throw new ConfigFieldsException("Issuer Security Domain AID is invalid.It must contain between 10 and 32 characters.\n");
            }
        } else {
            throw new ConfigFieldsException("Issuer Security Domain AID can't be empty.\n");
        }
    }

    private void checkSCP(String scp)
            throws ConfigFieldsException {

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

    private void checkTP(String tp)
            throws ConfigFieldsException {

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

    private void checkKeys(ArrayList<KeyModel> keys)
            throws ConfigFieldsException {

        int n = keys.size();

        for (int i = 0; i < n; i++) {
            checkKey(keys.get(i), ++i);
        }
    }

    private void checkKey(KeyModel key, int index)
            throws ConfigFieldsException {

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

    private void checkImpl(String impl)
            throws ConfigFieldsException {

        if (impl.length() > 0) {
            if (impl.compareToIgnoreCase("GemXpresso211Commands") != 0
                    && impl.compareToIgnoreCase("GP2xCommands") != 0
                /* If new implementations are allowed : && impl.compareToIgnoreCase("*Implementation*") != 0  */) {
                throw new ConfigFieldsException("The Implementation is unknown.\n");
            }
        } else {
            throw new ConfigFieldsException("Implementation can't be empty.\n");
        }
    }
}