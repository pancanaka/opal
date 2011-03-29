package fr.xlim.ssd.opal.library.params;

import fr.xlim.ssd.opal.library.*;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Delivers card configuration CardConfig
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @see CardConfig
 */
public class CardConfigFactory {

    /**
     * Get all card configs.
     *
     * @return a Map of CardConfig objects with the name of the config as a key
     * @throws CardConfigNotFoundException if an error occured while reading the XML file
     */
    public static CardConfig[] getAllCardConfigs()
            throws CardConfigNotFoundException {

        byte[] isd;
        SCPMode scpMode;
        String tp;
        SCKey[] keys;
        String impl;
        CardConfig configs[];

        try {
            
            InputStream input = CardConfigFactory.class.getResourceAsStream("/config.xml");

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(input);

            NodeList cards = document.getElementsByTagName("card");
            Element currentCard = null;
            configs = new CardConfig[cards.getLength()];

            // looking for the card identifier in config.xml file
            for (int i = 0; i < cards.getLength(); i++) {
                currentCard = (Element) cards.item(i);

                // set and return CardConfig
                isd = getISD(currentCard);
                scpMode = getSCP(currentCard);
                tp = getTP(currentCard);
                keys = getKeys(currentCard);
                impl = getImpl(currentCard);

                configs[i] = new CardConfig(isd, scpMode, tp, keys, impl);
            }

        } catch (IOException e) {
            throw new CardConfigNotFoundException("cannot read the config.xml file: " + e.getMessage());
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading config.xml file:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading config.xml file:" + e.getMessage());
        }

        

        return configs;

    }

    /**
     * Get the card config based on its name.
     *
     * @param cardName the card identifier in config.xml
     * @return a CardConfig object if the card identifier is found
     * @throws CardConfigNotFoundException if card configuration not found
     */
    public static CardConfig getCardConfig(String cardName)
            throws CardConfigNotFoundException {

        byte[] isd;
        SCPMode scpMode;
        String tp;
        SCKey[] keys;
        String impl;

        try {

            InputStream input = CardConfigFactory.class.getResourceAsStream("/config.xml");

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(input);

            NodeList cards = document.getElementsByTagName("card");
            Element desiredCard = null;

            // looking for the card identifier in config.xml file
            for (int i = 0; i < cards.getLength(); i++) {
                if (((Element) cards.item(i)).getAttribute("name").equals(cardName)) {
                    desiredCard = (Element) cards.item(i);
                }
            }

            if (desiredCard == null) {
                throw new CardConfigNotFoundException("Card \"" + cardName + "\" not found");
            }

            // set and return CardConfig
            isd = getISD(desiredCard);
            scpMode = getSCP(desiredCard);
            tp = getTP(desiredCard);
            keys = getKeys(desiredCard);
            impl = getImpl(desiredCard);

        } catch (IOException e) {
            throw new CardConfigNotFoundException("cannot read the config.xml file: " + e.getMessage());
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading config.xml file:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading config.xml file:" + e.getMessage());
        }

        return new CardConfig(isd, scpMode, tp, keys, impl);
    }

    /**
     * Get the value between isdAID tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return a byte array with the issuer security domain AID value
     */
    private static byte[] getISD(Element card) {
        return Conversion.hexToArray(((Element) card.getElementsByTagName("isdAID").item(0)).getAttribute("value"));
    }

    /**
     * Get the value between scpMode tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return the SCP mode
     */
    private static SCPMode getSCP(Element card) {
        String scp = ((Element) card.getElementsByTagName("scpMode").item(0)).getAttribute("value");
        SCPMode res = null;
        if (scp.equals("01_05")) {
            res = SCPMode.SCP_01_05;
        } else if (scp.equals("01_15")) {
            res = SCPMode.SCP_01_15;
        } else if (scp.equals("02_15")) {
            res = SCPMode.SCP_02_15;
        }
        return res;
    }

    /**
     * Get the value between transmissionProtocol tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return the transmission protocol used
     */
    private static String getTP(Element card) {
        return ((Element) card.getElementsByTagName("transmissionProtocol").item(0)).getAttribute("value");
    }

    /**
     * Get the keys between key tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return the credentials keys
     */
    private static SCKey[] getKeys(Element card) {

        NodeList keysElem = card.getElementsByTagName("key");
        SCKey[] keys = new SCKey[keysElem.getLength()];

        // for each key in the Element
        for (int i = 0; i < keysElem.getLength(); i++) {
            String keyType = ((Element) keysElem.item(i)).getAttribute("type");
            String keyVersionNumber = ((Element) keysElem.item(i)).getAttribute("keyVersionNumber");
            String keyDatas = ((Element) keysElem.item(i)).getAttribute("keyDatas");
            if (keyType.equals("DES_ECB")) {
                String keyId = ((Element) keysElem.item(i)).getAttribute("keyId");
                keys[i] = new SCGPKey((byte) Integer.parseInt(keyVersionNumber), (byte) Integer.parseInt(keyId),
                        KeyType.DES_ECB, Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("DES_CBC")) {
                String keyId = ((Element) keysElem.item(i)).getAttribute("keyId");
                keys[i] = new SCGPKey((byte) Integer.parseInt(keyVersionNumber), (byte) Integer.parseInt(keyId),
                        KeyType.DES_CBC, Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("SCGemVisa2")) {
                keys[i] = new SCGemVisa2((byte) Integer.parseInt(keyVersionNumber), Conversion.hexToArray(keyDatas));
            } else if (keyType.equals("SCGemVisa")) {
                keys[i] = new SCGemVisa((byte) Integer.parseInt(keyVersionNumber), Conversion.hexToArray(keyDatas));
            }
        }
        return keys;
    }

    /**
     * Get the value between defaultImpl tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return A string with the name of the implementation
     */
    private static String getImpl(Element card) {
        return card.getAttribute("defaultImpl");
    }

    /**
     * Return the card config based on the ATR returns by the card.
     *
     * @param atr the ATR returns by the card
     * @return the name of the card config
     * @throws CardConfigNotFoundException if ATR not found
     */
    public static String getCardConfig(byte[] atr)
            throws CardConfigNotFoundException {

        String config;

        try {

            InputStream input = CardConfigFactory.class.getResourceAsStream("/atr.xml");

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

        } catch (IOException e) {
            throw new CardConfigNotFoundException("cannot read the atr.xml file: " + e.getMessage());
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading atr.xml file: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading atr.xml file: " + e.getMessage());
        }

        return config;
    }

    /**
     * Get the value between config tags from an element in the atr.xml
     *
     * @param atr an element in the atr.xml
     * @return a byte array with the name of configuration for an smart card
     * @throws CardConfigNotFoundException if ATR tags not found
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
