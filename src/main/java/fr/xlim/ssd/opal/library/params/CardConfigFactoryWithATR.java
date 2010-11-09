/**
 * Delivers card configuration CardConfig when you only know the Card ATR
 * @author Guillaume Bouffard
 */
package fr.xlim.ssd.opal.library.params;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.InputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class CardConfigFactoryWithATR {

    private final static Logger logger = LoggerFactory.getLogger(CardConfigFactoryWithATR.class);

    /**
     * @param cardName  the card identifiant in config.xml
     * @return          a CardConfig object if the card identifiant is found
     * @throws CardNotFoundException
     */
    public static String getCardConfig(byte[] atr)
            throws CardConfigNotFoundException {

        String config;

        try {

            InputStream input = CardConfigFactoryWithATR.class.getResourceAsStream("/atr.xml");

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(input);

            NodeList cards = document.getElementsByTagName("card");
            Element desiredCard = null;

            // looking for the card identifiant in atr.xml file
            for (int i = 0; i < cards.getLength(); i++) {

                if (Arrays.equals(Conversion.hexToArray(((Element) cards.item(i)).getAttribute("ATR")), atr)) {
                    desiredCard = (Element) cards.item(i);
                }
            }

            if (desiredCard == null) {
                throw new CardConfigNotFoundException("ATR \"" + Conversion.arrayToHex(atr) + "\" not found");
            }

            config = getConfig(desiredCard);

            logger.debug(config);

        } catch (IOException e) {
            throw new CardConfigNotFoundException("cannot read the atr.xml file");
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading atr.xml file");
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading atr.xml file");
        }

        return config;
    }

    /**
     * Get the value between config tags from an element in the atr.xml
     * @param atr  an element in the atr.xml
     * @return      a byte array with the name of configuration for an smart card
     */
    private static String getConfig(Element atr) throws CardConfigNotFoundException {

        NodeList child = atr.getChildNodes();
        String config = null;

        for (int i = 0; i < child.getLength(); ++i) {
            Node node = child.item(i);
            if (node.getNodeName().equals("config")) {
                config = node.getTextContent();
            }
        }

        if (config == null) {
            throw new CardConfigNotFoundException("Configuration value not found");
        }

        return config;
    }
}
