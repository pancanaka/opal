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
package fr.xlim.ssd.opal.library.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The CAP converter extracts main components from the CAP file and reorder them. This is needed because order of
 * component in the CAP file are different from the sent order. Sent order of component is described in Section 6.2 of
 * the Virtual Machine Specification, Java Card Platform, Version 3.0, Classic Edition.
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 * @author Guillaume Bouffard
 */
public class CapConverter {

    /// the logger
    private final static Logger logger = LoggerFactory.getLogger(CapConverter.class);

    public static byte HEADER_COMPONENT_TAG = 0x01;
    public static byte DIRECTORY_COMPONENT_TAG = 0x02;
    public static byte APPLET_COMPONENT_TAG = 0x03;
    public static byte IMPORT_COMPONENT_TAG = 0x04;
    public static byte CONSTANTPOOL_COMPONENT_TAG = 0x05;
    public static byte CLASS_COMPONENT_TAG = 0x06;
    public static byte METHOD_COMPONENT_TAG = 0x07;
    public static byte STATICFIELD_COMPONENT_TAG = 0x08;
    public static byte REFERENCELOCATION_COMPONENT_TAG = 0x09;
    public static byte EXPORT_COMPONENT_TAG = 0x0A;
    public static byte DESCRIPTOR_COMPONENT_TAG = 0x0B;
    public static byte DEBUG_COMPONENT_TAG = 0x0C;

    public static byte HEADER_COMPONENT_INSTALLATION_ORDER = 0x01;
    public static byte DIRECTORY_COMPONENT_INSTALLATION_ORDER = 0x02;
    public static byte IMPORT_COMPONENT_INSTALLATION_ORDER = 0x03;
    public static byte APPLET_COMPONENT_INSTALLATION_ORDER = 0x04;
    public static byte CLASS_COMPONENT_INSTALLATION_ORDER = 0x05;
    public static byte METHOD_COMPONENT_INSTALLATION_ORDER = 0x06;
    public static byte STATICFIELD_COMPONENT_INSTALLATION_ORDER = 0x07;
    public static byte EXPORT_COMPONENT_INSTALLATION_ORDER = 0x08;
    public static byte CONSTANTPOOL_COMPONENT_INSTALLATION_ORDER = 0x09;
    public static byte REFERENCELOCATION_COMPONENT_INSTALLATION_ORDER = 0x0A;
    public static byte DESCRIPTOR_COMPONENT_INSTALLATION_ORDER = 0x0B;
    public static byte DEBUG_COMPONENT_INSTALLATION_ORDER = -1; // this component isn't sent


    /**
     * Get the CAP file (ZIP format) from the input stream and extract each component to a byte array, with respect
     * of the component install order (table 6-3, Virtual Machine Specification, Java Card Platform, Version 3.0,
     * Classic Edition).
     *
     * @param is the input stream of the CAP file
     * @return the ordered component in a byte array
     * @throws IllegalArgumentException if input stream is null
     * @throws IOException              if error during reading of the CAP file
     */
    public static byte[] convert(InputStream is) {

        if (is == null) {
            throw new IllegalArgumentException("is must be not null");
        }

        byte components[][] = new byte[11][];
        int size = 0;

        try {
            JarInputStream jis = new JarInputStream(is);
            JarEntry je;

            while ( (je = jis.getNextJarEntry() ) != null) {

                // read only the cap file
                String name = je.getName();
                // Ignore files not ending with .cap (case-insensitive matching)
                if (name.length() < 4 ||
                        !name.substring(name.length() - 4).equalsIgnoreCase(".cap"))
                    continue;

                // Split name into <directory part>/javacard/<file part>
                int lastslash = name.lastIndexOf('/');
                if (lastslash == -1) continue;
                int penultimateslash = name.lastIndexOf('/', lastslash - 1);

                // Ignore if no /javacard/ part
                if (penultimateslash == -1 ||
                        !name.substring(penultimateslash + 1, lastslash).equalsIgnoreCase("javacard"))
                    continue;


                if(je.getMethod() == je.DEFLATED) {
                    logger.info("DEFLATED MODE");
                    logger.info("Extracting: " + je.getName());
                    logger.info("Size: " + je.getSize());
                    logger.info("Compressed Size: " + je.getCompressedSize());
                } else {
                    logger.info("STORED MODE");
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int nrBytesRead = 0;
                while ((nrBytesRead = jis.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, nrBytesRead);
                }

                // int sizeEntry = (int) je.getSize();
                int sizeEntry = byteArrayOutputStream.size();

                logger.info("Size entry : " + sizeEntry);

                byte b[] = byteArrayOutputStream.toByteArray().clone(); // new byte[sizeEntry];
                byteArrayOutputStream.reset();

                switch (b[0]) {
                    case 1:
                        components[HEADER_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 2:
                        components[DIRECTORY_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 3:
                        components[APPLET_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 4:
                        components[IMPORT_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 5:
                        components[CONSTANTPOOL_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 6:
                        components[CLASS_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 7:
                        components[METHOD_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 8:
                        components[STATICFIELD_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 9:
                        components[REFERENCELOCATION_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 10:
                        components[EXPORT_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;

                    case 11:
                        components[DESCRIPTOR_COMPONENT_INSTALLATION_ORDER-1] = b;
                        size += sizeEntry;
                        break;
                    case 12:
                        // not send to the card
                        break;
                }
            }

            jis.close();

        } catch (IOException ex) {
            throw new IllegalStateException("Cannot open or read CAP file", ex);
        }

        ByteBuffer buffer = ByteBuffer.allocate(size);

        // concatenate components in one buffer
        for (int i = 0; i < components.length; i++) {
            if (components[i] != null) {
                buffer.put(components[i]);
            }

            // TODO: [#11558] Missing component export during CAP conversion
            /*
            else {
                throw new IllegalArgumentException("error in CAP file: not all components have been loaded (" + i + ")");
            }
            */

        }

        return buffer.array();
    }
}
