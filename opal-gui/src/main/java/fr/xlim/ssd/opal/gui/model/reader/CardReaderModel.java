package fr.xlim.ssd.opal.gui.model.reader;

import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

/**
 * The model which contains the list of card readers connected to the computer.
 *
 * @author David Pequegnot
 */
public class CardReaderModel {

    /**
     * Default constructor.
     */
    public CardReaderModel() {
    }

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

    /**
     * Set the selected terminal name identifier.
     * <p/>
     * If the terminalList is empty, <code>newTerminal</code> parameter will be ignored and selected terminal will
     * be set to <code>""</code>(empty string).<br/>
     * If the <code>newTerminal</code> parameter is not in the terminal list, the selected
     * terminal will be set to the value from the <code>0</code> index.
     * <p/>
     * If the selected terminal index has been changed using this method, the event
     * <code>CardReaderStateChangedEvent</code> will be fired.
     *
     * @param newTerminal the new selected terminal index
     * @see fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent
     */
    public void setSelectedCardReaderByCardReaderName(String newTerminal) {
        synchronized (this) {
            if (!this.selectedCardReaderItem.getCardReaderName().equalsIgnoreCase(newTerminal)) {
                if (this.cardReaderItems.isEmpty()) {
                    if (this.selectedCardReaderItem.getCardReaderName().equalsIgnoreCase("")) {
                        return;
                    }
                    this.selectedCardReaderItem = new CardReaderItem("", "");
                } else {
                    boolean found = false;
                    for (CardReaderItem cardReaderItem : cardReaderItems) {
                        if (newTerminal.equalsIgnoreCase(cardReaderItem.getCardReaderName())) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        if (this.selectedCardReaderItem.getCardReaderName().equalsIgnoreCase(newTerminal)) {
                            return;
                        }
                        this.selectedCardReaderItem = new CardReaderItem(newTerminal, "");
                    } else {
                        if (this.selectedCardReaderItem.getCardReaderName().equalsIgnoreCase(this.cardReaderItems.get(0).getCardReaderName())) {
                            return;
                        }
                        this.selectedCardReaderItem = this.cardReaderItems.get(0);
                    }
                }
                this.fireCardReaderStateChanged();
            }
        }
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
    public void setCardReaderItems(List<CardReaderItem> cardReaderItems) {
        synchronized (this) {
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


    private List<CardReaderItem> cardReaderItems = new ArrayList<CardReaderItem>();
    private CardReaderItem selectedCardReaderItem = new CardReaderItem("", "");
    private EventListenerList listeners = new EventListenerList();
}
