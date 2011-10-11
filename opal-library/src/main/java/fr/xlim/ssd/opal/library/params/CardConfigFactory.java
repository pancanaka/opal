package fr.xlim.ssd.opal.library.params;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.enums.EnumConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import fr.xlim.ssd.opal.library.*;
import fr.xlim.ssd.opal.library.commands.SCP.SCP;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
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

        XStream xstream = new XStream(new StaxDriver());
        xstream.registerConverter(new SCPConverter());
        xstream.registerConverter(new KeyConverter());
        xstream.registerConverter(new ATRConverter());
        xstream.registerLocalConverter(CardConfig.class, "isd", new HexadecimalConverter());
        xstream.alias("cards", LinkedList.class);
        xstream.alias("card", CardConfig.class);
        xstream.alias("atrs", LinkedList.class);
        xstream.alias("atr", ATR.class);
        xstream.aliasType("key",SCKey.class);
        return (List<CardConfig>)xstream.fromXML(stream);
    }

    private class ATRConverter implements Converter {
        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            byte[] value = Conversion.hexToArray(reader.getValue());
            return new ATR(value);
        }

        @Override
        public boolean canConvert(Class type) {
            return type.equals(ATR.class);
        }
    }

    private class HexadecimalConverter implements SingleValueConverter {

        @Override
        public String toString(Object obj) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public Object fromString(String str) {
            return Conversion.hexToArray(str);
        }

        @Override
        public boolean canConvert(Class type) {
            return type.equals(byte[].class);
        }
    }

    private class SCPConverter implements Converter {
        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return SCPMode.valueOf("SCP_" + reader.getValue());
        }

        @Override
        public boolean canConvert(Class type) {
            return type.equals(SCPMode.class);
        }
    }

    private class KeyConverter implements Converter {
        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

            String type = null;
            String version = null;
            String id = null;
            String value = null;

            while (reader.hasMoreChildren()) {
                reader.moveDown();
                if ("type".equals(reader.getNodeName())) {
                    type = reader.getValue();
                } else if ("version".equals(reader.getNodeName())) {
                    version = reader.getValue();
                } else if ("id".equals(reader.getNodeName())) {
                    id = reader.getValue();
                } else if ("value".equals(reader.getNodeName())) {
                    value = reader.getValue();
                }
                reader.moveUp();
            }

            if(type.equals("DES_ECB")) {
                return new SCGPKey((byte) Integer.parseInt(version),
                        (byte) Integer.parseInt(id),
                        KeyType.DES_ECB,
                        Conversion.hexToArray(value));
            } else if(type.equals("DES_CBC")) {
                return new SCGPKey((byte) Integer.parseInt(version),
                        (byte) Integer.parseInt(id),
                        KeyType.DES_CBC,
                        Conversion.hexToArray(value));
            } else if (type.equals("SCGemVisa2")) {
                return new SCGemVisa2((byte) Integer.parseInt(version), Conversion.hexToArray(value));
            } else if (type.equals("SCGemVisa")) {
                return new SCGemVisa((byte) Integer.parseInt(version),
                        Conversion.hexToArray(value));
            } else if (type.equals("AES_CBC")) {
                return new SCGPKey((byte) Integer.parseInt(version),
                        (byte) Integer.parseInt(id),
                        KeyType.AES_CBC,
                        Conversion.hexToArray(value));
            } else {
                throw new IllegalArgumentException("Cannot find key type in XML");
            }
        }

        @Override
        public boolean canConvert(Class type) {
            return SCKey.class.isAssignableFrom(type);
        }
    }

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
            valueidsAID.setValue(Conversion.arrayToHex(card.getIsd()));
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
