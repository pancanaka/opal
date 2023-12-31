package gnu.crypto.jce.cipher;

// --------------------------------------------------------------------------
// $Id: ARCFourSpi.java,v 1.1 2002/12/12 05:27:52 rsdio Exp $
//
// Copyright (C) 2002 Free Software Foundation, Inc.
//
// This file is part of GNU Crypto.
//
// GNU Crypto is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2 of the License, or (at
// your option) any later version.
//
// GNU Crypto is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the
//
//    Free Software Foundation, Inc.,
//    59 Temple Place, Suite 330,
//    Boston, MA  02111-1307
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
// --------------------------------------------------------------------------

import gnu.crypto.Registry;
import gnu.crypto.prng.ARCFour;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.PRNGFactory;

import javax.crypto.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;

/**
 * The <i>Service Provider Interface</i> (<b>SPI</b>) for the ARCFOUR
 * stream cipher.
 *
 * @version $Revision: 1.1 $
 */
public class ARCFourSpi extends CipherSpi {

    // Constants and variables.
    // -----------------------------------------------------------------------

    private IRandom keystream;

    // Constructors.
    // -----------------------------------------------------------------------

    public ARCFourSpi() {
        super();
        keystream = PRNGFactory.getInstance(Registry.ARCFOUR_PRNG);
    }

    // Methods implementing CipherSpi.
    // -----------------------------------------------------------------------

    protected int engineGetBlockSize() {
        return 0; // stream cipher.
    }

    protected void engineSetMode(String s) throws NoSuchAlgorithmException {
        // ignored.
    }

    protected void engineSetPadding(String s) throws NoSuchPaddingException {
        // ignored.
    }

    protected byte[] engineGetIV() {
        return null;
    }

    protected int engineGetOutputSize(int in) {
        return in;
    }

    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    protected void engineInit(int mode, Key key, SecureRandom r)
            throws InvalidKeyException {
        if (mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE) {
            throw new IllegalArgumentException("arcfour is for encryption or decryption only");
        }
        if (key == null || !key.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("key must be non-null raw bytes");
        }
        HashMap attrib = new HashMap();
        attrib.put(ARCFour.ARCFOUR_KEY_MATERIAL, key.getEncoded());
        keystream.init(attrib);
    }

    protected void engineInit(int mode, Key key,
                              AlgorithmParameterSpec p, SecureRandom r)
            throws InvalidKeyException, InvalidAlgorithmParameterException {
        engineInit(mode, key, r);
    }

    protected void engineInit(int mode, Key key,
                              AlgorithmParameters p, SecureRandom r)
            throws InvalidKeyException, InvalidAlgorithmParameterException {
        engineInit(mode, key, r);
    }

    protected byte[] engineUpdate(byte[] in, int offset, int length) {
        if (length < 0 || offset < 0 || length + offset > in.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        byte[] result = new byte[length];
        try {
            for (int i = 0; i < length; i++) {
                result[i] = (byte) (in[i + offset] ^ keystream.nextByte());
            }
        } catch (LimitReachedException wontHappen) {
        }
        return result;
    }

    protected int engineUpdate(byte[] in, int inOffset, int length,
                               byte[] out, int outOffset) throws ShortBufferException {
        if (length < 0 || inOffset < 0 || length + inOffset > in.length
                || outOffset < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (outOffset + length > out.length) {
            throw new ShortBufferException();
        }
        try {
            for (int i = 0; i < length; i++) {
                out[i + outOffset] = (byte) (in[i + inOffset] ^ keystream.nextByte());
            }
        } catch (LimitReachedException wontHappen) {
        }
        return length;
    }

    protected byte[] engineDoFinal(byte[] in, int offset, int length)
            throws IllegalBlockSizeException, BadPaddingException {
        return engineUpdate(in, offset, length);
    }

    protected int engineDoFinal(byte[] in, int inOffset, int length,
                                byte[] out, int outOffset) throws ShortBufferException,
            IllegalBlockSizeException, BadPaddingException {
        return engineUpdate(in, inOffset, length, out, outOffset);
    }
}
