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
        for (int i = 0; i < data.length - 3; i++) {
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
