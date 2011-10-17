/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import fr.xlim.ssd.opal.library.commands.ramoverhttp.HttpPostRequest;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.smartcardio.ResponseAPDU;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author anis
 */
public class httpPostRequestTest {

    HttpPostRequest httpPostRequest;
    byte[] postRequest;

    @Test
    public void httpPostRequestConstructorTest() throws IOException
    {
        String filename= "/fr/xlim/ssd/opal/library/test/055-HttpPostRequest-with-goof-response.txt";
        InputStream input = GP2xCommands.class.getResourceAsStream(filename);
        Reader reader = new InputStreamReader(input);
        if (reader == null) {
            throw new IllegalArgumentException("reader must be not null");
        }

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
    public void getURITest() throws UnsupportedEncodingException{
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("00 00 81 58 06 54 71 91 38 23 FF 02 00 9B 5C 03 BA A1 9D A6 83 56 23 74 DA 3F 84 84 90 00"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertEquals(httpPostRequest.getURI(), "URI01");
    }

    @Test
    public void getAgentIDTest() throws UnsupportedEncodingException{
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("00 00 81 58 06 54 71 91 38 23 FF 02 00 9B 5C 03 BA A1 9D A6 83 56 23 74 DA 3F 84 84 90 00"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertEquals(httpPostRequest.getAgentID(), "AgentID");
    }

    @Test
    public void getHostTest() throws UnsupportedEncodingException{
        ResponseAPDU responseAPDU = new ResponseAPDU(Conversion.hexToArray("00 00 81 58 06 54 71 91 38 23 FF 02 00 9B 5C 03 BA A1 9D A6 83 56 23 74 DA 3F 84 84 90 00"));
        httpPostRequest = new HttpPostRequest("URI01", "localhost", "AgentID", responseAPDU);
        assertEquals(httpPostRequest.getHost(), "localhost");
    }

    @Test
    public void HttpPostRequestBadFile() throws UnsupportedEncodingException, IOException{
        String filename= "/fr/xlim/ssd/opal/library/test/056-HttpPostRequest-with-response-file-not-found.txt";
        InputStream input = GP2xCommands.class.getResourceAsStream(filename);
        Reader reader = new InputStreamReader(input);
        if (reader == null) {
            throw new IllegalArgumentException("reader must be not null");
        }

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
    public void HttpPostRequestWith_SECURITY_STATUS_NOT_SATISFIED() throws UnsupportedEncodingException, IOException{
        String filename= "/fr/xlim/ssd/opal/library/test/057-HttpPostRequest-with-SECURITY_STATUS_NOT_SATISFIED.txt";
        InputStream input = GP2xCommands.class.getResourceAsStream(filename);
        Reader reader = new InputStreamReader(input);
        if (reader == null) {
            throw new IllegalArgumentException("reader must be not null");
        }

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
