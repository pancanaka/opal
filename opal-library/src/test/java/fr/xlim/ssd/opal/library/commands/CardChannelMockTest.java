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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertArrayEquals;

public class CardChannelMockTest {

    private CardChannel cardChannel;

    private final static File dummyTraceFile =
            new File(GP2xCommandsTest.getProjectRoot().getAbsolutePath() +
                    "/data-for-tests/dummy-traces/001-cardChannelMock-dummy.txt");


    @Before
    public void createCardChannelMock() throws IOException, CardException {
        InputStream input = new FileInputStream(dummyTraceFile);
        Reader reader = new InputStreamReader(input);
        cardChannel = new CardChannelMock(reader);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailedWhenCommandNull() throws CardException, IOException {
        new CardChannelMock(null);
    }

    @Test
    public void testClose() throws CardException, IOException {
        byte[] bb = {0x12, 0x34, 0x56, 0x78};
        ResponseAPDU response = cardChannel.transmit(new CommandAPDU(bb));
        byte[] expected = {0x00, 0x00, (byte) 0x90, 0x00};
        assertArrayEquals(expected, response.getBytes());
        cardChannel.close();
    }

    @Test
    public void testTransmitFailedIfNoMoreAPDUAvailable() throws CardException, IOException {
        byte[] bb = {0x12, 0x34, 0x56, 0x78};
        cardChannel.transmit(new CommandAPDU(bb));

        expectedException.expect(CardException.class);
        expectedException.expectMessage("No more command APDU expected");
        cardChannel.transmit(new CommandAPDU(bb));
    }

    private final static File dummyTraceFailedFile =
            new File(GP2xCommandsTest.getProjectRoot().getAbsolutePath() +
                    "/data-for-tests/dummy-traces/002-cardChannelMock-failed.txt");

    @Test
    public void testConstructorFailedIfNoAssociatedResponse() throws CardException, IOException {
        InputStream input = new FileInputStream(dummyTraceFailedFile);
        Reader reader = new InputStreamReader(input);

        expectedException.expect(CardException.class);
        expectedException.expectMessage("No response APDU available");
        new CardChannelMock(reader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransmitFailedWhenCommandNull() throws CardException {
        cardChannel.transmit(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBufferTransmitNotImplemented() throws CardException {
        cardChannel.transmit(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCardNotImplemented() {
        cardChannel.getCard();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetChannelNumberNotImplemented() {
        cardChannel.getChannelNumber();
    }
}
