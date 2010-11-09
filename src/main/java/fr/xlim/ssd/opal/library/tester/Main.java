/**
 * This is a simple example program
 *
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 * @author Guillaume Bouffard
 */
package fr.xlim.ssd.opal.library.tester;

import fr.xlim.ssd.opal.library.FileType;
import fr.xlim.ssd.opal.library.GetStatusResponseMode;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigFactoryWithATR;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.CapConverter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    private final static int TIMEOUT_CARD_PRESENT = 1000;
    private final static byte[] HELLO_WORLD = { // "HELLO"
        (byte)'H' , (byte)'E' , (byte)'L' , (byte)'L' , (byte)'O'
    };

    private final static byte[] APPLET_ID = {
        (byte)0xA0 , (byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x62 ,
        (byte)0x03 , (byte)0x01 , (byte)0x0C , (byte)0x01 , (byte)0x01
    };
    private final static byte[] PACKAGE_ID = {
        (byte)0xA0 , (byte)0x00 , (byte)0x00 ,
        (byte)0x00 , (byte)0x62 , (byte)0x03 ,
        (byte)0x01 , (byte)0x0C , (byte)0x01
    };

    private static CardChannel channel;

    private static CardConfig getCardChannel(int cardTerminalIndex,
            String transmissionProtocol) {

        TerminalFactory factory = TerminalFactory.getDefault();

        List<CardTerminal> terminals;

        try {
            terminals = factory.terminals().list();
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
        } else if (terminals.size() >= cardTerminalIndex) {
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
            String config = CardConfigFactoryWithATR.getCardConfig(atr.getBytes());
            logger.info("Card config:  " + config);
            return CardConfigFactory.getCardConfig(config);
        } catch (CardConfigNotFoundException ex) {
            logger.error(ex.getMessage());
        }
        
        return null;

    }

    public static void main(String[] args) throws CardException, CardConfigNotFoundException, CommandsImplementationNotFound, ClassNotFoundException, FileNotFoundException, IOException {

        channel = null ;

        CardConfig cardConfig = getCardChannel(0, "T=0");

        if (channel == null) {
            logger.error("Cannot access to the card");
            System.exit(-1);
        }

        SecurityDomain securityDomain = new SecurityDomain(cardConfig.getImplementation(), channel, cardConfig.getIssuerSecurityDomainAID());

        securityDomain.setOffCardKeys(cardConfig.getSCKeys());
        securityDomain.select();
        securityDomain.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        securityDomain.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);

        //securityDomain.getStatus(FileType.ISD, GetStatusResponseMode.OLD_TYPE, null);
        //securityDomain.getStatus(FileType.APP_AND_SD, GetStatusResponseMode.OLD_TYPE, null);
        //securityDomain.getStatus(FileType.LOAD_FILES, GetStatusResponseMode.OLD_TYPE, null);

        // Deleting Applet if existed
        try {
            ResponseAPDU[] resps = securityDomain.getStatus(FileType.APP_AND_SD, GetStatusResponseMode.OLD_TYPE, null);
            for (ResponseAPDU resp : resps) {
                String all = Conversion.arrayToHex(resp.getData());
                if (all.indexOf(Conversion.arrayToHex(APPLET_ID)) != -1) {
                    securityDomain.deleteOnCardObj(APPLET_ID, false);
                    logger.info("Applet " + Conversion.arrayToHex(APPLET_ID) + " deleted.");
                }
            }
        } catch (Exception e) {
            logger.debug("There is no installed Applet in this samrt card");
        }
        
        // Deleting package if existed
        try {
            ResponseAPDU[] resps = securityDomain.getStatus(FileType.LOAD_FILES, GetStatusResponseMode.OLD_TYPE, null);
            for (ResponseAPDU resp : resps) {
                String all = Conversion.arrayToHex(resp.getData());
                if (all.indexOf(Conversion.arrayToHex(PACKAGE_ID)) != -1) {
                    securityDomain.deleteOnCardObj(PACKAGE_ID, false);
                    logger.info("Package " + Conversion.arrayToHex(PACKAGE_ID) + " deleted.");
                }
            }
        } catch (Exception e) {
            logger.debug("There is no installed Applet in this samrt card");
        }

        // Installing Applet
        securityDomain.installForLoad(PACKAGE_ID, null, null);
        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        securityDomain.load(convertedBuffer, (byte) 0x10);
        securityDomain.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Applet
        CommandAPDU select = new CommandAPDU
                ( (byte) 0x00   // CLA
                , (byte) 0xA4   // INS
                , (byte) 0x04   // P1
                , (byte) 0x00   // P2
                , APPLET_ID     // DATA
                ) ;
        ResponseAPDU resp = securityDomain.send( select );
        logger.debug("Select Hello World Applet "
                + "(-> " + Conversion.arrayToHex(select.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        // Using Applet
        CommandAPDU hello = new CommandAPDU
                ( (byte) 0x00   // CLA
                , (byte) 0x00   // INS
                , (byte) 0x00   // P1
                , (byte) 0x00   // P2
                , HELLO_WORLD   // DATA
                ) ;
        resp = securityDomain.send( hello );

        logger.debug("Say \"Hello\" "
                + "(-> " + Conversion.arrayToHex(hello.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getData()) + ")");

        if ( Arrays.equals( hello.getBytes() , resp.getData() ) ) {
            logger.info("Hello OK");
        } else {
            logger.error("Hello FAIL");
        }

        // Select the Card Manager
        securityDomain.select();
        securityDomain.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        securityDomain.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);

        // Deleting Applet
        securityDomain.deleteOnCardObj(APPLET_ID, false);

        // Deleting package if existed
        securityDomain.deleteOnCardObj(PACKAGE_ID, false);
    }
}
