package fr.xlim.ssd.opal.library.commands;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.Assert.assertArrayEquals;

public class CardChannelMockTest {

    private CardChannel cardChannel;

    @Before
    public void createCardChannelMock() throws IOException, CardException {
        InputStream input = CardChannelMockTest.class.getResourceAsStream("/001-cardChannelMock-dummy.txt");
        Reader reader = new InputStreamReader(input);
        cardChannel = new CardChannelMock(reader);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailedWhenCommandNull() throws CardException, IOException {
        new CardChannelMock(null);
    }

    @Test
    public void testClose() throws CardException, IOException {
        byte[] bb = {0x12, 0x34, 0x56, 0x78};
        ResponseAPDU response = cardChannel.transmit(new CommandAPDU(bb));
        byte[] expected = {0x00, 0x00, (byte) 0x90, 0x00};
        assertArrayEquals(expected, response.getBytes());
        cardChannel.close();
    }

    @Test
    public void testTransmitFailedIfNoMoreAPDUAvailable() throws CardException, IOException {
        byte[] bb = {0x12, 0x34, 0x56, 0x78};
        cardChannel.transmit(new CommandAPDU(bb));

        expectedException.expect(CardException.class);
        expectedException.expectMessage("No more command APDU expected");
        cardChannel.transmit(new CommandAPDU(bb));
    }

    @Test
    public void testConstructorFailedIfNoAssociatedResponse() throws CardException, IOException {
        InputStream input = CardChannelMockTest.class.getResourceAsStream("/002-cardChannelMock-failed.txt");
        Reader reader = new InputStreamReader(input);

        expectedException.expect(CardException.class);
        expectedException.expectMessage("No response APDU available");
        new CardChannelMock(reader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransmitFailedWhenCommandNull() throws CardException {
        cardChannel.transmit(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBufferTransmitNotImplemented() throws CardException {
        cardChannel.transmit(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCardNotImplemented() {
        cardChannel.getCard();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetChannelNumberNotImplemented() {
        cardChannel.getChannelNumber();
    }
}
