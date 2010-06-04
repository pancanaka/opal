/**
 * This is a simple example program
 *
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
package fr.xlim.ssd.opal.library.tester;

import fr.xlim.ssd.opal.library.FileType;
import fr.xlim.ssd.opal.library.GetStatusResponseMode;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.CapConverter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.File;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    private final static int TIMEOUT_CARD_PRESENT = 1000;

    private static CardChannel getCardChannel(int cardTerminalIndex,
            String transmissionProtocol) {

        TerminalFactory factory = TerminalFactory.getDefault();

        List<CardTerminal> terminals;

        try {
            terminals = factory.terminals().list();
        }
        catch (CardException ex) {
            logger.error("Cannot get list of terminals", ex);
            return null;
        }

        if (terminals.size() == 0) {
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
        }
        catch (CardException ex) {
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
        }
        catch (CardException ex) {
            logger.error("Cannot connect to card", ex);
            return null;
        }

        logger.info("Card description: " + card);
        CardChannel channel = card.getBasicChannel();
        ATR atr = card.getATR();
        logger.info("Card ATR:  " + Conversion.arrayToHex(atr.getBytes()));

        return channel;
    }

    public static void main(String[] args) throws CardException, CardConfigNotFoundException, CommandsImplementationNotFound, ClassNotFoundException, FileNotFoundException, IOException {


        CardConfig cardConfig = CardConfigFactory.getCardConfig("Oberthur");

        CardChannel channel = getCardChannel(0, "T=0");

        if (channel == null) {
            logger.error("Cannot access to the card");
        }

        SecurityDomain securityDomain = new SecurityDomain(cardConfig.getImplementation(), channel, cardConfig.getIssuerSecurityDomainAID());

        securityDomain.setOffCardKeys(cardConfig.getSCKeys());
        securityDomain.select();
        securityDomain.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        securityDomain.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);

        /*
        securityDomain.getStatus(FileType.ISD, GetStatusResponseMode.OLD_TYPE, null);
        securityDomain.getStatus(FileType.APP_AND_SD, GetStatusResponseMode.OLD_TYPE, null);
        securityDomain.getStatus(FileType.LOAD_FILES, GetStatusResponseMode.OLD_TYPE, null);
        */

        /*
        a.deleteOnCardObj(Conversion.hexToArray("656E73696D6167747030337075727365"), false);
        securityDomain.deleteOnCardObj(Conversion.hexToArray("A00000006203010C0101"), false);
         */

        securityDomain.installForLoad(Conversion.hexToArray("A00000006203010C01"), null, null);

        File file = new File("/home/kartoch/works/javacard/docs/2.2.2/java_card_kit-2_2_2/samples/classes/com/sun/javacard/samples/HelloWorld/javacard/HelloWorld.cap");        
        byte[] convertedBuffer = CapConverter.convert(file);
        securityDomain.load(convertedBuffer);
        
        /*
        a.installForInstallAndMakeSelectable(Conversion.hexToArray("656E73696D616774703033"), Conversion.hexToArray("656E73696D6167747030337075727365"), Conversion.hexToArray("656E73696D6167747030337075727365"), Conversion.hexToArray("00"), null);
        a.getStatus(FileType.LOAD_FILES, GetStatusResponseMode.OLD_TYPE, null);
         */
    }
}