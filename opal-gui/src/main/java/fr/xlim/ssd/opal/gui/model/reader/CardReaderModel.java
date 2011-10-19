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
import fr.xlim.ssd.opal.library.params.ATR;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;
import javax.smartcardio.CardChannel;

/**
 * The model which contains the list of card readers connected to the computer.
 *
 * @author David Pequegnot
 * @author Tiana Razafindralambo
 */
public class CardReaderModel {

    private List<CardReaderItem> cardReaderItems = new ArrayList<CardReaderItem>();

    private CardReaderItem selectedCardReaderItem = new CardReaderItem("", "");

    private EventListenerList listeners = new EventListenerList();

    /**
     * Get selected terminal name identifier.
     * <p/>
     * If there are no terminal recognized, it will return <code>""</code> (empty string) value.
     *
     * @return the selected terminal name identifier
     */
    public String getSelectedCardReaderName() {
        return this.selectedCardReaderItem.getCardReaderName();
    }

    /**
     * Get selected card name identifier.
     *
     * @return the selected card name identifier
     */
    public String getSelectedCardName() {
        return this.selectedCardReaderItem.getCardName();
    }

    public boolean hasSelectedCardReaderItem() {
        return (this.selectedCardReaderItem != null);
    }
    /**
     * Get selected card ATR.
     *
     * @return the selected card ATR
     */
    public ATR getSelectedCardATR() {
        return this.selectedCardReaderItem.getCardATR();
    }

    /**
     * Get the selected card channel
     *
     * @return the selected card channel
     */
    public CardChannel getCardChannel() {
        return this.selectedCardReaderItem.getCardChannel();
    }

    /**
     * Get the terminal list as a <code>List</code> of <code>String</code> objects.
     *
     * @return the terminal list as a <code>List</code> of <code>String</code> objects
     */
    public List<CardReaderItem> getCardReaderItems() {
        return this.cardReaderItems;
    }

    /**
     * Set the terminal list.
     * <p/>
     * The selected terminal value will change when:
     * <ul>
     * <li>if <code>selectedCardReaderItem</code> is equal to <code>""</code> and the new terminal list is not empty,
     * <code>selectedCardReaderItem</code> will take the terminal name from the <code>0</code> index;</li>
     * <li>if <code>selectedCardReaderItem</code> is not present in the new terminal list,
     * <code>selectedCardReaderItem</code> will take the terminal name from the <code>0</code> index.</li>
     * </ul>
     * After changing the terminal list, a <code>CardReaderStateChangedEvent</code> will be fired.
     *
     * @param cardReaderItems the new terminal list
     */
    public synchronized void setCardReaderItems(List<CardReaderItem> cardReaderItems) {
            if (cardReaderItems.isEmpty()) {
                this.selectedCardReaderItem = new CardReaderItem("", "");
            } else if (this.selectedCardReaderItem.getCardReaderName().equalsIgnoreCase("")) {
                this.selectedCardReaderItem = cardReaderItems.get(0);
            } else {
                boolean found = false;
                for (CardReaderItem cardReaderItem : cardReaderItems) {
                    if (this.selectedCardReaderItem.getCardReaderName().equalsIgnoreCase(cardReaderItem.getCardReaderName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.selectedCardReaderItem = cardReaderItems.get(0);
                }
            }
            this.cardReaderItems = cardReaderItems;

            this.fireCardReaderStateChanged();
    }

    /**
     * Add a terminal state listener.
     *
     * @param listener the listener to add
     */
    public void addCardReaderStateListener(CardReaderStateListener listener) {
        this.listeners.add(CardReaderStateListener.class, listener);
    }

    /**
     * Remove a terminal state listener.
     *
     * @param listener the listener to remove
     */
    public void removeCardReaderStateListener(CardReaderStateListener listener) {
        this.listeners.remove(CardReaderStateListener.class, listener);
    }

    /**
     * Notify listeners when terminal state changed.
     */
    private void fireCardReaderStateChanged() {
        CardReaderStateListener[] listenerList = (CardReaderStateListener[]) this.listeners.getListeners(CardReaderStateListener.class);

        for (CardReaderStateListener listener : listenerList) {
            listener.cardReaderStateChanged(new CardReaderStateChangedEvent(this));
        }
    }
}
