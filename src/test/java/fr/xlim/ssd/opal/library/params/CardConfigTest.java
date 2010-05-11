package fr.xlim.ssd.opal.library.params;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import org.junit.Before;
import org.junit.Test;

public class CardConfigTest {

    private CardConfig cardConfig;
    private SCKey scKey;

    @Before
    public void setUp() {
        scKey = mock(SCKey.class);
        cardConfig = new CardConfig(new byte[0], SCPMode.SCP_UNDEFINED,
                "dummy", new SCKey[]{scKey}, "dummy");
    }

    @Test
    public void testGetDefaultInitUpdateP1() {
        when(scKey.getSetVersion()).thenReturn((byte)255);
        assertEquals(0,cardConfig.getDefaultInitUpdateP1());
        when(scKey.getSetVersion()).thenReturn((byte)1);
        assertEquals(1,cardConfig.getDefaultInitUpdateP1());
    }

    @Test
    public void testGetDefaultInitUpdateP2() {
        when(scKey.getKeyId()).thenReturn((byte)1);
        assertEquals(0,cardConfig.getDefaultInitUpdateP2());
        when(scKey.getKeyId()).thenReturn((byte)32);
        assertEquals(32,cardConfig.getDefaultInitUpdateP2());
    }
}
