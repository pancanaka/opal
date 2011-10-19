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
package fr.xlim.ssd.opal.library.commands.ramoverhttp;

import fr.xlim.ssd.opal.library.commands.ISO7816;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import javax.smartcardio.ResponseAPDU;
import java.io.UnsupportedEncodingException;

/**
 * @author Bkakria Anis
 */
public class HttpPostRequest {

    protected static final byte[] CRLF = {(byte) 0x0D, (byte) (0x0A)};

    private String URI;
    private String host;
    private String agentId;


    private byte[] content;

    /**
     * Create a POST request witch used by the Security Domain to fetch remote APDU format strings.
     * The "URI", the "X-Admin-From"(agentId) value and the "administrationHost" value to be used are defined in the administration session triggering message or by Security Domain parameters
     *
     * @param URI
     * @param administrationHost
     * @param agentId
     * @param response
     */
    public HttpPostRequest(String URI, String administrationHost, String agentId, ResponseAPDU response) throws UnsupportedEncodingException {
        this.URI = URI;
        this.agentId = agentId;
        this.host = administrationHost;
        String contentRequest = "";
        byte[] byteContentRequest;
        contentRequest += "POST " + URI + " HTTP/1.1 " + Conversion.toAsciiString(CRLF);
        contentRequest += "Host: " + administrationHost + " " + Conversion.toAsciiString(CRLF);
        contentRequest += "X-Admin-Protocol: globalplatform-remote-admin/1.0 " + Conversion.toAsciiString(CRLF);
        contentRequest += "X-Admin-From: " + agentId + " " + Conversion.toAsciiString(CRLF);
        if (response != null) {
            contentRequest += "Content-Type: application/vnd.globalplatform.card-content-mgt-response;version=1.0 " + Conversion.toAsciiString(CRLF);
            contentRequest += "Transfer-Encoding: chunked " + Conversion.toAsciiString(CRLF);
            if (response.getSW() == ISO7816.SW_FILE_NOT_FOUND.getValue()) {
                contentRequest += "X-Admin-Script-Status: unknown-application " + Conversion.toAsciiString(CRLF);
                response = null;
            } else if (response.getSW() == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED.getValue()) {
                contentRequest += "X-Admin-Script-Status: security-error " + Conversion.toAsciiString(CRLF);
                response = null;
            } else if (response.getSW() == ISO7816.SW_NO_ERROR.getValue()) {
                contentRequest += "X-Admin-Script-Status: ok " + Conversion.toAsciiString(CRLF);
            } else {
                response = null;
            }
        }
        contentRequest += Conversion.toAsciiString(CRLF);
        if (response != null) {
            int k = 0;
            byte[] temp;
            String chunkedResponse = "";
            String restOfdivision = "";
            byte[] byteChunkedResponse;
            for (k = 0; k < (response.getData().length / 8); k++) {
                chunkedResponse += "08 " + Conversion.arrayToHex(CRLF);
                temp = new byte[8];
                System.arraycopy(response.getData(), k * 8, temp, 0, 8);
                chunkedResponse += Conversion.arrayToHex(temp) + Conversion.arrayToHex(CRLF);
            }
            temp = new byte[response.getData().length % 8];
            System.arraycopy(response.getData(), k * 8, temp, 0, response.getData().length % 8);
            restOfdivision = Integer.toHexString(response.getData().length % 8);
            if ((restOfdivision.length() % 2) != 0) {
                restOfdivision = "0" + restOfdivision;
            }
            chunkedResponse += restOfdivision + " " + Conversion.arrayToHex(CRLF);
            chunkedResponse += Conversion.arrayToHex(temp) + Conversion.arrayToHex(CRLF);
            chunkedResponse += "00 " + Conversion.arrayToHex(CRLF);
            chunkedResponse += Conversion.arrayToHex(CRLF);
            byteChunkedResponse = Conversion.hexToArray(chunkedResponse);
            byteContentRequest = new byte[contentRequest.getBytes().length + byteChunkedResponse.length];
            System.arraycopy(contentRequest.getBytes(), 0, byteContentRequest, 0, contentRequest.getBytes().length);
            System.arraycopy(byteChunkedResponse, 0, byteContentRequest, contentRequest.getBytes().length, byteChunkedResponse.length);
            this.content = new byte[byteContentRequest.length];
            System.arraycopy(byteContentRequest, 0, content, 0, byteContentRequest.length);
        } else {
            this.content = new byte[contentRequest.getBytes().length];
            System.arraycopy(contentRequest.getBytes(), 0, content, 0, contentRequest.getBytes().length);
        }

    }

    public String getURI() {
        return URI;
    }

    public String getAgentID() {
        return agentId;
    }

    public byte[] getContent() {
        return content;
    }

    public String getHost() {
        return host;
    }

}
