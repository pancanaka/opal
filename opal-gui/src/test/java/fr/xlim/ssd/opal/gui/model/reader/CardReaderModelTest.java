package fr.xlim.ssd.opal.gui.model.reader;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CardReaderModelTest {

    @Test
    public void testSetCardReaderItems() {
        CardReaderModel crm = new CardReaderModel();

        List<CardReaderItem> cris = new LinkedList<CardReaderItem>();
        crm.setCardReaderItems(cris);
        assertEquals(0, crm.getCardReaderItems().size());
        assertEquals("", crm.getSelectedCardName());
        assertEquals("",crm.getSelectedCardReaderName());
        assertNull(crm.getSelectedCardATR());
        assertNull(crm.getCardChannel());

        crm = new CardReaderModel();
        cris = new LinkedList<CardReaderItem>();
        cris.add(new CardReaderItem("abc","abc",null));
        cris.add(new CardReaderItem("DEF","DEF",null));
        crm.setCardReaderItems(cris);
        assertEquals(2, crm.getCardReaderItems().size());
        assertEquals("abc", crm.getSelectedCardName());
        assertEquals("abc",crm.getSelectedCardReaderName());
        assertNull(crm.getSelectedCardATR());
        assertNull(crm.getCardChannel());

        cris.add(new CardReaderItem("ghi","ghi",null));
        cris.add(new CardReaderItem("jkl","jkl",null));
        crm.setCardReaderItems(cris);
        assertEquals(4, crm.getCardReaderItems().size());
        assertEquals("abc", crm.getSelectedCardName());
        assertEquals("abc",crm.getSelectedCardReaderName());
        assertNull(crm.getSelectedCardATR());
        assertNull(crm.getCardChannel());
    }
}
