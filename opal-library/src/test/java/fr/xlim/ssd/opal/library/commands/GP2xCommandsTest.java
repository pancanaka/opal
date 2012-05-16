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
package fr.xlim.ssd.opal.library.commands;

import fr.xlim.ssd.opal.library.config.KeyType;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCGemVisa;
import fr.xlim.ssd.opal.library.config.SCGemVisa2;
import fr.xlim.ssd.opal.library.config.SCKey;
import fr.xlim.ssd.opal.library.config.SCPMode;
import fr.xlim.ssd.opal.library.utilities.RandomGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class GP2xCommandsTest {

    private List<SCKey> keys;

    @Before
    public void resetRandomGenerator() {
        RandomGenerator.setRandomSequence(null);

    }

    @Before
    public void createKeys() {
        keys = new LinkedList<SCKey>();

        byte[] key0Data = new byte[]{
                (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA,
                (byte) 0xCA, (byte) 0xCA, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D,
                (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA,
                (byte) 0xCA, (byte) 0xCA
        };
        SCKey key0 = new SCGPKey((byte) 13, (byte) 1, KeyType.DES_ECB, key0Data);
        keys.add(key0);


        byte[] key1Data = new byte[]{
                0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D,
                (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA,
                (byte) 0xCA, (byte) 0xCA, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D
        };
        SCKey key1 = new SCGPKey((byte) 13, (byte) 2, KeyType.DES_ECB, key1Data);
        keys.add(key1);

        byte[] key2Data = new byte[]{
                (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D,
                (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D,
                (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D
        };
        SCKey key2 = new SCGPKey((byte) 13, (byte) 3, KeyType.DES_ECB, key2Data);
        keys.add(key2);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private GP2xCommands createCommands(String filename) {
        GP2xCommands commands = new GP2xCommands();
        InputStream input = GP2xCommands.class.getResourceAsStream(filename);
        Reader reader = new InputStreamReader(input);
        CardChannel cardChannel = null;
        try {
            cardChannel = new CardChannelMock(reader);
        } catch (CardException ce) {
            throw new IllegalStateException("CardException");
        } catch (IOException ioe) {
            throw new IllegalStateException("IOException");
        }
        commands.setCardChannel(cardChannel);
        return commands;
    }

    @Test
    public void testSetOffCardKey() {

        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/001-cardChannelMock-dummy.txt");

        commands.setOffCardKey(keys.get(0));
        assertEquals(commands.getKeys().length, 1);
        assertSame(commands.getKeys()[0], keys.get(0));
        assertSame(commands.getKey((byte) 13, (byte) 1), keys.get(0));

        commands.setOffCardKey(keys.get(2));
        assertEquals(commands.getKeys().length, 2);
        assertSame(commands.getKeys()[1], keys.get(2));
        assertSame(commands.getKey((byte) 13, (byte) 3), keys.get(2));

        commands.setOffCardKey(keys.get(1));
        assertEquals(commands.getKeys().length, 3);
        assertSame(commands.getKey((byte) 13, (byte) 2), keys.get(1));

        SCKey key3 = new SCGPKey((byte) 13, (byte) 1, KeyType.DES_CBC, new byte[4]);
        commands.setOffCardKey(key3);
        assertEquals(commands.getKeys().length, 3);
        assertSame(commands.getKey((byte) 13, (byte) 1), key3);


        assertNull(commands.getKey((byte) 14, (byte) 1));
        assertNull(commands.getKey((byte) 13, (byte) 4));
        assertNull(commands.getKey((byte) 14, (byte) 4));
    }

    @Test
    public void testSetOffCardKeys() {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/001-cardChannelMock-dummy.txt");

        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        assertEquals(commands.getKeys().length, 3);
        assertSame(commands.getKey((byte) 13, (byte) 1), keys.get(0));
        assertSame(commands.getKey((byte) 13, (byte) 2), keys.get(1));
        assertSame(commands.getKey((byte) 13, (byte) 3), keys.get(2));
    }

    @Test
    public void testDeleteOffCardKey() {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/001-cardChannelMock-dummy.txt");

        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        SCKey key = commands.deleteOffCardKey((byte) 13, (byte) 1);
        assertNotNull(key);
        assertSame(key, keys.get(0));
        assertEquals(commands.getKeys().length, 2);
        assertSame(commands.getKey((byte) 13, (byte) 2), keys.get(1));
        assertNull(commands.getKey((byte) 13, (byte) 1));

        key = commands.deleteOffCardKey((byte) 14, (byte) 1);
        assertNull(key);

        key = commands.deleteOffCardKey((byte) 13, (byte) 2);
        assertEquals(commands.getKeys().length, 1);
        assertSame(key, keys.get(1));
    }

    @Test
    public void testSelect() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/010-GP2xCommands-select-good.txt");
        byte[] aid = {0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34};
        commands.select(aid);
        commands.getCardChannel().close();
    }

    @Test
    public void testSelectFailedWhenResponseSWNot9000() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/011-GP2xCommands-select-failed.txt");
        byte[] aid = {0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34};

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Invalid response SW after SELECT command (1000)");
        commands.select(aid);
    }

    @Test
    public void testInitializeUpdateFailWhenFirstResponseNot9000() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/012-GP2xCommands-initialize-update-failed.txt");
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Invalid response SW after first INIT UPDATE command (4096)");
        commands.initializeUpdate((byte) 0x1, (byte) 0x2, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailWhenFirstResponseHasIllegalSize() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/013-GP2xCommands-initialize-update-failed.txt");
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Invalid response size after first INIT UPDATE command (2)");
        commands.initializeUpdate((byte) 0x1, (byte) 0x2, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailWhenSCPNotImplemented() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/014-GP2xCommands-initialize-update-failed.txt");
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("SCP version not available (-103)");
        commands.initializeUpdate((byte) 0x1, (byte) 0x2, SCPMode.SCP_10);
    }

    @Test
    public void testInitializeUpdateFailWhenDesiredSCPNotInResponse() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/015-GP2xCommands-initialize-update-failed.txt");
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Desired SCP does not match with card SCP value (1)");
        commands.initializeUpdate((byte) 0x1, (byte) 0x64, SCPMode.SCP_10);
    }

    @Test
    public void testInitializeUpdateFailWhenKeyNotFoundInLocalRepository() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/015-GP2xCommands-initialize-update-failed.txt");
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Selected key not found in local repository (keySetVersion: 1, keyId: 100)");
        commands.initializeUpdate((byte) 0x1, (byte) 0x64, SCPMode.SCP_01_05);
    }

    @Test
    public void testCalculateCryptograms() {
        GP2xCommands commands = new GP2xCommands();

        commands.hostChallenge = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };

        commands.cardChallenge = new byte[]{
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18
        };

        commands.sessEnc = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_15);
        commands.initIcv();
        commands.calculateCryptograms();

        byte[] expectedHostCrypto = new byte[]{
                0x3B, 0x20, 0x73, 0x5B, 0x46, 0x3A, (byte) 0xFC, (byte) 0xAF
        };

        byte[] expectedCardCrypto = new byte[]{
                (byte) 0x92, 0x04, 0x27, (byte) 0xC6, (byte) 0xA4, (byte) 0xA4,
                (byte) 0xE3, (byte) 0x95
        };

        assertArrayEquals(expectedHostCrypto, commands.hostCrypto);
        assertArrayEquals(expectedCardCrypto, commands.cardCrypto);
    }

    @Test
    public void testGenerateSessionKeys() {
        GP2xCommands commands = new GP2xCommands();

        byte[] encData = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17
        };

        SCGPKey enc = new SCGPKey((byte) -1, (byte) -1, KeyType.DES_ECB, encData);

        byte[] macData = new byte[]{
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
        };

        SCGPKey mac = new SCGPKey((byte) -1, (byte) -1, KeyType.DES_ECB, macData);

        byte[] kekData = new byte[]{
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
        };

        SCGPKey kek = new SCGPKey((byte) -1, (byte) -1, KeyType.DES_ECB, kekData);

        commands.derivationData = new byte[]{
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,};

        commands.generateSessionKeys(enc, mac, kek);

        byte[] expectedSessEnc = {
                0x65, (byte) 0xAB, (byte) 0xD6, (byte) 0xAE, 0x1A, (byte) 0xD5,
                (byte) 0x85, 0x20, (byte) 0x8C, 0x45, (byte) 0xC6, 0x18, 0x4F, 0x56,
                (byte) 0x88, 0x63, 0x65, (byte) 0xAB, (byte) 0xD6, (byte) 0xAE, 0x1A,
                (byte) 0xD5, (byte) 0x85, 0x20
        };

        byte[] expectedSessMac = {
                (byte) 0xB9, 0x02, (byte) 0xD3, (byte) 0xF2, 0x63, (byte) 0xC9,
                (byte) 0xCD, (byte) 0xA7, 0x6C, 0x62, 0x4A, (byte) 0x8B, 0x7F,
                (byte) 0xA7, (byte) 0xA7, (byte) 0xBA, (byte) 0xB9, 0x02, (byte) 0xD3,
                (byte) 0xF2, 0x63, (byte) 0xC9, (byte) 0xCD, (byte) 0xA7
        };

        byte[] expectedSessKek = {
                (byte) 0xB5, 0x0B, 0x76, (byte) 0x91, (byte) 0xFB, (byte) 0xF0,
                (byte) 0xB6, (byte) 0x8A, (byte) 0xF7, (byte) 0xA9, 0x1E, 0x4C, 0x4E,
                0x27, 0x0A, (byte) 0xF0, (byte) 0xB5, 0x0B, 0x76, (byte) 0x91,
                (byte) 0xFB, (byte) 0xF0, (byte) 0xB6, (byte) 0x8A
        };

        assertArrayEquals(expectedSessEnc, commands.sessEnc);
        assertArrayEquals(expectedSessMac, commands.sessMac);
        assertArrayEquals(expectedSessKek, commands.sessKek);
    }

    @Test
    public void testCalculateDerivationData() {
        GP2xCommands commands = new GP2xCommands();

        commands.hostChallenge = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };

        commands.cardChallenge = new byte[]{
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18
        };

        commands.calculateDerivationData();

        byte[] expected = new byte[]{
                0x15, 0x16, 0x17, 0x18, 0x01, 0x02, 0x03, 0x04,
                0x11, 0x12, 0x13, 0x14, 0x05, 0x06, 0x07, 0x08
        };

        assertArrayEquals(expected, commands.derivationData);
    }

    @Test
    public void testInitializeUpdateWhenGemVisaKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/016-GP2xCommands-initialize-update-good.txt");
        byte[] data = new byte[]{
                0x47, 0x45, 0x4D, 0x58, 0x50, 0x52, 0x45, 0x53, 0x53, 0x4F, 0x53, 0x41, 0x4D,
                0x50, 0x4C, 0x45, 0x47, 0x45, 0x4D, 0x58, 0x50, 0x52, 0x45, 0x53
        };
        SCGemVisa key = new SCGemVisa((byte) 255, data);
        commands.setOffCardKey(key);
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});
        commands.initializeUpdate((byte) 0xFF, (byte) 0x1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateWhenGemVisa2Key() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/017-GP2xCommands-initialize-update-good.txt");
        byte[] data = new byte[]{
                0x47, 0x45, 0x4D, 0x58, 0x50, 0x52, 0x45, 0x53, 0x53, 0x4F, 0x53, 0x41, 0x4D,
                0x50, 0x4C, 0x45, 0x47, 0x45, 0x4D, 0x58, 0x50, 0x52, 0x45, 0x53
        };
        SCGemVisa2 key = new SCGemVisa2((byte) 255, data);
        commands.setOffCardKey(key);
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});
        commands.initializeUpdate((byte) 0xFF, (byte) 0x1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailedWhenOnlyEncSCGPKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/018-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKey(keys.get(0));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Selected MAC Key not found in Local Repository : keySetVersion : 13, keyId : 2");
        commands.initializeUpdate((byte) 13, (byte) 1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailedWhenOnlyMacSCGPKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/018-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKey(keys.get(1));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Selected key not found in local repository (keySetVersion: 13, keyId: 1)");
        commands.initializeUpdate((byte) 13, (byte) 1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailedWhenOnlyKekSCGPKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/018-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKey(keys.get(2));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Selected key not found in local repository (keySetVersion: 13, keyId: 1)");
        commands.initializeUpdate((byte) 13, (byte) 1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailedWhenOnlyEncAndMacSCGPKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/018-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKey(keys.get(0));
        commands.setOffCardKey(keys.get(1));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Selected KEK Key not found in Local Repository : keySetVersion : 13, keyId : 3");
        commands.initializeUpdate((byte) 13, (byte) 1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailedWhenOnlyEncAndKekSCGPKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/018-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKey(keys.get(0));
        commands.setOffCardKey(keys.get(2));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Selected MAC Key not found in Local Repository : keySetVersion : 13, keyId : 2");
        commands.initializeUpdate((byte) 13, (byte) 1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailedWhenOnlyMacAndKekSCGPKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/018-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKey(keys.get(1));
        commands.setOffCardKey(keys.get(2));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Selected key not found in local repository (keySetVersion: 13, keyId: 1)");
        commands.initializeUpdate((byte) 13, (byte) 1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateWhenThreeSCGPKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/018-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});
        commands.initializeUpdate((byte) 13, (byte) 1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateFailedWhenCardCryptogramError() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/049-GP2xCommands-initialize-update-failed.txt");
        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Error verifying Card Cryptogram");
        commands.initializeUpdate((byte) 13, (byte) 1, SCPMode.SCP_01_05);
    }

    @Test
    public void testInitializeUpdateWhenKeyIdIs0AndScpUndefined() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/048-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});
        commands.initializeUpdate((byte) 13, (byte) 0, SCPMode.SCP_UNDEFINED);
        System.out.println("tada");
    }

    @Test
    public void testInitializeUpdateWhenKeyIdIs0AndScp0115() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/048-GP2xCommands-initialize-update-good.txt");
        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67, 0x01, 0x23, 0x45, 0x67});
        commands.initializeUpdate((byte) 13, (byte) 0, SCPMode.SCP_01_15);
    }

    @Test
    public void testExternalAuthenticateFailedWhenNoSession() throws CardException {
        Commands commands = new GP2xCommands();

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Session is not initialized");
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
    }

    @Test
    public void testExternalAuthenticateFailedWhenAuthSession() throws CardException {
        GP2xCommands commands = new GP2xCommands();
        commands.sessState = SessionState.SESSION_AUTH;

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Session is not initialized");
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
    }

    @Test
    public void testExternalAuthenticateFailedWhenSecLevelIsNull() throws CardException {
        Commands commands = new GP2xCommands();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("secLevel must be not null");
        commands.externalAuthenticate(null);
    }

    @Test
    public void testExternalAuthenticate() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/019-GP2xCommands-external-authenticate-good.txt");
        commands.hostCrypto = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        commands.sessState = SessionState.SESSION_INIT;
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_05);
        commands.initIcv();
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        assertEquals(SessionState.SESSION_AUTH, commands.sessState);
    }

    @Test
    public void testExternalAuthenticateFailedWhenSWNot9000() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/020-GP2xCommands-external-authenticate-failed.txt");
        commands.hostCrypto = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        commands.sessState = SessionState.SESSION_INIT;
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };

        commands.getScp().setScpMode(SCPMode.SCP_01_15);
        commands.initIcv();
        expectedException.expect(CardException.class);
        expectedException.expectMessage("Error in External Authenticate : 1000");
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
    }

    @Test
    public void testGenerateMacWithPadding() {
        GP2xCommands commands = new GP2xCommands();
        byte[] data = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
        };
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };

        byte[] expected = new byte[]{
                (byte) 0xE8, (byte) 0xA5, 0x2C, (byte) 0xD7, 0x1D, 0x5F, 0x4A, 0x6D
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_15);
        commands.initIcv();
        assertArrayEquals(expected, commands.generateMac(data));
    }

    @Test
    public void testGenerateMacWithoutPadding() {
        GP2xCommands commands = new GP2xCommands();
        byte[] data = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        byte[] expected = new byte[]{
                0x2F, (byte) 0xCD, 0x78, 0x2C, 0x1C, (byte) 0xE5, (byte) 0xDE, 0x14
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_05);
        commands.initIcv();
        assertArrayEquals(expected, commands.generateMac(data));
    }

    @Test
    public void testGenerateMacWithSCP0105() {
        GP2xCommands commands = new GP2xCommands();
        byte[] data = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };

        byte[] expected = new byte[]{
                0x2F, (byte) 0xCD, 0x78, 0x2C, 0x1C, (byte) 0xE5, (byte) 0xDE, 0x14
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_05);
        commands.initIcv();
        assertArrayEquals(expected, commands.generateMac(data));
        assertArrayEquals(expected, commands.icv);
    }

    @Test
    public void testGenerateMacWithSCP0115() {
        GP2xCommands commands = new GP2xCommands();
        byte[] data = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_15);
        commands.initIcv();
        byte[] expected = new byte[]{
                0x2F, (byte) 0xCD, 0x78, 0x2C, 0x1C, (byte) 0xE5, (byte) 0xDE, 0x14
        };
        assertArrayEquals(expected, commands.generateMac(data));

        byte[] expectedIcv = new byte[]{
                (byte) 0xFB, 0x26, 0x75, 0x1D, 0x56, (byte) 0xB2, (byte) 0xF9, 0x5D
        };
        assertArrayEquals(expectedIcv, commands.icv);

        expected = new byte[]{
                (byte) 0x9A, 0x39, 0x36, 0x18, (byte) 0x87, (byte) 0x81, 0x6C, (byte) 0xF1
        };
        assertArrayEquals(expected, commands.generateMac(data));

        expectedIcv = new byte[]{
                (byte) 0xB1, 0x03, (byte) 0xAD, 0x40, 0x4C, (byte) 0xF9, (byte) 0x9C, 0x0F
        };
        assertArrayEquals(expectedIcv, commands.icv);
    }

    @Test
    public void testGetStatusSimple() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/021-GP2xCommands-get-status-good.txt");
        commands.getStatus(GetStatusFileType.ISD, GetStatusResponseMode.OLD_TYPE, null);
    }

    @Test
    public void testGetStatusExtended() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/022-GP2xCommands-get-status-good.txt");
        commands.getStatus(GetStatusFileType.LOAD_FILES, GetStatusResponseMode.NEW_TYPE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStatusFailIfFileTypeNull() throws CardException {
        GP2xCommands commands = new GP2xCommands();
        commands.getStatus(null, GetStatusResponseMode.OLD_TYPE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStatusFailIfResponseModeNull() throws CardException {
        GP2xCommands commands = new GP2xCommands();
        commands.getStatus(GetStatusFileType.ISD, null, null);
    }

    @Test
    public void testGetStatusWithCMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/023-GP2xCommands-get-status-good.txt");
        commands.initIcv();
        commands.getScp().setSecMode(SecLevel.C_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.getScp().setScpMode(SCPMode.SCP_UNDEFINED);
        commands.getStatus(GetStatusFileType.ISD, GetStatusResponseMode.OLD_TYPE, null);
    }

    @Test
    public void testGetStatusWithCEncAndMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/024-GP2xCommands-get-status-good.txt");
        commands.getScp().setSecMode(SecLevel.C_ENC_AND_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.sessEnc = new byte[]{
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        commands.getScp().setScpMode(SCPMode.SCP_02_15);
        commands.initIcv();
        commands.getStatus(GetStatusFileType.APP_AND_SD, GetStatusResponseMode.NEW_TYPE, null);
    }

    @Test
    public void testGetStatusFailedWhenSWNot9000() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/025-GP2xCommands-get-status-failed.txt");

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Error in Get Status : 1000");
        commands.getStatus(GetStatusFileType.ISD, GetStatusResponseMode.OLD_TYPE, null);
    }

    @Test
    public void testGetStatusWhenSearchQualifierNotNull() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/026-GP2xCommands-get-status-good.txt");
        commands.getStatus(GetStatusFileType.LOAD_FILES, GetStatusResponseMode.NEW_TYPE,
                new byte[]{0x01, 0x02, 0x03});
    }

    @Test
    public void testDeleteOnCardObj() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/027-GP2xCommands-delete-object-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.deleteOnCardObj(aid, true);
    }

    @Test
    public void testDeleteOnCardObjWithCMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/028-GP2xCommands-delete-object-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.getScp().setSecMode(SecLevel.C_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_15);
        commands.initIcv();
        commands.deleteOnCardObj(aid, true);
    }

    @Test
    public void testDeleteOnCardObjWithCEncAndMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/029-GP2xCommands-delete-object-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.getScp().setSecMode(SecLevel.C_ENC_AND_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.sessEnc = new byte[]{
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        commands.getScp().setScpMode(SCPMode.SCP_02_15);
        commands.initIcv();
        commands.deleteOnCardObj(aid, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteOnCardObjFailIfAidNull() throws CardException {
        GP2xCommands commands = new GP2xCommands();
        commands.deleteOnCardObj(null, true);
    }

    @Test
    public void testDeleteOnCardObjWithoutCascade() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/030-GP2xCommands-delete-object-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.deleteOnCardObj(aid, false);
    }

    @Test
    public void testDeleteOnCardObjFailedWhenSWNot9000() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/031-GP2xCommands-delete-object-failed.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        expectedException.expect(CardException.class);
        expectedException.expectMessage("Error in DELETE OBJECT : 1000");
        commands.deleteOnCardObj(aid, false);
    }

    @Test
    public void testDeleteOnCardKey() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/032-GP2xCommands-delete-key-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.deleteOnCardKey((byte) 0x32, (byte) 0X3F);
    }

    @Test
    public void testDeleteOnCardKeyWithCMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/033-GP2xCommands-delete-key-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.getScp().setSecMode(SecLevel.C_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_15);
        commands.initIcv();
        commands.deleteOnCardKey((byte) 0x32, (byte) 0X3F);
    }

    @Test
    public void testDeleteOnCardKeyWithCEncAndMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/034-GP2xCommands-delete-key-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.getScp().setSecMode(SecLevel.C_ENC_AND_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.sessEnc = new byte[]{
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        commands.getScp().setScpMode(SCPMode.SCP_02_15);
        commands.initIcv();
        commands.deleteOnCardKey((byte) 0x32, (byte) 0X3F);
    }

    @Test
    public void testDeleteOnCardKeyFailedWhenSWNot9000() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/035-GP2xCommands-delete-key-failed.txt");

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Error in DELETE KEY : 1000");
        commands.deleteOnCardKey((byte) 0x3C, (byte) 0XD7);
    }

    @Test
    public void testInstallForLoad() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/036-GP2xCommands-install-for-load-good.txt");
        byte[] packageAid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        byte[] securityDomainAid = new byte[]{
                (byte) 0xA1, 0x01, 0x01, 0x01, 0x19, 0x44, 0x4E, 0x10, 0x28, 0x53, 0x5D
        };
        commands.installForLoad(packageAid, securityDomainAid, null);
    }

    @Test
    public void testInstallForLoadWithParamInferior128() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/050-GP2xCommands-install-for-load-good.txt");
        byte[] packageAid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        byte[] securityDomainAid = new byte[]{
                (byte) 0xA1, 0x01, 0x01, 0x01, 0x19, 0x44, 0x4E, 0x10, 0x28, 0x53, 0x5D
        };
        byte[] params = new byte[89];
        commands.installForLoad(packageAid, securityDomainAid, params);
    }

    @Ignore
    @Test
    public void testInstallForLoadWithParamSuperior128() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/051-GP2xCommands-install-for-load-good.txt");
        byte[] packageAid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        byte[] securityDomainAid = new byte[]{
                (byte) 0xA1, 0x01, 0x01, 0x01, 0x19, 0x44, 0x4E, 0x10, 0x28, 0x53, 0x5D
        };
        byte[] params = new byte[178];
        commands.installForLoad(packageAid, securityDomainAid, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstallForLoadFailIfPackageAidNull() throws CardException {
        GP2xCommands commands = new GP2xCommands();
        byte[] securityDomainAid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.installForLoad(null, securityDomainAid, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstallForLoadFailIfSecurityDomainAidNull() throws CardException {
        GP2xCommands commands = new GP2xCommands();
        byte[] packageAid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.installForLoad(packageAid, null, null);
    }

    @Test
    public void testInstallForLoadWithCMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/037-GP2xCommands-install-for-load-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.getScp().setSecMode(SecLevel.C_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        byte[] packageAid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        byte[] securityDomainAid = new byte[]{
                (byte) 0xA1, 0x01, 0x01, 0x01, 0x19, 0x44, 0x4E, 0x10, 0x28, 0x53, 0x5D
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_15);
        commands.initIcv();
        commands.installForLoad(packageAid, securityDomainAid, null);
    }

    @Test
    public void testInstallForLoadWithCEncAndMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/038-GP2xCommands-install-for-load-good.txt");
        byte[] aid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        commands.getScp().setSecMode(SecLevel.C_ENC_AND_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.sessEnc = new byte[]{
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        byte[] packageAid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        byte[] securityDomainAid = new byte[]{
                (byte) 0xA1, 0x01, 0x01, 0x01, 0x19, 0x44, 0x4E, 0x10, 0x28, 0x53, 0x5D
        };
        commands.getScp().setScpMode(SCPMode.SCP_02_15);
        commands.initIcv();
        commands.installForLoad(packageAid, securityDomainAid, null);
    }

    @Test
    public void testInstallForLoadFailedWhenSWNot9000() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/039-GP2xCommands-install-for-load-failed.txt");

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Error in INSTALL FOR LOAD : 1000");
        byte[] packageAid = new byte[]{
                (byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D
        };
        byte[] securityDomainAid = new byte[]{
                (byte) 0xA1, 0x01, 0x01, 0x01, 0x19, 0x44, 0x4E, 0x10, 0x28, 0x53, 0x5D
        };
        commands.installForLoad(packageAid, securityDomainAid, null);
    }

    @Test
    public void testLoad() throws CardException, FileNotFoundException, IOException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/040-GP2xCommands-load-good.txt");
        URL url = GP2xCommandsTest.class.getResource("/HelloWorld.cap");
        File file = new File(url.getFile());
        byte maxDataLength = (byte) 0xFF;

        byte[] buffer = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(buffer);
        commands.load(buffer, maxDataLength);
    }

    @Test
    public void testLoadWithCMac() throws CardException, FileNotFoundException, IOException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/041-GP2xCommands-load-good.txt");
        URL url = GP2xCommandsTest.class.getResource("/HelloWorld.cap");
        File file = new File(url.getFile());
        commands.getScp().setSecMode(SecLevel.C_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };

        commands.getScp().setScpMode(SCPMode.SCP_UNDEFINED);
        commands.initIcv();
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(buffer);
        commands.load(buffer);
    }

    @Test
    public void testLoadWithCEncAndMac() throws CardException, FileNotFoundException, IOException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/042-GP2xCommands-load-good.txt");
        URL url = GP2xCommandsTest.class.getResource("/HelloWorld.cap");
        File file = new File(url.getFile());
        commands.getScp().setSecMode(SecLevel.C_ENC_AND_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.sessEnc = new byte[]{
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };

        commands.getScp().setScpMode(SCPMode.SCP_02_15);
        commands.initIcv();
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(buffer);
        commands.load(buffer);
    }

    @Test
    public void testLoadFailWhenSWNot9000() throws CardException, FileNotFoundException, IOException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/047-GP2xCommands-load-failed.txt");
        URL url = GP2xCommandsTest.class.getResource("/HelloWorld.cap");
        File file = new File(url.getFile());
        byte maxDataLength = (byte) 0xFF;

        byte[] buffer = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(buffer);

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Error in LOAD : 1000");
        commands.load(buffer, maxDataLength);
    }

    @Test
    public void testInstallForInstallAndMakeSelectable() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/043-GP2xCommands-install-for-install-good.txt");
        byte[] loadFileAid = new byte[]{
                (byte) 0xAA, 0x0A, 0x0A, 0x0A, 0x1A, 0x4A, 0x4A
        };
        byte[] moduleAid = new byte[]{
                (byte) 0xAB, 0x0B, 0x0B, 0x0B, 0x1B, 0x4B, 0x4B
        };
        byte[] applicationAid = new byte[]{
                (byte) 0xAC, 0x0C, 0x0C, 0x0C, 0x1C, 0x4C, 0x4C
        };
        byte[] privileges = new byte[]{
                0X01, 0x02, 0x03
        };
        commands.installForInstallAndMakeSelectable(loadFileAid, moduleAid, applicationAid, privileges, null);
    }

    @Test
    public void testInstallForInstallAndMakeSelectableWithCMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/044-GP2xCommands-install-for-install-good.txt");
        commands.getScp().setSecMode(SecLevel.C_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        byte[] loadFileAid = new byte[]{
                (byte) 0xAA, 0x0A, 0x0A, 0x0A, 0x1A, 0x4A, 0x4A
        };
        byte[] moduleAid = new byte[]{
                (byte) 0xAB, 0x0B, 0x0B, 0x0B, 0x1B, 0x4B, 0x4B
        };
        byte[] applicationAid = new byte[]{
                (byte) 0xAC, 0x0C, 0x0C, 0x0C, 0x1C, 0x4C, 0x4C
        };
        byte[] privileges = new byte[]{
                0X01, 0x02, 0x03
        };
        commands.getScp().setScpMode(SCPMode.SCP_01_15);
        commands.initIcv();
        commands.installForInstallAndMakeSelectable(loadFileAid, moduleAid, applicationAid, privileges, null);
    }

    @Test
    public void testInstallForInstallAndMakeSelectableWithCEncAndMac() throws CardException {
        GP2xCommands commands = createCommands("/fr/xlim/ssd/opal/library/test/045-GP2xCommands-install-for-install-good.txt");
        commands.getScp().setSecMode(SecLevel.C_ENC_AND_MAC);
        commands.sessMac = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28
        };
        commands.sessEnc = new byte[]{
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
        };
        byte[] loadFileAid = new byte[]{
                (byte) 0xAA, 0x0A, 0x0A, 0x0A, 0x1A, 0x4A, 0x4A
        };
        byte[] moduleAid = new byte[]{
                (byte) 0xAB, 0x0B, 0x0B, 0x0B, 0x1B, 0x4B, 0x4B
        };
        byte[] applicationAid = new byte[]{
                (byte) 0xAC, 0x0C, 0x0C, 0x0C, 0x1C, 0x4C, 0x4C
        };
        byte[] privileges = new byte[]{
                0X01, 0x02, 0x03
        };
        commands.getScp().setScpMode(SCPMode.SCP_02_15);
        commands.initIcv();
        commands.installForInstallAndMakeSelectable(loadFileAid, moduleAid, applicationAid, privileges, null);
    }

    @Test
    public void testInstallForInstallAndMakeSelectableFailWhenSWNot9000() throws CardException {
        Commands commands = createCommands("/fr/xlim/ssd/opal/library/test/046-GP2xCommands-install-for-install-failed.txt");
        byte[] loadFileAid = new byte[]{
                (byte) 0xAA, 0x0A, 0x0A, 0x0A, 0x1A, 0x4A, 0x4A
        };
        byte[] moduleAid = new byte[]{
                (byte) 0xAB, 0x0B, 0x0B, 0x0B, 0x1B, 0x4B, 0x4B
        };
        byte[] applicationAid = new byte[]{
                (byte) 0xAC, 0x0C, 0x0C, 0x0C, 0x1C, 0x4C, 0x4C
        };
        byte[] privileges = new byte[]{
                0X01, 0x02, 0x03
        };

        expectedException.expect(CardException.class);
        expectedException.expectMessage("Error in INSTALL FOR INSTALL AND MAKE SELECTABLE : 1000");
        commands.installForInstallAndMakeSelectable(loadFileAid, moduleAid, applicationAid, privileges, null);
    }
}
