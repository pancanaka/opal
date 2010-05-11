package fr.xlim.ssd.opal.library.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import fr.xlim.ssd.opal.library.SCKey;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GP2xCommandsTest {

    private GP2xCommands commands;
    private List<SCKey> keys;

    @Before
    public void createCommands() {
        commands = new GP2xCommands();
        keys = new LinkedList<SCKey>();

        SCKey key0 = mock(SCKey.class);
        when(key0.getSetVersion()).thenReturn((byte)1);
        when(key0.getKeyId()).thenReturn((byte)100);
        keys.add(key0);

        SCKey key1 = mock(SCKey.class);
        when(key1.getSetVersion()).thenReturn((byte)2);
        when(key1.getKeyId()).thenReturn((byte)200);
        keys.add(key1);

        SCKey key2 = mock(SCKey.class);
        when(key2.getSetVersion()).thenReturn((byte)1);
        when(key2.getKeyId()).thenReturn((byte)100);
        keys.add(key2);
    }

    @Test
    public void testSetOffCardKey() {
        commands.setOffCardKey(keys.get(0));
        assertEquals(commands.getKeys().length, 1);
        assertSame(commands.getKeys()[0],keys.get(0));
        assertSame(commands.getKey((byte)1,(byte)100),keys.get(0));

        commands.setOffCardKey(keys.get(2));
        assertEquals(commands.getKeys().length, 1);
        assertSame(commands.getKeys()[0],keys.get(2));
        assertSame(commands.getKey((byte)1,(byte)100),keys.get(2));

        commands.setOffCardKey(keys.get(1));
        assertEquals(commands.getKeys().length, 2);
        assertSame(commands.getKey((byte)2,(byte)200),keys.get(1));

        assertNull(commands.getKey((byte)2,(byte)300));
        assertNull(commands.getKey((byte)3,(byte)300));
        assertNull(commands.getKey((byte)3,(byte)200));
    }

    @Test
    public void testSetOffCardKeys() {
        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        assertEquals(commands.getKeys().length, 2);
        assertSame(commands.getKey((byte)1,(byte)100),keys.get(2));
        assertSame(commands.getKey((byte)2,(byte)200),keys.get(1));
    }

    @Test
    public void testDeleteOffCardKey() {
        commands.setOffCardKeys(keys.toArray(new SCKey[0]));
        SCKey key = commands.deleteOffCardKey((byte)2, (byte)200);
        assertNotNull(key);
        assertSame(key, keys.get(1));
        assertEquals(commands.getKeys().length, 1);
        assertSame(commands.getKey((byte)1,(byte)100),keys.get(2));
        assertNull(commands.getKey((byte)2,(byte)200));

        key = commands.deleteOffCardKey((byte)3, (byte)300);
        assertNull(key);

        key = commands.deleteOffCardKey((byte)1, (byte)100);
        assertEquals(commands.getKeys().length, 0);
        assertSame(key, keys.get(2));
    }
}
