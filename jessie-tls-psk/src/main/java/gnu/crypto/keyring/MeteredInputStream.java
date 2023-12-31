package gnu.crypto.keyring;  // -*- c-basic-offset: 3 -*-

// ---------------------------------------------------------------------------
// $Id: MeteredInputStream.java,v 1.2 2003/09/30 04:57:07 rsdio Exp $
//
// Copyright (C) 2003 Free Software Foundation, Inc.
//
// This file is part of GNU Crypto.
//
// GNU Crypto is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
//
// GNU Crypto is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to the
//
//    Free Software Foundation Inc.,
//    59 Temple Place - Suite 330,
//    Boston, MA 02111-1307
//    USA
//
// Linking this library statically or dynamically with other modules is
// making a combined work based on this library.  Thus, the terms and
// conditions of the GNU General Public License cover the whole
// combination.
//
// As a special exception, the copyright holders of this library give
// you permission to link this library with independent modules to
// produce an executable, regardless of the license terms of these
// independent modules, and to copy and distribute the resulting
// executable under terms of your choice, provided that you also meet,
// for each linked independent module, the terms and conditions of the
// license of that module.  An independent module is a module which is
// not derived from or based on this library.  If you modify this
// library, you may extend this exception to your version of the
// library, but you are not obligated to do so.  If you do not wish to
// do so, delete this exception statement from your version.
//
// ---------------------------------------------------------------------------

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class MeteredInputStream extends FilterInputStream {

    // Fields.
    // ------------------------------------------------------------------------

    private int count;
    private final int limit;

    // Constructor.
    // ------------------------------------------------------------------------

    MeteredInputStream(InputStream in, int limit) {
        super(in);
        if (limit < 0)
            throw new IllegalArgumentException("limit must be nonnegative");
        this.limit = limit;
        count = 0;
    }

    // Instance methods.
    // ------------------------------------------------------------------------

    /**
     * Tests if the number of bytes read has reached the limit.
     *
     * @return True if the limit has been reached.
     */
    public boolean limitReached() {
        return count == limit;
    }

    public int available() throws IOException {
        return Math.min(in.available(), limit - count);
    }

    public void close() throws IOException {
        in.close();
    }

    public void mark(int readLimit) {
    }

    public boolean markSupported() {
        return false;
    }

    public int read() throws IOException {
        if (limitReached())
            return -1;
        int i = in.read();
        if (i != -1)
            count++;
        return i;
    }

    public int read(byte[] buf) throws IOException {
        return read(buf, 0, buf.length);
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        if (limitReached())
            return -1;
        int i = in.read(buf, off, Math.min(len, limit - count));
        if (i != -1)
            count += i;
        return i;
    }

    public void reset() throws IOException {
    }

    public long skip(long len) throws IOException {
        if (limitReached())
            return 0L;
        len = Math.min(len, limit - count);
        len = in.skip(len);
        count += (int) len;
        return len;
    }
}
