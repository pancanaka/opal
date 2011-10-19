/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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
