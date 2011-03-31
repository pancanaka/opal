package fr.xlim.ssd.opal.library.params;

import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardConfigTest {

    private CardConfig cardConfig;
    private SCKey scKey;

    @Before
    public void setUp() {
        scKey = mock(SCKey.class);
        cardConfig = new CardConfig
                ("dummy", "dummy", new ATR[]{new ATR(new byte[0])}, new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, "dummy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenNameNull() {
        CardConfig cc = new CardConfig
                (null, "dummy", new ATR[]{new ATR(new byte[0])}, new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, "dummy");
    }

    @Test
    public void checkCardConfigFailedWhenDescriptionNull() {
        CardConfig cc = new CardConfig
                ("dummy", null, new ATR[]{new ATR(new byte[0])}, new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, "dummy");
    }

    @Test
    public void checkCardConfigFailedWhenATRsNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", null, new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, "dummy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenIsdNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", new ATR[]{new ATR(new byte[0])}, null, SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, "dummy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenScpNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", new ATR[]{new ATR(new byte[0])}, new byte[0], null,
                        "dummy", new SCKey[]{scKey}, "dummy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenTpNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", new ATR[]{new ATR(new byte[0])}, new byte[0], SCPMode.SCP_UNDEFINED,
                        null, new SCKey[]{scKey}, "dummy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenKeysNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", new ATR[]{new ATR(new byte[0])}, new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", null, "dummy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenImplNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", new ATR[]{new ATR(new byte[0])}, new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, null);
    }

    @Test
    public void testGetDefaultInitUpdateP1() {
        when(scKey.getSetVersion()).thenReturn((byte) 255);
        assertEquals(0, cardConfig.getDefaultInitUpdateP1());
        when(scKey.getSetVersion()).thenReturn((byte) 1);
        assertEquals(1, cardConfig.getDefaultInitUpdateP1());
    }

    @Test
    public void testGetDefaultInitUpdateP2() {
        when(scKey.getId()).thenReturn((byte) 1);
        assertEquals(0, cardConfig.getDefaultInitUpdateP2());
        when(scKey.getId()).thenReturn((byte) 32);
        assertEquals(32, cardConfig.getDefaultInitUpdateP2());
    }
}
