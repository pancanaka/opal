package fr.xlim.ssd.opal.library.config;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.utilities.Conversion;
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
        assertEquals(13,key.getVersion());
        assertEquals(1,key.getId());
        assertArrayEquals(new byte[]{(byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA,
                (byte)0xCA, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, (byte)0xCA, (byte)0xCA, (byte)0xCA,
                (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA},key.getValue());

        key = cardConfig.getSCKeys()[1];
        assertEquals(SCGPKey.class,key.getClass());
        assertEquals(KeyType.DES_ECB,key.getType());
        assertEquals(13,key.getVersion());
        assertEquals(2,key.getId());
        assertArrayEquals(new byte[]{0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D,
                (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA, (byte)0xCA,
                0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D},key.getValue());

        key = cardConfig.getSCKeys()[2];
        assertEquals(SCGPKey.class,key.getClass());
        assertEquals(KeyType.DES_ECB,key.getType());
        assertEquals(13,key.getVersion());
        assertEquals(3,key.getId());
        assertArrayEquals(new byte[]{(byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA,
                0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D, (byte)0xCA, 0x2D,
                 (byte)0xCA, 0x2D, (byte)0xCA, 0x2D},key.getValue());
    }

    @Test
    public void testSaveLocalCardConfigsToXML() {
        LinkedList<byte[]> atrs = new LinkedList<byte[]>();
        atrs.add(Conversion.hexToArray("01 23 45 67 89"));
        atrs.add(Conversion.hexToArray("AB CD EF 01 23"));
        LinkedList<SCKey> keys = new LinkedList<SCKey>();
        keys.add(new SCGPKey((byte)0,(byte)1,KeyType.AES_CBC,Conversion.hexToArray("01 23")));
        keys.add(new SCGPKey((byte)2,(byte)3,KeyType.DES_CBC,Conversion.hexToArray("45 67")));
        keys.add(new SCGPKey((byte)4,(byte)5,KeyType.DES_ECB,Conversion.hexToArray("89 AB")));
        keys.add(new SCGPKey((byte)6,(byte)7,KeyType.MOTHER_KEY,Conversion.hexToArray("CD EF")));
        CardConfig cardConfig = new CardConfig("name","description",atrs,
                Conversion.hexToArray("01 23 45 67 89"), SCPMode.SCP_02_05,
                "T=0", keys.toArray(new SCKey[0]),"implementation");
        cardConfig.setLocal(true);
        cardConfigFactory.registerLocalCardConfig(cardConfig);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
        cardConfigFactory.saveLocalCardConfigsToXML(baos);
        byte [] expected =  ("<?xml version=\"1.0\" ?><cards><card><name>name</name><description>description" +
                "</description><atrs class=\"cards\"><atr>01 23 45 67 89 </atr><atr>AB CD EF 01 23 </atr></atrs>" +
                "<isd>01 23 45 67 89 </isd><scp>02_05</scp><tp>T=0</tp><keys><key><type>AES_CBC</type><version>0" +
                "</version><id>1</id><value>01 23 </value></key><key><type>DES_CBC</type><version>2</version><id>3</id>" +
                "<value>45 67 </value></key><key><type>DES_ECB</type><version>4</version><id>5</id><value>89 AB " +
                "</value></key><key><type>MOTHER_KEY</type><version>6</version><id>7</id><value>CD EF </value></key>" +
                "</keys><implementation>implementation</implementation></card></cards>").getBytes();
        assertArrayEquals(expected,baos.toByteArray());
    }

}
