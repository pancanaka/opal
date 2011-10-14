package fr.xlim.ssd.opal.library.config;

import fr.xlim.ssd.opal.library.config.KeyType;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCGemVisa;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SCGemVisaTest {

    private SCGemVisa scgv;

    @Before
    public void createSCGemVisaKey() {
        byte[] key = {
                0x47, 0x45, 0x4D, 0x58, 0x50, 0x52, 0x45, 0x53, 0x53, 0x4F, 0x53,
                0x41, 0x4D, 0x50, 0x4C, 0x45, 0x47, 0x45, 0x4D, 0x58, 0x50, 0x52,
                0x45, 0x53
        };

        scgv = new SCGemVisa((byte) 0x32, key);

        assertEquals(0x32, scgv.getSetVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailedIfDataNull() {
        scgv = new SCGemVisa((byte) 0x32, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailedIfDataNotCorrectSize() {
        scgv = new SCGemVisa((byte) 0x32, new byte[12]);
    }

    @Test
    public void testDerivateKey() {

        byte[] data = {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A
        };

        SCGPKey[] keys = scgv.deriveKey(data);

        // TODO: check why mac and kek key are identical...

        byte[] encKey = {
                0x42, (byte) 0xBD, (byte) 0xA3, 0x26, 0x3A, (byte) 0xC8,
                (byte) 0x94, (byte) 0xB3, 0x11, 0x2B, (byte) 0x9D, 0x0E,
                (byte) 0xE5, 0x22, (byte) 0xC8, (byte) 0x9D, 0x42, (byte) 0xBD,
                (byte) 0xA3, 0x26, 0x3A, (byte) 0xC8, (byte) 0x94, (byte) 0xB3
        };

        assertArrayEquals(encKey, keys[0].getData());
        assertEquals((byte) 0x32, keys[0].getSetVersion());
        assertEquals((byte) 0x01, keys[0].getId());
        assertEquals(KeyType.DES_ECB, keys[0].getType());

        byte[] macKey = {
                (byte) 0xAF, (byte) 0xBB, (byte) 0xF1, (byte) 0xD6, (byte) 0xC2,
                (byte) 0xED, (byte) 0x8C, 0x56, (byte) 0xF5, 0x18, (byte) 0xD5,
                0x24, 0x48, (byte) 0xE9, (byte) 0xBF, 0x2D, (byte) 0xAF,
                (byte) 0xBB, (byte) 0xF1, (byte) 0xD6, (byte) 0xC2, (byte) 0xED,
                (byte) 0x8C, 0x56
        };

        assertArrayEquals(macKey, keys[1].getData());
        assertEquals((byte) 0x32, keys[1].getSetVersion());
        assertEquals((byte) 0x02, keys[1].getId());
        assertEquals(KeyType.DES_ECB, keys[1].getType());

        byte[] kekKey = {
                0x1F, (byte) 0xE0, (byte) 0xFD, (byte) 0x8E, (byte) 0xAC, 0x14,
                (byte) 0xDD, 0x27, (byte) 0x9B, 0x46, (byte) 0x80, (byte) 0x8A,
                0x40, 0x7E, 0x55, (byte) 0xA1, 0x1F, (byte) 0xE0, (byte) 0xFD,
                (byte) 0x8E, (byte) 0xAC, 0x14, (byte) 0xDD, 0x27
        };

        assertArrayEquals(kekKey, keys[2].getData());
        assertEquals((byte) 0x32, keys[2].getSetVersion());
        assertEquals((byte) 0x03, keys[2].getId());
        assertEquals(KeyType.DES_ECB, keys[2].getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDerivateKeyFailedIfDataNull() {
        scgv.deriveKey(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDerivateKeyFailedIfDataNotCorrectSize() {
        scgv.deriveKey(new byte[9]);
    }

    @Test
    public void testGetId() {
        assertEquals(1, scgv.getId());
    }

    @Test
    public void testType() {
        assertEquals(KeyType.MOTHER_KEY, scgv.getType());
    }
}
