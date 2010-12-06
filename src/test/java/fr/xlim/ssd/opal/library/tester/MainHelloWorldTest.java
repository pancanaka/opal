package fr.xlim.ssd.opal.library.tester;

import fr.xlim.ssd.opal.library.commands.CardChannelMock;
import fr.xlim.ssd.opal.library.commands.Commands;
import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import fr.xlim.ssd.opal.library.commands.GP2xCommandsTest;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import org.junit.Test;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class MainHelloWorldTest {

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
    public void testCyberflexPalmeraV3NoSecurityLevel() throws CardConfigNotFoundException, CardException {
        Commands commands = createCommands("/HelloWorld-Cyberflex_Palmera_v3_ENC_AND_MAC.txt");
        CardConfig cardConfig = CardConfigFactory.getCardConfig("Cyberflex_Palmera_V3");
        commands.select(cardConfig.getIssuerSecurityDomainAID());
        commands.getCc().close();        
    }
}
