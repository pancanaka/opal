package fr.xlim.ssd.opal.library.tester;

import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.commands.CardChannelMock;
import fr.xlim.ssd.opal.library.commands.Commands;
import fr.xlim.ssd.opal.library.commands.GP2xCommands;
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

    @Test
    public void testCyberflexPalmeraV3NoSecurityLevel() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_V3-NO_SECURITY_LEVEL.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Cyberflex_Palmera_V3");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte) 0x5B, (byte) 0xCD, (byte) 0xC5, (byte) 0xD1,
                (byte) 0x73, (byte) 0x02, (byte) 0x97, (byte) 0xEC});

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
                (byte) 0xCE, (byte) 0x6B, (byte) 0x00, (byte) 0x3F,
                (byte) 0xF6, (byte) 0x89, (byte) 0xE3, (byte) 0x51});
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

    /*
    @Test
    public void testCyberflexPalmeraV3C_ENC_AND_MAC() throws CardConfigNotFoundException, CardException, FileNotFoundException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_V3-C_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Cyberflex_Palmera_V3");
        commands.setOffCardKeys(cardConfig.getSCKeys());
        commands.select(cardConfig.getIssuerSecurityDomainAID());

        RandomGenerator.setRandomSequence(new byte[]{
                (byte)0x15, (byte)0x0B, (byte)0x6F, (byte)0x1F,
                (byte)0x19, (byte)0x81, (byte)0x5D, (byte)0x07});

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
                (byte)0x40, (byte)0x20, (byte)0x62, (byte)0x43,
                (byte)0xAA, (byte)0x6B, (byte)0xD4, (byte)0xDA});
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(SecLevel.C_ENC_AND_MAC);
        commands.deleteOnCardObj(APPLET_ID,false);
        commands.deleteOnCardObj(PACKAGE_ID,false);

        commands.getCc().close();
    } */
}
