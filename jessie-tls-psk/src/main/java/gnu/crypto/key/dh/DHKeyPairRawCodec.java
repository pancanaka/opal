package gnu.crypto.key.dh;

// ----------------------------------------------------------------------------
// $Id: DHKeyPairRawCodec.java,v 1.1 2003/09/26 23:50:48 raif Exp $
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
import gnu.crypto.key.IKeyPairCodec;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <p>An object that implements the {@link IKeyPairCodec} operations for the
 * <i>Raw</i> format to use with Diffie-Hellman keypairs.</p>
 *
 * @version $Revision: 1.1 $
 */
public class DHKeyPairRawCodec implements IKeyPairCodec {

    // Constants and variables
    // -------------------------------------------------------------------------

    // Constructor(s)
    // -------------------------------------------------------------------------

    // implicit 0-arguments ctor

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    // gnu.crypto.keys.IKeyPairCodec interface implementation -------------------

    public int getFormatID() {
        return RAW_FORMAT;
    }

    /**
     * <p>Returns the encoded form of the designated Diffie-Hellman public key
     * according to the <i>Raw</i> format supported by this library.</p>
     * <p/>
     * <p>The <i>Raw</i> format for a DH public key, in this implementation, is
     * a byte sequence consisting of the following:</p>
     * <p/>
     * <ol>
     * <li>4-byte magic consisting of the value of the literal
     * {@link Registry#MAGIC_RAW_DH_PUBLIC_KEY},<li>
     * <li>1-byte version consisting of the constant: 0x01,</li>
     * <li>4-byte count of following bytes representing the DH parameter
     * <code>q</code> in internet order,</li>
     * <li>n-bytes representation of a {@link BigInteger} obtained by invoking
     * the <code>toByteArray()</code> method on the DH parameter <code>q</code>,</li>
     * <li>4-byte count of following bytes representing the DH parameter
     * <code>p</code> in internet order,</li>
     * <li>n-bytes representation of a {@link BigInteger} obtained by invoking
     * the <code>toByteArray()</code> method on the DH parameter <code>p</code>,</li>
     * <li>4-byte count of following bytes representing the DH parameter
     * <code>g</code>,</li>
     * <li>n-bytes representation of a {@link BigInteger} obtained by invoking
     * the <code>toByteArray()</code> method on the DH parameter <code>g</code>,</li>
     * <li>4-byte count of following bytes representing the DH parameter
     * <code>y</code>,</li>
     * <li>n-bytes representation of a {@link BigInteger} obtained by invoking
     * the <code>toByteArray()</code> method on the DH parameter <code>y</code>,</li>
     * </ol>
     *
     * @param key the key to encode.
     * @return the <i>Raw</i> format encoding of the designated key.
     * @throws IllegalArgumentException if the designated key is not a DH one.
     * @see Registry#MAGIC_RAW_DH_PUBLIC_KEY
     */
    public byte[] encodePublicKey(PublicKey key) {
        if (!(key instanceof GnuDHPublicKey)) {
            throw new IllegalArgumentException("key");
        }

        GnuDHPublicKey dhKey = (GnuDHPublicKey) key;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // magic
        baos.write(Registry.MAGIC_RAW_DH_PUBLIC_KEY[0]);
        baos.write(Registry.MAGIC_RAW_DH_PUBLIC_KEY[1]);
        baos.write(Registry.MAGIC_RAW_DH_PUBLIC_KEY[2]);
        baos.write(Registry.MAGIC_RAW_DH_PUBLIC_KEY[3]);

        // version
        baos.write(0x01);

        // q
        byte[] buffer = dhKey.getQ().toByteArray();
        int length = buffer.length;
        baos.write(length >>> 24);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        baos.write(buffer, 0, length);

        // p
        buffer = dhKey.getParams().getP().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        baos.write(buffer, 0, length);

        // g
        buffer = dhKey.getParams().getG().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        baos.write(buffer, 0, length);

        // y
        buffer = dhKey.getY().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        baos.write(buffer, 0, length);

        return baos.toByteArray();
    }

    public PublicKey decodePublicKey(byte[] k) {
        // magic
        if (k[0] != Registry.MAGIC_RAW_DH_PUBLIC_KEY[0]
                || k[1] != Registry.MAGIC_RAW_DH_PUBLIC_KEY[1]
                || k[2] != Registry.MAGIC_RAW_DH_PUBLIC_KEY[2]
                || k[3] != Registry.MAGIC_RAW_DH_PUBLIC_KEY[3]) {
            throw new IllegalArgumentException("magic");
        }

        // version
        if (k[4] != 0x01) {
            throw new IllegalArgumentException("version");
        }
        int i = 5;
        int l;
        byte[] buffer;

        // q
        l = k[i++] << 24 | (k[i++] & 0xFF) << 16 | (k[i++] & 0xFF) << 8 | (k[i++] & 0xFF);
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger q = new BigInteger(1, buffer);

        // p
        l = k[i++] << 24 | (k[i++] & 0xFF) << 16 | (k[i++] & 0xFF) << 8 | (k[i++] & 0xFF);
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger p = new BigInteger(1, buffer);

        // g
        l = k[i++] << 24 | (k[i++] & 0xFF) << 16 | (k[i++] & 0xFF) << 8 | (k[i++] & 0xFF);
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger g = new BigInteger(1, buffer);

        // y
        l = k[i++] << 24 | (k[i++] & 0xFF) << 16 | (k[i++] & 0xFF) << 8 | (k[i++] & 0xFF);
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger y = new BigInteger(1, buffer);

        return new GnuDHPublicKey(q, p, g, y);
    }

    /**
     * <p>Returns the encoded form of the designated Diffie-Hellman private key
     * according to the <i>Raw</i> format supported by this library.</p>
     * <p/>
     * <p>The <i>Raw</i> format for a DH private key, in this implementation, is
     * a byte sequence consisting of the following:</p>
     * <p/>
     * <ol>
     * <li>4-byte magic consisting of the value of the literal
     * {@link Registry#MAGIC_RAW_DH_PRIVATE_KEY},<li>
     * <li>1-byte version consisting of the constant: 0x01,</li>
     * <li>4-byte count of following bytes representing the DH parameter
     * <code>q</code>,</li>
     * <li>n-bytes representation of a {@link BigInteger} obtained by invoking
     * the <code>toByteArray()</code> method on the DH parameter <code>q</code>,</li>
     * <li>4-byte count of following bytes representing the DH parameter
     * <code>p</code> in internet order,</li>
     * <li>n-bytes representation of a {@link BigInteger} obtained by invoking
     * the <code>toByteArray()</code> method on the DH parameter <code>p</code>,</li>
     * <li>4-byte count of following bytes representing the DH parameter
     * <code>g</code>,</li>
     * <li>n-bytes representation of a {@link BigInteger} obtained by invoking
     * the <code>toByteArray()</code> method on the DH parameter <code>g</code>,</li>
     * <li>4-byte count of following bytes representing the DH parameter
     * <code>x</code>,</li>
     * <li>n-bytes representation of a {@link BigInteger} obtained by invoking
     * the <code>toByteArray()</code> method on the DH parameter <code>x</code>,</li>
     * </ol>
     *
     * @param key the key to encode.
     * @return the <i>Raw</i> format encoding of the designated key.
     * @throws IllegalArgumentException if the designated key is not a DH one.
     * @see Registry#MAGIC_RAW_DH_PRIVATE_KEY
     */
    public byte[] encodePrivateKey(PrivateKey key) {
        if (!(key instanceof GnuDHPrivateKey)) {
            throw new IllegalArgumentException("key");
        }

        GnuDHPrivateKey dhKey = (GnuDHPrivateKey) key;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // magic
        baos.write(Registry.MAGIC_RAW_DH_PRIVATE_KEY[0]);
        baos.write(Registry.MAGIC_RAW_DH_PRIVATE_KEY[1]);
        baos.write(Registry.MAGIC_RAW_DH_PRIVATE_KEY[2]);
        baos.write(Registry.MAGIC_RAW_DH_PRIVATE_KEY[3]);

        // version
        baos.write(0x01);

        // q
        byte[] buffer = dhKey.getQ().toByteArray();
        int length = buffer.length;
        baos.write(length >>> 24);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        baos.write(buffer, 0, length);

        // p
        buffer = dhKey.getParams().getP().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        baos.write(buffer, 0, length);

        // g
        buffer = dhKey.getParams().getG().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        baos.write(buffer, 0, length);

        // x
        buffer = dhKey.getX().toByteArray();
        length = buffer.length;
        baos.write(length >>> 24);
        baos.write((length >>> 16) & 0xFF);
        baos.write((length >>> 8) & 0xFF);
        baos.write(length & 0xFF);
        baos.write(buffer, 0, length);

        return baos.toByteArray();
    }

    public PrivateKey decodePrivateKey(byte[] k) {
        // magic
        if (k[0] != Registry.MAGIC_RAW_DH_PRIVATE_KEY[0]
                || k[1] != Registry.MAGIC_RAW_DH_PRIVATE_KEY[1]
                || k[2] != Registry.MAGIC_RAW_DH_PRIVATE_KEY[2]
                || k[3] != Registry.MAGIC_RAW_DH_PRIVATE_KEY[3]) {
            throw new IllegalArgumentException("magic");
        }

        // version
        if (k[4] != 0x01) {
            throw new IllegalArgumentException("version");
        }
        int i = 5;
        int l;
        byte[] buffer;

        // q
        l = k[i++] << 24 | (k[i++] & 0xFF) << 16 | (k[i++] & 0xFF) << 8 | (k[i++] & 0xFF);
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger q = new BigInteger(1, buffer);

        // p
        l = k[i++] << 24 | (k[i++] & 0xFF) << 16 | (k[i++] & 0xFF) << 8 | (k[i++] & 0xFF);
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger p = new BigInteger(1, buffer);

        // g
        l = k[i++] << 24 | (k[i++] & 0xFF) << 16 | (k[i++] & 0xFF) << 8 | (k[i++] & 0xFF);
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger g = new BigInteger(1, buffer);

        // x
        l = k[i++] << 24 | (k[i++] & 0xFF) << 16 | (k[i++] & 0xFF) << 8 | (k[i++] & 0xFF);
        buffer = new byte[l];
        System.arraycopy(k, i, buffer, 0, l);
        i += l;
        BigInteger x = new BigInteger(1, buffer);

        return new GnuDHPrivateKey(q, p, g, x);
    }
}
