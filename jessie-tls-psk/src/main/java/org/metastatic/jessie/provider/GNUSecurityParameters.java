/* GNUSecurityParameters.java -- SSL security parameters.
   Copyright (C) 2003,2004  Casey Marshall <csm@gnu.org>

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

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;
import gnu.crypto.mac.IMac;
import gnu.crypto.mode.IMode;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;

import javax.net.ssl.SSLException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * This class implements the {@link SecurityParameters} interface, using the GNU
 * Crypto interface for ciphers and macs, and the JZlib package for record
 * compression.
 */
class GNUSecurityParameters implements SecurityParameters {

    // Fields.
    // -------------------------------------------------------------------------

    private static final boolean DEBUG_RECORD_LAYER = false;
    private static final PrintWriter debug = new PrintWriter(System.err, true);

    /**
     * The CBC block cipher, if any.
     */
    IMode inCipher, outCipher;

    /**
     * The RC4 PRNG, if any.
     */
    IRandom inRandom, outRandom;

    /**
     * The MAC algorithm.
     */
    IMac inMac, outMac;

    ZStream deflater;
    ZStream inflater;

    long inSequence, outSequence;
    IRandom random;
    ProtocolVersion version;
    int fragmentLength;

    // Constructors.
    // -------------------------------------------------------------------------

    GNUSecurityParameters() {
        inSequence = 0;
        outSequence = 0;
        random = CSPRNG.SYSTEM_RANDOM;
        fragmentLength = 16384;
    }

    // Instance methods.
    // -------------------------------------------------------------------------

    public void reset() {
        inSequence = 0L;
        outSequence = 0L;
        inCipher = null;
        outCipher = null;
        inMac = null;
        outMac = null;
        inRandom = null;
        outRandom = null;
        deflater = null;
        inflater = null;
    }

    public ProtocolVersion getVersion() {
        return version;
    }

    public void setVersion(ProtocolVersion version) {
        this.version = version;
    }

    public void setInCipher(Object inCipher) {
        if (inCipher instanceof IMode) {
            this.inCipher = (IMode) inCipher;
            inRandom = null;
        } else {
            inRandom = (IRandom) inCipher;
            this.inCipher = null;
        }
    }

    public void setOutCipher(Object outCipher) {
        if (outCipher instanceof IMode) {
            this.outCipher = (IMode) outCipher;
            outRandom = null;
        } else {
            outRandom = (IRandom) outCipher;
            this.outCipher = null;
        }
    }

    public void setInMac(Object inMac) {
        this.inMac = (IMac) inMac;
        inSequence = 0L;
    }

    public void setOutMac(Object outMac) {
        this.outMac = (IMac) outMac;
        outSequence = 0L;
    }

    public void setDeflating(boolean deflating) {
        if (deflating) {
            deflater = new ZStream();
            int level = JZlib.Z_DEFAULT_COMPRESSION;
            try {
                level = Integer.parseInt(Util
                        .getSecurityProperty("jessie.compression.level"));
                if ((level < JZlib.Z_NO_COMPRESSION || level > JZlib.Z_BEST_COMPRESSION)
                        && level != JZlib.Z_DEFAULT_COMPRESSION) {
                    level = JZlib.Z_DEFAULT_COMPRESSION;
                }
            } catch (Exception x) {
                level = JZlib.Z_DEFAULT_COMPRESSION;
            }
            deflater.deflateInit(level);
        } else
            deflater = null;
    }

    public void setInflating(boolean inflating) {
        if (inflating) {
            inflater = new ZStream();
            inflater.inflateInit();
        } else
            inflater = null;
    }

    public int getFragmentLength() {
        return fragmentLength;
    }

    public void setFragmentLength(int fragmentLength) {
        this.fragmentLength = fragmentLength;
    }

    /**
     * Decrypt, verify, and decompress a fragment, returning the transformed
     * fragment.
     *
     * @param fragment The fragment to decrypt.
     * @param version  The protocol version of the fragment's record.
     * @param type     The content type of the record.
     * @return The decrypted fragment.
     * @throws MacException      If the MAC could not be verified.
     * @throws OverflowException If the inflated data is too large.
     * @throws SSLException      If decompressing fails.
     */
    public synchronized byte[] decrypt(byte[] fragment,
                                       ProtocolVersion version, ContentType type) throws MacException,
            OverflowException, SSLException {
        boolean badPadding = false;

        // Decrypt the ciphertext, if it is encrypted.
        if (inCipher != null) {
            int bs = inCipher.currentBlockSize();
            for (int i = 0; i < fragment.length; i += bs) {
                inCipher.update(fragment, i, fragment, i);
            }
            int padLen = fragment[fragment.length - 1] & 0xFF;
            int len = fragment.length - padLen - 1;
            if (version == ProtocolVersion.SSL_3) {
                // SSLv3 requires that the padding length not exceed the
                // cipher's block size.
                if (padLen >= bs) {
                    badPadding = true;
                }
            } else {
                for (int i = len; i < fragment.length; i++) {
                    // If the TLS padding is wrong, throw a MAC exception below.
                    if ((fragment[i] & 0xFF) != padLen) {
                        badPadding = true;
                    }
                }
            }
            fragment = Util.trim(fragment, len);
        } else if (inRandom != null) {
            transformRC4(fragment, 0, fragment.length, fragment, 0, inRandom);
        }

        // Check the MAC.
        if (inMac != null) {
            inMac.update((byte) (inSequence >>> 56));
            inMac.update((byte) (inSequence >>> 48));
            inMac.update((byte) (inSequence >>> 40));
            inMac.update((byte) (inSequence >>> 32));
            inMac.update((byte) (inSequence >>> 24));
            inMac.update((byte) (inSequence >>> 16));
            inMac.update((byte) (inSequence >>> 8));
            inMac.update((byte) inSequence);
            inMac.update((byte) type.getValue());
            if (version != ProtocolVersion.SSL_3) {
                inMac.update((byte) version.getMajor());
                inMac.update((byte) version.getMinor());
            }
            int macLen = inMac.macSize();
            int fragLen = fragment.length - macLen;
            inMac.update((byte) (fragLen >>> 8));
            inMac.update((byte) fragLen);
            inMac.update(fragment, 0, fragLen);
            byte[] mac = inMac.digest();
            inMac.reset();
            for (int i = 0; i < macLen; i++) {
                if (fragment[i + fragLen] != mac[i]) {
                    throw new MacException();
                }
            }
            if (badPadding) {
                throw new MacException();
            }
            fragment = Util.trim(fragment, fragLen);
        }

        // Decompress the fragment.
        if (inflater != null) {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bout = new ByteArrayOutputStream(
                    fragment.length);
            inflater.next_in = fragment;
            inflater.next_in_index = 0;
            inflater.avail_in = fragment.length;
            int err = 0;
            do {
                inflater.next_out = buffer;
                inflater.next_out_index = 0;
                inflater.avail_out = buffer.length;

                err = inflater.inflate(JZlib.Z_SYNC_FLUSH);

                bout.write(buffer, 0, buffer.length - inflater.avail_out);
                if (bout.size() > fragmentLength) {
                    throw new OverflowException();
                }
            } while ((err == JZlib.Z_BUF_ERROR && inflater.avail_out == 0)
                    || (err == JZlib.Z_OK && inflater.avail_in != 0));

            if (err != JZlib.Z_OK) {
                throw new SSLException("decompression failed");
            }

            fragment = bout.toByteArray();
        }

        inSequence++;
        return fragment;
    }

    /**
     * Compress, MAC, encrypt, and write a record. The fragment of the record is
     * taken from <i>buf</i> as <i>len</i> bytes starting at <i>offset</i>.
     * <i>len</i> <b>must</b> be smaller than or equal to the configured
     * fragment length.
     *
     * @param buf  The fragment bytes.
     * @param off  The offset from whence to read.
     * @param len  The size of the fragment.
     * @param type The content-type for this record.
     * @param out  The output stream to write the record to.
     * @throws IOException       If an I/O error occurs.
     * @throws SSLException      If compression fails.
     * @throws OverflowException If compression inflates the data beyond the fragment length
     *                           plus 1024 bytes.
     */
    public synchronized byte[] encrypt(byte[] buf, int off, int len,
                                       ContentType type) throws SSLException, OverflowException {
        // If we are compressing, do it.
        if (deflater != null) {
            byte[] buf2 = new byte[len + 1024];
            deflater.next_in = buf;
            deflater.next_in_index = off;
            deflater.avail_in = len;
            deflater.next_out = buf2;
            deflater.next_out_index = 0;
            deflater.avail_out = buf2.length;
            if (deflater.deflate(JZlib.Z_SYNC_FLUSH) != JZlib.Z_OK) {
                System.out.println(deflater.deflate(JZlib.Z_SYNC_FLUSH));
                throw new SSLException("compression failed");
            }
            if (deflater.avail_in > 0)
                throw new OverflowException("deflated data too large");
            buf = buf2;
            off = 0;
            len = buf2.length - deflater.avail_out;
        }

        // If there is a MAC, compute it.
        byte[] mac = new byte[0];
        if (outMac != null) {
            outMac.update((byte) (outSequence >>> 56));
            outMac.update((byte) (outSequence >>> 48));
            outMac.update((byte) (outSequence >>> 40));
            outMac.update((byte) (outSequence >>> 32));
            outMac.update((byte) (outSequence >>> 24));
            outMac.update((byte) (outSequence >>> 16));
            outMac.update((byte) (outSequence >>> 8));
            outMac.update((byte) outSequence);
            outMac.update((byte) type.getValue());
            if (version != ProtocolVersion.SSL_3) {
                outMac.update((byte) version.getMajor());
                outMac.update((byte) version.getMinor());
            }
            outMac.update((byte) (len >>> 8));
            outMac.update((byte) len);
            outMac.update(buf, off, len);
            mac = outMac.digest();
            outMac.reset();
        }
        outSequence++;

        // Compute padding if needed.
        byte[] pad = new byte[0];
        if (outCipher != null) {
            int padLen = outCipher.currentBlockSize()
                    - ((len + mac.length + 1) % outCipher.currentBlockSize());
            // Use a random amount of padding if the protocol is TLS.
            if (version != ProtocolVersion.SSL_3) {
                try {
                    padLen += (Math.abs(random.nextByte()) & 7)
                            * outCipher.currentBlockSize();
                } catch (LimitReachedException lre) {
                    throw new Error(lre.toString());
                }
                while (padLen > 255) {
                    padLen -= outCipher.currentBlockSize();
                }
            }
            pad = new byte[padLen + 1];
            Arrays.fill(pad, (byte) padLen);
        }

        // Write the record header.
        final int fraglen = len + mac.length + pad.length;

        // Encrypt and write the fragment.
        if (outCipher != null) {
            byte[] buf2 = new byte[fraglen];
            System.arraycopy(buf, off, buf2, 0, len);
            System.arraycopy(mac, 0, buf2, len, mac.length);
            System.arraycopy(pad, 0, buf2, len + mac.length, pad.length);
            int bs = outCipher.currentBlockSize();
            for (int i = 0; i < fraglen; i += bs) {
                outCipher.update(buf2, i, buf2, i);
            }
            return buf2;
        } else if (outRandom != null) {
            byte[] buf2 = new byte[fraglen];
            transformRC4(buf, off, len, buf2, 0, outRandom);
            transformRC4(mac, 0, mac.length, buf2, len, outRandom);
            return buf2;
        } else {
            if (mac.length == 0) {
                return Util.trim(buf, off, len);
            } else {
                return Util.concat(Util.trim(buf, off, len), mac);
            }
        }
    }

    // Own methods.
    // -------------------------------------------------------------------------

    /**
     * Encrypt/decrypt a byte array with the RC4 stream cipher.
     *
     * @param in        The input data.
     * @param off       The input offset.
     * @param len       The number of bytes to transform.
     * @param out       The output buffer.
     * @param outOffset The offest into the output buffer.
     * @param random    The ARCFOUR PRNG.
     */
    private static void transformRC4(byte[] in, int off, int len, byte[] out,
                                     int outOffset, IRandom random) {
        if (random == null) {
            throw new IllegalStateException();
        }
        if (in == null || out == null) {
            throw new NullPointerException();
        }
        if (off < 0 || off + len > in.length || outOffset < 0
                || outOffset + len > out.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        try {
            for (int i = 0; i < len; i++) {
                out[outOffset + i] = (byte) (in[off + i] ^ random.nextByte());
            }
        } catch (LimitReachedException cannotHappen) {
            throw new Error(cannotHappen.toString());
        }
    }
}
