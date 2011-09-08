package gnu.crypto.key.dh;

// ----------------------------------------------------------------------------
// $Id: ElGamalKeyAgreement.java,v 1.1 2003/09/26 23:50:48 raif Exp $
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
import gnu.crypto.key.BaseKeyAgreementParty;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.util.Util;

import java.math.BigInteger;

/**
 * <p>The ElGamal key agreement, also known as the half-certified Diffie-Hellman
 * key agreement, is described in the Handbook of Applied Cryptography [HAC] as
 * follows:</p>
 * <ul>
 * <li>A sends to B a single message allowing one-pass key agreement.</li>
 * <li>A obtains an authentic copy of B's public key (p, g, yb), where
 * yb = g**xb.</li>
 * <li>A chooses a random integer x, 1 &lt;= x &lt;= p-2, and sends B the
 * message g**x.  A computes the shared secret key K as yb**x.</li>
 * <li>B computes the same key K on receipt of the previous message as
 * (g**x)**xb.</li>
 * </ul>
 * <p/>
 * <p>RFC-2631 describes an <i>Ephemeral-Static Mode</i> of operations with
 * Diffie-Hellman keypairs as follows:</p>
 * <pre>
 * "In Ephemeral-Static mode, the recipient has a static (and certified)
 * key pair, but the sender generates a new key pair for each message
 * and sends it using the originatorKey production. If the sender's key
 * is freshly generated for each message, the shared secret ZZ will be
 * similarly different for each message and partyAInfo MAY be omitted,
 * since it serves merely to decouple multiple KEKs generated by the
 * same set of pairwise keys. If, however, the same ephemeral sender key
 * is used for multiple messages (e.g. it is cached as a performance
 * optimization) then a separate partyAInfo MUST be used for each
 * message. All implementations of this standard MUST implement
 * Ephemeral-Static mode."
 * </pre>
 * <p/>
 * <p>Reference:</p>
 * <ol>
 * <li><a href="http://www.ietf.org/rfc/rfc2631.txt">Diffie-Hellman Key
 * Agreement Method</a><br>
 * Eric Rescorla.</li>
 * <li><a href="http://www.cacr.math.uwaterloo.ca/hac">[HAC]</a>: Handbook of
 * Applied Cryptography.<br>
 * CRC Press, Inc. ISBN 0-8493-8523-7, 1997<br>
 * Menezes, A., van Oorschot, P. and S. Vanstone.</li>
 * </ol>
 *
 * @version $Revision: 1.1 $
 */
public abstract class ElGamalKeyAgreement extends BaseKeyAgreementParty {

    // Constants and variables
    // -------------------------------------------------------------------------

    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.elgamal.ka.prng";

    public static final String KA_ELGAMAL_RECIPIENT_PRIVATE_KEY =
            "gnu.crypto.elgamal.ka.recipient.private.key";
    public static final String KA_ELGAMAL_RECIPIENT_PUBLIC_KEY =
            "gnu.crypto.elgamal.ka.recipient.public.key";

    /**
     * The shared secret key.
     */
    protected BigInteger ZZ;

    // Constructor(s)
    // -------------------------------------------------------------------------

    protected ElGamalKeyAgreement() {
        super(Registry.ELGAMAL_KA);
    }

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    // implementation of common abstract methods in BaseKeyAGreementParty ------

    protected byte[] engineSharedSecret() throws KeyAgreementException {
        return Util.trim(ZZ);
    }

    protected void engineReset() {
        ZZ = null;
    }
}