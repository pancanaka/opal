package fr.xlim.ssd.opal.library.utilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConversionTest {

    @Test
    public void testArrayToHex() {
        byte[] tab = new byte[]{(byte) 0xAB, 0x07, 0x13, 0x56};
        String res = Conversion.arrayToHex(tab);
        assertEquals("AB 07 13 56 ", res);
    }

    @Test
    public void testEmptyArrayToHex() {
        byte[] tab = new byte[0];
        String res = Conversion.arrayToHex(tab);
        assertEquals("", res);
    }

    @Test
    public void testHexToArray() {
        String s = "AB 12 F7 65 23";
        byte[] res = Conversion.hexToArray(s);
        byte[] expected = new byte[]{(byte) 0xAB, 0x12, (byte) 0xF7, 0x65, 0x23};
        assertEquals(expected.length, res.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res[i]);
        }
    }

    @Test
    public void testEmptyHexToArray() {
        String s = "";
        byte[] res = Conversion.hexToArray(s);
        assertEquals(0, res.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToArrayFailedWithIllegalHexadecimalLetter() {
        String s = "AG";
        byte[] res = Conversion.hexToArray(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToArrayFailedWithOnlyOneLetter() {
        String s = "A";
        byte[] res = Conversion.hexToArray(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToArrayFailedWithOnlyThreeLetters() {
        String s = "A15";
        byte[] res = Conversion.hexToArray(s);
    }
}
