package gnu.crypto.jce.spec;

// --------------------------------------------------------------------------
// $Id: UMac32ParameterSpec.java,v 1.1 2002/11/11 23:06:20 rsdio Exp $
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

import java.security.spec.AlgorithmParameterSpec;

/**
 * This class represents the parameters for the UMAC-32 message
 * authentication code algorithm. In practice this means the
 * <i>Nonce</i> material used to initialize the algorithm.
 *
 * @version $Revision: 1.1 $
 */
public class UMac32ParameterSpec implements AlgorithmParameterSpec {

    // Constants and variables.
    // -----------------------------------------------------------------------

    /**
     * The <i>Nonce</i> material.
     */
    protected byte[] nonce;

    // Constructors.
    // -----------------------------------------------------------------------

    /**
     * Create a new parameter instance.
     *
     * @param nonce The nonce material.
     */
    public UMac32ParameterSpec(byte[] nonce) {
        this.nonce = nonce;
    }

    // Instance methods.
    // -----------------------------------------------------------------------

    /**
     * Return the nonce material.
     *
     * @return The nonce material.
     */
    public byte[] getNonce() {
        return nonce;
    }
}
