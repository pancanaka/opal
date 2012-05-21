/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a Java 6 library that implements Global Platform 2.x card
 * specification. OPAL is able to upload and manage Java Card applet lifecycle
 * on Java Card while dealing of the authentication of the user and encryption
 * of the communication between the user and the card. OPAL is also able
 * to manage different implementations of the specification via a pluggable
 * interface.
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
package fr.xlim.ssd.opal.library.tester;

import fr.xlim.ssd.opal.library.CardConfigFactory;
import fr.xlim.ssd.opal.library.applet.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.SecLevel;
import fr.xlim.ssd.opal.library.commands.ramoverhttp.RAMOverHTTP;
import fr.xlim.ssd.opal.library.config.CardConfig;
import fr.xlim.ssd.opal.library.utilities.CapConverter;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals.State;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import org.metastatic.jessie.provider.CipherSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A program to test compatibility SCPO1 and SCP02 between OPAL and a card. It works as follow:
 * <ul>
 * <li>Get the card channel</li>
 * <li>GET the ATR and load the card configuration</li>
 * <li>Select the security domain (card manager)</li>
 * <li>initialize update and external authenticate on security domain</li>
 * <li>install the hello applet (from SUN): convert the cap, load and install for install and make selectable</li>
 * <li>select the hello world applet and exchange a hello world APDU</li>
 * <li>select the security domain (card manager)</li>
 * <li>initialize update and external authenticate on security domain</li>
 * <li>erase the applet then erase the package</li>
 * </ul>
 * <p/>
 * For more information about SCP01 and SCP02 steps, please see Chapter D (SCP01) and E (SCP02) of the global platform
 * card specification version 2.1.1 - march 2003.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @author Guillaume Bouffard
 */
public class Main {

    /// the logger
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    /// timeout to detect card presence
    private final static int TIMEOUT_CARD_PRESENT = 1000;

    ///
    private final static byte[] HELLO_WORLD = { // "HELLO"
            (byte) 'H', (byte) 'E', (byte) 'L', (byte) 'L', (byte) 'O'
    };

    /// applet ID of hello world CAP
    private final static byte[] APPLET_ID = {
            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x62,
            (byte) 0x03, (byte) 0x01, (byte) 0x0C, (byte) 0x01, (byte) 0x01
    };

    /// package ID of hello world CAP
    private final static byte[] PACKAGE_ID = {
            (byte) 0xA0, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x62, (byte) 0x03,
            (byte) 0x01, (byte) 0x0C, (byte) 0x01
    };

    /// channel to the card
    private static CardChannel channel;

    private static CardConfig getCardChannel(int cardTerminalIndex,
                                             String transmissionProtocol) {

        TerminalFactory factory = TerminalFactory.getDefault();

        List<CardTerminal> terminals;

        try {
            terminals = factory.terminals().list(State.ALL);
        } catch (CardException ex) {
            logger.error("Cannot get list of terminals", ex);
            return null;
        }

        if (terminals.isEmpty()) {
            logger.error("No card terminal found");
            return null;
        } else {
            for (int i = 0; i < terminals.size(); i++) {
                logger.info("Card terminal found: " + i + " - " + terminals.get(i));
            }
        }

        if (terminals.size() == 1) {
            cardTerminalIndex = 0;
        } else if (terminals.size() < cardTerminalIndex) {
            logger.error("Card terminal index not available: " + cardTerminalIndex);
            return null;
        }

        logger.info("Card terminal selected: " + terminals.get(cardTerminalIndex));
        CardTerminal terminal = terminals.get(cardTerminalIndex);

        logger.info("Wait for card (during " + TIMEOUT_CARD_PRESENT + "ms)");

        boolean cardFound;

        try {
            cardFound = terminal.waitForCardPresent(TIMEOUT_CARD_PRESENT);
        } catch (CardException ex) {
            logger.error("Cannot detect card presence", ex);
            return null;
        }

        if (!cardFound) {
            logger.error("Card not found");
            return null;
        }

        logger.info("Connect to card with transmission protocol " + transmissionProtocol);

        Card card;

        try {
            card = terminal.connect(transmissionProtocol);
        } catch (CardException ex) {
            logger.error("Cannot connect to card", ex);
            return null;
        }

        logger.info("Card description: " + card);
        channel = card.getBasicChannel();
        ATR atr = card.getATR();
        logger.info("Card ATR:  " + Conversion.arrayToHex(atr.getBytes()));

        CardConfigFactory ccFactory = new CardConfigFactory();
        return ccFactory.getCardConfigByATR(atr.getBytes());
    }

    public static void RAMOverHTTP() throws ClassNotFoundException, IOException, CardException {

        channel = null;

        /// get the card config and card channel, detection of t=0 or t=1 is automatic
        CardConfig cardConfig = getCardChannel(1, "*");

        if (channel == null) {
            logger.error("Cannot access to the card");
            System.exit(-1);
        }
        //  select the security domain
        logger.info("Selecting Security Domain");
        cardConfig.getImplementation().setCardChannel(channel);
        SecurityDomain securityDomain = new SecurityDomain(cardConfig.getImplementation(),
                cardConfig.getIsd());
        securityDomain.setOffCardKeys(cardConfig.getSCKeys());


        RAMOverHTTP ram = new RAMOverHTTP("psk-tls.key", "null", "PSK_A", "localhost", "OPALJcop21");
        ram.setup("localhost", 9020, CipherSuite.TLS_PSK_WITH_3DES_EDE_CBC_SHA);
        ram.manage(securityDomain);
    }

    public static void classicCommunication() throws ClassNotFoundException, CardException, IOException {
        channel = null;

        SecLevel secLevel = SecLevel.C_ENC_AND_MAC;

        /// get the card config and card channel, detection of t=0 or t=1 is automatic
        CardConfig cardConfig = getCardChannel(1, "*");

        if (channel == null) {
            logger.error("Cannot access to the card");
            System.exit(-1);
        }

        //  select the security domain
        logger.info("Selecting Security Domain");
        cardConfig.getImplementation().setCardChannel(channel);
        SecurityDomain securityDomain = new SecurityDomain(cardConfig.getImplementation(),
                cardConfig.getIsd());
        securityDomain.setOffCardKeys(cardConfig.getSCKeys());
        try {
            securityDomain.select();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


        // initialize update
        logger.info("Initialize Update");
        securityDomain.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(),
                cardConfig.getScpMode());

        // external authenticate
        logger.info("External Authenticate");
        securityDomain.externalAuthenticate(secLevel);

        // install Applet
        logger.info("Installing Applet");
        logger.info("* Install For Load");
        securityDomain.installForLoad(PACKAGE_ID, null, null);

        InputStream is = ClassLoader.getSystemClassLoader().getClass().getResourceAsStream("/cap/HelloWorld-2_1_2.cap");
        byte[] convertedBuffer = CapConverter.convert(is);
        logger.info("* Loading file");
        securityDomain.load(convertedBuffer, (byte) 0x10);
        logger.info("* Install for install");
        securityDomain.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Applet
        CommandAPDU select = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0xA4 // INS
                , (byte) 0x04 // P1
                , (byte) 0x00 // P2
                , APPLET_ID   // DATA
        );
        logger.info("Selecting Applet");
        ResponseAPDU resp = securityDomain.send(select);
        logger.debug("Select Hello World Applet "
                + "(-> " + Conversion.arrayToHex(select.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        // Using Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );
        logger.info("Saying Hello");
        resp = securityDomain.send(hello);

        logger.debug("Say \"Hello\" "
                + "(-> " + Conversion.arrayToHex(hello.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        // Select the Card Manager
        logger.info("Select the Card Manager");
        securityDomain.select();
        logger.info("Initialize Update");
        securityDomain.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(),
                cardConfig.getScpMode());
        logger.info("External Authenticate");
        securityDomain.externalAuthenticate(secLevel);

        // Deleting Applet
        logger.info("Deleting applet");
        securityDomain.deleteOnCardObj(APPLET_ID, false);

        // Deleting package if existed
        logger.info("Deleting package");
        securityDomain.deleteOnCardObj(PACKAGE_ID, false);
    }

    public static void main(String[] args) throws CardException, ClassNotFoundException,
            IOException {

        boolean ramOverHTTP = true;

        if (ramOverHTTP) {
            Main.RAMOverHTTP();
        } else {
            Main.classicCommunication();
        }

    }

}
