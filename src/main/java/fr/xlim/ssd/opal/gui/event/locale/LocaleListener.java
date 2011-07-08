package fr.xlim.ssd.opal.gui.event.locale;

import java.util.EventListener;

/**
 * Listener interface for locale changes event.
 *
 * This interface must be implemented when a view needs to listen when application locale changed.
 * Moreover, the view needs also to be registered to the model responsible of the locale management.
 *
 * @author David Pequegnot
 */
public interface LocaleListener extends EventListener {
    /**
     * Method to implement to manage the <code>LocaleChangedEvent</code> event.
     *
     * @param event the associated event
     */
    void localeChanged(LocaleChangedEvent event);
}
