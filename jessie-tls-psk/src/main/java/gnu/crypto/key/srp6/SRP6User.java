package gnu.crypto.key.srp6;

// ----------------------------------------------------------------------------
// $Id: SRP6User.java,v 1.2 2003/12/25 02:15:10 uid66198 Exp $
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

import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.sasl.srp.SRP;
import gnu.crypto.util.Util;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The implementation of the User in the SRP-6 protocol.</p>
 * <p/>
 * <p>Reference:</p>
 * <ol>
 * <li><a href="http://srp.stanford.edu/design.html">SRP Protocol Design</a><br>
 * Thomas J. Wu.</li>
 * </ol>
 *
 * @version $Revision: 1.2 $
 */
public class SRP6User extends SRP6KeyAgreement {

    // Constants and variables
    // -------------------------------------------------------------------------

    /**
     * The user's identity.
     */
    private String I;

    /**
     * The user's cleartext password.
     */
    private byte[] p;

    /**
     * The user's ephemeral key pair.
     */
    private KeyPair userKeyPair;

    // Constructor(s)
    // -------------------------------------------------------------------------

    // default 0-arguments constructor

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    // implementation of abstract methods in base class ------------------------

    protected void engineInit(final Map attributes) throws KeyAgreementException {
        rnd = (SecureRandom) attributes.get(SOURCE_OF_RANDOMNESS);
        N = (BigInteger) attributes.get(SHARED_MODULUS);
        if (N == null) {
            throw new KeyAgreementException("missing shared modulus");
        }
        g = (BigInteger) attributes.get(GENERATOR);
        if (g == null) {
            throw new KeyAgreementException("missing generator");
        }

        final String md = (String) attributes.get(HASH_FUNCTION);
        if (md == null || "".equals(md.trim())) {
            throw new KeyAgreementException("missing hash function");
        }
        srp = SRP.instance(md);

        I = (String) attributes.get(USER_IDENTITY);
        if (I == null) {
            throw new KeyAgreementException("missing user identity");
        }
        p = (byte[]) attributes.get(USER_PASSWORD);
        if (p == null) {
            throw new KeyAgreementException("missing user password");
        }
    }

    protected OutgoingMessage engineProcessMessage(final IncomingMessage in)
            throws KeyAgreementException {
        switch (step) {
            case 0:
                return sendIdentity(in);
            case 1:
                return computeSharedSecret(in);
            default:
                throw new IllegalStateException("unexpected state");
        }
    }

    protected void engineReset() {
        I = null;
        p = null;
        userKeyPair = null;
        super.engineReset();
    }

    // own methods -------------------------------------------------------------

    private OutgoingMessage sendIdentity(final IncomingMessage in)
            throws KeyAgreementException {
        // generate an ephemeral keypair
        final SRPKeyPairGenerator kpg = new SRPKeyPairGenerator();
        final Map attributes = new HashMap();
        if (rnd != null) {
            attributes.put(SRPKeyPairGenerator.SOURCE_OF_RANDOMNESS, rnd);
        }
        attributes.put(SRPKeyPairGenerator.SHARED_MODULUS, N);
        attributes.put(SRPKeyPairGenerator.GENERATOR, g);
        kpg.setup(attributes);
        userKeyPair = kpg.generate();

        final OutgoingMessage result = new OutgoingMessage();
        result.writeString(I);
        result.writeMPI(((SRPPublicKey) userKeyPair.getPublic()).getY());

        return result;
    }

    private OutgoingMessage computeSharedSecret(final IncomingMessage in)
            throws KeyAgreementException {
        final BigInteger s = in.readMPI();
        final BigInteger B = in.readMPI();

        final BigInteger A = ((SRPPublicKey) userKeyPair.getPublic()).getY();
        final BigInteger u = uValue(A, B); // u = H(A | B)

        final BigInteger x;
        try {
            x = new BigInteger(1, srp.computeX(Util.trim(s), I, p));
        } catch (Exception e) {
            throw new KeyAgreementException("computeSharedSecret()", e);
        }

        // compute S = (B - 3g^x) ^ (a + ux)
        final BigInteger a = ((SRPPrivateKey) userKeyPair.getPrivate()).getX();
        final BigInteger S = B.subtract(THREE.multiply(g.modPow(x, N)))
                .modPow(a.add(u.multiply(x)), N);

        final byte[] sBytes = Util.trim(S);
        final IMessageDigest hash = srp.newDigest();
        hash.update(sBytes, 0, sBytes.length);
        K = new BigInteger(1, hash.digest());

        complete = true;
        return null;
    }
}
