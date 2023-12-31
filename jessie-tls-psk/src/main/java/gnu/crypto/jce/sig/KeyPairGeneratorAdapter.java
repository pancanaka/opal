package gnu.crypto.jce.sig;

// ----------------------------------------------------------------------------
// $Id: KeyPairGeneratorAdapter.java,v 1.3 2003/09/26 23:45:34 raif Exp $
//
// Copyright (C) 2001, 2002, 2003 Free Software Foundation, Inc.
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

import gnu.crypto.key.IKeyPairGenerator;
import gnu.crypto.key.KeyPairGeneratorFactory;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * The implementation of a generic {@link java.security.KeyPairGenerator}
 * adapter class to wrap gnu.crypto keypair generator instances.<p>
 * <p/>
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>) for the
 * {@link java.security.KeyPairGenerator} class, which is used to generate pairs
 * of public and private keys.<p>
 * <p/>
 * All the abstract methods in the {@link java.security.KeyPairGeneratorSpi}
 * class are implemented by this class and all its sub-classes.<p>
 * <p/>
 * In case the client does not explicitly initialize the KeyPairGenerator (via
 * a call to an <code>initialize()</code> method), the GNU Crypto provider
 * supplies (and document) default values to be used. For example, the GNU
 * Crypto provider uses a default <i>modulus</i> size (keysize) of 1024 bits for
 * the DSS (Digital Signature Standard) a.k.a <i>DSA</i>.<p>
 *
 * @version $Revision: 1.3 $
 */
abstract class KeyPairGeneratorAdapter extends KeyPairGeneratorSpi {

    // Constants and variables
    // -------------------------------------------------------------------------

    /**
     * Our underlying keypair instance.
     */
    protected IKeyPairGenerator adaptee;

    // Constructor(s)
    // -------------------------------------------------------------------------

    /**
     * Trivial protected constructor.
     *
     * @param kpgName the canonical name of the keypair generator algorithm.
     */
    protected KeyPairGeneratorAdapter(String kpgName) {
        super();

        this.adaptee = KeyPairGeneratorFactory.getInstance(kpgName);
    }

    // Class methods
    // -------------------------------------------------------------------------

    // java.security.KeyPairGeneratorSpi interface implementation
    // -------------------------------------------------------------------------

    public abstract void initialize(int keysize, SecureRandom random);

    public abstract void
    initialize(AlgorithmParameterSpec params, SecureRandom random)
            throws InvalidAlgorithmParameterException;

    public KeyPair generateKeyPair() {
        return adaptee.generate();
    }
}
