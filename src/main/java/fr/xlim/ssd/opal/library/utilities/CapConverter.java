package fr.xlim.ssd.opal.library.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapConverter {

    private final static Logger logger = LoggerFactory.getLogger(CapConverter.class);

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
            logger.error("Cannot open or read capfile", ex);
            return null;
        }

        ByteBuffer buffer = ByteBuffer.allocate(size);

        for (int i = 0; i < components.length; i++) {
            if (components[i] != null) {
                buffer.put(components[i]);
            }
        }

        return buffer.array();
    }
}
