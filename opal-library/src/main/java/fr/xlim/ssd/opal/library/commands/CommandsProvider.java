/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a Java 6 library that implements Global Platform 2.x card
 * specification. OPAL is able to upload and manage Java Card applet lifecycle
 * on Java Card while dealing of the authentication of the user and encryption
 * of the communication between the user and the card. OPAL is also able
 * to manage different implementations of the specification via a pluggable
 * interface.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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
