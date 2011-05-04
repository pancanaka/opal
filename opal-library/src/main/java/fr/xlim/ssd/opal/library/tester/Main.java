package fr.xlim.ssd.opal.library.tester;

import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.CapConverter;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.*;
import javax.smartcardio.CardTerminals.State;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

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

        try {
            return CardConfigFactory.getCardConfig(atr.getBytes());
        } catch (CardConfigNotFoundException ex) {
            logger.error(ex.getMessage());
        }

        return null;

    }

    public static void main(String[] args) throws CardException, CardConfigNotFoundException,
            CommandsImplementationNotFound, ClassNotFoundException, IOException {

        channel = null;

        SecLevel secLevel = SecLevel.C_MAC;

        /// get the card config and card channel, detection of t=0 or t=1 is automatic
        CardConfig cardConfig = getCardChannel(1, "*");

        if (channel == null) {
            logger.error("Cannot access to the card");
            System.exit(-1);
        }

        //  select the security domain
        logger.info("Selecting Security Domain");
        SecurityDomain securityDomain = new SecurityDomain(cardConfig.getImplementation(), channel,
                cardConfig.getIssuerSecurityDomainAID());
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
        
        //File file = new File("cap/HelloWorld-2_1_2.cap");

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

}
