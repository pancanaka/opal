/**
 * A simple exception where an command implementation is not found
 *
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
package fr.xlim.ssd.opal.library.commands;

public class CommandsImplementationNotFound extends Exception {

    /**
     * Class constructor with a message
     *
     * @param message Exception message
     */
    public CommandsImplementationNotFound(String message) {
        super(message);
    }

    /**
     * Class constructor with a throwable cause
     *
     * @param cause Exception used to throw this exception
     */
    public CommandsImplementationNotFound(Throwable cause) {
        super(cause);
    }

    /**
     * Class constructor with a message and a throwable cause
     *
     * @param message Message explains this exception
     * @param cause   Exception throws
     */
    public CommandsImplementationNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}