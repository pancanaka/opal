package gnu.crypto.key.dh;

// ----------------------------------------------------------------------------
// $Id: DiffieHellmanReceiver.java,v 1.2 2004/01/07 23:03:28 rsdio Exp $
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

import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.prng.IRandom;

import javax.crypto.interfaces.DHPrivateKey;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

/**
 * <p>This implementation is the receiver's part of the basic version of the
 * Diffie-Hellman key agreement exchange (B in [HAC]).</p>
 *
 * @version $Revision: 1.2 $
 * @see DiffieHellmanKeyAgreement
 */
public class DiffieHellmanReceiver extends DiffieHellmanKeyAgreement {

    // Constants and variables
    // -------------------------------------------------------------------------

    private BigInteger y; // the receiver's random secret

    // Constructor(s)
    // -------------------------------------------------------------------------

    // default 0-arguments constructor

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    // implementation of abstract methods in base class ------------------------

    protected void engineInit(Map attributes) throws KeyAgreementException {
        Object random = attributes.get(SOURCE_OF_RANDOMNESS);
        rnd = null;
        irnd = null;
        if (random instanceof SecureRandom) {
            rnd = (SecureRandom) random;
        } else if (random instanceof IRandom) {
            irnd = (IRandom) random;
        }
        ownerKey = (DHPrivateKey) attributes.get(KA_DIFFIE_HELLMAN_OWNER_PRIVATE_KEY);
        if (ownerKey == null) {
            throw new KeyAgreementException("missing owner's private key");
        }
    }

    protected OutgoingMessage engineProcessMessage(IncomingMessage in)
            throws KeyAgreementException {
        switch (step) {
            case 0:
                return computeSharedSecret(in);
            default:
                throw new IllegalStateException("unexpected state");
        }
    }

    // own methods -------------------------------------------------------------

    private OutgoingMessage computeSharedSecret(IncomingMessage in)
            throws KeyAgreementException {
        BigInteger m1 = in.readMPI();
        if (m1 == null) {
            throw new KeyAgreementException("missing message (1)");
        }

        BigInteger p = ownerKey.getParams().getP();
        BigInteger g = ownerKey.getParams().getG();

        // B chooses a random integer y, 1 <= y <= p-2
        // rfc-2631 restricts y to only be in [2, p-1]
        BigInteger p_minus_2 = p.subtract(TWO);
        byte[] xBytes = new byte[(p_minus_2.bitLength() + 7) / 8];
        do {
            nextRandomBytes(xBytes);
            y = new BigInteger(1, xBytes);
        } while (!(y.compareTo(TWO) >= 0 && y.compareTo(p_minus_2) <= 0));

        ZZ = m1.modPow(y, p); // ZZ = (yb ^ xa) mod p

        complete = true;

        // B sends A the message: g^y mod p
        OutgoingMessage result = new OutgoingMessage();
        result.writeMPI(g.modPow(y, p)); // message (2)

        return result;
    }
}
