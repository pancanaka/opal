package fr.xlim.ssd.opal.library.commands;

import static org.junit.Assert.*;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import org.junit.Test;

public class CardChannelMockTest {

    @Test
    public void testTransmit() throws CardException {
        CardChannelMock cc = new CardChannelMock();
        byte[] rapdu = new byte[]{(byte) 0x90, 0x00, 0x00, 0x00};
        ResponseAPDU response = new ResponseAPDU(rapdu);
        cc.addResponse(response);
        byte[] capdu = new byte[]{0x11, 0x22, 0x33, 0x44};
        CommandAPDU command = new CommandAPDU(capdu);
        ResponseAPDU response2 = cc.transmit(command);
        assertSame(response, response2);
        assertEquals(0, cc.getResponses().size());
        assertEquals(1, cc.getSentAPDU().size());
        assertEquals(1, cc.getReceivedAPDU().size());
        assertSame(response,cc.getSentAPDU().get(0));
        assertSame(command,cc.getReceivedAPDU().get(0));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTransmitFailedWhenCommandNull() throws CardException {
        CardChannelMock cc = new CardChannelMock();
        cc.transmit(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddResponseFailedWhenResponseNull() throws CardException {
        CardChannelMock cc = new CardChannelMock();
        cc.addResponse(null);
    }
}
