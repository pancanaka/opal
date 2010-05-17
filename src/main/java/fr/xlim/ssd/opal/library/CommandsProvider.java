package fr.xlim.ssd.opal.library;

import java.util.HashMap;
import java.util.Map;

import javax.smartcardio.CardChannel;

import fr.xlim.ssd.opal.library.commands.Commands;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;

/**
 * @author Damien Arcuset, Eric Linke
 */
public class CommandsProvider {

    /**
     *
     */
    private static Map<String, Commands> cmds = new HashMap<String, Commands>();

    /**
     * @param c
     */
    public static void register(Commands c) {
        CommandsProvider.cmds.put(c.getClass().getName(), c);
    }

    /**
     * @param name
     * @param cc
     * @return
     * @throws CommandsImplementationNotFound
     */
    static Commands getImplementation(String name, CardChannel cc) throws CommandsImplementationNotFound {
        Commands cmdret = CommandsProvider.cmds.get(name);
        if (cmdret == null) {
            throw new CommandsImplementationNotFound("\"" + name +
                    "\" : This Implementation is not registered");
        }
        cmdret.setCc(cc);
        return cmdret;
    }

    /*
     * XXX: There is a drawback with this approach: it exists one and only
     * one state if several cards are used simultanouesly. We need to instance
     * one command by card, or crate a object "state of a card"
     */

}