package gnu.crypto.key.srp6;

// ----------------------------------------------------------------------------
// $Id: SRPKey.java,v 1.1 2003/09/26 23:50:48 raif Exp $
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

import java.io.Serializable;
import java.math.BigInteger;
import java.security.Key;

/**
 * <p>An abstract representation of a base SRP ephemeral key.</p>
 * <p/>
 * <p>This object encapsulates the two numbers:</p>
 * <ul>
 * <li><b>N</b>: A large safe prime (N = 2q+1, where q is prime).</li>
 * <li><b>g</b>: A generator modulo N.</li>
 * </ul>
 * <p/>
 * <p>Note that in SRP, all arithmetic is done modulo N.</p>
 * <p/>
 * <p>Reference:</p>
 * <ol>
 * <li><a href="http://srp.stanford.edu/design.html">SRP Protocol Design</a><br>
 * Thomas J. Wu.</li>
 * </ol>
 *
 * @version $Revision: 1.1 $
 */
public abstract class SRPKey implements Key, Serializable {

    // Constants and variables
    // -------------------------------------------------------------------------

    /**
     * The public, Germaine prime, shared modulus.
     */
    protected final BigInteger N;

    /**
     * The generator.
     */
    protected final BigInteger g;

    // Constructor(s)
    // -------------------------------------------------------------------------

    protected SRPKey(BigInteger N, BigInteger g) {
        super();

        this.N = N;
        this.g = g;
    }

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    // java.security.Key interface implementation ------------------------------

    /**
     * <p>Returns the standard algorithm name for this key.</p>
     *
     * @return the standard algorithm name for this key.
     */
    public String getAlgorithm() {
        return Registry.SRP_KPG;
    }

    /**
     * @deprecated see getEncoded(int).
     */
    public byte[] getEncoded() {
        return getEncoded(IKeyPairCodec.RAW_FORMAT);
    }

    /**
     * <p>Returns <code>null</code> since this implementation does not encode SRP
     * keys.</p>
     *
     * @return null since this implementation does not encode SRP keys.
     */
    public String getFormat() {
        return null;
    }

    // other methods -----------------------------------------------------------

    /**
     * <p>Returns the public shared modulus.</p>
     *
     * @return <code>N</code>.
     */
    public BigInteger getN() {
        return N;
    }

    /**
     * <p>Returns the generator.</p>
     *
     * @return <code>g</code>.
     */
    public BigInteger getG() {
        return g;
    }

    /**
     * <p>Returns <code>true</code> if the designated object is an instance of
     * <code>SRPKey</code> and has the same SRP parameter values as this one.</p>
     *
     * @param obj the other non-null SRP key to compare to.
     * @return <code>true</code> if the designated object is of the same type and
     *         value as this one.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SRPKey)) {
            return false;
        }
        SRPKey that = (SRPKey) obj;
        return N.equals(that.getN()) && g.equals(that.getG());
    }

    // abstract methods to be implemented by subclasses ------------------------

    public abstract byte[] getEncoded(int format);
}
