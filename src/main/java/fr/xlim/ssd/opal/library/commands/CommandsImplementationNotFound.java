/**
 * A simple exception where an command implementation is not found
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
package fr.xlim.ssd.opal.library.commands;

public class CommandsImplementationNotFound extends Exception {

    public CommandsImplementationNotFound(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public CommandsImplementationNotFound(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public CommandsImplementationNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}