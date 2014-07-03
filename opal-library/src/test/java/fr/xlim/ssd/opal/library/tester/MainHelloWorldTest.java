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
import fr.xlim.ssd.opal.library.commands.SecLevel;
import fr.xlim.ssd.opal.library.commands.*;
import fr.xlim.ssd.opal.library.config.CardConfig;
import fr.xlim.ssd.opal.library.utilities.CapConverter;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.library.utilities.RandomGenerator;
import org.junit.Before;
import org.junit.Test;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import java.io.*;
import java.net.URL;

public class MainHelloWorldTest {

    private final static byte[] HELLO_WORLD = { // "HELLO"
            (byte) 'H', (byte) 'E', (byte) 'L', (byte) 'L', (byte) 'O'
    };
    private final static byte[] APPLET_ID = {
            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x62,
            (byte) 0x03, (byte) 0x01, (byte) 0x0C, (byte) 0x01, (byte) 0x01
    };
    private final static byte[] PACKAGE_ID = {
            (byte) 0xA0, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x62, (byte) 0x03,
            (byte) 0x01, (byte) 0x0C, (byte) 0x01
    };

    private static byte[] iv_zero = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

    @Before
    public void resetRandomGenerator() {
        RandomGenerator.setRandomSequence(null);
    }

    private GP2xCommands createCommands(String filename) throws IOException, CardException {
        GP2xCommands commands = new GP2xCommands();
        InputStream input = GP2xCommands.class.getResourceAsStream("/fr/xlim/ssd/opal/library/test/cards" + filename);
        Reader reader = new InputStreamReader(input);
        CardChannel cardChannel = new CardChannelMock(reader);
        commands.setCardChannel(cardChannel);
        return commands;
    }

    private GP2xCommands createCommandsgemalto211(String filename) throws IOException, CardException {
        GP2xCommands commands = new GemXpresso211Commands();
        InputStream input = GP2xCommands.class.getResourceAsStream("/fr/xlim/ssd/opal/library/test/cards" + filename);
        Reader reader = new InputStreamReader(input);
        CardChannel cardChannel = new CardChannelMock(reader);
        commands.setCardChannel(cardChannel);
        return commands;
    }

    /**
     * ****************************************************************************************************************
     * SCP 01
     * ****************************************************************************************************************
     */

    @Test
    public void testCyberflexPalmeraV3NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_V3-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Cyberflex_Palmera_V3");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x5F, (byte) 0x29, (byte) 0x97, (byte) 0x7B,
                (byte) 0xBD, (byte) 0x7C, (byte) 0x23, (byte) 0x8C});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2A, (byte) 0x98, (byte) 0xE0, (byte) 0x3A,
                (byte) 0xA7, (byte) 0x38, (byte) 0xD5, (byte) 0x73});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testCyberflexPalmeraV3C_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_V3-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Cyberflex_Palmera_V3");

        org.junit.Assert.assertArrayEquals(cardConfig.getSCKeys()[0].getValue(),
                Conversion.hexToArray("40 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 40 41 42 43 44 45 46 47"));
        org.junit.Assert.assertArrayEquals(cardConfig.getSCKeys()[1].getValue(),
                Conversion.hexToArray("40 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 40 41 42 43 44 45 46 47"));
        org.junit.Assert.assertArrayEquals(cardConfig.getSCKeys()[2].getValue(),
                Conversion.hexToArray("40 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 40 41 42 43 44 45 46 47"));

        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xE8, (byte) 0x2A, (byte) 0xFE, (byte) 0x1D,
                (byte) 0xE5, (byte) 0x95, (byte) 0x97, (byte) 0x83});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(),
                cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);

        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xA2, (byte) 0xDA, (byte) 0x7B, (byte) 0x90,
                (byte) 0x5F, (byte) 0xC6, (byte) 0x17, (byte) 0x0E});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testCyberflexPalmeraV3C_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_V3-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Cyberflex_Palmera_V3");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x52, (byte) 0x61, (byte) 0xCF, (byte) 0xC6,
                (byte) 0xE2, (byte) 0x3E, (byte) 0xEB, (byte) 0xD1});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x96, (byte) 0x8F, (byte) 0x33, (byte) 0x47,
                (byte) 0x06, (byte) 0x50, (byte) 0x52, (byte) 0xC1});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testInfineon_JTOP_V2_16kNoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Infineon_JTOP_V2_16k-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Infineon_JTOP_V2_16k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x13, (byte) 0x47, (byte) 0x11, (byte) 0x0D,
                (byte) 0xEC, (byte) 0xC0, (byte) 0xCC, (byte) 0xF0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x72, (byte) 0xEA, (byte) 0x4A, (byte) 0x8B,
                (byte) 0x68, (byte) 0xB5, (byte) 0xC8, (byte) 0x81});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testInfineon_JTOP_V2_16kC_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Infineon_JTOP_V2_16k-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Infineon_JTOP_V2_16k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x6E, (byte) 0xA6, (byte) 0xAC, (byte) 0x96,
                (byte) 0xD9, (byte) 0x51, (byte) 0x78, (byte) 0x2E});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xD0, (byte) 0x0B, (byte) 0x94,
                (byte) 0x52, (byte) 0xF3, (byte) 0xF4, (byte) 0xA3});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testInfineon_JTOP_V2_16kC_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Infineon_JTOP_V2_16k-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Infineon_JTOP_V2_16k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x81, (byte) 0xF8, (byte) 0xF0, (byte) 0x44,
                (byte) 0xB4, (byte) 0x72, (byte) 0xB9, (byte) 0x4D});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x24, (byte) 0x68, (byte) 0xEE, (byte) 0x39,
                (byte) 0xDF, (byte) 0x6B, (byte) 0xA2, (byte) 0x75});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testOberthur_Cosmo_Dual_72kNoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Oberthur_Cosmo_Dual_72k-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Oberthur_Cosmo_Dual_72k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x76, (byte) 0x6E, (byte) 0x19, (byte) 0xB0,
                (byte) 0xCD, (byte) 0x65, (byte) 0x96, (byte) 0x49});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2A, (byte) 0x6F, (byte) 0xD0, (byte) 0x6E,
                (byte) 0x9C, (byte) 0x37, (byte) 0x4F, (byte) 0xA3});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testOberthur_Cosmo_Dual_72kC_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Oberthur_Cosmo_Dual_72k-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Oberthur_Cosmo_Dual_72k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x0A, (byte) 0x09, (byte) 0x5B, (byte) 0x68,
                (byte) 0xD2, (byte) 0x26, (byte) 0x6B, (byte) 0xC9});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x5F, (byte) 0x13, (byte) 0xC1, (byte) 0xE4,
                (byte) 0xC8, (byte) 0x91, (byte) 0xE8, (byte) 0x66});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testOberthur_Cosmo_Dual_72kC_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Oberthur_Cosmo_Dual_72k-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Oberthur_Cosmo_Dual_72k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xB5, (byte) 0xC6, (byte) 0x18, (byte) 0xD6,
                (byte) 0xF7, (byte) 0xE0, (byte) 0xEA, (byte) 0x18});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x16, (byte) 0xC4, (byte) 0x98, (byte) 0xE8,
                (byte) 0xFE, (byte) 0x6B, (byte) 0x8B, (byte) 0xDA});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testOberthurCosmopolicNoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Oberthur_CosmopolIC-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Oberthur_Cosmopolic");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x54, (byte) 0x66, (byte) 0x3A, (byte) 0x80,
                (byte) 0x34, (byte) 0x01, (byte) 0x93, (byte) 0x15});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x82, (byte) 0xF2, (byte) 0xE6, (byte) 0x11,
                (byte) 0xEF, (byte) 0xDF, (byte) 0xBD, (byte) 0xF5});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testOberthurCosmopolicC_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Oberthur_CosmopolIC-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Oberthur_Cosmopolic");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x0C, (byte) 0x04, (byte) 0x53, (byte) 0x91,
                (byte) 0x5F, (byte) 0x7E, (byte) 0xA9, (byte) 0x34});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x56, (byte) 0x89, (byte) 0x04, (byte) 0x7A,
                (byte) 0xA1, (byte) 0x62, (byte) 0x82, (byte) 0x06});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testOberthurCosmopolicC_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Oberthur_CosmopolIC-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("Oberthur_Cosmopolic");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x26, (byte) 0x2B, (byte) 0x48, (byte) 0x3C,
                (byte) 0x4C, (byte) 0x0C, (byte) 0xA1, (byte) 0x33});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xE6, (byte) 0x7C, (byte) 0x65, (byte) 0xDE,
                (byte) 0x65, (byte) 0xE3, (byte) 0x00, (byte) 0x2B});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGemCombiXpresso_Lite_R2_Std_Jcop30NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-GemCombiXpresso_Lite_R2_Std_Jcop30-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GemCombiXpresso_Lite_R2_Std_Jcop30");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xA3, (byte) 0x81, (byte) 0x21, (byte) 0xDC,
                (byte) 0xFF, (byte) 0xE4, (byte) 0x5F, (byte) 0x8C});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xEC, (byte) 0x2B, (byte) 0x63, (byte) 0xDE,
                (byte) 0xA7, (byte) 0x56, (byte) 0x9E, (byte) 0x98});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGemCombiXpresso_Lite_R2_Std_Jcop30C_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-GemCombiXpresso_Lite_R2_Std_Jcop30-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GemCombiXpresso_Lite_R2_Std_Jcop30");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x8A, (byte) 0xC4, (byte) 0xC7, (byte) 0x11,
                (byte) 0xAB, (byte) 0x66, (byte) 0x8C, (byte) 0xE4});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xCE, (byte) 0x14, (byte) 0x85, (byte) 0xEB,
                (byte) 0x10, (byte) 0x6D, (byte) 0xAB, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGemCombiXpresso_Lite_R2_Std_Jcop30C_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-GemCombiXpresso_Lite_R2_Std_Jcop30-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GemCombiXpresso_Lite_R2_Std_Jcop30");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xE1, (byte) 0xED, (byte) 0x0B, (byte) 0x24,
                (byte) 0x7E, (byte) 0x76, (byte) 0xBF, (byte) 0x1D});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x88, (byte) 0x79, (byte) 0xE7, (byte) 0x07,
                (byte) 0x7C, (byte) 0x17, (byte) 0x3E, (byte) 0x08});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGemXplore3GC_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-GemXplore3G-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GemXplore3G");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x73, (byte) 0xD2, (byte) 0xBA, (byte) 0x0E,
                (byte) 0xC5, (byte) 0x2E, (byte) 0x78, (byte) 0x42});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x4F, (byte) 0xE5, (byte) 0xA9, (byte) 0x48,
                (byte) 0x77, (byte) 0xFE, (byte) 0x1B, (byte) 0xA2});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGemXplore3GC_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-GemXplore3G-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GemXplore3G");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x86, (byte) 0x38, (byte) 0x36, (byte) 0xB4,
                (byte) 0x97, (byte) 0x3A, (byte) 0xED, (byte) 0x79});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2C, (byte) 0x22, (byte) 0x15, (byte) 0x8E,
                (byte) 0x36, (byte) 0xB8, (byte) 0x56, (byte) 0x4E});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGemXpresso211isNoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommandsgemalto211("/HelloWorld-GemXpresso_211is-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GemXpresso211");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x64, (byte) 0xE1, (byte) 0xA9, (byte) 0xDC,
                (byte) 0xB5, (byte) 0xAE, (byte) 0x5B, (byte) 0x06});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet -- test
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x6B, (byte) 0xE6, (byte) 0xDF, (byte) 0xA0,
                (byte) 0x6F, (byte) 0x9A, (byte) 0x41, (byte) 0x95});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGemXpresso211isC_MAC() throws  CardException, IOException {
        Commands commands = createCommandsgemalto211("/HelloWorld-GemXpresso_211is-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GemXpresso211");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x41, (byte) 0x86, (byte) 0x4E, (byte) 0xA7,
                (byte) 0xE7, (byte) 0x43, (byte) 0x6D, (byte) 0xCB});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x22, (byte) 0xAC, (byte) 0x61, (byte) 0x21,
                (byte) 0xA9, (byte) 0x2E, (byte) 0x73, (byte) 0xB4});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGemXpresso211isC_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommandsgemalto211("/HelloWorld-GemXpresso_211is-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GemXpresso211");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x58, (byte) 0xAF, (byte) 0x89, (byte) 0x57,
                (byte) 0xAA, (byte) 0x82, (byte) 0xF7, (byte) 0xE0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x36, (byte) 0xB2, (byte) 0x62, (byte) 0xB2,
                (byte) 0xFE, (byte) 0x75, (byte) 0xA0, (byte) 0x5B});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    //    /**
//     * ****************************************************************************************************************
//     * SCP 02
//     * ****************************************************************************************************************
//     */
//
    @Test
    public void testJCOP21NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP21-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP21");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9F, (byte) 0x5D, (byte) 0x02, (byte) 0x20,
                (byte) 0x2D, (byte) 0xF3, (byte) 0x22, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testJCOP21C_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP21-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP21");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testJCOP21C_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP21-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP21");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xF9, (byte) 0x0B, (byte) 0xF1, (byte) 0x91,
                (byte) 0x5D, (byte) 0x36, (byte) 0x54, (byte) 0x34});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2C, (byte) 0x3A, (byte) 0x49, (byte) 0x8C,
                (byte) 0xDA, (byte) 0x30, (byte) 0x0D, (byte) 0xAD});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGALITT_WADAPA_NO_SECURITY_LEVEL() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-GALITT_WADAPA-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GALITT_WADAPA");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xCD, (byte) 0xD8, (byte) 0xF4, (byte) 0x49,
                (byte) 0xC9, (byte) 0x6F, (byte) 0x8B, (byte) 0x5D});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x21, (byte) 0x8C, (byte) 0xFC, (byte) 0xC8,
                (byte) 0x86, (byte) 0xFB, (byte) 0xF2, (byte) 0xF8});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGALITT_WADAPA_C_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-GALITT_WADAPA-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GALITT_WADAPA");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9A, (byte) 0x35, (byte) 0xBB, (byte) 0xC0,
                (byte) 0x0F, (byte) 0xCB, (byte) 0x4F, (byte) 0x37});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x27, (byte) 0x6E, (byte) 0x24, (byte) 0x8C,
                (byte) 0x99, (byte) 0xE1, (byte) 0xE8, (byte) 0x2F});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testGALITT_WADAPA_C_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-GALITT_WADAPA-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("GALITT_WADAPA");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xDC, (byte) 0x83, (byte) 0x65, (byte) 0x0B,
                (byte) 0x77, (byte) 0xB6, (byte) 0xE1, (byte) 0x35
        });

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x88, (byte) 0x77, (byte) 0xE9, (byte) 0x7A,
                (byte) 0xB1, (byte) 0x69, (byte) 0x61, (byte) 0x7D
        });
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testJCOP31_72B1_V2_2_NO_SECURITY_LEVEL() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP31_72B1_V2.2-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP31_72B1_V2.2");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xF8, (byte) 0xAC, (byte) 0x7B, (byte) 0x46,
                (byte) 0x7B, (byte) 0x9D, (byte) 0x0C, (byte) 0xC5});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xC4, (byte) 0x1C, (byte) 0xAC, (byte) 0x77,
                (byte) 0xF1, (byte) 0x4A, (byte) 0x35, (byte) 0x08});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testJCOP31_72B1_V2_2_C_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP31_72B1_V2.2-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP31_72B1_V2.2");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x56, (byte) 0x5E, (byte) 0x4F, (byte) 0x5E,
                (byte) 0xAE, (byte) 0x72, (byte) 0xB2, (byte) 0xB1});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x5D, (byte) 0x7A, (byte) 0x36, (byte) 0x9D,
                (byte) 0x41, (byte) 0x2A, (byte) 0xD0, (byte) 0x1D});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testJCOP31_72B1_V2_2_C_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP31_72B1_V2.2-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP31_72B1_V2.2");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x23, (byte) 0x3D, (byte) 0x5B, (byte) 0x00,
                (byte) 0x81, (byte) 0x10, (byte) 0x21, (byte) 0x6C
        });

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xFC, (byte) 0x82, (byte) 0x09, (byte) 0xE4,
                (byte) 0x60, (byte) 0x55, (byte) 0x7D, (byte) 0xCE
        });
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testJCOP31_72B1_V2_2_NFC_NO_SECURITY_LEVEL() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP31_72B1_V2.2_NFC-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP31_72B1_V2.2");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x76, (byte) 0x81, (byte) 0xB4, (byte) 0xAD,
                (byte) 0x29, (byte) 0x19, (byte) 0x2E, (byte) 0xFF});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xB2, (byte) 0x59, (byte) 0xB4, (byte) 0x90,
                (byte) 0x02, (byte) 0xE7, (byte) 0xCD, (byte) 0xCD});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testJCOP31_72B1_V2_2_NFC_C_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP31_72B1_V2.2_NFC-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP31_72B1_V2.2");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xCA, (byte) 0x69, (byte) 0xDC, (byte) 0x3C,
                (byte) 0x62, (byte) 0x94, (byte) 0x8E, (byte) 0xFA});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x20, (byte) 0x91, (byte) 0x46, (byte) 0x04,
                (byte) 0xB3, (byte) 0x14, (byte) 0xD9, (byte) 0x1F});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testJCOP31_72B1_V2_2_NFC_C_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-JCOP31_72B1_V2.2_NFC-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("JCOP31_72B1_V2.2");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x17, (byte) 0x7B, (byte) 0x70, (byte) 0x16,
                (byte) 0xA0, (byte) 0x01, (byte) 0x93, (byte) 0x3D
        });

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xAA, (byte) 0xC3, (byte) 0x90, (byte) 0x99,
                (byte) 0x6C, (byte) 0x2B, (byte) 0xB5, (byte) 0xD5
        });
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testCOSMO_16_RSA_V3_5_NO_SECURITY_LEVEL() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-COSMO_16_RSA_V3_5-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("COSMO_16_RSA_V3.5");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x22, (byte) 0xE7, (byte) 0x6A, (byte) 0xF0,
                (byte) 0x43, (byte) 0x00, (byte) 0x43, (byte) 0xCD});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x18, (byte) 0x9D, (byte) 0x78, (byte) 0x11,
                (byte) 0xF1, (byte) 0xEA, (byte) 0x3A, (byte) 0xDE});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testCOSMO_16_RSA_V3_5_C_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-COSMO_16_RSA_V3_5-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("COSMO_16_RSA_V3.5");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x16, (byte) 0x42, (byte) 0xF2, (byte) 0x22,
                (byte) 0xE9, (byte) 0x54, (byte) 0xEA, (byte) 0x06
        });

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xE1, (byte) 0x69, (byte) 0x20, (byte) 0xBF,
                (byte) 0x5B, (byte) 0x97, (byte) 0x9D, (byte) 0x46
        });
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testCOSMO_16_RSA_V3_5_C_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-COSMO_16_RSA_V3_5-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("COSMO_16_RSA_V3.5");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xCA, (byte) 0xC8, (byte) 0x13, (byte) 0xB6,
                (byte) 0x3A, (byte) 0x5A, (byte) 0x80, (byte) 0x8F
        });

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x8D, (byte) 0x4C, (byte) 0x0D, (byte) 0xE4,
                (byte) 0xEE, (byte) 0x31, (byte) 0x7A, (byte) 0xCA
        });
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testSAMSUNG_NFC_SGH_X700N_NO_SECURITY_LEVEL() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-SAMSUNG_NFC_SGH-X700N-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("SAMSUNG_NFC_SGH-X700N");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x8B, (byte) 0x76, (byte) 0x07, (byte) 0xDA,
                (byte) 0x9D, (byte) 0x13, (byte) 0x5C, (byte) 0xC0
        });

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xC0, (byte) 0x02, (byte) 0xB5, (byte) 0x9C,
                (byte) 0xE8, (byte) 0x30, (byte) 0x49, (byte) 0x48});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testSAMSUNG_NFC_SGH_X700N_C_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-SAMSUNG_NFC_SGH-X700N-C_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("SAMSUNG_NFC_SGH-X700N");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xC6, (byte) 0x2E, (byte) 0xF6, (byte) 0xEF,
                (byte) 0x6B, (byte) 0xD2, (byte) 0x51, (byte) 0x90
        });

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x91, (byte) 0xFE, (byte) 0x7E, (byte) 0x73,
                (byte) 0xAD, (byte) 0x35, (byte) 0x51, (byte) 0x68
        });
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testSAMSUNG_NFC_SGH_X700N_C_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-SAMSUNG_NFC_SGH-X700N-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName("SAMSUNG_NFC_SGH-X700N");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x10, (byte) 0xBC, (byte) 0xEE, (byte) 0xC0,
                (byte) 0xA1, (byte) 0x29, (byte) 0x83, (byte) 0x26
        });

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9B, (byte) 0x27, (byte) 0x02, (byte) 0xBA,
                (byte) 0x85, (byte) 0xFE, (byte) 0x7F, (byte) 0x38
        });
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp02_04_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_04-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_04");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9F, (byte) 0x5D, (byte) 0x02, (byte) 0x20,
                (byte) 0x2D, (byte) 0xF3, (byte) 0x22, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testTestCardScp02_04_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_04-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_04");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testTestCardScp02_04_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_04-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_04");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xF9, (byte) 0x0B, (byte) 0xF1, (byte) 0x91,
                (byte) 0x5D, (byte) 0x36, (byte) 0x54, (byte) 0x34});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2C, (byte) 0x3A, (byte) 0x49, (byte) 0x8C,
                (byte) 0xDA, (byte) 0x30, (byte) 0x0D, (byte) 0xAD});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp02_05_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_05-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_05");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9F, (byte) 0x5D, (byte) 0x02, (byte) 0x20,
                (byte) 0x2D, (byte) 0xF3, (byte) 0x22, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testTestCardScp02_05_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_05-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_05");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testTestCardScp02_05_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_05-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_05");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xF9, (byte) 0x0B, (byte) 0xF1, (byte) 0x91,
                (byte) 0x5D, (byte) 0x36, (byte) 0x54, (byte) 0x34});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2C, (byte) 0x3A, (byte) 0x49, (byte) 0x8C,
                (byte) 0xDA, (byte) 0x30, (byte) 0x0D, (byte) 0xAD});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    //     Test SCP 02 Option '14'
    @Test
    public void testTestCardScp02_14_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_14-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_14");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9F, (byte) 0x5D, (byte) 0x02, (byte) 0x20,
                (byte) 0x2D, (byte) 0xF3, (byte) 0x22, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testTestCardScp02_14_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_14-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_14");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp02_14_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_14-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_14");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    //SCP 02_45 Tests
    @Test
    public void testTestCardScp02_45_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_45-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_45");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9F, (byte) 0x5D, (byte) 0x02, (byte) 0x20,
                (byte) 0x2D, (byte) 0xF3, (byte) 0x22, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp02_45_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_45-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_45");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp02_45_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_45-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_45");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    //SCP 02_55 Tests
    @Test
    public void testTestCardScp02_55_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_55-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_55");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9F, (byte) 0x5D, (byte) 0x02, (byte) 0x20,
                (byte) 0x2D, (byte) 0xF3, (byte) 0x22, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp02_55_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_55-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_55");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp02_55_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test02_55-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest02_55");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x80, (byte) 0x74, (byte) 0x4A, (byte) 0x9F,
                (byte) 0x76, (byte) 0x69, (byte) 0x88, (byte) 0xB8});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    /*@Test
    public void testTestCardScp03_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_65");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testTestCardScp03_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_65");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_65");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_ENC_AND_MAC_AND_RMAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03-C_ENC_AND_MAC_AND_RMAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_65");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_ENC_AND_MAC_AND_RDNC_AND_RMAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03-C_ENC_AND_MAC_AND_CDEC_AND_RMAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_65");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }


    @Test
    public void testTestCardScp03_6D_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_6D-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_6D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }


    @Test
    public void testTestCardScp03_6D_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_6D-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_6D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_6D_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_6D-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_6D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }


    @Test
    public void testTestCardScp03_6D_CENC_AND_MAC_AND_RMAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_6D-C_ENC_AND_MAC_AND_RMAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_6D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_05_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_05-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_05");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp03_05_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_05-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_05");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_05_ENC_AND_MAC_RENC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_05-C_ENC_AND_MAC_RENC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_05");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_0D_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_0D-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_0D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp03_0D_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_0D-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_0D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_0D_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_0D-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_0D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_2D_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_2D-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_2D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp03_2D_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_2D-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_2D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_2D_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_2D-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_2D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_2D_CENC_AND_MAC_AND_RMAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_2D-C_ENC_AND_MAC_AND_RMAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_2D");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }


    @Test
    public void testTestCardScp03_25_NoSecurityLevel() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_25-NO_SECURITY_LEVEL.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_25");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();
    }

    @Test
    public void testTestCardScp03_25_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_25-MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_25");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }


    @Test
    public void testTestCardScp03_25_ENC_AND_MAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_25-C_ENC_AND_MAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_25");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }

    @Test
    public void testTestCardScp03_25_CENC_AND_MAC_AND_RMAC() throws  CardException, IOException {
        Commands commands = createCommands("/HelloWorld-Test03_25-C_ENC_AND_MAC_AND_RMAC.txt");
        CardConfigFactory cardConfigFactory = new CardConfigFactory();
        File localXml = new File(MainHelloWorldTest.class.getResource("/test-config.xml").getFile());
        cardConfigFactory.registerLocalCardConfigsFromXML(localXml);
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("CardTest03_25");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIsd());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null);

        URL url = MainHelloWorldTest.class.getResource("/cap/HelloWorld-2_1_2.cap");
        File file = new File(url.getFile());

        // Installing Applet
        InputStream is = new FileInputStream(file);
        byte[] convertedBuffer = CapConverter.convert(is);
        commands.load(convertedBuffer, (byte) 0x10);
        commands.installForInstallAndMakeSelectable(
                PACKAGE_ID,
                APPLET_ID,
                APPLET_ID,
                Conversion.hexToArray("00"), null);

        // Selecting Hello World Applet
        commands.select(APPLET_ID);

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU((byte) 0x00 // CLA
                , (byte) 0x00 // INS
                , (byte) 0x00 // P1
                , (byte) 0x00 // P2
                , HELLO_WORLD // DATA
        );

        commands.getCardChannel().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xEE, (byte) 0x47, (byte) 0x03,
                (byte) 0xD3, (byte) 0x94, (byte) 0x20, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_C_MAC_AND_R_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCardChannel().close();

    }*/
}
