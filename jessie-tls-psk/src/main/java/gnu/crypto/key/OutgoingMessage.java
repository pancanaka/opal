package gnu.crypto.key;

// ----------------------------------------------------------------------------
// $Id: OutgoingMessage.java,v 1.1 2003/09/26 23:50:48 raif Exp $
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
// ----------------------------------------------------------------------------

import gnu.crypto.Registry;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <p>An implementation of outgoing messages for use with key agreement
 * protocols.</p>
 *
 * @version $Revision: 1.1 $
 */
public class OutgoingMessage {

    // Constants and variables
    // -------------------------------------------------------------------------

    /**
     * The internal output stream.
     */
    private ByteArrayOutputStream out;

    // Constructor(s)
    // -------------------------------------------------------------------------

    public OutgoingMessage() {
        super();

        out = new ByteArrayOutputStream();
    }

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    /**
     * <p>Returns the encoded form of the current message including the 4-byte
     * length header.</p>
     *
     * @throws KeyAgreementException if an encoding size constraint is violated.
     */
    public byte[] toByteArray() throws KeyAgreementException {
        byte[] buffer = wrap();
        int length = buffer.length;
        byte[] result = new byte[length + 4];
        result[0] = (byte) (length >>> 24);
        result[1] = (byte) (length >>> 16);
        result[2] = (byte) (length >>> 8);
        result[3] = (byte) length;
        System.arraycopy(buffer, 0, result, 4, length);

        return result;
    }

    /**
     * <p>Returns the encoded form of the current message excluding the 4-byte
     * length header.</p>
     *
     * @throws KeyAgreementException if an encoding size constraint is violated.
     */
    public byte[] wrap() throws KeyAgreementException {
        int length = out.size();
        if (length > Registry.SASL_BUFFER_MAX_LIMIT || length < 0) {
            throw new KeyAgreementException("message content is too long");
        }
        return out.toByteArray();
    }

    /**
     * <p>Encodes a public key into the message.</p>
     *
     * @param k the public key to encode.
     * @throws KeyAgreementException if an encoding size constraint is violated.
     */
    public void writePublicKey(PublicKey k) throws KeyAgreementException {
        IKeyPairCodec kpc = KeyPairCodecFactory.getInstance(k);
        if (kpc == null) {
            throw new KeyAgreementException("");
        }
        byte[] b = kpc.encodePublicKey(k);
        int length = b.length;
        if (length > Registry.SASL_FOUR_BYTE_MAX_LIMIT) {
            throw new KeyAgreementException("encoded public key is too long");
        }
        byte[] lengthBytes = {
                (byte) (length >>> 24),
                (byte) (length >>> 16),
                (byte) (length >>> 8),
                (byte) length
        };
        out.write(lengthBytes, 0, 4);
        out.write(b, 0, b.length);
    }

    /**
     * <p>Encodes a private key into the message.</p>
     *
     * @param k the private key to encode.
     * @throws KeyAgreementException if an encoding size constraint is violated.
     */
    public void writePrivateKey(PrivateKey k) throws KeyAgreementException {
        IKeyPairCodec kpc = KeyPairCodecFactory.getInstance(k);
        if (kpc == null) {
            throw new KeyAgreementException("");
        }
        byte[] b = kpc.encodePrivateKey(k);
        int length = b.length;
        if (length > Registry.SASL_FOUR_BYTE_MAX_LIMIT) {
            throw new KeyAgreementException("encoded private key is too long");
        }
        byte[] lengthBytes = {
                (byte) (length >>> 24),
                (byte) (length >>> 16),
                (byte) (length >>> 8),
                (byte) length
        };
        out.write(lengthBytes, 0, 4);
        out.write(b, 0, b.length);
    }

    /**
     * <p>Encodes an MPI into the message.</p>
     *
     * @param val the MPI to encode.
     * @throws KeyAgreementException if an encoding size constraint is violated.
     */
    public void writeMPI(BigInteger val) throws KeyAgreementException {
        byte[] b = val.toByteArray();
        int length = b.length;
        if (length > Registry.SASL_TWO_BYTE_MAX_LIMIT) {
            throw new KeyAgreementException("MPI is too long");
        }
        byte[] lengthBytes = {(byte) (length >>> 8), (byte) length};
        out.write(lengthBytes, 0, 2);
        out.write(b, 0, b.length);
    }

    /**
     * <p>Encodes a string into the message.</p>
     *
     * @param s the string to encode.
     * @throws KeyAgreementException if the UTF8 encoding is not supported on
     *                               this platform, or if an encoding size constraint is violated.
     */
    public void writeString(String s) throws KeyAgreementException {
        byte[] b = null;
        try {
            b = s.getBytes("UTF8");
        } catch (UnsupportedEncodingException x) {
            throw new KeyAgreementException("unxupported UTF8 encoding", x);
        }
        int length = b.length;
        if (length > Registry.SASL_TWO_BYTE_MAX_LIMIT) {
            throw new KeyAgreementException("text too long");
        }
        byte[] lengthBytes = {(byte) (length >>> 8), (byte) length};
        out.write(lengthBytes, 0, 2);
        out.write(b, 0, b.length);
    }
}
