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
import fr.xlim.ssd.opal.library.commands.ramoverhttp.HttpPostResponse;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Bkakria Anis
 */
public class HttpPostResponseTest {


    byte[] postResponseLenthEncoding;
    byte[] postResponseChunkEncoding;
    HttpPostResponse httpPostResponse;
    HttpPostResponse httpPostResponseChunked;

    @Before
    public void createInstance() throws IOException {
        File file = new File(GP2xCommandsTest.getProjectRoot().getAbsolutePath() +
                "/data-for-tests/dummy-traces/053-HttpPostResponse-Encoding-Length.txt");
        Reader reader = new FileReader(file);
        assertNotNull(reader);

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

        file = new File(GP2xCommandsTest.getProjectRoot().getAbsolutePath() +
                "/data-for-tests/dummy-traces/054-HttpPostResponse-Encoding-Chunked.txt");
        reader = new FileReader(file);
        assertNotNull(reader);

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
    public void getHeaderContentTest() {
        assertEquals(httpPostResponse.getHeaderContent(), "HTTP/1.1 200 OK \r\nX-Admin-Protocol: globalplatform-remote-admin/1.0 \r\nX-Admin-Next-URI: URI01 \r\nContent-Type: application/vnd.globalplatform.card-content-mgt;version=1.0 \r\nX-Admin-Targeted-Application: A000000003000000 \r\nContent-Length: 13\r\n\r\n");
    }

    @Test
    public void getAdminTargetApplicationTest() {
        assertEquals(httpPostResponse.getAdminTargetApplication(), "A000000003000000 ");
    }

    @Test
    public void getCommandAPDUTest() {
        assertArrayEquals(httpPostResponse.getCommandAPDU(), Conversion.hexToArray("00 A4 04 00 08 A0 00 00 00 03 00 00 00"));
    }

    @Test
    public void getNextURITest() {
        assertEquals(httpPostResponse.getNextURI(), "URI01 ");
    }

    @Test
    public void getEncodingMethodLengthTest() {
        assertEquals(httpPostResponse.getEncodingMethod(httpPostResponse.getHeaderContent()), "length");
    }

    @Test
    public void getEncodingMethodChunkedTest() {
        assertEquals(httpPostResponseChunked.getEncodingMethod(httpPostResponseChunked.getHeaderContent()), "chunked ");
    }

    @Test
    public void getDataLengthTest() {
        assertEquals(httpPostResponse.getDataLength(httpPostResponse.getHeaderContent()), 13);
    }
}
