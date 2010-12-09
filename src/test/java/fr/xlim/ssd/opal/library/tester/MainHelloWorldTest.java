package fr.xlim.ssd.opal.library.tester;

import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.commands.CardChannelMock;
import fr.xlim.ssd.opal.library.commands.Commands;
import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import fr.xlim.ssd.opal.library.commands.GemXpresso211Commands;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.CapConverter;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.library.utilities.RandomGenerator;
import org.junit.Before;
import org.junit.Test;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import java.io.*;

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

    @Before
    public void resetRandomGenerator() {
        RandomGenerator.setRandomSequence(null);
    }

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
        commands.setCc(cardChannel);
        return commands;
    }

    private GP2xCommands createCommandsgemalto211(String filename) {
        GP2xCommands commands = new GemXpresso211Commands();
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
        commands.setCc(cardChannel);
        return commands;
    }

    /**
     * ****************************************************************************************************************
     * SCP 01
     * ****************************************************************************************************************
     */

    @Test
    public void testCyberflexPalmeraV3NoSecurityLevel() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_V3-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Cyberflex_Palmera_V3");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x5F, (byte) 0x29, (byte) 0x97, (byte) 0x7B,
                (byte) 0xBD, (byte) 0x7C, (byte) 0x23, (byte) 0x8C});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2A, (byte) 0x98, (byte) 0xE0, (byte) 0x3A,
                (byte) 0xA7, (byte) 0x38, (byte) 0xD5, (byte) 0x73});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testCyberflexPalmeraV3C_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_V3-C_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Cyberflex_Palmera_V3");

        org.junit.Assert.assertArrayEquals(cardConfig.getSCKeys()[0].getData(),
                Conversion.hexToArray("40 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 40 41 42 43 44 45 46 47"));
        org.junit.Assert.assertArrayEquals(cardConfig.getSCKeys()[1].getData(),
                Conversion.hexToArray("40 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 40 41 42 43 44 45 46 47"));
        org.junit.Assert.assertArrayEquals(cardConfig.getSCKeys()[2].getData(),
                Conversion.hexToArray("40 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 40 41 42 43 44 45 46 47"));

        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xE8, (byte) 0x2A, (byte) 0xFE, (byte) 0x1D,
                (byte) 0xE5, (byte) 0x95, (byte) 0x97, (byte) 0x83});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);

        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xA2, (byte) 0xDA, (byte) 0x7B, (byte) 0x90,
                (byte) 0x5F, (byte) 0xC6, (byte) 0x17, (byte) 0x0E});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testCyberflexPalmeraV3C_ENC_AND_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_V3-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Cyberflex_Palmera_V3");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x52, (byte) 0x61, (byte) 0xCF, (byte) 0xC6,
                (byte) 0xE2, (byte) 0x3E, (byte) 0xEB, (byte) 0xD1});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x96, (byte) 0x8F, (byte) 0x33, (byte) 0x47,
                (byte) 0x06, (byte) 0x50, (byte) 0x52, (byte) 0xC1});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testInfineon_JTOP_V2_16kNoSecurityLevel() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Infineon_JTOP_V2_16k-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Infineon_JTOP_V2_16k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x13, (byte) 0x47, (byte) 0x11, (byte) 0x0D,
                (byte) 0xEC, (byte) 0xC0, (byte) 0xCC, (byte) 0xF0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x72, (byte) 0xEA, (byte) 0x4A, (byte) 0x8B,
                (byte) 0x68, (byte) 0xB5, (byte) 0xC8, (byte) 0x81});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testInfineon_JTOP_V2_16kC_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Infineon_JTOP_V2_16k-C_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Infineon_JTOP_V2_16k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x6E, (byte) 0xA6, (byte) 0xAC, (byte) 0x96,
                (byte) 0xD9, (byte) 0x51, (byte) 0x78, (byte) 0x2E});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x53, (byte) 0xD0, (byte) 0x0B, (byte) 0x94,
                (byte) 0x52, (byte) 0xF3, (byte) 0xF4, (byte) 0xA3});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testInfineon_JTOP_V2_16kC_ENC_AND_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Infineon_JTOP_V2_16k-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Infineon_JTOP_V2_16k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x81, (byte) 0xF8, (byte) 0xF0, (byte) 0x44,
                (byte) 0xB4, (byte) 0x72, (byte) 0xB9, (byte) 0x4D});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x24, (byte) 0x68, (byte) 0xEE, (byte) 0x39,
                (byte) 0xDF, (byte) 0x6B, (byte) 0xA2, (byte) 0x75});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testOberthur_Cosmo_Dual_72kNoSecurityLevel() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Oberthur_Cosmo_Dual_72k-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Oberthur_Cosmo_Dual_72k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x76, (byte) 0x6E, (byte) 0x19, (byte) 0xB0,
                (byte) 0xCD, (byte) 0x65, (byte) 0x96, (byte) 0x49});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2A, (byte) 0x6F, (byte) 0xD0, (byte) 0x6E,
                (byte) 0x9C, (byte) 0x37, (byte) 0x4F, (byte) 0xA3});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testOberthur_Cosmo_Dual_72kC_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Oberthur_Cosmo_Dual_72k-C_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Oberthur_Cosmo_Dual_72k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x0A, (byte) 0x09, (byte) 0x5B, (byte) 0x68,
                (byte) 0xD2, (byte) 0x26, (byte) 0x6B, (byte) 0xC9});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x5F, (byte) 0x13, (byte) 0xC1, (byte) 0xE4,
                (byte) 0xC8, (byte) 0x91, (byte) 0xE8, (byte) 0x66});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testOberthur_Cosmo_Dual_72kC_ENC_AND_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Oberthur_Cosmo_Dual_72k-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Oberthur_Cosmo_Dual_72k");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xB5, (byte) 0xC6, (byte) 0x18, (byte) 0xD6,
                (byte) 0xF7, (byte) 0xE0, (byte) 0xEA, (byte) 0x18});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x16, (byte) 0xC4, (byte) 0x98, (byte) 0xE8,
                (byte) 0xFE, (byte) 0x6B, (byte) 0x8B, (byte) 0xDA});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testOberthurCosmopolicNoSecurityLevel() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Oberthur_CosmopolIC-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Oberthur_Cosmopolic");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x54, (byte) 0x66, (byte) 0x3A, (byte) 0x80,
                (byte) 0x34, (byte) 0x01, (byte) 0x93, (byte) 0x15});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x82, (byte) 0xF2, (byte) 0xE6, (byte) 0x11,
                (byte) 0xEF, (byte) 0xDF, (byte) 0xBD, (byte) 0xF5});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testOberthurCosmopolicC_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Oberthur_CosmopolIC-C_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Oberthur_Cosmopolic");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x0C, (byte) 0x04, (byte) 0x53, (byte) 0x91,
                (byte) 0x5F, (byte) 0x7E, (byte) 0xA9, (byte) 0x34});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x56, (byte) 0x89, (byte) 0x04, (byte) 0x7A,
                (byte) 0xA1, (byte) 0x62, (byte) 0x82, (byte) 0x06});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testOberthurCosmopolicC_ENC_AND_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Oberthur_CosmopolIC-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Oberthur_Cosmopolic");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x26, (byte) 0x2B, (byte) 0x48, (byte) 0x3C,
                (byte) 0x4C, (byte) 0x0C, (byte) 0xA1, (byte) 0x33});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xE6, (byte) 0x7C, (byte) 0x65, (byte) 0xDE,
                (byte) 0x65, (byte) 0xE3, (byte) 0x00, (byte) 0x2B});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testGemCombiXpresso_Lite_R2_Std_Jcop30NoSecurityLevel() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-GemCombiXpresso_Lite_R2_Std_Jcop30-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("GemCombiXpresso_Lite_R2_Std_Jcop30");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xA3, (byte) 0x81, (byte) 0x21, (byte) 0xDC,
                (byte) 0xFF, (byte) 0xE4, (byte) 0x5F, (byte) 0x8C});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xEC, (byte) 0x2B, (byte) 0x63, (byte) 0xDE,
                (byte) 0xA7, (byte) 0x56, (byte) 0x9E, (byte) 0x98});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testGemCombiXpresso_Lite_R2_Std_Jcop30C_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-GemCombiXpresso_Lite_R2_Std_Jcop30-C_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("GemCombiXpresso_Lite_R2_Std_Jcop30");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x8A, (byte) 0xC4, (byte) 0xC7, (byte) 0x11,
                (byte) 0xAB, (byte) 0x66, (byte) 0x8C, (byte) 0xE4});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xCE, (byte) 0x14, (byte) 0x85, (byte) 0xEB,
                (byte) 0x10, (byte) 0x6D, (byte) 0xAB, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testGemCombiXpresso_Lite_R2_Std_Jcop30C_ENC_AND_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-GemCombiXpresso_Lite_R2_Std_Jcop30-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("GemCombiXpresso_Lite_R2_Std_Jcop30");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0xE1, (byte) 0xED, (byte) 0x0B, (byte) 0x24,
                (byte) 0x7E, (byte) 0x76, (byte) 0xBF, (byte) 0x1D});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x88, (byte) 0x79, (byte) 0xE7, (byte) 0x07,
                (byte) 0x7C, (byte) 0x17, (byte) 0x3E, (byte) 0x08});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testGemXplore3GC_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-GemXplore3G-C_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("GemXplore3G");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x73, (byte) 0xD2, (byte) 0xBA, (byte) 0x0E,
                (byte) 0xC5, (byte) 0x2E, (byte) 0x78, (byte) 0x42});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x4F, (byte) 0xE5, (byte) 0xA9, (byte) 0x48,
                (byte) 0x77, (byte) 0xFE, (byte) 0x1B, (byte) 0xA2});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testGemXplore3GC_ENC_AND_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-GemXplore3G-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("GemXplore3G");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x86, (byte) 0x38, (byte) 0x36, (byte) 0xB4,
                (byte) 0x97, (byte) 0x3A, (byte) 0xED, (byte) 0x79});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x2C, (byte) 0x22, (byte) 0x15, (byte) 0x8E,
                (byte) 0x36, (byte) 0xB8, (byte) 0x56, (byte) 0x4E});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testGemXpresso211isNoSecurityLevel() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommandsgemalto211("/HelloWorld-GemXpresso_211is-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("GemXpresso211");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x64, (byte) 0xE1, (byte) 0xA9, (byte) 0xDC,
                (byte) 0xB5, (byte) 0xAE, (byte) 0x5B, (byte) 0x06});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x6B, (byte) 0xE6, (byte) 0xDF, (byte) 0xA0,
                (byte) 0x6F, (byte) 0x9A, (byte) 0x41, (byte) 0x95});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testGemXpresso211isC_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommandsgemalto211("/HelloWorld-GemXpresso_211is-C_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("GemXpresso211");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x41, (byte) 0x86, (byte) 0x4E, (byte) 0xA7,
                (byte) 0xE7, (byte) 0x43, (byte) 0x6D, (byte) 0xCB});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x22, (byte) 0xAC, (byte) 0x61, (byte) 0x21,
                (byte) 0xA9, (byte) 0x2E, (byte) 0x73, (byte) 0xB4});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    @Test
    public void testGemXpresso211isC_ENC_AND_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommandsgemalto211("/HelloWorld-GemXpresso_211is-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("GemXpresso211");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x58, (byte) 0xAF, (byte) 0x89, (byte) 0x57,
                (byte) 0xAA, (byte) 0x82, (byte) 0xF7, (byte) 0xE0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x36, (byte) 0xB2, (byte) 0x62, (byte) 0xB2,
                (byte) 0xFE, (byte) 0x75, (byte) 0xA0, (byte) 0x5B});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }

    /**
     * ****************************************************************************************************************
     * SCP 02
     * ****************************************************************************************************************
     */

    @Test
    public void testJCOP21NoSecurityLevel() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-JCOP21-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("JCOP21");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x87, (byte) 0xB4, (byte) 0xF1, (byte) 0x07,
                (byte) 0x62, (byte) 0x54, (byte) 0x3F, (byte) 0xC0});

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.installForLoad(PACKAGE_ID, cardConfig.getIssuerSecurityDomainAID(), null);

        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");

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

        commands.getCc().transmit(hello);

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x9F, (byte) 0x5D, (byte) 0x02, (byte) 0x20,
                (byte) 0x2D, (byte) 0xF3, (byte) 0x22, (byte) 0x00});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.NO_SECURITY_LEVEL);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);

        commands.getCc().close();
    }
}
