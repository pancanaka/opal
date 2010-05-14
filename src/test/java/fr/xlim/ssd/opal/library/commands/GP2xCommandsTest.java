package fr.xlim.ssd.opal.library.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.List;
import javax.smartcardio.CardException;
import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import org.junit.Before;
import org.junit.Test;

public class GP2xCommandsTest {

    @Test
    public void testDummy() {
        assertTrue(true);
    }

    private GP2xCommands commands;
    private List<SCKey> keys;
    private CardChannelMock cardChannel;
/*
    @Before
    public void createCommands() {
        commands = new GP2xCommands();
        cardChannel = new CardChannelMock();
        commands.setCc(cardChannel);

        keys = new LinkedList<SCKey>();
        SCKey key0 = mock(SCKey.class);
        when(key0.getSetVersion()).thenReturn((byte) 1);
        when(key0.getKeyId()).thenReturn((byte) 100);
        keys.add(key0);
        SCKey key1 = mock(SCKey.class);
        when(key1.getSetVersion()).thenReturn((byte) 2);
        when(key1.getKeyId()).thenReturn((byte) 200);
        keys.add(key1);
        SCKey key2 = mock(SCKey.class);
        when(key2.getSetVersion()).thenReturn((byte) 1);
        when(key2.getKeyId()).thenReturn((byte) 100);
        keys.add(key2);
    }

    @Test
    public void testSetOffCardKey() {
        commands.setOffCardKey(keys.get(0));
        assertEquals(commands.getKeys().length, 1);
        assertSame(commands.getKeys()[0], keys.get(0));
        assertSame(commands.getKey((byte) 1, (byte) 100), keys.get(0));

        commands.setOffCardKey(keys.get(2));
        assertEquals(commands.getKeys().length, 1);
        assertSame(commands.getKeys()[0], keys.get(2));
        assertSame(commands.getKey((byte) 1, (byte) 100), keys.get(2));

        commands.setOffCardKey(keys.get(1));
        assertEquals(commands.getKeys().length, 2);
        assertSame(commands.getKey((byte) 2, (byte) 200), keys.get(1));

        assertNull(commands.getKey((byte) 2, (byte) 300));
        assertNull(commands.getKey((byte) 3, (byte) 300));
        assertNull(commands.getKey((byte) 3, (byte) 200));
    }

    @Test
    public void testSetOffCardKeys() {
        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        assertEquals(commands.getKeys().length, 2);
        assertSame(commands.getKey((byte) 1, (byte) 100), keys.get(2));
        assertSame(commands.getKey((byte) 2, (byte) 200), keys.get(1));
    }

    @Test
    public void testDeleteOffCardKey() {
        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        SCKey key = commands.deleteOffCardKey((byte) 2, (byte) 200);
        assertNotNull(key);
        assertSame(key, keys.get(1));
        assertEquals(commands.getKeys().length, 1);
        assertSame(commands.getKey((byte) 1, (byte) 100), keys.get(2));
        assertNull(commands.getKey((byte) 2, (byte) 200));

        key = commands.deleteOffCardKey((byte) 3, (byte) 300);
        assertNull(key);

        key = commands.deleteOffCardKey((byte) 1, (byte) 100);
        assertEquals(commands.getKeys().length, 0);
        assertSame(key, keys.get(2));
    }

    @Test
    public void testSelect() throws CardException {
        byte[] apdu = new byte[]{ (byte)0x00, 0x00, (byte)0x90, 0x00 };
        ResponseAPDU response = new ResponseAPDU(apdu);
        cardChannel.addResponse(response);
        byte[] aid = new byte[]{0x12, 0x34, 0x56, 0x78};
        response = commands.select(aid);

        // checking command
//        assertEquals(1,cardChannel.getReceivedAPDU().size());
//        CommandAPDU command = cardChannel.getReceivedAPDU().get(0);
        byte[] expectedCommand = new byte[]{0x00, (byte)0xA4, (byte)0x04,
            0x00, 0x04, 0x12, 0x34, 0x56, 0x78};
//        assertArrayEquals(command.getBytes(), expectedCommand);

        // checking response
        assertNotNull(response);
        byte[] expectedResponse = new byte[]{0x00, 0x00, (byte)0x90, 0x00};
        assertArrayEquals(response.getBytes(), expectedResponse);
    }

    @Test(expected=CardException.class)
    public void testSelectFailedWhenSWNot9000() throws CardException {
        byte[] apdu = new byte[]{ (byte)0x00, 0x00, (byte)0x00, 0x00 };
        ResponseAPDU response = new ResponseAPDU(apdu);
        cardChannel.addResponse(response);
        byte[] aid = new byte[]{0x12, 0x34, 0x56, 0x78};
        ResponseAPDU reponse = commands.select(aid);
    }

    @Test
    public void testInitializeUpdateFailWhenSCP02() throws CardException {
        byte[] apdu = new byte[]{ (byte)0x00, 0x00, (byte)0x90, 0x00 };
        ResponseAPDU response = new ResponseAPDU(apdu);
        cardChannel.addResponse(response);
        response = commands.initializeUpdate((byte)0x1,(byte)0x2,SCPMode.SCP_02);
    }

    @Test
    public void testInitializeUpdateFailWhenSCP10() throws CardException {
        byte[] apdu = new byte[]{ (byte)0x00, 0x00, (byte)0x90, 0x00 };
        ResponseAPDU response = new ResponseAPDU(apdu);
        cardChannel.addResponse(response);
        response = commands.initializeUpdate((byte)0x1,(byte)0x2,SCPMode.SCP_10);
    }
 */
}
