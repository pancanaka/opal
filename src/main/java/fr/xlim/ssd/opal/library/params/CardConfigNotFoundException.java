/**
 * A simple exception class when a card configuration is not found in
 * CardConfigFactory (or in the case of a problem).
 * 
 * @author  Damien Arcuset, Eric Linke
 * @author  Julien Iguchi-Cartigny
 */
package fr.xlim.ssd.opal.library.params;

public class CardConfigNotFoundException extends Exception {

    /**
     * Class constructor.
     */
    public CardConfigNotFoundException(String message) {
        super(message);
    }
}