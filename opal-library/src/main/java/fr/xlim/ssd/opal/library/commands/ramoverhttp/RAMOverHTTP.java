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
package fr.xlim.ssd.opal.library.commands.ramoverhttp;

import fr.xlim.ssd.opal.library.commands.ISO7816;
import fr.xlim.ssd.opal.library.applet.SecurityDomain;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.metastatic.jessie.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anis
 */
public class RAMOverHTTP {
    /// the logger
    private final static Logger logger = LoggerFactory.getLogger(RAMOverHTTP.class);

    String serverAddress;
    int portNumber;
    String administrationHost;
    String agentId;

    SSLSocket cSock;


    public RAMOverHTTP(String PSKTLSKeyFile, String clientSessionCodec, String PSKIdentity, String administartionHost, String agentId) {
        Security.setProperty("jessie.psk.storage.file", PSKTLSKeyFile);
        Security.setProperty("jessie.clientSessionContext.codec", clientSessionCodec);
        Security.setProperty("jessie.psk.identity", PSKIdentity);

        String nextURI = null;
        this.administrationHost = "localhost";
        this.agentId = "OpalJcop21";
    }

    public void setup(String serverAdress, int portNumber, CipherSuite cipherSuite) throws IOException {
        this.serverAddress = serverAdress;
        this.portNumber = portNumber;
        Security.addProvider(new Jessie());
        SSLSocketFactory fact = new SSLSocketFactory(null, null, new SecureRandom(), null);
        SessionContext sessionContext = new SessionContext();
        cSock = (SSLSocket) fact.createSocket(serverAdress, portNumber);
        cSock.setSessionContext(sessionContext);
        List enabledCipherSuite = new ArrayList();
        enabledCipherSuite.add(cipherSuite);
        cSock.setEnabledCipherSuites(enabledCipherSuite);
        cSock.startHandshake();
        cSock.session.params.setInflating(false);
        cSock.session.params.setDeflating(false);
    }

    public void manage(SecurityDomain securityDomain) throws IOException, CardException {

        OutputStream out = cSock.getOutputStream();
        InputStream in = cSock.getInputStream();


        boolean moreCommand = true;
        int i = 1;
        byte[] CRLF = {(byte) 0x0D, (byte) (0x0A)};
        String encoding = null;
        while (moreCommand) {
            String message = "";
            String temp = "";
            int ch = 0;
            while ((ch = in.read()) > -1) {
                message += (char) ch;
                if ((ch == 13) || (ch == 10)) {
                    temp += (char) ch;
                    //System.out.println("je suis la ");
                } else {
                    temp = "";
                }
                if (temp.equals(Conversion.toAsciiString(CRLF) + Conversion.toAsciiString(CRLF))) {
                    encoding = HttpPostResponse.getEncodingMethod(message);
                    System.out.println("encoding " + encoding);
                    break;
                }
            }
            if (encoding.contains("chunked")) {
                logger.debug("Chunked encoding is used");
                boolean moreChunk = true;
                while (moreChunk) {
                    String lenthChunkedData = "";
                    int lenthChunked = -1;
                    String tempChunked = "";
                    ch = 0;
                    while ((ch = in.read()) > -1) {
                        if ((ch == 13) || (ch == 10)) {
                            temp += (char) ch;
                            //System.out.println("je suis la ");
                        } else {
                            lenthChunkedData += (char) ch;
                            temp = "";
                        }
                        if (temp.equals(Conversion.toAsciiString(CRLF))) {
                            //System.out.println("lenth "+Conversion.arrayToHex(lenthChunkedData.getBytes()));
                            lenthChunked = Conversion.byteArrayToInt(lenthChunkedData.getBytes());
                            lenthChunkedData = "";
                            for (int k = 0; k < lenthChunked; k++) {
                                message += (char) in.read();
                            }
                        }
                        if ((temp.equals(Conversion.toAsciiString(CRLF) + Conversion.toAsciiString(CRLF))) && (lenthChunked == 0)) {
                            System.out.println("end chunked data");
                            moreChunk = false;
                            break;
                        }
                    }
                }
                encoding = "";
            }
            if (encoding.contains("length")) {
                int lengthData = HttpPostResponse.getDataLength(message);
                logger.debug("Length is " + lengthData);
                for (int k = 0; k < lengthData; k++) {
                    message += (char) in.read();
                }
                encoding = "";
            }
            logger.debug("message " + i + "\n" + message);
            HttpPostResponse httpResponse = new HttpPostResponse(message.getBytes());
            String nextURI = httpResponse.getNextURI();
            ResponseAPDU response = securityDomain.sendCommand(httpResponse.getCommandAPDU());
            //logger.info(Conversion.arrayToHex(response.getValue()));
            if (nextURI == null) {
                moreCommand = false;
                logger.debug("No more URI");
            } else {
                HttpPostRequest httpPostRequest = new HttpPostRequest(nextURI, administrationHost, agentId, response);
                out.write(httpPostRequest.getContent());
            }
            if (response.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
                throw new CardException("Error  : " + Integer.toHexString(response.getSW()));
            }
            i++;
        }
        logger.debug("Closing Socket");
    }

}
