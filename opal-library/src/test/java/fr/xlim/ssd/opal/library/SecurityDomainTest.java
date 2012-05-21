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
package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.applet.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CardChannelMock;
import fr.xlim.ssd.opal.library.commands.FileControlInformation;
import fr.xlim.ssd.opal.library.commands.GP2xCommandsTest;
import fr.xlim.ssd.opal.library.config.CardConfig;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SecurityDomainTest {

    private SecurityDomain createCommands(String filename, CardConfig cardConfig) throws ClassNotFoundException, IOException, CardException {
        CardChannel cardChannel = null;
        SecurityDomain commands = null;

        File file = new File(GP2xCommandsTest.getProjectRoot().getAbsolutePath() +
                "/data-for-tests/dummy-traces/" + filename);
        Reader reader = new FileReader(file);
        assertNotNull(reader);

        cardChannel = new CardChannelMock(reader);
        cardConfig.getImplementation().setCardChannel(cardChannel);
        Assert.assertNotNull(cardConfig.getImplementation());
        Assert.assertNotNull(cardConfig.getImplementation().getCardChannel());
        Assert.assertNotNull(cardChannel);
        commands = new SecurityDomain(cardConfig.getImplementation(), cardConfig.getIsd());
        return commands;
    }

    @Test
    public void testSelect() throws ClassNotFoundException, IOException, CardException {

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
        cardConfig = new CardConfigFactory().getCardConfigByName("JCOP21");
        SecurityDomain commands = createCommands("052-SecurityDomain-select-good.txt", cardConfig);

        Assert.assertNotNull(commands.getCc());
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
    }
}
