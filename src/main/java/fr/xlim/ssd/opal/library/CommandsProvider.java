package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.commands.Commands;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;

import javax.smartcardio.CardChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Each implementation register its command class with this class.
 * <p/>
 * TODO: There is a drawback with this approach: it exists one and only one state if several cards are used
 * simultaneously. We need to instance one command by card, or crate a object "state of a card"
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class CommandsProvider {

    /// contains all the command instances available
    private static Map<String, Commands> cmds = new HashMap<String, Commands>();

    /**
     * Register a new command class
     *
     * @param c the command class to register
     * @throws IllegalArgumentException if command is null
     */
    public static void register(Commands c) {
        if (c == null) {
            throw new IllegalArgumentException("command must be not null");
        }

        CommandsProvider.cmds.put(c.getClass().getName(), c);
    }

    /**
     * get the implementation registered before using the full name and path to find it.
     *
     * @param name the name of the implementatipon to look for
     * @param cc   the card channel to set into the implementation
     * @return the command implementation
     * @throws CommandsImplementationNotFound if command implementation not found
     */
    static Commands getImplementation(String name, CardChannel cc) throws CommandsImplementationNotFound {
        Commands cmdret = CommandsProvider.cmds.get(name);
        if (cmdret == null) {
            throw new CommandsImplementationNotFound("\"" + name + "\" : implementation not registered");
        }
        cmdret.setCc(cc);
        return cmdret;
    }
}
