package fr.xlim.ssd.opal.library.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CapConverter {

    public static void createCapFromJar(File inputJar, File outputCap) {
        try {
            ZipFile zf = new ZipFile(inputJar, 1);
            Enumeration listZe = zf.entries();
            FileOutputStream fos = new FileOutputStream(outputCap);
            byte tabout[][] = new byte[11][];
            while (listZe.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) listZe.nextElement();
                byte b[] = new byte[(int) ze.getCompressedSize()];
                zf.getInputStream(ze).read(b);
                switch (b[0]) {
                    case 1:
                        tabout[0] = b;
                        break;

                    case 2:
                        tabout[1] = b;
                        break;

                    case 3:
                        tabout[3] = b;
                        break;

                    case 4:
                        tabout[2] = b;
                        break;

                    case 5:
                        tabout[8] = b;
                        break;

                    case 6:
                        tabout[4] = b;
                        break;

                    case 7:
                        tabout[5] = b;
                        break;

                    case 8:
                        tabout[6] = b;
                        break;

                    case 9:
                        tabout[9] = b;
                        break;

                    case 10:
                        tabout[7] = b;
                        break;

                    case 11:
                        tabout[10] = b;
                        break;
                }
            }
            for (int i = 0; i < tabout.length; i++) {
                if (tabout[i] != null) {
                    fos.write(tabout[i]);
                }
            }

            zf.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
