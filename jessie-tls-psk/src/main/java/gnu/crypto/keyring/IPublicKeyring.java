package gnu.crypto.keyring;  // -*- c-basic-offset: 3 -*-

// ---------------------------------------------------------------------------
// $Id: IPublicKeyring.java,v 1.2 2003/10/21 20:03:21 raif Exp $
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
//
// ---------------------------------------------------------------------------

import java.security.cert.Certificate;

/**
 * <p>An interface for keyrings that contain trusted (by the owner) public
 * credentials (incl. certificates).</p>
 *
 * @version $Revision: 1.2 $
 * @see IKeyring
 */
public interface IPublicKeyring extends IKeyring {

    /**
     * <p>Tests if this keyring contains a certificate entry with the specified
     * <code>alias</code>.</p>
     *
     * @param alias The alias of the certificate to check.
     * @return <code>true</code> if this keyring contains a certificate entry
     *         that has the given <code>alias</code>; <code>false</code> otherwise.
     */
    boolean containsCertificate(String alias);

    /**
     * <p>Returns a certificate that has the given <code>alias</code>, or
     * <code>null</code> if this keyring has no such entry.</p>
     *
     * @param alias The alias of the certificate to find.
     * @return The certificate with the designated <code>alias</code>, or
     *         <code>null</code> if none found.
     */
    Certificate getCertificate(String alias);

    /**
     * <p>Adds a certificate in this keyring, with the given <code>alias</code>.</p>
     * <p/>
     * <p>What happens if there is already a certificate entry with this alias?</p>
     *
     * @param alias The alias of this certificate entry.
     * @param cert  The certificate.
     */
    void putCertificate(String alias, Certificate cert);
}
