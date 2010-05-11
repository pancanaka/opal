package fr.xlim.ssd.opal.library.utilities;

import junit.framework.TestCase;

public class ConversionTest extends TestCase {

    public void testArrayToHex() {
        byte[] tab = new byte[] { (byte)0xAB, 0x13, 0x56 };
        String res = Conversion.arrayToHex(tab);
        assertEquals("AB 13 56 ", res);
    }

    public void testEmptyArrayToHex() {
        byte[] tab = new byte[0];
        String res = Conversion.arrayToHex(tab);
        assertEquals("", res);
    }

    public void testHexToArray() {
        String s = "AB 12 F7 65 23";
        byte[] res = Conversion.hexToArray(s);
        byte[] expected = new byte[]{ (byte)0xAB, 0x12, (byte)0xF7, 0x65, 0x23};
        assertEquals(expected.length,res.length);
        for(int i=0; i < expected.length; i++) {
            assertEquals(expected[i], res[i]);
        }
    }
}
