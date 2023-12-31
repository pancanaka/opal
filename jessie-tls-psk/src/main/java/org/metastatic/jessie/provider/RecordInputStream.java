/* RecordInputStream.java -- record layer input stream interface.
   Copyright (C) 2003,2004  Casey Marshall <rsdio@metastatic.org>

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

import java.io.IOException;
import java.io.InputStream;

class RecordInputStream extends InputStream {

    // Fields.
    // -------------------------------------------------------------------------

    /**
     * The record input instance.
     */
    private final RecordInput in;

    /**
     * The content type this stream is reading.
     */
    private final ContentType type;

    // Constructor.
    // -------------------------------------------------------------------------

    RecordInputStream(RecordInput in, ContentType type) {
        this.in = in;
        this.type = type;
    }

    // Instance methods.
    // -------------------------------------------------------------------------

    public int available() throws IOException {
        return in.available(type);
    }

    public int read() throws IOException {
        byte[] b = new byte[1];
        int ret;
        while ((ret = read(b)) != 1) {
            if (ret == -1) {
                return -1;
            }
            Thread.yield();
        }
        return b[0] & 0xFF;
    }

    public int read(byte[] buf) throws IOException {
        return read(buf, 0, buf.length);
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        return in.read(buf, off, len, type);
    }

    public String toString() {
        return RecordInputStream.class.getName() + " [ type=" + type + " ]";
    }
}
