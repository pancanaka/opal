package fr.xlim.ssd.opal.gui.model.reader.event;

import java.util.EventObject;

/**
 * Event notified when terminal state changed.
 * <p/>
 * A terminal state event will be notified to listeners when :
 * <ul>
 * <li>the terminal list changed;</li>
 * <li>the selected terminal changed.</li>
 * </ul>
 * There is not internal methods: we think that listeners must use the
 * {@link fr.xlim.ssd.opal.gui.model.reader.CardReaderModel} model to check new values.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public class CardReaderStateChangedEvent extends EventObject {

    /**
     * Default constructor.
     *
     * @param source the source object
     */
    public CardReaderStateChangedEvent(Object source) {
        super(source);
    }
}
