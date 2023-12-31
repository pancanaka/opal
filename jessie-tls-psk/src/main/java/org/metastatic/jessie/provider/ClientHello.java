/* ClientHello.java -- SSL ClientHello message.
   Copyright (C) 2003  Casey Marshall <rsdio@metastatic.org>

This file is a part of Jessie.

Jessie is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2 of the License, or (at your
option) any later version.

Jessie is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with Jessie; if not, write to the

   Free Software Foundation, Inc.,
   59 Temple Place, Suite 330,
   Boston, MA  02111-1307
   USA  */

package org.metastatic.jessie.provider;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class ClientHello implements Handshake.Body {

    // Fields.
    // -------------------------------------------------------------------------

    private ProtocolVersion version;
    private Random random;
    private byte[] sessionId;
    private List suites;
    private List comp;
    private List extensions;

    // Constructor.
    // -------------------------------------------------------------------------

    ClientHello(ProtocolVersion version, Random random, byte[] sessionId,
                List suites, List comp) {
        this(version, random, sessionId, suites, comp, null);
    }

    ClientHello(ProtocolVersion version, Random random, byte[] sessionId,
                List suites, List comp, List extensions) {
        this.version = version;
        this.random = random;
        this.sessionId = sessionId;
        this.suites = suites;
        this.comp = comp;
        this.extensions = extensions;
    }

    // Class methods.
    // -------------------------------------------------------------------------

    static ClientHello read(InputStream in) throws IOException {
        ProtocolVersion vers = ProtocolVersion.read(in);
        Random rand = Random.read(in);
        byte[] id = new byte[in.read() & 0xFF];
        in.read(id);
        int len = (in.read() & 0xFF) << 8 | (in.read() & 0xFF);
        ArrayList suites = new ArrayList(len / 2);
        for (int i = 0; i < len; i += 2) {
            suites.add(CipherSuite.read(in).resolve(vers));
        }
        len = in.read() & 0xFF;
        ArrayList comp = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            comp.add(CompressionMethod.read(in));
        }

        List ext = null;
        // Since parsing MAY need to continue into the extensions fields, or it
        // may end here, the specified input stream MUST be a
        // ByteArrayInputStream
        // over all the data this hello contains. Otherwise this will mess up
        // the data stream.
        if (in.available() > 0) // then we have extensions.
        {
            ext = new LinkedList();
            len = (in.read() & 0xFF) << 8 | (in.read() & 0xFF);
            int count = 0;
            while (count < len) {
                Extension e = Extension.read(in);
                ext.add(e);
                count += e.getValue().length + 4;
            }
        }
        return new ClientHello(vers, rand, id, suites, comp, ext);
    }

    // Instance methods.
    // -------------------------------------------------------------------------

    public void write(OutputStream out) throws IOException {
        version.write(out);
        random.write(out);
        out.write(sessionId.length);
        out.write(sessionId);
        out.write((suites.size() << 1) >>> 8 & 0xFF);
        out.write((suites.size() << 1) & 0xFF);
        for (Iterator i = suites.iterator(); i.hasNext(); ) {
            ((CipherSuite) i.next()).write(out);
        }
        out.write(comp.size());
        for (Iterator i = comp.iterator(); i.hasNext(); ) {
            out.write(((CompressionMethod) i.next()).getValue());
        }
        if (extensions != null) {
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            for (Iterator i = extensions.iterator(); i.hasNext(); ) {
                ((Extension) i.next()).write(out2);
            }
            out.write(out2.size() >>> 8 & 0xFF);
            out.write(out2.size() & 0xFF);
            out2.writeTo(out);
        }
    }

    ProtocolVersion getVersion() {
        return version;
    }

    Random getRandom() {
        return random;
    }

    byte[] getSessionId() {
        return sessionId;
    }

    List getCipherSuites() {
        return suites;
    }

    List getCompressionMethods() {
        return comp;
    }

    List getExtensions() {
        return extensions;
    }

    public String toString() {
        StringWriter str = new StringWriter();
        PrintWriter out = new PrintWriter(str);
        out.println("struct {");
        out.println("  version = " + version + ";");
        BufferedReader r = new BufferedReader(new StringReader(
                random.toString()));
        String s;
        try {
            while ((s = r.readLine()) != null) {
                out.print("  ");
                out.println(s);
            }
        } catch (IOException ignored) {
        }
        out.println("  sessionId = " + Util.toHexString(sessionId, ':') + ";");
        out.println("  cipherSuites = {");
        for (Iterator i = suites.iterator(); i.hasNext(); ) {
            out.print("    ");
            out.println(i.next());
        }
        out.println("  };");
        out.print("  compressionMethods = { ");
        for (Iterator i = comp.iterator(); i.hasNext(); ) {
            out.print(i.next());
            if (i.hasNext())
                out.print(", ");
        }
        out.println(" };");
        if (extensions != null) {
            out.println("  extensions = {");
            for (Iterator i = extensions.iterator(); i.hasNext(); ) {
                r = new BufferedReader(new StringReader(i.next().toString()));
                try {
                    while ((s = r.readLine()) != null) {
                        out.print("    ");
                        out.println(s);
                    }
                } catch (IOException ignored) {
                }
            }
            out.println("  };");
        }
        out.println("} ClientHello;");
        return str.toString();
    }
}
