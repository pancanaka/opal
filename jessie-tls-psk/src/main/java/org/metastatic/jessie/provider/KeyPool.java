/* KeyPool.java -- A set of ephemeral key pairs.
   Copyright (C) 2003  Casey Marshall <rsdio@metastatic.org>
   Parts copyright (C) 2001,2002,2003  Free Software Foundation, Inc.

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

import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.util.Prime;

import java.math.BigInteger;
import java.security.KeyPair;

final class KeyPool {

    // Fields.
    // -------------------------------------------------------------------------

    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private static final BigInteger E = BigInteger.valueOf(65537L);

    // Constructor.
    // -------------------------------------------------------------------------

    private KeyPool() {
    }

    // Class methods.
    // -------------------------------------------------------------------------

    /**
     * Generate an export-class (512 bit) RSA key pair.
     *
     * @return The new key pair.
     */
    static KeyPair generateRSAKeyPair() {
        BigInteger p, q, n, d;

        // Simplified version of GNU Crypto's RSAKeyPairGenerator.

        int M = 256;
        BigInteger lower = TWO.pow(255);
        BigInteger upper = TWO.pow(256).subtract(ONE);
        byte[] kb = new byte[32];
        while (true) {
            nextBytes(kb);
            p = new BigInteger(1, kb).setBit(0);
            if (p.compareTo(lower) >= 0 && p.compareTo(upper) <= 0
                    && Prime.isProbablePrime(p) && p.gcd(E).equals(ONE))
                break;
        }

        while (true) {
            nextBytes(kb);
            q = new BigInteger(1, kb).setBit(0);
            n = q.multiply(p);
            if (n.bitLength() == 512 && Prime.isProbablePrime(q)
                    && q.gcd(E).equals(ONE))
                break;
        }

        d = E.modInverse(p.subtract(ONE).multiply(q.subtract(ONE)));

        return new KeyPair(new JessieRSAPublicKey(n, E),
                new JessieRSAPrivateKey(n, d));
    }

    private static void nextBytes(byte[] buf) {
        try {
            CSPRNG.SYSTEM_RANDOM.nextBytes(buf, 0, buf.length);
        } catch (LimitReachedException lre) {
            throw new Error(lre.toString());
        }
    }
}
