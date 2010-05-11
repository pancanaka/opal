package fr.xlim.ssd.opal.library.commands;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardChannelMock extends CardChannel {

    private final Logger logger = LoggerFactory.getLogger(CardChannelMock.class);

    private Queue<ResponseAPDU> responses = new LinkedList<ResponseAPDU>();

    private List<CommandAPDU> receivedAPDU = new LinkedList<CommandAPDU>();
    private List<ResponseAPDU> sentAPDU = new LinkedList<ResponseAPDU>();

    public void addResponse(ResponseAPDU response) {
        if(response == null) {
            throw new IllegalArgumentException("response must be not null");
        }

        this.responses.add(response);
    }

    @Override
    public Card getCard() {
        return null;
    }

    @Override
    public int getChannelNumber() {
        return 0;
    }

    @Override
    public ResponseAPDU transmit(CommandAPDU capdu) throws CardException {
        if(capdu == null) {
            throw new IllegalArgumentException("capdu must be not null");
        }

        if(this.responses.size() == 0) {
            throw new CardException("no response APDU available");
        }

        ResponseAPDU response = this.responses.remove();
        receivedAPDU.add(capdu);
        sentAPDU.add(response);
        return response;
    }

    @Override
    public int transmit(ByteBuffer command, ByteBuffer response) throws CardException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void close() throws CardException {}

    public List<CommandAPDU> getReceivedAPDU() {
        return receivedAPDU;
    }

    public Queue<ResponseAPDU> getResponses() {
        return responses;
    }

    public List<ResponseAPDU> getSentAPDU() {
        return sentAPDU;
    }
}