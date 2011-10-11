package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.commands.CardChannelMock;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: stroumph
 * Date: 12/14/10
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityDomainTest {

    private SecurityDomain createCommands(String filename, CardConfig cardConfig) {
        CardChannel cardChannel = null;
        SecurityDomain commands = null;
        InputStream input = SecurityDomain.class.getResourceAsStream(filename);
        Reader reader = new InputStreamReader(input);
        try {
            cardChannel = new CardChannelMock(reader);
            commands = new SecurityDomain(cardConfig.getImplementation(), cardChannel, cardConfig.getIsd());
        } catch (CardException ce) {
            throw new IllegalStateException("CardException");
        } catch (IOException ioe) {
            throw new IllegalStateException("IOException");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("ClassNotFoundException");
        } catch (CommandsImplementationNotFound commandsImplementationNotFound) {
            throw new IllegalStateException("CommandsImplementationNotFound");
        }
        return commands;
    }

    @Test
    public void testSelect() {

        /**
         * Select APDU Response:
         *
         *  6F 65
         *     84 08 // Application / file AID
         *        A0 00 00 00 03 00 00 00
         *     A5 59
         *        9F 65 01 // Maximum Length value
         *           FF
         *        9F 6E 06 // Application production life cycle
         *           40 51 63 45 29 00
         *        73 4A
         *           06 07 // Identifies Global Platform as the Tag Allocation Authority
         *              2A 86 48 86 FC 6B 01
         *           60 0C
         *              06 0A // Card Management Type and Version
         *                 2A 86 48 86 FC 6B 02 02 01 01
         *              63 09
         *                 06 07 // Card Identification Scheme
         *                    2A 86 48 86 FC 6B 03
         *              64 0B
         *                 06 09 // Secure Channel Protocol of the selected Security Domain and its implementation options
         *                    2A 86 48 86 FC 6B 04 02 15
         *              65 0B // Card Configuration details
         *                 06 09 2B 85 10 86 48 64 02 01 03
         *              66 0C // Card/Chips details
         *                 06 0A 2B 06 01 04 01 2A 02 6E 01 02
         *  SW: 90 00
         */

        byte[] allInformation = {
                (byte) 0x6F, (byte) 0x65, (byte) 0x84, (byte) 0x08,
                (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xA5, (byte) 0x59, (byte) 0x9F, (byte) 0x65,
                (byte) 0x01, (byte) 0xFF, (byte) 0x9F, (byte) 0x6E,
                (byte) 0x06, (byte) 0x40, (byte) 0x51, (byte) 0x63,
                (byte) 0x45, (byte) 0x29, (byte) 0x00, (byte) 0x73,
                (byte) 0x4A, (byte) 0x06, (byte) 0x07, (byte) 0x2A,
                (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xFC,
                (byte) 0x6B, (byte) 0x01, (byte) 0x60, (byte) 0x0C,
                (byte) 0x06, (byte) 0x0A, (byte) 0x2A, (byte) 0x86,
                (byte) 0x48, (byte) 0x86, (byte) 0xFC, (byte) 0x6B,
                (byte) 0x02, (byte) 0x02, (byte) 0x01, (byte) 0x01,
                (byte) 0x63, (byte) 0x09, (byte) 0x06, (byte) 0x07,
                (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86,
                (byte) 0xFC, (byte) 0x6B, (byte) 0x03, (byte) 0x64,
                (byte) 0x0B, (byte) 0x06, (byte) 0x09, (byte) 0x2A,
                (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xFC,
                (byte) 0x6B, (byte) 0x04, (byte) 0x02, (byte) 0x15,
                (byte) 0x65, (byte) 0x0B, (byte) 0x06, (byte) 0x09,
                (byte) 0x2B, (byte) 0x85, (byte) 0x10, (byte) 0x86,
                (byte) 0x48, (byte) 0x64, (byte) 0x02, (byte) 0x01,
                (byte) 0x03, (byte) 0x66, (byte) 0x0C, (byte) 0x06,
                (byte) 0x0A, (byte) 0x2B, (byte) 0x06, (byte) 0x01,
                (byte) 0x04, (byte) 0x01, (byte) 0x2A, (byte) 0x02,
                (byte) 0x6E, (byte) 0x01, (byte) 0x02
        };

        byte[] applicationAID = {
                (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        byte[] MaximumLengthOfDataFieldInCommandMessage = {
                (byte) 0xFF
        };

        byte[] ApplicationProductionLifeCycleData = {
                (byte) 0x40, (byte) 0x51, (byte) 0x63, (byte) 0x45,
                (byte) 0x29, (byte) 0x00
        };

        byte[] GPTagAllocationAuthority = {
                (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86,
                (byte) 0xFC, (byte) 0x6B, (byte) 0x01
        };

        byte[] cardManagementTypeAndVersion = {
                (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86,
                (byte) 0xFC, (byte) 0x6B, (byte) 0x02, (byte) 0x02,
                (byte) 0x01, (byte) 0x01
        };

        byte[] cardIdentificationScheme = {
                (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86,
                (byte) 0xFC, (byte) 0x6B, (byte) 0x03
        };

        byte[] scpConfiguration = {
                (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86,
                (byte) 0xFC, (byte) 0x6B, (byte) 0x04, (byte) 0x02,
                (byte) 0x15
        };

        byte[] cardConfiguration = {
                (byte) 0x06, (byte) 0x09, (byte) 0x2B, (byte) 0x85,
                (byte) 0x10, (byte) 0x86, (byte) 0x48, (byte) 0x64,
                (byte) 0x02, (byte) 0x01, (byte) 0x03
        };

        byte[] cardDetails = {
                (byte) 0x06, (byte) 0x0A, (byte) 0x2B, (byte) 0x06,
                (byte) 0x01, (byte) 0x04, (byte) 0x01, (byte) 0x2A,
                (byte) 0x02, (byte) 0x6E, (byte) 0x01, (byte) 0x02
        };

        CardConfig cardConfig = null;
        try {
            cardConfig = new CardConfigFactory().getCardConfigByName("JCOP21");
            SecurityDomain commands = createCommands("/fr/xlim/ssd/opal/library/test/052-SecurityDomain-select-good.txt", cardConfig);
            commands.select();

            FileControlInformation cardInformation = commands.getCardInformation();

            //Assert.assertArrayEquals(cardInformation.getAllInformation(),allInformation);
            Assert.assertArrayEquals(cardInformation.getApplicationAID(), applicationAID);
            Assert.assertArrayEquals(cardInformation.getMaximumLengthOfDataFieldInCommandMessage(), MaximumLengthOfDataFieldInCommandMessage);
            Assert.assertArrayEquals(cardInformation.getApplicationProductionLifeCycleData(), ApplicationProductionLifeCycleData);
            Assert.assertArrayEquals(cardInformation.getGpTagAllocationAuthority(), GPTagAllocationAuthority);
            Assert.assertArrayEquals(cardInformation.getCardManagementTypeAndVersion(), cardManagementTypeAndVersion);
            Assert.assertArrayEquals(cardInformation.getCardIdentificationScheme(), cardIdentificationScheme);
            Assert.assertArrayEquals(cardInformation.getScpConfiguration(), scpConfiguration);
            Assert.assertArrayEquals(cardInformation.getCardConfiguration(), cardConfiguration);
            Assert.assertArrayEquals(cardInformation.getCardDetails(), cardDetails);

        } catch (CardConfigNotFoundException e) {
            throw new IllegalStateException("CardConfigNotFoundException");
        } catch (CardException e) {
            throw new IllegalStateException("CardException");
        } catch (IOException e) {
            throw new IllegalStateException("IOException:" + e);
        }
    }
}
