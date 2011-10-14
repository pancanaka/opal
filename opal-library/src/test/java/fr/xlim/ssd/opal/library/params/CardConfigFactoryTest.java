package fr.xlim.ssd.opal.library.params;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import fr.xlim.ssd.opal.library.KeyType;
import fr.xlim.ssd.opal.library.SCGPKey;
import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CardConfigFactoryTest {

    private List<String> cardNames = new LinkedList<String>();
    private CardConfigFactory cardConfigFactory;

    @Before
    public void fillCardConfigFactory() {
        cardConfigFactory = new CardConfigFactory();
    }

    @Before
    public void getAllCardName() throws JDOMException, IOException {
        InputStream input = CardConfigFactoryTest.class.getResourceAsStream("/config.xml");
        Reader reader = new InputStreamReader(input);

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(reader);

        Element root = document.getRootElement();
        assertNotNull(root);

        List<Element> cards = root.getChildren("card");
        assertTrue(cards.size() > 0);

        for(Element card : cards) {
            cardNames.add(card.getChild("name").getValue());
        }
    }

    @Test
    public void testAllExistingCodeList() {
        for (String s : cardNames) {
            CardConfig config = cardConfigFactory.getCardConfigByName(s);
            assertNotNull("Looking for card " + s,config);
        }
    }

    @Test
    public void testNotExistingCode() {
        assertNull(cardConfigFactory.getCardConfigByName("dummy"));
    }

    @Test
    public void testGetAllCardConfigsSize() {
        assertEquals(cardNames.size(), cardConfigFactory.getCardConfigs().size());
        assertEquals(21,cardConfigFactory.getCardConfigs().size());
    }

    @Test
    public void testGetAllCardConfigsNames() {
        for (CardConfig cardConfig : cardConfigFactory.getCardConfigs()) {
            assert(cardNames.contains(cardConfig.getName()));
        }
    }

    @Test
    public void testUnmarshalGemXpresso211() {
        CardConfig cardConfig = cardConfigFactory.getCardConfigByName("GemXpresso211");
        assertEquals("GemXpresso211",cardConfig.getName());
        assertEquals("GemXpresso 211", cardConfig.getDescription());
        assertEquals("fr.xlim.ssd.opal.library.commands.GemXpresso211Commands", cardConfig.getImplementation());
        assertEquals(1,cardConfig.getAtrs().size());
        assertArrayEquals(new byte[]{0x3B, 0x6E, 0x00, 0x00, (byte) 0x80, 0x31, (byte) 0x80, 0x65, (byte) 0xB0, 0x03, 0x02,
                0x01, 0x5E, (byte) 0x83, 0x00, 0x00, (byte) 0x90, 0x00}, cardConfig.getAtrs().get(0));
        assertArrayEquals(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x18, 0x43, 0x4D}, cardConfig.getIsd());
        assertEquals(SCPMode.SCP_01_05, cardConfig.getScpMode());
        assertEquals("T=0",cardConfig.getTransmissionProtocol());
        assertEquals(3,cardConfig.getSCKeys().length);

        SCKey key = cardConfig.getSCKeys()[0];
        assertEquals(SCGPKey.class,key.getClass());
        assertEquals(KeyType.DES_ECB,key.getType());
        assertEquals(13,key.getSetVersion());
        assertEquals(1,key.getId());
        assertArrayEquals(new byte[]{(byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA,
                (byte)0xCA, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, (byte)0xCA, (byte)0xCA, (byte)0xCA,
                (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA},key.getData());

        key = cardConfig.getSCKeys()[1];
        assertEquals(SCGPKey.class,key.getClass());
        assertEquals(KeyType.DES_ECB,key.getType());
        assertEquals(13,key.getSetVersion());
        assertEquals(2,key.getId());
        assertArrayEquals(new byte[]{0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D,
                (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA,
                0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D},key.getData());

        key = cardConfig.getSCKeys()[2];
        assertEquals(SCGPKey.class,key.getClass());
        assertEquals(KeyType.DES_ECB,key.getType());
        assertEquals(13,key.getSetVersion());
        assertEquals(3,key.getId());
        assertArrayEquals(new byte[]{(byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA,
                0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D,
                 (byte)0xCA, 0x2D, (byte)0xCA, 0x2D},key.getData());
    }
}
