/**
 * This is a simple example program
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
package fr.xlim.ssd.opal.library.examples;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.FileType;
import fr.xlim.ssd.opal.library.GetStatusResponseMode;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.utilities.Conversion;

public class Main {

    private static CardChannel connectAndgetCardChannel(String transmissionproto)
            throws CardException {

        CardChannel channel = null;

        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminalList = factory.terminals().list();

        //List numbers of Card readers
        System.out.println("Card Terminals:");
        for (int i = 0; i < terminalList.size(); i++) {
            System.out.println("  - " + i + " : " + terminalList.get(i));
        }

        if (terminalList.size() == 0) {
            System.out.println("No Card Terminal");
            return null;
        }

        // take the first terminal in the list
        CardTerminal terminal = (CardTerminal) terminalList.get(0);

        System.out.println("Please insert card...");

        // waiting for the card
        terminal.waitForCardPresent(10000);

        // establish a connection with the card
        Card card = terminal.connect(transmissionproto);
        System.out.println("card: " + card);
        channel = card.getBasicChannel();

        //reset the card
        ATR atr = card.getATR();
        System.out.println("ATR = " + Conversion.arrayToHex(atr.getBytes()));

        return channel;
    }

    /**
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CardNotFoundException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static void main(String[] args) throws CardConfigNotFoundException, CardException,
            CommandsImplementationNotFound, ClassNotFoundException {

        CardConfig c = CardConfigFactory.getCardConfig("JCOP31");

        CardChannel channel = connectAndgetCardChannel(c.getTransmissionProtocol());

        SecurityDomain a = new SecurityDomain(c.getImplementation(), channel, c.getIssuerSecurityDomainAID());

        a.setOffCardKeys(c.getSCKeys());

        a.select();
        a.initializeUpdate(c.getDefaultInitUpdateP1(), c.getDefaultInitUpdateP2(), c.getScpMode());
        a.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        a.deleteOnCardObj(Conversion.hexToArray("656E73696D6167747030337075727365"), false);
        a.deleteOnCardObj(Conversion.hexToArray("656E73696D616774703033"), false);
        a.installForLoad(Conversion.hexToArray("656E73696D616774703033"), null, null);
        a.load(new File("C:\\java_card_kit-2_1_2\\damsApplet03.cap"));
        a.installForInstallAndMakeSelectable(Conversion.hexToArray("656E73696D616774703033"), Conversion.hexToArray("656E73696D6167747030337075727365"), Conversion.hexToArray("656E73696D6167747030337075727365"), Conversion.hexToArray("00"), null);
        a.getStatus(FileType.LOAD_FILES, GetStatusResponseMode.OLD_TYPE, null);
    }
}
