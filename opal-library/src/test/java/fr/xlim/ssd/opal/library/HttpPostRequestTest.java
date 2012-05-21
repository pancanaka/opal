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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.commands.GP2xCommandsTest;
import fr.xlim.ssd.opal.library.commands.ramoverhttp.HttpPostRequest;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.smartcardio.ResponseAPDU;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author anis
 */
public class HttpPostRequestTest {

    HttpPostRequest httpPostRequest;
    byte[] postRequest;

    @Test
    public void httpPostRequestConstructorTest() throws IOException {
        File file = new File(GP2xCommandsTest.getProjectRoot().getAbsolutePath() +
                "/data-for-tests/dummy-traces/055-HttpPostRequest-with-goof-response.txt");
        Reader reader = new FileReader(file);
        assertNotNull(reader);

        BufferedReader br = new BufferedReader(reader);
        String buffer = br.readLine();


        postRequest = null;

        while (buffer != null) {
            if (buffer.startsWith("#") || buffer.isEmpty()) {
                // do nothing: it's a comment or an empty line
            } else if (postRequest == null) {
                postRequest = Conversion.hexToArray(buffer);
            }
            buffer = br.readLine();
        }
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("00 00 81 58 06 54 71 91 38 23 FF 02 00 9B 5C 03 BA A1 9D A6 83 56 23 74 DA 3F 84 84 90 00"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertArrayEquals(postRequest, httpPostRequest.getContent());
    }

    @Test
    public void getURITest() throws UnsupportedEncodingException {
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("00 00 81 58 06 54 71 91 38 23 FF 02 00 9B 5C 03 BA A1 9D A6 83 56 23 74 DA 3F 84 84 90 00"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertEquals(httpPostRequest.getURI(), "URI01");
    }

    @Test
    public void getAgentIDTest() throws UnsupportedEncodingException {
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("00 00 81 58 06 54 71 91 38 23 FF 02 00 9B 5C 03 BA A1 9D A6 83 56 23 74 DA 3F 84 84 90 00"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertEquals(httpPostRequest.getAgentID(), "AgentID");
    }

    @Test
    public void getHostTest() throws UnsupportedEncodingException {
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("00 00 81 58 06 54 71 91 38 23 FF 02 00 9B 5C 03 BA A1 9D A6 83 56 23 74 DA 3F 84 84 90 00"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertEquals(httpPostRequest.getHost(), "localhost");
    }

    @Test
    public void HttpPostRequestBadFile() throws UnsupportedEncodingException, IOException {
        File file = new File(GP2xCommandsTest.getProjectRoot().getAbsolutePath() +
                "/data-for-tests/dummy-traces/056-HttpPostRequest-with-response-file-not-found.txt");
        Reader reader = new FileReader(file);
        assertNotNull(reader);

        BufferedReader br = new BufferedReader(reader);
        String buffer = br.readLine();


        postRequest = null;

        while (buffer != null) {
            if (buffer.startsWith("#") || buffer.isEmpty()) {
                // do nothing: it's a comment or an empty line
            } else if (postRequest == null) {
                postRequest = Conversion.hexToArray(buffer);
            }
            buffer = br.readLine();
        }
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("6A 82"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertArrayEquals(httpPostRequest.getContent(), postRequest);
    }

    @Test
    public void HttpPostRequestWith_SECURITY_STATUS_NOT_SATISFIED() throws UnsupportedEncodingException, IOException {
        File file = new File(GP2xCommandsTest.getProjectRoot().getAbsolutePath() +
                "/data-for-tests/dummy-traces/057-HttpPostRequest-with-SECURITY_STATUS_NOT_SATISFIED.txt");
        Reader reader = new FileReader(file);
        assertNotNull(reader);

        BufferedReader br = new BufferedReader(reader);
        String buffer = br.readLine();


        postRequest = null;

        while (buffer != null) {
            if (buffer.startsWith("#") || buffer.isEmpty()) {
                // do nothing: it's a comment or an empty line
            } else if (postRequest == null) {
                postRequest = Conversion.hexToArray(buffer);
            }
            buffer = br.readLine();
        }
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("69 82"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertArrayEquals(httpPostRequest.getContent(), postRequest);
    }
}
