package gnu.crypto.key.dh;

// ----------------------------------------------------------------------------
// $Id: GnuDHKey.java,v 1.1 2003/09/26 23:50:48 raif Exp $
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

import javax.crypto.interfaces.DHKey;
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import java.security.Key;

/**
 * <p>A base asbtract class for both public and private Diffie-Hellman keys. It
 * encapsulates the two DH numbers: <code>p</code>, and <code>g</code>.</p>
 * <p/>
 * <p>According to the JDK, cryptographic <i>Keys</i> all have a <i>format</i>.
 * The format used in this implementation is called <i>Raw</i>, and basically
 * consists of the raw byte sequences of algorithm parameters. The exact order
 * of the byte sequences and the implementation details are given in each of
 * the relevant <code>getEncoded()</code> methods of each of the private and
 * public keys.</p>
 * <p/>
 * <p>Reference:</p>
 * <ol>
 * <li><a href="http://www.ietf.org/rfc/rfc2631.txt">Diffie-Hellman Key
 * Agreement Method</a><br>
 * Eric Rescorla.</li>
 * </ol>
 *
 * @version $Revision: 1.1 $
 */
public abstract class GnuDHKey implements Key, DHKey {

    // Constants and variables
    // -------------------------------------------------------------------------

    /**
     * The public prime q. A prime divisor of p-1.
     */
    protected BigInteger q;

    /**
     * The public prime p.
     */
    protected BigInteger p;

    /**
     * The generator g.
     */
    protected BigInteger g;

    // Constructor(s)
    // -------------------------------------------------------------------------

    /**
     * <p>Trivial protected constructor.</p>
     *
     * @param q a prime divisor of p-1.
     * @param p the public prime.
     * @param g the generator of the group.
     */
    protected GnuDHKey(BigInteger q, BigInteger p, BigInteger g) {
        super();

        this.q = q;
        this.p = p;
        this.g = g;
    }

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    // javax.crypto.interfaces.DHKey interface implementation ------------------

    public DHParameterSpec getParams() {
        if (q == null) {
            return new DHParameterSpec(p, g);
        } else {
            return new DHParameterSpec(p, g, q.bitLength());
        }
    }

    // java.security.Key interface implementation ------------------------------

    public String getAlgorithm() {
        return Registry.DH_KPG;
    }

    public String getFormat() {
        return null;
    }

    // Other instance methods --------------------------------------------------

    public BigInteger getQ() {
        return q;
    }

    /**
     * <p>Returns <code>true</code> if the designated object is an instance of
     * {@link DHKey} and has the same Diffie-Hellman parameter values as this
     * one.</p>
     *
     * @param obj the other non-null DH key to compare to.
     * @return <code>true</code> if the designated object is of the same type and
     *         value as this one.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DHKey)) {
            return false;
        }
        DHKey that = (DHKey) obj;
        return p.equals(that.getParams().getP()) && g.equals(that.getParams().getG());
    }
}
