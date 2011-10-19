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

import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CardChannelMock extends CardChannel {

    private static final Logger logger = LoggerFactory.getLogger(CardChannelMock.class);

    private class CommandAndResponse {

        private CommandAPDU command;
        private ResponseAPDU response;

        public CommandAndResponse(ResponseAPDU response) {
            this.command = null;
            this.response = response;
        }

        public CommandAndResponse(CommandAPDU command, ResponseAPDU response) {
            this.command = command;
            this.response = response;
        }

        public CommandAPDU getCommand() {
            return command;
        }

        public void setCommand(CommandAPDU command) {
            this.command = command;
        }

        public ResponseAPDU getResponse() {
            return response;
        }

        public void setResponse(ResponseAPDU response) {
            this.response = response;
        }
    }

    private List<CommandAndResponse> apdus = new LinkedList<CommandAndResponse>();
    private int receivedCommands = 0;
    private int sentResponses = 0;

    public CardChannelMock(Reader reader) throws IOException, CardException {
        if (reader == null) {
            throw new IllegalArgumentException("reader must be not null");
        }

        BufferedReader br = new BufferedReader(reader);
        String buffer = br.readLine();


        CommandAPDU command = null;

        while (buffer != null) {
            if (buffer.startsWith("#") || buffer.isEmpty()) {
                // do nothing: it's a comment or an empty line
            } else if (command == null) {
                command = new CommandAPDU(Conversion.hexToArray(buffer));
            } else {
                ResponseAPDU response = new ResponseAPDU(Conversion.hexToArray(buffer));
                apdus.add(new CommandAndResponse(command, response));
                command = null;
            }
            buffer = br.readLine();
        }

        if (command != null) {
            throw new CardException("No response APDU available");
        }

        logger.info("working in replay-and-check mode (" + apdus.size() + " exchanges loaded)");
    }

    @Override
    public ResponseAPDU transmit(CommandAPDU command) throws CardException {
        if (command == null) {
            throw new IllegalArgumentException("command must be not null");
        }

        logger.info("Command APDU (" + receivedCommands + "): " + Conversion.arrayToHex(command.getBytes()));


        if (apdus.size() <= receivedCommands) {
            throw new CardException("No more command APDU expected");
        }

        boolean eq = Arrays.equals(command.getBytes(), apdus.get(receivedCommands).getCommand().getBytes());
        if (!eq) {
            throw new CardException("Command APDU expected: " + Conversion.arrayToHex(apdus.get(receivedCommands).getCommand().getBytes()));
        }

        receivedCommands++;

        ResponseAPDU response = apdus.get(sentResponses).getResponse();
        logger.info("Response APDU (" + sentResponses + "): " + Conversion.arrayToHex(response.getBytes()));
        sentResponses++;

        return response;
    }

    @Override
    public void close() throws CardException {
        if (apdus.size() != this.sentResponses && apdus.size() != this.receivedCommands) {
            throw new CardException("Exchange sequence not finished (size: " + apdus.size()
                    + ", sent: " + sentResponses + ", received:" + receivedCommands + ")");
        }
    }

    @Override
    public int transmit(ByteBuffer command, ByteBuffer response) throws CardException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Card getCard() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getChannelNumber() {
        throw new UnsupportedOperationException("not implemented");
    }
}
