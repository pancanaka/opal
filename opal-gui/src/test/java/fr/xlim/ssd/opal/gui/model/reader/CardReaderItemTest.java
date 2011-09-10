package fr.xlim.ssd.opal.gui.model.reader;

import fr.xlim.ssd.opal.library.params.ATR;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CardReaderItemTest {

    @Test
    public void testEquals() {
        CardReaderItem cri1 = new CardReaderItem();
        CardReaderItem cri2 = new CardReaderItem();
        assertEquals(cri1,cri2);

        cri1 = new CardReaderItem("a","b");
        cri2 = new CardReaderItem("a","b");
        assertEquals(cri1,cri2);

        cri1 = new CardReaderItem("a","b",new ATR(new byte[]{0}));
        cri2 = new CardReaderItem("a","b",new ATR(new byte[]{0}));
        assertEquals(cri1,cri2);

        cri1 = new CardReaderItem("a","b");
        cri2 = new CardReaderItem("a","c");
        assertFalse(cri1.equals(cri2));

        cri1 = new CardReaderItem("a","b");
        cri2 = new CardReaderItem("c","b");
        assertFalse(cri1.equals(cri2));
    }
}
