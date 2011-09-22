package fr.xlim.ssd.opal.library.params;

/**
 * A simple exception class when a card configuration is not found in
 * CardConfigFactory (or in the case of a problem).
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class CardConfigNotFoundException extends Exception {

    public CardConfigNotFoundException(String message) {
        super(message);
    }

    public CardConfigNotFoundException(String message, Exception exception) {
        super(message,exception);
    }

}