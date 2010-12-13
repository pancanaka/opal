package fr.xlim.ssd.opal.library.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.IllegalFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CAP converter extracts main components from the CAP file and reorder them. This is needed because order of
 * component in the CAP file are different from the sent order. Sent order of component is described in Section 6.2 of
 * the Virtual Machine Specification, Java Card Platform, Version 3.0, Classic Edition.
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public class CapConverter {

    /// logger
    private final static Logger logger = LoggerFactory.getLogger(CapConverter.class);

    /**
     * Get the CAP file (ZIP format) from the input stream and extract each component to a byte array, with respect
     * of the component install order (table 6-3, Virtual Machine Specification, Java Card Platform, Version 3.0,
     * Classic Edition).
     *
     * @param is the input stream of the CAP file
     * @return the ordered component in a byte array
     */
    public static byte[] convert(InputStream is) {

        if (is == null) {
            throw new IllegalArgumentException("is must be not null");
        }

        byte components[][] = new byte[11][];
        int size = 0;

        try {
            ZipInputStream jis = new ZipInputStream(is);

            ZipEntry ze = jis.getNextEntry();

            while (ze != null) {
                int sizeEntry = (int) ze.getCompressedSize();
                byte b[] = new byte[sizeEntry];
                jis.read(b);

                switch (b[0]) {
                    case 1:
                        components[0] = b;
                        size += sizeEntry;
                        break;

                    case 2:
                        components[1] = b;
                        size += sizeEntry;
                        break;

                    case 3:
                        components[3] = b;
                        size += sizeEntry;
                        break;

                    case 4:
                        components[2] = b;
                        size += sizeEntry;
                        break;

                    case 5:
                        components[8] = b;
                        size += sizeEntry;
                        break;

                    case 6:
                        components[4] = b;
                        size += sizeEntry;
                        break;

                    case 7:
                        components[5] = b;
                        size += sizeEntry;
                        break;

                    case 8:
                        components[6] = b;
                        size += sizeEntry;
                        break;

                    case 9:
                        components[9] = b;
                        size += sizeEntry;
                        break;

                    case 10:
                        components[7] = b;
                        size += sizeEntry;
                        break;

                    case 11:
                        components[10] = b;
                        size += sizeEntry;
                        break;
                }

                ze = jis.getNextEntry();
            }

            jis.close();

        } catch (IOException ex) {
            throw new IllegalStateException("Cannot open or read CAP file", ex);
        }

        ByteBuffer buffer = ByteBuffer.allocate(size);

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
