package fr.xlim.ssd.opal.library.commands;

import java.io.IOException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.List;
import javax.smartcardio.CardException;
import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.utilities.RandomGenerator;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
    private List<SCKey> keys;

    @Before
    public void createKeys() {
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

    private GP2xCommands createCommands(String filename) {
        GP2xCommands commands = new GP2xCommands();
        InputStream input = GP2xCommands.class.getResourceAsStream(filename);
        Reader reader = new InputStreamReader(input);
        CardChannel cardChannel = null;
        try {
            cardChannel = new CardChannelMock(reader);
        } catch (CardException ce) {
            throw new IllegalStateException("CardException");
        } catch (IOException ioe) {
            throw new IllegalStateException("IOException");
        }
        commands.setCc(cardChannel);
        return commands;
    }

    @Test
    public void testSetOffCardKey() {

        Commands commands = createCommands("/001-cardChannelMock-dummy.txt");

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
        Commands commands = createCommands("/001-cardChannelMock-dummy.txt");

        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        assertEquals(commands.getKeys().length, 2);
        assertSame(commands.getKey((byte) 1, (byte) 100), keys.get(2));
        assertSame(commands.getKey((byte) 2, (byte) 200), keys.get(1));
    }

    @Test
    public void testDeleteOffCardKey() {
        Commands commands = createCommands("/001-cardChannelMock-dummy.txt");

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
        Commands commands = createCommands("/010-GP2xCommands-select.txt");

        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67,0x01, 0x23, 0x45, 0x67});
        byte[] aid = {0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34};

        commands.select(aid);
        commands.getCc().close();
    }

    @Test(expected=CardException.class)
    public void testSelectFailedWhenResponseSWNot9000() throws CardException {
        Commands commands = createCommands("/011-GP2xCommands-select-not9000.txt");

        RandomGenerator.setRandomSequence(new byte[]{0x01, 0x23, 0x45, 0x67,0x01, 0x23, 0x45, 0x67});
        byte[] aid = {0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34};

        commands.select(aid);
    }

    /*

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
    commands.initializeUpdate((byte)0x1,(byte)0x2,SCPMode.SCP_02);
    }

    @Test
    public void testInitializeUpdateFailWhenSCP10() throws CardException {
    byte[] apdu = new byte[]{ (byte)0x00, 0x00, (byte)0x90, 0x00 };
    ResponseAPDU response = new ResponseAPDU(apdu);
    commands.initializeUpdate((byte)0x1,(byte)0x2,SCPMode.SCP_10);
    }
     */
}
