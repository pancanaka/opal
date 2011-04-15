package fr.xlim.ssd.opal.library.params;

import fr.xlim.ssd.opal.library.*;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Delivers card configuration CardConfig
 *
 * @author Yorick Lesecque
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @see CardConfig
 */
public class CardConfigFactory {

    /// the logger
    private final static Logger logger = LoggerFactory.getLogger(CardConfigFactory.class);

    private static String configFile = "/src/main/resources/config.xml";

    public static void setConfigFile (String configFile) {
        CardConfigFactory.configFile = configFile;
    }

    public static String getConfigFile () {
        return CardConfigFactory.configFile;
    }

    /**
     * Get all card configs.
     *
     * @return a Map of CardConfig objects with the name of the config as a key
     * @throws CardConfigNotFoundException if an error occured while reading the XML file
     */
    public static CardConfig[] getAllCardConfigs()
            throws CardConfigNotFoundException {

        String name = null;
        String description = null;
        ATR[] atrs = null ;
        byte[] isd = null;
        SCPMode scpMode = null;
        String tp = null;
        SCKey[] keys = null;
        String impl = null;
        CardConfig configs[];

        try {
            File f = new File(System.getProperty("user.dir") + configFile);

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(f);

            NodeList cards = document.getElementsByTagName("card");
            Element currentCard = null;
            configs = new CardConfig[cards.getLength()];

            for (int i = 0; i < cards.getLength(); i++) {
                currentCard = (Element) cards.item(i);

                // set and return CardConfig
                name = getName(currentCard);
                description = getDescription(currentCard);
                atrs = getATRs(currentCard);
                isd = getISD(currentCard);
                scpMode = getSCP(currentCard);
                tp = getTP(currentCard);
                keys = getKeys(currentCard);
                impl = getImpl(currentCard);

                configs[i] = new CardConfig(name, description, atrs, isd, scpMode, tp, keys, impl);
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

        String name = null;
        String description = null;
        ATR[] atrs = null ;
        byte[] isd = null;
        SCPMode scpMode = null;
        String tp = null;
        SCKey[] keys = null;
        String impl = null;

        try {

            File f = new File(System.getProperty("user.dir") + configFile);

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(f);

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
            name = getName(desiredCard);
            description = getDescription(desiredCard);
            atrs = getATRs(desiredCard);
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

        return new CardConfig(name,description, atrs, isd, scpMode, tp, keys, impl);
    }

    /**
     * Get the value between name tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return A string with the name of the configuration
     */
    private static String getName(Element card) {
        return card.getAttribute("name");
    }

    /**
     * Get the value between description tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return A string with the description of the configuration
     */
    private static String getDescription(Element card) {
        NodeList desc = card.getElementsByTagName("description");

        if(desc.getLength() == 0) {
            return "No description";
        }
        else {
            return desc.item(0).getChildNodes().item(0).getNodeValue();
        }
    }

    /**
     * Get the keys between key tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return the credentials keys
     */
    private static ATR[] getATRs(Element card) {

        NodeList listATR = card.getElementsByTagName("ATR");
        ATR[] atrs = null;
        try {
            atrs = new ATR[listATR.getLength()];

            // for each key in the Element
            for (int i = 0; i < listATR.getLength(); i++) {
                atrs[i] = new ATR (Conversion.hexToArray (((Element) listATR.item(i)).getAttribute("value")));
            }
        } catch (ClassCastException e){
            logger.warn("There are not ATR list" + e.getMessage());
        } finally {
            return atrs;
        }
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
        }else if (scp.equals("02_04")) {
            res = SCPMode.SCP_02_04;
        }else if (scp.equals("02_05")) {
            res = SCPMode.SCP_02_05;
        }else if (scp.equals("02_14")) {
            res = SCPMode.SCP_02_14;
        }else if (scp.equals("02_0A")) {
            res = SCPMode.SCP_02_0A;
        }else if (scp.equals("02_45")) {
            res = SCPMode.SCP_02_45;
        }else if (scp.equals("02_55")) {
            res = SCPMode.SCP_02_55;
        }else if (scp.equals("03_65")) {
            res = SCPMode.SCP_03_65;
        }else if (scp.equals("03_6D")) {
            res = SCPMode.SCP_03_6D;
        }else if (scp.equals("03_05")) {
            res = SCPMode.SCP_03_05;
        }else if (scp.equals("03_0D")) {
            res = SCPMode.SCP_03_0D;
        }else if (scp.equals("03_2D")) {
            res = SCPMode.SCP_03_2D;
        }else if (scp.equals("03_25")) {
            res = SCPMode.SCP_03_25;
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
            if (keyType.equals("AES")) {
                String keyId = ((Element) keysElem.item(i)).getAttribute("keyId");
                keys[i] = new SCGPKey((byte) Integer.parseInt(keyVersionNumber), (byte) Integer.parseInt(keyId),
                        KeyType.AES_CBC, Conversion.hexToArray(keyDatas));
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
    public static CardConfig getCardConfig(byte[] atr)
            throws CardConfigNotFoundException {

        String name = null;
        String description = null;
        ATR[] atrs = null ;
        byte[] isd = null;
        SCPMode scpMode = null;
        String tp = null;
        SCKey[] keys = null;
        String impl = null;

        ATR arr2found = new ATR (atr);

        try {

            File f = new File(System.getProperty("user.dir") + configFile);

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(f);

            NodeList cards = document.getElementsByTagName("card");
            Element card = null;
            Element cardFound = null;

            // looking for the card identifiant in atr.xml file
            for (int i = 0; (i < cards.getLength()) & (cardFound != null); i++) {
                card = (Element) cards.item(i);
                atrs = getATRs(card);
                for(ATR a: atrs){
                    if (arr2found.equals(a)) {
                        cardFound = card;
                        break;
                    }
                }
            }

            if (cardFound == null) {
                throw new CardConfigNotFoundException("ATR \"" + Conversion.arrayToHex(atr) + "\" not found");
            }

            // set and return CardConfig
            name = getName(cardFound);
            description = getDescription(cardFound);
            isd = getISD(cardFound);
            scpMode = getSCP(cardFound);
            tp = getTP(cardFound);
            keys = getKeys(cardFound);
            impl = getImpl(cardFound);

            logger.debug("==> card matching is " + name);

        } catch (IOException e) {
            throw new CardConfigNotFoundException("cannot read the atr.xml file: " + e.getMessage());
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading atr.xml file: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading atr.xml file: " + e.getMessage());
        }

        return new CardConfig(name,description, atrs, isd, scpMode, tp, keys, impl);
    }

    public static boolean deleteCardConfig(String name)
            throws CardConfigNotFoundException {

        boolean t = false;

        try {
            File f = new File(System.getProperty("user.dir") + configFile);

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(f);

            NodeList cards = document.getElementsByTagName("card");
            Element desiredCard = null;

            // looking for the card identifier in config.xml file
            for (int i = 0; i < cards.getLength() && !t; i++) {
                desiredCard = (Element) cards.item(i);

                if (desiredCard.getAttribute("name").equals(name)) {
                    desiredCard.getParentNode().removeChild(desiredCard);
                    t = true;
                }
            }

           document.normalize();

            if (!t) {
                throw new CardConfigNotFoundException("Card \"" + name + "\" not found");
            } else {
                try {
                    //write the content into xml file
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    StreamResult result =  new StreamResult(f);
                    DOMSource source = new DOMSource(document);
                    transformer.transform(source, result);
                } catch (TransformerException e){
                    throw new CardConfigNotFoundException("Cannot transform the config file: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new CardConfigNotFoundException("Cannot read the config file: " + e.getMessage());
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading config file:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading config file:" + e.getMessage());
        }

        return (t)?true:false;
    }
}
