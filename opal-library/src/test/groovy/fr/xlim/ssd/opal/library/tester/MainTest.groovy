package fr.xlim.ssd.opal.library.tester

import fr.xlim.ssd.opal.library.commands.CardChannelMock
import fr.xlim.ssd.opal.library.commands.GP2xCommands
import fr.xlim.ssd.opal.library.commands.SecLevel
import fr.xlim.ssd.opal.library.config.CardConfig
import fr.xlim.ssd.opal.library.utilities.CapConverter
import fr.xlim.ssd.opal.library.utilities.Conversion
import fr.xlim.ssd.opal.library.utilities.RandomGenerator
import javax.smartcardio.CardChannel
import javax.smartcardio.CommandAPDU
import fr.xlim.ssd.opal.library.CardConfigFactory

class MainTest extends spock.lang.Specification {


    byte[] HELLO_WORLD = ['H', 'E', 'L', 'L', 'O']

    byte[] APPLET_ID = [0xA0, 0x00, 0x00, 0x00, 0x62, 0x03, 0x01, 0x0C, 0x01, 0x01]

    byte[] PACKAGE_ID = [0xA0, 0x00, 0x00, 0x00, 0x62, 0x03, 0x01, 0x0C, 0x01]

    byte[] IV_ZERO = [0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]

    def testCards(String filename, String cardName, SecLevel secLevel, byte[] firstRandomSequence,
                  byte[] secondRandomSequence) {

        RandomGenerator.setRandomSequence(null)
        InputStream input = MainTest.class.getResourceAsStream("/fr/xlim/ssd/opal/library/test/cards" + filename)
        Reader reader = new InputStreamReader(input)
        CardChannel cardChannel = new CardChannelMock(reader)
        GP2xCommands commands = new GP2xCommands()
        commands.setCardChannel(cardChannel)
        CardConfig cardConfig = new CardConfigFactory().getCardConfigByName(cardName)
        commands.setOffCardKeys cardConfig.getSCKeys()
        commands.select cardConfig.getIsd()

        RandomGenerator.setRandomSequence(firstRandomSequence)

        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode())
        commands.externalAuthenticate(secLevel)
        commands.installForLoad(PACKAGE_ID, cardConfig.getIsd(), null)

        URL url = MainTest.class.getResource("/cap/HelloWorld-2_1_2.cap")
        File file = new File(url.getFile())

        // Installing Applet
        InputStream is = new FileInputStream(file)
        byte[] convertedBuffer = CapConverter.convert(is)
        commands.load(convertedBuffer, (byte) 0x10)
        commands.installForInstallAndMakeSelectable(PACKAGE_ID, APPLET_ID, APPLET_ID, Conversion.hexToArray("00"), null)

        // Selecting Hello World Applet
        commands.select(APPLET_ID)

        // Using Hello World Applet
        CommandAPDU hello = new CommandAPDU(
                0x00, // CLA
                0x00, // INS
                0x00, // P1
                0x00, // P2
                HELLO_WORLD // DATA
        )

        commands.getCardChannel().transmit(hello)

        // Select Security Domain to delete Hello World Applet & Package
        commands.select(cardConfig.getIsd())

        RandomGenerator.setRandomSequence(secondRandomSequence);
        commands.initializeUpdate(cardConfig.getDefaultInitUpdateP1(), cardConfig.getDefaultInitUpdateP2(), cardConfig.getScpMode());
        commands.externalAuthenticate(secLevel);
        commands.deleteOnCardObj(APPLET_ID, false);
        commands.deleteOnCardObj(PACKAGE_ID, false);
        commands.getCardChannel().close()

        where:

        [filename, cardName, secLevel, firstRandomSequence, secondRandomSequence] << [
                [
                        "/HelloWorld-Cyberflex_Palmera_V3-NO_SECURITY_LEVEL.txt",
                        "Cyberflex_Palmera_V3",
                        SecLevel.NO_SECURITY_LEVEL,
                        [0x5F, 0x29, 0x97, 0x7B, 0xBD, 0x7C, 0x23, 0x8C],
                        [0x2A, 0x98, 0xE0, 0x3A, 0xA7, 0x38, 0xD5, 0x73]
                ],
                [
                        "/HelloWorld-Cyberflex_Palmera_V3-C_MAC.txt",
                        "Cyberflex_Palmera_V3",
                        SecLevel.C_MAC,
                        [0xE8, 0x2A, 0xFE, 0x1D, 0xE5, 0x95, 0x97, 0x83],
                        [0xA2, 0xDA, 0x7B, 0x90, 0x5F, 0xC6, 0x17, 0x0E]
                ]
        ]

    }
}
