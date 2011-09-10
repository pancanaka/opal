/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.utilities.Conversion;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bkakria Anis
 */
public class HttpPostResponse {

    private String nextURI = null;
    private String adminTargetApplication;
    private byte[] commandAPDU;
    private String headerContent;

    public HttpPostResponse(byte[] data) throws UnsupportedEncodingException {
        byte[] header = getHeaderData(data);
        this.commandAPDU = new byte[data.length - header.length];
        System.arraycopy(data, header.length, this.commandAPDU, 0, commandAPDU.length);
        this.headerContent = Conversion.toAsciiString(header);
        System.out.print(headerContent);
        System.out.print(Conversion.arrayToHex(commandAPDU));

        // Extraction of next-URI value from header (if exist)
        Pattern p = Pattern.compile("X-Admin-Next-URI: (.+)");
        Matcher m = p.matcher(headerContent);
        while (m.find()) {
            this.nextURI = m.group(1);
        }

        p = Pattern.compile("X-Admin-Targeted-Application: (.+)");
        m = p.matcher(headerContent);
        while (m.find()) {
            this.adminTargetApplication = m.group(1);
        }
    }

    public String getHeaderContent() {
        return headerContent;
    }

    public String getAdminTargetApplication() {
        return adminTargetApplication;
    }

    public byte[] getCommandAPDU() {
        return commandAPDU;
    }

    public String getNextURI() {
        return nextURI;
    }

    private byte[] getHeaderData(byte[] data) {
        int indexEndHeader = 0;
        byte[] header = null;
        byte[] seperator = {(byte) 0x0D, (byte) (0x0A), (byte) 0x0D, (byte) (0x0A)};
        for (int i = 0; i < data.length - 4; i++) {
            byte[] temp = new byte[4];
            System.arraycopy(data, i, temp, 0, 4);
            if (Arrays.equals(temp, seperator)) {
                indexEndHeader = i + 4;
                header = new byte[indexEndHeader];
                System.arraycopy(data, 0, header, 0, indexEndHeader);
            }
        }
        return header;
    }

    public static String getEncodingMethod(String header) {
        String encoding = null;
        Pattern p = Pattern.compile("Transfer-Encoding: (.+)");
        Matcher m = p.matcher(header);
        while (m.find()) {
            encoding = m.group(1);
        }

        p = Pattern.compile("Content-Length: (.+)");
        m = p.matcher(header);
        while (m.find()) {
            encoding = "length";
        }
        return encoding;
    }

    public static int getDataLength(String header) {
        int encoding = 0;
        Pattern p = Pattern.compile("Content-Length: (.+)");
        Matcher m = p.matcher(header);
        while (m.find()) {
            encoding = Integer.parseInt(m.group(1));
        }
        return encoding;
    }

}
