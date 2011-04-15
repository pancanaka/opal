package fr.xlim.ssd.opal.gui.controller;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author Yorick Lesecque
 */
public class profilesController {
    private static CardConfig profils[];

    public profilesController() {
        try {
            profils = CardConfigFactory.getAllCardConfigs();
        } catch (CardConfigNotFoundException ex) {
            Logger.getLogger(profilesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String[][] getProfils() {
        String allProfils[][] = new String[profils.length][3];

        for(int i = 0; i < profils.length; i++) {

            allProfils[i][0] = profils[i].getName();
            allProfils[i][1] = profils[i].getDescription();
            allProfils[i][2] = profils[i].getImplementation();
        }

        return allProfils;
    }

    public static boolean deleteProfile(int id)
            throws CardConfigNotFoundException, TransformerException, ParserConfigurationException {

        String name = profils[id].getName();
        boolean t = false;
        

        try {
            String splitted[] = CardConfigFactory.class.getResource(CardConfigFactory.getConfigFile()).getPath().split("file:/");
            System.out.println(splitted[1]);
            File f = new File("file://" + splitted[1]);

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(f);

            NodeList cards = document.getElementsByTagName("card");
                System.out.println("plop");
            Element desiredCard = null;

            // looking for the card identifier in config.xml file
            for (int i = 0; i < cards.getLength() && !t; i++) {
                desiredCard = (Element) cards.item(i);

                System.out.println("name:" + desiredCard.getAttribute("name") + " cherché: " + name);

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
                } catch (TransformerException tfe){
                    tfe.printStackTrace();
                }
            }

        } catch (IOException e) {
            throw new CardConfigNotFoundException("cannot read the config.xml file: " + e.getMessage());
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading config.xml file:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading config.xml file:" + e.getMessage());
        }

        return (t)?true:false;
        
    }
}
