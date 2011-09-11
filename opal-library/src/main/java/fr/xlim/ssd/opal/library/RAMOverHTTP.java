/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.xlim.ssd.opal.library;

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
            //logger.info(Conversion.arrayToHex(response.getData()));
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
