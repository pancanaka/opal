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

import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateListener;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.utilities.CapConverter;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Tiana Razafindralambo
 * @author Estelle Blandinières
 */
public class CommunicationController {

    private static final CustomLogger logger = new CustomLogger();
    private CommunicationModel model;
    private SecurityDomain securityDomain;

    /**
     * Default contstructor
     *
     * @author Tiana Razafindralambo
     */
    public CommunicationController() {
        this.model = new CommunicationModel();
    }

    /**
     * Constructor wich allows to specify directly the security level
     *
     * @param secLevel
     * @author Tiana Razafindralambo
     */
    public CommunicationController(SecLevel secLevel) {
        this.model = new CommunicationModel(secLevel);
    }

    /**
     * Security level setter
     *
     * @param secLevel
     * @author Tiana Razafindralambo
     */
    public void setSecurityLevel(SecLevel secLevel) {
        logger.info("Setting security level to " + secLevel.toString() + " and transmitting to model");
        this.model.setSecurityLevel(secLevel);
    }

    /**
     * Communication model getter
     *
     * @return <code>Object</code> CommunicationModel
     * @author Tiana Razafindralambo
     */
    public CommunicationModel getModel() {
        return this.model;
    }

    /**
     * Test if the current security domain is set
     *
     * @return boolean
     * @author Tiana Razafindralambo
     */
    public boolean hasDomain() {
        return this.model.getSecurityDomainModel().hasDomain();
    }

    /**
     * test if the current card is authenticated
     *
     * @return boolean
     * @author Tiana Razafindralambo
     */
    public boolean isAuthenticated() {
        return this.model.getSecurityDomainModel().isAuthenticated();
    }

    /**
     * Test if we can communicate with the card
     *
     * @return boolean
     * @author Tiana Razafindralambo
     */
    public boolean canCommunicate() {
        if (this.hasDomain()) {
            if (this.isAuthenticated()) return true;
            else {
                logger.error("Card isn't authenticated yet");
                return false;
            }
        }
        logger.error("Security domain isn't set yet");
        return false;
    }

    /**
     * Select applet command
     *
     * @param APPLET_ID
     * @author Tiana Razafindralambo
     */
    public void selectApplet(byte[] APPLET_ID) {
        CommandAPDU select = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0xA4 // INS
                , (byte) 0x04 // P1
                , (byte) 0x00 // P2
                , APPLET_ID   // DATA
        );
        logger.info("Selecting Applet (AID = " + Conversion.arrayToHex(select.getBytes()) + ")");
        ResponseAPDU resp = send(select);
    }

    /**
     * Send an APDU command
     *
     * @param command
     * @return <code>Object</code> ResponseAPDU
     * @author Tiana Razafindralambo
     */
    public ResponseAPDU send(CommandAPDU command) {
        logger.info("Sending an APDU command");
        if (this.canCommunicate()) {
            try {
                ResponseAPDU resp = securityDomain.send(command);

                logger.debug("Response to command "
                        + "(-> " + Conversion.arrayToHex(command.getBytes()) + " " + Conversion.arrayToHex(resp.getBytes()) + ") "
                        + "\n (<- " + Conversion.arrayToHex(resp.getBytes()) + ")");
                return resp;
            } catch (CardException ex) {
                logger.info(ex.getMessage());
            }
        }
        return null;
    }

    /**
     * Delete command
     *
     * @param AID
     * @param cascade
     * @author Tiana Razafindralambo
     */
    public void delete(byte[] AID, boolean cascade) {
        logger.info("Deleting object on card");
        try {
            ResponseAPDU resp = securityDomain.deleteOnCardObj(AID, cascade);
        } catch (CardException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * Install for install command
     *
     * @param PACKAGE_ID
     * @param MODULE_AID
     * @param APPLET_ID
     * @param privileges
     * @param params
     * @author Tiana Razafindralambo
     */
    private void install4install(byte[] PACKAGE_ID, byte[] MODULE_AID, byte[] APPLET_ID, byte[] privileges, byte[] params) {
        try {
            logger.info("* Install for install");
            ResponseAPDU resp = securityDomain.installForInstallAndMakeSelectable(
                    PACKAGE_ID,
                    MODULE_AID,
                    APPLET_ID,
                    privileges, params);
        } catch (CardException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * Install for load command
     *
     * @param PACKAGE_ID
     * @param APPLET_ID
     * @param securityDomainAID
     * @param params
     * @author Tiana Razafindralambo
     */
    private void install4load(byte[] PACKAGE_ID, byte[] APPLET_ID, byte[] securityDomainAID, byte[] params) {
        try {
            logger.info("* Install For Load");
            ResponseAPDU resp = securityDomain.installForLoad(PACKAGE_ID, securityDomainAID, params);
        } catch (CardException ex) {
            logger.error(ex.getMessage());
            logger.info("Deleting previous applet install and package install");
            delete(PACKAGE_ID, false);
            delete(APPLET_ID, false);
            install4load(PACKAGE_ID, APPLET_ID, securityDomainAID, params);
        }
    }

    /**
     * Applet installation commands
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
        if (this.canCommunicate()) {
            install4load(PACKAGE_ID, APPLET_ID, securityDomainAID, params4Install4load);

            if (reorderCapFileComponents) {
                InputStream is = null;

                try {
                    is = new FileInputStream(ressource);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                byte[] convertedBuffer = CapConverter.convert(is);

                try {
                    logger.info("* Loading file");
                    ResponseAPDU[] resp = securityDomain.load(convertedBuffer, maxDataLength);
                } catch (CardException ex) {
                    logger.error(ex.getMessage());
                }
            }

            install4install(PACKAGE_ID, MODULE_AID, APPLET_ID, privileges, paramsInstall4Install);
        }
    }

    /**
     * Authentication command
     *
     * @param _cardConfig
     * @author Tiana Razafindralambo
     */
    public void authenticate(CardConfig _cardConfig) {
        logger.info("Initialize Update");
        final CardConfig cardConfig = _cardConfig;
        this.model.getSecurityDomainModel().addSecurityDomainStateListener(new SecurityDomainStateListener() {
            @Override
            public void securityDomainStateChanged(SecurityDomainStateChangedEvent event) {
                ResponseAPDU response = null;
                securityDomain = model.getSecurityDomain();
                if (model.getSecurityDomainModel().hasDomain()) {
                    try {
                        response = securityDomain.initializeUpdate(cardConfig.getDefaultInitUpdateP1(),
                                cardConfig.getDefaultInitUpdateP2(),
                                cardConfig.getScpMode()
                        );
                        logger.debug("Initialize update APDU response : " + response);

                        response = securityDomain.externalAuthenticate(model.getSecurityLevel());
                        logger.debug("External Authenticate APDU response : " + response);

                        model.getSecurityDomainModel().isAuthenticated(true);
                        logger.warn(Conversion.arrayToHex(cardConfig.getIssuerSecurityDomainAID()) + "__");
                    } catch (CardException ex) {
                        logger.error(ex.getMessage());
                    }
                } else logger.error("No security domain set yet.");
                model.getSecurityDomainModel().removeSecurityDomainStateListener(this);
            }
        });
    }
}
