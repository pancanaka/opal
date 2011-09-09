/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : David Pequegnot <david.pequegnot@etu.unilim.fr>                  *
 *           Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>       *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

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
            } else if (this.selectedCardReaderItem.getCardReaderName().equalsIgnoreCase("") && !cardReaderItems.isEmpty()) {
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
