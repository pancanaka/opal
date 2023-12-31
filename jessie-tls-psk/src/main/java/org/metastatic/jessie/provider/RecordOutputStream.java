/* RecordOutputStream.java -- record layer output.
   Copyright (C) 2004  Casey Marshall <rsdio@metastatic.org>

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
   USA

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under terms
of your choice, provided that you also meet, for each linked independent
module, the terms and conditions of the license of that module.  An
independent module is a module which is not derived from or based on
this library.  If you modify this library, you may extend this exception
to your version of the library, but you are not obligated to do so.  If
you do not wish to do so, delete this exception statement from your
version.  */

package org.metastatic.jessie.provider;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * An output stream for writing data to the record layer. All data written to
 * this stream (through any of the write methods) is immediately sent as a full
 * record, so it is advisable to write large arrays to the stream instead of one
 * byte at a time (alternatively, a {@link java.io.BufferedOutputStream} can be
 * used).
 */
class RecordOutputStream extends FilterOutputStream {

    // Fields.
    // -------------------------------------------------------------------------

    private static final boolean DEBUG_RECORD_LAYER = false;
    private static final PrintWriter debug = new PrintWriter(System.err, true);

    /**
     * The content type of this output stream.
     */
    private final ContentType type;

    /**
     * The security parameters.
     */
    private final SecurityParameters params;

    private final boolean emitEmpty;

    private static final byte[] ZERO = new byte[0];

    // Constructor.
    // -------------------------------------------------------------------------

    RecordOutputStream(final OutputStream out, final ContentType type,
                       final SecurityParameters params) {
        super(out);
        this.type = type;
        this.params = params;
        String empty = Util.getSecurityProperty("jessie.emit.empty.records");
        if (empty == null) {
            empty = "true";
        }
        emitEmpty = Boolean.valueOf(empty).booleanValue()
                && type == ContentType.APPLICATION_DATA;
    }

    // Instance methods.
    // -------------------------------------------------------------------------

    public void write(int b) throws IOException {
        write(new byte[]{(byte) b});
    }

    public void write(byte[] buf) throws IOException {
        write(buf, 0, buf.length);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > buf.length) {
            throw new ArrayIndexOutOfBoundsException("size=" + buf.length
                    + " off=" + off + " len=" + len);
        }

        CSPRNG.SYSTEM_RANDOM.addRandomBytes(buf, off, len);

        int count = 0;
        int len2 = 0;
        do {
            if (emitEmpty) {
                byte[] fragment = params.encrypt(ZERO, 0, 0, type);
                if (DEBUG_RECORD_LAYER) {
                    debug.println(">> WRITING RECORD <<");
                    debug.println("struct {");
                    debug.println("  type = " + type);
                    debug.println("  version = " + params.getVersion());
                    debug.println("  length = " + fragment.length);
                    debug.print(Util.hexDump(fragment, "  "));
                    debug.println("} TLSCiphertext;");
                }
                out.write(type.getValue());
                params.getVersion().write(out);
                out.write((fragment.length >>> 8) & 0xFF);
                out.write(fragment.length & 0xFF);
                out.write(fragment);
                out.flush();
            }
            len2 = Math.min(len - count, params.getFragmentLength());
            if (DEBUG_RECORD_LAYER) {
                debug.println("writing chunk size=" + len2);
            }
            synchronized (out) {
                byte[] fragment = params.encrypt(buf, off + count, len2, type);
                if (DEBUG_RECORD_LAYER) {
                    debug.println(">> WRITING RECORD <<");
                    debug.println("struct {");
                    debug.println("  type = " + type);
                    debug.println("  version = " + params.getVersion());
                    debug.println("  length = " + fragment.length);
                    debug.print(Util.hexDump(fragment, "  "));
                    debug.println("} TLSCiphertext;");
                }
                out.write(type.getValue());
                params.getVersion().write(out);
                out.write((fragment.length >>> 8) & 0xFF);
                out.write(fragment.length & 0xFF);
                out.write(fragment);
                out.flush();
            }
            count += len2;
        } while (count < len);
    }
}
