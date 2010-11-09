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
import fr.xlim.ssd.opal.library.params.CardConfigFactoryWithATR;
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
import java.io.InputStream;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
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
        CardChannel channel = card.getBasicChannel();
        ATR atr = card.getATR();
        logger.info("Card ATR:  " + Conversion.arrayToHex(atr.getBytes()));

        return channel;
    }

    public static void main(String[] args) throws CardException, CardConfigNotFoundException, CommandsImplementationNotFound, ClassNotFoundException, FileNotFoundException, IOException {


        //CardConfig cardConfig = CardConfigFactory.getCardConfig("Cyberflex");

        CardChannel channel = getCardChannel(0, "T=0");

        if (channel == null) {
            logger.error("Cannot access to the card");
        }

        //CardConfig cardConfig = CardConfigFactory.getCardConfig( CardConfigFactoryWithATR.getCardConfig(Conversion.hexToArray("3B 65 00 00 44 04 01 08 03")) );
        CardConfig cardConfig = CardConfigFactory.getCardConfig( "JCOP30" );

        SecurityDomain securityDomain = new SecurityDomain(cardConfig.getImplementation(), channel, cardConfig.getIssuerSecurityDomainAID());

        securityDomain.setOffCardKeys(cardConfig.getSCKeys());
        securityDomain.select();
        securityDomain.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        securityDomain.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);

        securityDomain.getStatus(FileType.ISD, GetStatusResponseMode.OLD_TYPE, null);
        securityDomain.getStatus(FileType.APP_AND_SD, GetStatusResponseMode.OLD_TYPE, null);
        securityDomain.getStatus(FileType.LOAD_FILES, GetStatusResponseMode.OLD_TYPE, null);

        //System.exit(0);

        // Delete Applet
        try {
            securityDomain.deleteOnCardObj(Conversion.hexToArray("A0 00 00 00 62 03 01 0C 01 01"), false);
        } catch (Exception e) {
            System.err.println("Unable to delete applet");
        }
        
        // Delete package
        try {
            securityDomain.deleteOnCardObj(Conversion.hexToArray("A0 00 00 00 62 03 01 0C 01"), false);
        } catch (Exception e) {
            System.err.println("Unable to delete package");
        }

//        securityDomain.deleteOnCardObj(Conversion.hexToArray("A0 00 00 00 62 03 01 0C 01 01"), false);

        System.exit(0);
  
        securityDomain.installForLoad(Conversion.hexToArray("A0 00 00 00 62 03 01 0C 01"), null, null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        securityDomain.load(convertedBuffer, (byte) 0x10);

        securityDomain.installForInstallAndMakeSelectable(
                Conversion.hexToArray("A0 00 00 00 62 03 01 0C 01"),
                Conversion.hexToArray("A0 00 00 00 62 03 01 0C 01 01"),
                Conversion.hexToArray("A0 00 00 00 62 03 01 0C 01 01"),
                Conversion.hexToArray("00"), null);
    }
}
