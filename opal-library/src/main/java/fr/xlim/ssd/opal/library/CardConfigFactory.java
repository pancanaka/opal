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
package fr.xlim.ssd.opal.library;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import fr.xlim.ssd.opal.library.commands.Commands;
import fr.xlim.ssd.opal.library.commands.CommandsProvider;
import fr.xlim.ssd.opal.library.config.*;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
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
 * @see fr.xlim.ssd.opal.library.config.CardConfig
 */
public class CardConfigFactory {

    private final static Logger logger = LoggerFactory.getLogger(CardConfigFactory.class);

    private final static String MAIN_OPAL_CONFIG_IN_CLASSPATH = "/config.xml";

    private XStream xstream;

    private List<CardConfig> cardConfigs = new LinkedList<CardConfig>();

    private CommandsProvider commandsProvider;

    public CardConfigFactory(CommandsProvider commandsProvider) {

        this.commandsProvider = commandsProvider;

        configureXStream();

        // add all card configs from main config file
        InputStream mainConfigFile = CardConfigFactory.class.getResourceAsStream(MAIN_OPAL_CONFIG_IN_CLASSPATH);
        cardConfigs.addAll((List<CardConfig>) xstream.fromXML(mainConfigFile));
    }

    public CardConfigFactory() {
        this(new CommandsProvider());
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

    /*
     * ************************ XStream converters **************************
     *
     * TODO: At the present time, this is not perfect, we need to get rid of the "class='cards'" at output
     */

    private void configureXStream() {
        xstream = new XStream(new StaxDriver());
        xstream.registerConverter(new SCPConverter());
        xstream.registerConverter(new KeyConverter());
        xstream.registerLocalConverter(CardConfig.class, "isd", new HexadecimalConverter());
        xstream.registerLocalConverter(CardConfig.class, "atrs", new ATRConverter());
        xstream.registerLocalConverter(CardConfig.class, "implementation", new CommandsConverter());
        xstream.alias("cards",LinkedList.class);
        xstream.alias("card", CardConfig.class);
        xstream.aliasType("key", SCKey.class);
        xstream.omitField(CardConfig.class, "local");
    }

    private class ATRConverter implements Converter {

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            List<byte[]> atrs = (List<byte[]>) source;
            for (byte[] atr : atrs) {
                writer.startNode("atr");
                writer.setValue(Conversion.arrayToHex(atr));
                writer.endNode();
            }
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            List l = new LinkedList<byte[]>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                l.add(Conversion.hexToArray(reader.getValue()));
                reader.moveUp();
            }
            return l;
        }

        @Override
        public boolean canConvert(Class type) {
            return List.class.isAssignableFrom(type);
        }
    }

    private class CommandsConverter implements SingleValueConverter {

        @Override
        public String toString(Object obj) {
            return obj.getClass().getCanonicalName();
        }

        @Override
        public Object fromString(String str) {
            Class c = commandsProvider.getImplementation(str);
            if(c == null) {
                throw new IllegalStateException("cannot found implementation " + str);
            }
            try {
                return c.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalStateException("cannot instantiate commands " + str,e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("cannot access to commands "  + str,e);
            }
        }

        @Override
        public boolean canConvert(Class type) {
            return Commands.class.isAssignableFrom(type);
        }
    }

    private class HexadecimalConverter implements SingleValueConverter {

        @Override
        public String toString(Object obj) {
            return Conversion.arrayToHex((byte[])obj);
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

    private class SCPConverter implements SingleValueConverter {

        @Override
        public String toString(Object obj) {
            return ((SCPMode)obj).name().substring(4);
        }

        @Override
        public Object fromString(String str) {
            return SCPMode.valueOf("SCP_" + str);
        }

        @Override
        public boolean canConvert(Class type) {
            return type.equals(SCPMode.class);
        }
    }

    private class KeyConverter implements Converter {

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            SCKey key = (SCKey)source;
            writer.startNode("type");
            writer.setValue(key.getType().name());
            writer.endNode();
            writer.startNode("version");
            writer.setValue(new Byte(key.getVersion()).toString());
            writer.endNode();
            if (key instanceof SCGPKey) {
                writer.startNode("id");
                writer.setValue(new Byte(key.getId()).toString());
                writer.endNode();
            }
            writer.startNode("value");
            writer.setValue(Conversion.arrayToHex(key.getValue()));
            writer.endNode();
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