package fr.xlim.ssd.opal.gui.event.locale;

import java.util.EventObject;

/**
 * Event notified when the application locale changes.
 *
 * @author David Pequegnot
 */
public class LocaleChangedEvent extends EventObject {
    /**
     * Default constructor.
     *
     * @param source the source event
     */
    public LocaleChangedEvent(Object source) {
        super(source);
    }
}
