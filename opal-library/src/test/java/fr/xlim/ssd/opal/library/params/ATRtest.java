package fr.xlim.ssd.opal.library.params;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ATRtest {

    private ATR atr;

    @Test
    public void checkGetATR() {

        byte[] mockATR =
                {0x00, 0x01, 0x02, 0x03, 0x04,
                        0x05, 0x06, 0x07, 0x08, 0x09,
                        0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
                        0x0F, 0x10, 0x11, 0x12, 0x13};

        atr = new ATR(mockATR);

        assertArrayEquals(mockATR, atr.getValue());

    }


}
