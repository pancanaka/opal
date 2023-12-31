package gnu.crypto.assembly;

// ----------------------------------------------------------------------------
// $Id: CascadeTransformer.java,v 1.1 2003/05/10 18:48:27 raif Exp $
//
// Copyright (C) 2003, Free Software Foundation, Inc.
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
// ----------------------------------------------------------------------------

import java.security.InvalidKeyException;
import java.util.Map;

/**
 * An Adapter to use any {@link Cascade} as a {@link Transformer} in an
 * {@link Assembly}.
 *
 * @version $Revision: 1.1 $
 */
class CascadeTransformer extends Transformer {

    // Constants and variables
    // -------------------------------------------------------------------------

    private Cascade delegate;
    private int blockSize;

    // Constructor(s)
    // -------------------------------------------------------------------------

    CascadeTransformer(Cascade delegate) {
        super();

        this.delegate = delegate;
    }

    // Class methods
    // -------------------------------------------------------------------------

    // Instant methods
    // -------------------------------------------------------------------------

    void initDelegate(Map attributes) throws TransformerException {
        attributes.put(Cascade.DIRECTION, wired);
        try {
            delegate.init(attributes);
        } catch (InvalidKeyException x) {
            throw new TransformerException("initDelegate()", x);
        }
        blockSize = delegate.currentBlockSize();
    }

    int delegateBlockSize() {
        return blockSize;
    }

    void resetDelegate() {
        delegate.reset();
        blockSize = 0;
    }

    byte[] updateDelegate(byte[] in, int offset, int length) throws TransformerException {
        byte[] result = updateInternal(in, offset, length);
        return result;
    }

    byte[] lastUpdateDelegate() throws TransformerException {
        if (inBuffer.size() != 0) {
            throw new TransformerException("lastUpdateDelegate()",
                    new IllegalStateException("Cascade transformer, after last "
                            + "update, must be empty but isn't"));
        }
        return new byte[0];
    }

    private byte[] updateInternal(byte[] in, int offset, int length) {
        byte[] result;
        for (int i = 0; i < length; i++) {
            inBuffer.write(in[offset++] & 0xFF);
            if (inBuffer.size() >= blockSize) {
                result = inBuffer.toByteArray();
                inBuffer.reset();
                delegate.update(result, 0, result, 0);
                outBuffer.write(result, 0, blockSize);
            }
        }
        result = outBuffer.toByteArray();
        outBuffer.reset();
        return result;
    }
}
