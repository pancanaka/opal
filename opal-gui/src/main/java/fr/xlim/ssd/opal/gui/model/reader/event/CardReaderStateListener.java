package fr.xlim.ssd.opal.gui.model.reader.event;

import java.util.EventListener;

/**
 * Listener interface for terminal state event.
 * <p/>
 * Objects which listen terminal state event must implement this interface.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public interface CardReaderStateListener extends EventListener {
    /**
     * Method to implement to handle terminal state event.
     *
     * @param event the associated event
     */
    void cardReaderStateChanged(CardReaderStateChangedEvent event);
}