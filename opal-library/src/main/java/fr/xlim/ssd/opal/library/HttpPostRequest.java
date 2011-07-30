package fr.xlim.ssd.opal.library;

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
            if ((restOfdivision.length() % 2) == 1) {
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
