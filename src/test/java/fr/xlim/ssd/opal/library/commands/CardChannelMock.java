package fr.xlim.ssd.opal.library.commands;

import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            if (buffer.startsWith("#")) {
                // do nohting: it's a comment
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
                    + ", sent: " + sentResponses + ", received:" + receivedCommands);
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