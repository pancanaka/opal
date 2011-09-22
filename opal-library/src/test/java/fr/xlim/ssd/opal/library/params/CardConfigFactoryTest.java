package fr.xlim.ssd.opal.library.params;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class CardConfigFactoryTest {

    private static List<String> cardNames = new LinkedList<String>();
    private static CardConfig [] cardConfigs = null;

    @BeforeClass
    public static void getAllCardName() throws JDOMException, IOException {
        InputStream input = CardConfigFactoryTest.class.getResourceAsStream("/config.xml");
        Reader reader = new InputStreamReader(input);

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(reader);

        Element root = document.getRootElement();
        assertNotNull(root);

        List<Element> cards = root.getChildren("card");
        assertTrue(cards.size() > 0);

        for(Element card : cards) {
            cardNames.add(card.getAttributeValue("name"));
        }
    }

    @BeforeClass
    public static void getAllCardConfigs() throws CardConfigNotFoundException {
         cardConfigs = CardConfigFactory.getAllCardConfigs();
    }

    @Test
    public void testAllExistingCodeList() throws CardConfigNotFoundException {
        for (String s : cardNames) {
            CardConfig config = CardConfigFactory.getCardConfig(s);
            assertNotNull(config);
        }
    }

    @Test(expected = CardConfigNotFoundException.class)
    public void testNotExistingCode() throws CardConfigNotFoundException {
        CardConfigFactory.getCardConfig("dummy");
    }

    @Test
    public void testGetAllCardConfigsSize() {
        assertEquals(cardNames.size(), cardConfigs.length);
    }

    @Test
    public void testGetAllCardConfigsNames() {
        for (CardConfig cardConfig : cardConfigs) {
            assert(cardNames.contains(cardConfig.getName()));
        }
    }

    @Test
    public void testGetAllCardFromATR() throws JDOMException, IOException, CardConfigNotFoundException {
        InputStream input = CardConfigFactoryTest.class.getResourceAsStream("/atr.xml");
        Reader reader = new InputStreamReader(input);

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(reader);

        Element root = document.getRootElement();
        List<Element> cards = root.getChildren("card");

        for(Element card : cards) {
            String atr = card.getAttribute("ATR").getValue();
            Element config = card.getChild("config");
            String  configName = config.getValue();
            CardConfig response = CardConfigFactory.getCardConfig(Conversion.hexToArray(atr));
            assertEquals(response.getName(),configName);
        }
    }
}
