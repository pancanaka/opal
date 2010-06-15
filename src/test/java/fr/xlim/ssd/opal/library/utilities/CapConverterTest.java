package fr.xlim.ssd.opal.library.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import static org.junit.Assert.*;

import org.junit.Test;

public class CapConverterTest {

    byte[] expected = {
         0x01, 0x00, 0x13, (byte)0xDE, (byte)0xCA, (byte)0xFF, (byte)0xED, 0x01,
         0x02, 0x04, 0x00, 0x01, 0x09, (byte)0xA0, 0x00, 0x00, 0x00, 0x62, 0x03,
         0x01, 0x0C, 0x01, 0x02, 0x00, 0x1F, 0x00, 0x13, 0x00, 0x1F, 0x00, 0x0E,
         0x00, 0x0B, 0x00, 0x36, 0x00, 0x0C, 0x00, 0x65, 0x00, 0x0A, 0x00, 0x13,
         0x00, 0x00, 0x00, 0x6C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01,
         0x00, 0x04, 0x00, 0x0B, 0x01, 0x00, 0x01, 0x07, (byte)0xA0, 0x00, 0x00,
         0x00, 0x62, 0x01, 0x01, 0x03, 0x00, 0x0E, 0x01, 0x0A, (byte)0xA0, 0x00,
         0x00, 0x00, 0x62, 0x03, 0x01, 0x0C, 0x01, 0x01, 0x00, 0x14, 0x06, 0x00,
         0x0C, 0x00, (byte)0x80, 0x03, 0x01, 0x00, 0x01, 0x07, 0x01, 0x00, 0x00,
         0x00, 0x1D, 0x07, 0x00, 0x65, 0x00, 0x02, 0x10, 0x18, (byte)0x8C, 0x00,
         0x01, 0x18, 0x11, 0x01, 0x00, (byte)0x90, 0x0B, (byte)0x87, 0x00, 0x18,
         (byte)0x8B, 0x00, 0x02, 0x7A, 0x01, 0x30, (byte)0x8F, 0x00, 0x03,
         (byte)0x8C, 0x00, 0x04, 0x7A, 0x05, 0x23, 0x19, (byte)0x8B, 0x00, 0x05,
         0x2D, 0x19, (byte)0x8B, 0x00, 0x06, 0x32, 0x03, 0x29, 0x04, 0x70, 0x19,
         0x1A, 0x08, (byte)0xAD, 0x00, 0x16, 0x04, 0x1F, (byte)0x8D, 0x00, 0x0B,
         0x3B, 0x16, 0x04, 0x1F, 0x41, 0x29, 0x04, 0x19, 0x08, (byte)0x8B, 0x00,
         0x0C, 0x32, 0x1F, 0x64, (byte)0xE8, 0x19, (byte)0x8B, 0x00, 0x07, 0x3B,
         0x19, 0x16, 0x04, 0x08, 0x41, (byte)0x8B, 0x00, 0x08, 0x19, 0x03, 0x08,
         (byte)0x8B, 0x00, 0x09, 0x19, (byte)0xAD, 0x00, 0x03, 0x16, 0x04,
         (byte)0x8B, 0x00, 0x0A, 0x7A, 0x08, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x00,
         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x36, 0x00, 0x0D, 0x02,
         0x00, 0x00, 0x00, 0x06, (byte)0x80, 0x03, 0x00, 0x03, (byte)0x80, 0x03,
         0x01, 0x01, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x01, 0x03, (byte)0x80,
         0x0A, 0x01, 0x03, (byte)0x80, 0x0A, 0x06, 0x03, (byte)0x80, 0x0A, 0x07,
         0x03, (byte)0x80, 0x0A, 0x09, 0x03, (byte)0x80, 0x0A, 0x04, 0x03,
         (byte)0x80, 0x0A, 0x05, 0x06, (byte)0x80, 0x10, 0x02, 0x03, (byte)0x80,
         0x0A, 0x03, 0x09, 0x00, 0x13, 0x00, 0x03, 0x0E, 0x23, 0x2C, 0x00, 0x0C,
         0x05, 0x0C, 0x06, 0x03, 0x07, 0x05, 0x10, 0x0C, 0x08, 0x09, 0x06, 0x09,
         0x0B, 0x00, 0x6C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
         0x03, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x1C, 0x00, (byte)0x84, 0x00,
         0x01, 0x00, 0x1E, 0x00, 0x11, 0x00, 0x00, 0x00, 0x00, 0x01, 0x09, 0x00,
         0x14, 0x00, 0x30, 0x00, 0x07, 0x00, 0x00, 0x00, 0x00, 0x07, 0x01, 0x00,
         0x1D, 0x00, 0x33, 0x00, 0x46, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x00,
         0x1C, 0x00, 0x1E, 0x00, 0x1E, (byte)0xFF, (byte)0xFF, 0x00, 0x1E, 0x00,
         0x1C, 0x00, 0x20, 0x00, 0x20, 0x00, 0x22, 0x00, 0x24, 0x00, 0x27, 0x00,
         0x2A, 0x00, 0x2E, 0x01, (byte)0xB0, 0x01, 0x10, 0x01, 0x40, 0x02, 0x41,
         0x03, 0x44, 0x10, 0x04, (byte)0xB4, 0x41, 0x06, (byte)0xB4, (byte)0xB4,
         0x44, 0x02, 0x44, 0x04, (byte)0xB4, 0x31, 0x06, 0x68, 0x00, (byte)0xA1
     };

    @Test
    public void testConvert() throws FileNotFoundException {
        File file = new File("src/main/resources/cap/HelloWorld-2_1_2.cap");
        InputStream is = new FileInputStream(file);
        byte[] result = CapConverter.convert(is);
        System.out.println(Conversion.arrayToHex(result));
        assertArrayEquals(expected, result);
    }

}
