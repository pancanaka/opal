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
package fr.xlim.ssd.opal.library.config;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import fr.xlim.ssd.opal.library.CardConfigFactory;
import fr.xlim.ssd.opal.library.commands.GP2xCommands;
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
        assertEquals(23,cardConfigFactory.getCardConfigs().size());
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
        assertEquals("fr.xlim.ssd.opal.library.commands.GemXpresso211Commands", cardConfig.getImplementation().getClass().getCanonicalName());
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
        keys.add(new SCGemVisa((byte)6, Conversion.hexToArray("CD EF 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31")));
        keys.add(new SCGemVisa2((byte)8, Conversion.hexToArray("CD EF 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31")));
        CardConfig cardConfig = new CardConfig("name","description",atrs,
                Conversion.hexToArray("01 23 45 67 89"), SCPMode.SCP_02_05,
                "T=0", keys.toArray(new SCKey[0]), new GP2xCommands());
        cardConfigFactory.deleteAllCardConfig();
        cardConfigFactory.registerLocalCardConfig(cardConfig);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
        cardConfigFactory.saveLocalCardConfigsToXML(baos);
        byte [] expected =  ("<?xml version=\"1.0\" ?><cards><card><name>name</name><description>description" +
                "</description><atrs class=\"cards\"><atr>01 23 45 67 89 </atr><atr>AB CD EF 01 23 </atr></atrs>" +
                "<isd>01 23 45 67 89 </isd><scp>02_05</scp><tp>T=0</tp><keys><key><type>AES_CBC</type><version>0" +
                "</version><id>1</id><value>01 23 </value></key><key><type>DES_CBC</type><version>2</version><id>3" +
                "</id><value>45 67 </value></key><key><type>DES_ECB</type><version>4</version><id>5</id><value>89 AB " +
                "</value></key><key><type>SCGemVisa</type><version>6</version><value>CD EF 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 </value></key>" +
                "<key><type>SCGemVisa2</type><version>8</version><value>CD EF 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 </value></key>" +
                "</keys><implementation class=\"fr.xlim.ssd.opal.library.commands.GP2xCommands\">" +
                "fr.xlim.ssd.opal.library.commands.GP2xCommands</implementation></card></cards>").getBytes();
        assertArrayEquals(expected,baos.toByteArray());
    }

}
