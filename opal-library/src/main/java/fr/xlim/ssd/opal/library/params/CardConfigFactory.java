package fr.xlim.ssd.opal.library.params;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Delivers card configuration CardConfig
 *
 * @author Yorick Lesecque
 * @author David Pequegnot
 * @author Guillaume Bouffard
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 *
 * @see CardConfig
 */
public class CardConfigFactory {

    private final static Logger logger = LoggerFactory.getLogger(CardConfigFactory.class);

    private final static String MAIN_OPAL_CONFIG_IN_CLASSPATH = "/config.xml";

    private File localConfigFile;

    public void setConfigFile(String filename) {
        this.localConfigFile = new File(filename);
        if (!this.localConfigFile.canRead()) {
            logger.warn("cannot read to config XML file");
        }
        if (!this.localConfigFile.canWrite()) {
            logger.debug("cannot write to config XML file");
        }
    }

    /**
     * Get the card config based on its name in local filename and then, if not found, in classpath:/config.xml
     *
     * @param cardName the card identifier in config.xml
     * @return a CardConfig object if the card identifier is found
     * @throws CardConfigNotFoundException if card configuration not found
     */
    public CardConfig getCardConfigByName(String cardName) throws CardConfigNotFoundException {

        for (CardConfig cardConfig : getAllCardConfigs()) {
            if (cardConfig.getName().equals(cardName)) {
                return cardConfig;
            }
        }

        throw new CardConfigNotFoundException("cannot found card in config XML file");
    }

    public List<CardConfig> getAllCardConfigs() throws CardConfigNotFoundException {

        List<CardConfig> cardConfigs = new LinkedList<CardConfig>();

        if (localConfigFile != null) {
            try {
                cardConfigs.addAll(getAllCardConfigs(new FileInputStream(localConfigFile)));
            } catch (IOException e) {
                throw new CardConfigNotFoundException("cannot open input stream to local config XML file ", e);
            }
        }

        InputStream mainConfigFile = CardConfigFactory.class.getResourceAsStream(MAIN_OPAL_CONFIG_IN_CLASSPATH);
        cardConfigs.addAll(getAllCardConfigs(mainConfigFile));

        return cardConfigs;
    }

    /**
     * Get all card configs.
     *
     * @return an array of CardConfig objects with the name of the config as a key
     * @throws CardConfigNotFoundException if an error occured while reading the XML file
     */
    private List<CardConfig> getAllCardConfigs(InputStream stream) throws CardConfigNotFoundException {

        List<CardConfig> configs = new LinkedList<CardConfig>();

        XStream xStream = new XStream(new StaxDriver());

        configs = (List<CardConfig>)xStream.fromXML(stream);

        /*
        List<Element> elements = document.getRootElement().getChildren("card");

        for (Element currentCard : elements) {

            // get name
            String name = currentCard.getAttribute("name").getValue();

            // get description
            String description = "No description";
            Element desc = currentCard.getChild("description");
            if (desc.getValue().length() != 0) {
                description = desc.getValue();
            }

            // get ATR
            List<ATR> ATRs = null;
            List<Element> listATR = currentCard.getChildren("ATR");
            if (listATR.size() == 0) {
                logger.warn("There are not ATR list for card with name: " + name);
            } else {
                for(Element atr : listATR) {
                    ATRs.add(new ATR(Conversion.hexToArray(atr.getAttribute("value").getValue())));
                }
            }

            // get ISD
            byte[] isd = Conversion.hexToArray((currentCard.getChild("isdAID").getAttribute("value").getValue()));

            // get SCP mode
            SCPMode scpMode = getSCP(currentCard);

            String tp = currentCard.getChild("transmissionProtocol").getAttribute("value").getValue();

            // get keys
            SCKey[] keys = getKeys(currentCard);

            // get implementation
            String impl = currentCard.getAttribute("defaultImpl").getValue();

            configs.add(new CardConfig(name, description, ATRs, isd, scpMode, tp, keys, impl));
        }
        */

        return configs;
    }

    /**
     * Get the value between scpMode tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return the SCP mode
     */             /*
    private SCPMode getSCP(Element card) {
        String scp = card.getChild("scpMode").getAttribute("value").getValue();
        SCPMode res = null;
        if (scp.equals("01_05")) {
            res = SCPMode.SCP_01_05;
        } else if (scp.equals("01_15")) {
            res = SCPMode.SCP_01_15;
        } else if (scp.equals("02_15")) {
            res = SCPMode.SCP_02_15;
        } else if (scp.equals("02_04")) {
            res = SCPMode.SCP_02_04;
        } else if (scp.equals("02_05")) {
            res = SCPMode.SCP_02_05;
        } else if (scp.equals("02_14")) {
            res = SCPMode.SCP_02_14;
        } else if (scp.equals("02_0A")) {
            res = SCPMode.SCP_02_0A;
        } else if (scp.equals("02_45")) {
            res = SCPMode.SCP_02_45;
        } else if (scp.equals("02_55")) {
            res = SCPMode.SCP_02_55;
        } else if (scp.equals("03_65")) {
            res = SCPMode.SCP_03_65;
        } else if (scp.equals("03_6D")) {
            res = SCPMode.SCP_03_6D;
        } else if (scp.equals("03_05")) {
            res = SCPMode.SCP_03_05;
        } else if (scp.equals("03_0D")) {
            res = SCPMode.SCP_03_0D;
        } else if (scp.equals("03_2D")) {
            res = SCPMode.SCP_03_2D;
        } else if (scp.equals("03_25")) {
            res = SCPMode.SCP_03_25;
        }
        return res;
    }             */

    /**
     * Get the keys between key tags from an element in the config.xml
     *
     * @param card an element in the config.xml
     * @return the credentials keys
     */         /*
    private SCKey[] getKeys(Element card) {

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
    }         */

    /**
     * Return the card config based on the ATR returns by the card.
     *
     * @param atr the ATR returns by the card
     * @return the name of the card config
     * @throws CardConfigNotFoundException if ATR not found
     */
    public CardConfig getCardConfigByATR(byte[] atr) throws CardConfigNotFoundException {

        for (CardConfig cardConfig : getAllCardConfigs()) {
            for (ATR atr2 : cardConfig.getAtrs()) {
                if (Arrays.equals(atr, atr2.getValue())) {
                    return cardConfig;
                }
            }
        }

        throw new CardConfigNotFoundException("cannot found card in config XML file");
    }

    /*
    public boolean deleteCardConfigInLocalConfigFile(String name)
            throws CardConfigNotFoundException {

        try {

            NodeList cards = getXMLCardConfigs(new FileInputStream(localConfigFile));
            Element desiredCard = null;

            boolean found = false;

            // looking for the card identifier in config.xml file
            for (int i = 0; i < cards.getLength() && !t; i++) {
                desiredCard = (Element) cards.item(i);

                if (desiredCard.getAttribute("name").equals(name)) {
                    desiredCard.getParentNode().removeChild(desiredCard);
                    found = true;
                }
            }

            if (!found) {
                throw new CardConfigNotFoundException("Card \"" + name + "\" not found");
            } else {
                try {
                    //write the content into xml file
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    StreamResult result = new StreamResult(f);
                    DOMSource source = new DOMSource(document);
                    transformer.transform(source, result);
                } catch (TransformerException e) {
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

        return t;
    }

    public static void addCardConfigInLocalConfigFile(CardConfig card)
            throws CardConfigNotFoundException {

        try {
            File f = new File(System.getProperty("user.dir") + localConfigFile);

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(f);

            Node cardsconfig = document.getFirstChild();
            Node newCard = document.createElement("card");
            NamedNodeMap newCardAttributes = newCard.getAttributes();
            Attr defaultImpl = document.createAttribute("defaultImpl");
            defaultImpl.setValue("fr.xlim.ssd.opal.library.commands." + card.getImplementation());
            newCardAttributes.setNamedItem(defaultImpl);

            Attr name = document.createAttribute("name");
            name.setValue(card.getName());
            newCardAttributes.setNamedItem(name);
            cardsconfig.appendChild(newCard);

            Node description = document.createElement("description");
            description.setTextContent(card.getDescription());
            newCard.appendChild(description);


            ATR[] atrs = card.getAtrs();
            Node listeATR = document.createElement("listeATR");
            newCard.appendChild(listeATR);
            for (int i = 0; i < atrs.length; i++) {
                Node ATR = document.createElement("ATR");
                NamedNodeMap ATRAttributes = ATR.getAttributes();
                Attr valueATR = document.createAttribute("value");
                valueATR.setValue(Conversion.arrayToHex(atrs[i].getValue()));
                ATRAttributes.setNamedItem(valueATR);
                listeATR.appendChild(ATR);
            }


            Node isdAID = document.createElement("isdAID");
            NamedNodeMap isdAIDAttributes = isdAID.getAttributes();
            Attr valueidsAID = document.createAttribute("value");
            valueidsAID.setValue(Conversion.arrayToHex(card.getIssuerSecurityDomainAID()));
            isdAIDAttributes.setNamedItem(valueidsAID);
            newCard.appendChild(isdAID);

            Node scpMode = document.createElement("scpMode");
            NamedNodeMap scpModeAttributes = scpMode.getAttributes();
            Attr valuescpMode = document.createAttribute("value");
            valuescpMode.setValue(card.getScpMode().toString().replace("SCP_", ""));
            scpModeAttributes.setNamedItem(valuescpMode);
            newCard.appendChild(scpMode);

            Node transmissionProtocol = document.createElement("transmissionProtocol");
            NamedNodeMap tPAttributes = transmissionProtocol.getAttributes();
            Attr valuesTP = document.createAttribute("value");
            valuesTP.setValue(card.getTransmissionProtocol());
            tPAttributes.setNamedItem(valuesTP);
            newCard.appendChild(transmissionProtocol);


            SCKey[] keys = card.getSCKeys();
            Node listedefaultKeys = document.createElement("defaultKeys");
            newCard.appendChild(listedefaultKeys);
            for (int i = 0; i < keys.length; i++) {
                Node key = document.createElement("key");
                NamedNodeMap keyAttributes = key.getAttributes();
                Attr keyDatas = document.createAttribute("keyDatas");
                keyDatas.setValue(Conversion.arrayToHex(keys[i].getData()));
                keyAttributes.setNamedItem(keyDatas);

                Attr id = document.createAttribute("keyId");
                id.setValue(Integer.toHexString(keys[i].getId() & 0xFF).toUpperCase());
                keyAttributes.setNamedItem(id);

                Attr keyVersionNumber = document.createAttribute("keyVersionNumber");
                keyVersionNumber.setValue(String.valueOf(Integer.parseInt(Integer.toHexString(keys[i].getSetVersion() & 0xFF).toUpperCase(), 16)));
                keyAttributes.setNamedItem(keyVersionNumber);

                Attr type = document.createAttribute("type");
                if (keys[i] instanceof SCGemVisa2 || keys[i] instanceof SCGemVisa) {
                    type.setValue(keys[i].getClass().getSimpleName());
                } else {
                    if (keys[i].getType().toString().compareTo("AES_CBC") == 0) {
                        type.setValue("AES");
                    } else {
                        type.setValue(keys[i].getType().toString());
                    }
                }
                keyAttributes.setNamedItem(type);
                listedefaultKeys.appendChild(key);
            }


            document.normalize();

            try {
                //write the content into xml file
                TransformerFactory transfac = TransformerFactory.newInstance();
                Transformer transformer = transfac.newTransformer();
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                StreamResult result = new StreamResult(f);
                DOMSource source = new DOMSource(document);
                transformer.transform(source, result);

            } catch (TransformerException e) {
                throw new CardConfigNotFoundException("Cannot transform the config file: " + e.getMessage());
            }

        } catch (IOException e) {
            throw new CardConfigNotFoundException("Cannot read the config file: " + e.getMessage());
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading config file:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading config file:" + e.getMessage());
        }
    }     */
}
