package fr.xlim.ssd.opal.library.commands;

import org.junit.Test;

import javax.smartcardio.CardChannel;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertSame;

public class AbstractCommandtest {

    private class AbstractCommandImpl extends AbstractCommands {}

    @Test
    public void testSetCC() {
        AbstractCommands ac = new AbstractCommandImpl();
        CardChannel cc = mock(CardChannel.class);
        ac.setCc(cc);
        assertSame(cc,ac.getCc());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCCFailsIfCcNull() {
        AbstractCommands ac = new AbstractCommandImpl();
        ac.setCc(null);
    }
}
