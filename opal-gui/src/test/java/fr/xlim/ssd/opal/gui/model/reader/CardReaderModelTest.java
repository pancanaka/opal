package fr.xlim.ssd.opal.gui.model.reader;

import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.verification.VerificationMode;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

        crm = new CardReaderModel();
        cris = new LinkedList<CardReaderItem>();
        cris.add(new CardReaderItem("mno","mno",null));
        crm.setCardReaderItems(cris);
        cris = new LinkedList<CardReaderItem>();
        cris.add(new CardReaderItem("abc","abc",null));
        crm.setCardReaderItems(cris);
        assertEquals(1, crm.getCardReaderItems().size());
        assertEquals("abc", crm.getSelectedCardName());
        assertEquals("abc",crm.getSelectedCardReaderName());
        assertNull(crm.getSelectedCardATR());
        assertNull(crm.getCardChannel());
    }

    @Test
    public void testListeners() {

        CardReaderStateListener crsl1 = mock(CardReaderStateListener.class);
        CardReaderStateListener crsl2 = mock(CardReaderStateListener.class);

        CardReaderModel crm = new CardReaderModel();
        crm.addCardReaderStateListener(crsl1);
        crm.addCardReaderStateListener(crsl2);

        List<CardReaderItem> cris = new LinkedList<CardReaderItem>();
        cris.add(new CardReaderItem("abc","abc",null));
        cris.add(new CardReaderItem("def","def",null));
        crm.setCardReaderItems(cris);

        verify(crsl1, times(1)).cardReaderStateChanged((CardReaderStateChangedEvent)anyObject());;
        verify(crsl2, times(1)).cardReaderStateChanged((CardReaderStateChangedEvent)anyObject());;

        crm.removeCardReaderStateListener(crsl1);

        crm.removeCardReaderStateListener(crsl2);
    }
}
