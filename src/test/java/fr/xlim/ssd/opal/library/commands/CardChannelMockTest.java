package fr.xlim.ssd.opal.library.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.xml.ws.Response;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardChannelMockTest {

    private CardChannel cc;

    @Before
    public void createCardChannelMock() throws IOException, CardException {
        InputStream input = CardChannelMockTest.class
                .getResourceAsStream("/001-cardChannelMock-dummy.txt");
        Reader reader = new InputStreamReader(input);
        cc = new CardChannelMock(reader);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailedWhenCommandNull() throws CardException, IOException {
        new CardChannelMock(null);
    }

    @Test
    public void testClose() throws CardException, IOException {
        byte[] bb = { 0x12, 0x34, 0x56, 0x78};
        ResponseAPDU response = cc.transmit(new CommandAPDU(bb));
        byte[] expected = { 0x00, 0x00, (byte)0x90, 0x00 };
        assertArrayEquals(expected,response.getBytes());
        cc.close();
    }

    @Test(expected=CardException.class)
    public void testTransmitFailedIfNoMoreAPDUAvailable() throws CardException, IOException {
        byte[] bb = { 0x12, 0x34, 0x56, 0x78};
        cc.transmit(new CommandAPDU(bb));
        cc.transmit(new CommandAPDU(bb));
    }

    @Test(expected=CardException.class)
    public void testTransmitFailedIfNoAssociatedResponse() throws CardException, IOException {
        InputStream input = CardChannelMockTest.class
                .getResourceAsStream("/002-cardChannelMock-defective.txt");
        Reader reader = new InputStreamReader(input);
        cc = new CardChannelMock(reader);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTransmitFailedWhenCommandNull() throws CardException {
        cc.transmit(null);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testBufferTransmitNotImplemented() throws CardException {
        cc.transmit(null,null);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testGetCardNotImplemented() {
        cc.getCard();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testGetChannelNumberNotImplemented() {
        cc.getChannelNumber();
    }
}
