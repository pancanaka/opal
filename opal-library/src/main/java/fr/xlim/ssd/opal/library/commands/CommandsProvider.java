package fr.xlim.ssd.opal.library.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Each implementation register its command class with this class.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class CommandsProvider {

    /// contains all the command instances available
    private Map<String, Class> commands = new HashMap<String, Class>();

    /// Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(CommandsProvider.class);

    public CommandsProvider() {
        logger.info("register GP2xCommands");
        register(GP2xCommands.class);
        logger.info("register GemXpresso211Commands");
        register(GemXpresso211Commands.class);
    }

    /**
     * Register a new command class
     *
     * @param c the command class to register
     * @throws IllegalArgumentException if command is null
     */
    public void register(Class c) {
        if (!Commands.class.isAssignableFrom(c)) {
            throw new IllegalArgumentException("not based on Commands class");
        }
        logger.debug("register " + c.getName());
        this.commands.put(c.getName(), c);
    }

    /**
     * get the implementation registered before using the full name and path to find it.
     *
     * @param name the name of the implementatipon to look for
     * @return the command implementation or null if not found
     */
    public Class getImplementation(String name) {
        Class c = this.commands.get(name);
        if (c == null) {
            return null;
        }
        return c;
    }
}
