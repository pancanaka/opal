package fr.xlim.ssd.opal.library.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import fr.xlim.ssd.opal.library.*;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Delivers card configuration instances.
 *
 * @author Yorick Lesecque
 * @author David Pequegnot
 * @author Guillaume Bouffard
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @see CardConfig
 */
public class CardConfigFactory {

    private final static Logger logger = LoggerFactory.getLogger(CardConfigFactory.class);

    private final static String MAIN_OPAL_CONFIG_IN_CLASSPATH = "/config.xml";

    private XStream xstream;

    private List<CardConfig> cardConfigs = new LinkedList<CardConfig>();

    public CardConfigFactory() {

        // configuring xstream
        xstream = new XStream(new StaxDriver());
        xstream.registerConverter(new SCPConverter());
        xstream.registerConverter(new KeyConverter());
        xstream.registerLocalConverter(CardConfig.class, "isd", new HexadecimalConverter());
        xstream.registerLocalConverter(CardConfig.class, "atrs", new ATRConverter());
        xstream.alias("cards", ArrayList.class);
        xstream.alias("card", CardConfig.class);
        xstream.aliasType("key", SCKey.class);

        // add all card configs from main config file
        InputStream mainConfigFile = CardConfigFactory.class.getResourceAsStream(MAIN_OPAL_CONFIG_IN_CLASSPATH);
        cardConfigs.addAll((List<CardConfig>) xstream.fromXML(mainConfigFile));
    }

    /**
     * Get the card config based on its name in local filename and then, if not found, in classpath:/config.xml
     *
     * @param cardName the card identifier in config.xml
     * @return a CardConfig object if the card identifier is found
     */
    public CardConfig getCardConfigByName(String cardName) {

        for (CardConfig cardConfig : cardConfigs) {
            if (cardConfig.getName().equals(cardName)) {
                return cardConfig;
            }
        }

        return null;
    }

    /**
     * Return the card config based on the ATR returns by the card.
     *
     * @param atr the ATR returns by the card
     * @return the name of the card config
     */
    public CardConfig getCardConfigByATR(byte[] atr) {

        for (CardConfig cardConfig : cardConfigs) {
            for (byte[] atr2 : cardConfig.getAtrs()) {
                if (Arrays.equals(atr, atr2)) {
                    return cardConfig;
                }
            }
        }

        return null;
    }

    public boolean registerLocalCardConfig(CardConfig cardConfig) {
        if (cardConfigs.contains(cardConfig)) {
            return false;
        }
        cardConfig.setLocal(true);
        cardConfigs.add(cardConfig);
        return true;
    }

    public void registerLocalCardConfigsFromXML(File inputStream) {
        List<CardConfig> l = (List<CardConfig>) xstream.fromXML(inputStream);
        for (CardConfig cardConfig : l) {
            registerLocalCardConfig(cardConfig);
        }
    }

    public void saveLocalCardConfigsToXML(OutputStream outputStream) {
        List<CardConfig> l = new LinkedList<CardConfig>();
        for (CardConfig c : cardConfigs) {
            if (c.isLocal()) {
                l.add(c);
            }
        }
        xstream.toXML(l, outputStream);
    }

    public List<CardConfig> getCardConfigs() {
        return cardConfigs;
    }

    /**
     * ************************ XStream converters **************************
     */

    private class ATRConverter implements Converter {
        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            List l = new ArrayList<byte[]>();
            while (reader.hasMoreChildren()) {
            reader.moveDown();
            l.add(Conversion.hexToArray(reader.getValue()));
            reader.moveUp();
            }
            return l;
        }

        @Override
        public boolean canConvert(Class type) {
            return type.equals(ArrayList.class);
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

            if (type.equals("DES_ECB")) {
                return new SCGPKey((byte) Integer.parseInt(version),
                        (byte) Integer.parseInt(id),
                        KeyType.DES_ECB,
                        Conversion.hexToArray(value));
            } else if (type.equals("DES_CBC")) {
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
}