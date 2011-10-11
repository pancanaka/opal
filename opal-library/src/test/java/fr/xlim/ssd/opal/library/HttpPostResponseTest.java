/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.xlim.ssd.opal.library;


import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bkakria Anis
 */
public class HttpPostResponseTest {
    
    
    byte[] postResponseLenthEncoding;
    byte[] postResponseChunkEncoding;
    HttpPostResponse httpPostResponse;
    HttpPostResponse httpPostResponseChunked;
    
    @Before
    public void createInstance() throws IOException
    {
        String filename= "/fr/xlim/ssd/opal/library/test/053-HttpPostResponse-Encoding-Length.txt";
        InputStream input = GP2xCommands.class.getResourceAsStream(filename);
        Reader reader = new InputStreamReader(input);
        if (reader == null) {
            throw new IllegalArgumentException("reader must be not null");
        }

        BufferedReader br = new BufferedReader(reader);
        String buffer = br.readLine();


        postResponseLenthEncoding = null;

        while (buffer != null) {
            if (buffer.startsWith("#") || buffer.isEmpty()) {
                // do nothing: it's a comment or an empty line
            } else if (postResponseLenthEncoding == null) {
                postResponseLenthEncoding = Conversion.hexToArray(buffer);
            }
            buffer = br.readLine();
        }
        httpPostResponse = new HttpPostResponse(postResponseLenthEncoding);
        
        filename= "/fr/xlim/ssd/opal/library/test/054-HttpPostResponse-Encoding-Chunked.txt";
        input = GP2xCommands.class.getResourceAsStream(filename);
        reader = new InputStreamReader(input);
        if (reader == null) {
            throw new IllegalArgumentException("reader must be not null");
        }

        br = new BufferedReader(reader);
        buffer = br.readLine();


        postResponseChunkEncoding = null;

        while (buffer != null) {
            if (buffer.startsWith("#") || buffer.isEmpty()) {
                // do nothing: it's a comment or an empty line
            } else if (postResponseChunkEncoding == null) {
                postResponseChunkEncoding = Conversion.hexToArray(buffer);
            }
            buffer = br.readLine();
        }
        httpPostResponseChunked = new HttpPostResponse(postResponseChunkEncoding);
        
    }
    
    @Test
    public void getHeaderContentTest()
    {
        assertEquals(httpPostResponse.getHeaderContent(),"HTTP/1.1 200 OK \r\nX-Admin-Protocol: globalplatform-remote-admin/1.0 \r\nX-Admin-Next-URI: URI01 \r\nContent-Type: application/vnd.globalplatform.card-content-mgt;version=1.0 \r\nX-Admin-Targeted-Application: A000000003000000 \r\nContent-Length: 13\r\n\r\n");
    }
    
    @Test
    public void getAdminTargetApplicationTest()
    {
        assertEquals(httpPostResponse.getAdminTargetApplication(),"A000000003000000 ");
    }
    
    @Test
    public void getCommandAPDUTest()
    {
        assertArrayEquals(httpPostResponse.getCommandAPDU(),Conversion.hexToArray("00 A4 04 00 08 A0 00 00 00 03 00 00 00"));
    }
    
    @Test
    public void getNextURITest()
    {
        assertEquals(httpPostResponse.getNextURI(),"URI01 ");
    }
    
    @Test
    public void getEncodingMethodLengthTest()
    {
        assertEquals(httpPostResponse.getEncodingMethod(httpPostResponse.getHeaderContent()),"length");
    }
    
    @Test
    public void getEncodingMethodChunkedTest()
    {
        assertEquals(httpPostResponseChunked.getEncodingMethod(httpPostResponseChunked.getHeaderContent()),"chunked ");
    }
    
    @Test
    public void getDataLengthTest()
    {
        assertEquals(httpPostResponse.getDataLength(httpPostResponse.getHeaderContent()), 13);
    }
}
