package fr.xlim.ssd.opal.library.utilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TLVTest {

    byte[] aSimpleTLV = {
            (byte) 0xAA, // Tag
            (byte) 0x10, // Length
            (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, // V
            (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, //  A
            (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, //   L
            (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F   //    U E
    };

    byte[] aLongTLV = {
            (byte) 0xAA, (byte) 0xBB, // Long Tag
            (byte) 0x10, // Length
            (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, // V
            (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, //  A
            (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, //   L
            (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F   //    U E
    };

    byte[] aWrongTLV = {
            (byte) 0xAA, // Long Tag
            (byte) 0xFF, // Wrong Length
            (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, //
            (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, // V
            (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, //  A
            (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F, //   L
            (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, //    U
            (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, //     E
            (byte) 0x18, (byte) 0x19, (byte) 0x1A, (byte) 0x1B, //
            (byte) 0x1C, (byte) 0x1D, (byte) 0x1E, (byte) 0x1F   //
    };

    @Test
    public void tlvConstructorTest() throws IllegalStateException {
        try {
            byte[] value = new byte[aSimpleTLV[1]];
            System.arraycopy(aSimpleTLV, 2, value, 0, aSimpleTLV[1]);
            TLV tlv = new TLV(aSimpleTLV[0], aSimpleTLV[1], value);

            Assert.assertEquals((byte) tlv.getTag(), aSimpleTLV[0]);
            Assert.assertEquals(tlv.getLength(), aSimpleTLV[1]);
            Assert.assertArrayEquals(tlv.getValue(), value);
            Assert.assertArrayEquals(tlv.toBinary(), aSimpleTLV);
        } catch (IOException e) {
            throw new IllegalStateException("IOException");
        }

    }

    @Test
    public void tlvBinaryTest() throws IllegalStateException {
        try {
            TLV tlv = new TLV(aSimpleTLV);
            Assert.assertArrayEquals(tlv.toBinary(), aSimpleTLV);
        } catch (IOException e) {
            throw new IllegalStateException("IOException");
        }

    }

    @Test
    public void tlvWrongBinaryTest() throws IllegalStateException {
        try {
            TLV tlv = new TLV(aWrongTLV);
            assert (false);
        } catch (IOException e) {
            assert (true);
        }
    }

}
