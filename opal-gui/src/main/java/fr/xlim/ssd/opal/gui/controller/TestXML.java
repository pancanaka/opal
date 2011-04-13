package fr.xlim.ssd.opal.gui.controller;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.xlim.ssd.opal.library.*;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Format;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author Yorick Lesecque
 */
public class TestXML {/*
    private static CardConfig profils[];

    public TestXML() {
        try {
            profils = CardConfigFactory.getAllCardConfigs();
        } catch (CardConfigNotFoundException ex) {
            Logger.getLogger(TestXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String[][] getProfils() {
        String allProfils[][] = new String[profils.length][3];

        for(int i = 0; i < profils.length; i++) {
            allProfils[i][0] = Conversion.arrayToHex(profils[i].getIssuerSecurityDomainAID());
            allProfils[i][1] = profils[i].getTransmissionProtocol();
            allProfils[i][2] = profils[i].getImplementation();
        }

        return allProfils;
    }

    public static boolean deleteProfil(int id)
            throws CardConfigNotFoundException, TransformerException {

        String isAID = Conversion.arrayToHex(profils[id].getIssuerSecurityDomainAID());
        boolean t = false;

        try {
            File f = new File(System.getProperty("user.dir") + "/src/main/resources/config.xml");

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(f);

            NodeList cards = document.getElementsByTagName("card");
            Element desiredCard = null;

            // looking for the card identifier in config.xml file
            for (int i = 0; i < cards.getLength() && !t; i++) {
                desiredCard = (Element) cards.item(i);

                if (((Element) desiredCard.getElementsByTagName("isdAID").item(0)).getAttribute("value").equals(isAID)) {
                    desiredCard.getParentNode().removeChild(desiredCard);
                    t = true;
                }
            }

           document.normalize();

            if (!t) {
                throw new CardConfigNotFoundException("Card \"" + isAID + "\" not found");
            } else {
                /**//*
                try {
                    //write the content into xml file
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(document);
                    StreamResult result =  new StreamResult(f);
                    transformer.transform(source, result);
                } catch (TransformerException tfe){
                    tfe.printStackTrace();
                }
                /*
                try {
                    OutputFormat format = new OutputFormat(document);
                    FileWriter fw = new FileWriter("/config.xml");
                    XMLSerializer serial = new XMLSerializer(fw, format);
                    serial.asDOMSerializer();
                    serial.serialize(document.getDocumentElement());
                    fw.flush();
                    fw.close();
                } catch (IOException e) {
                    System.out.println("ploooooooooooooooooooooooooooooop");
                    throw new CardConfigNotFoundException("cannot read the config.xml file: " + e.getMessage());
                }
                /**//*
            }

        } catch (IOException e) {
            throw new CardConfigNotFoundException("cannot read the config.xml file: " + e.getMessage());
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading config.xml file:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading config.xml file:" + e.getMessage());
        }

        return (t)?true:false;
    }*/
}