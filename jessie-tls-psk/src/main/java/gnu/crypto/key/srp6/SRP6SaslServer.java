package gnu.crypto.key.srp6;

// ----------------------------------------------------------------------------
// $Id: SRP6SaslServer.java,v 1.3 2003/12/25 02:15:10 uid66198 Exp $
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
import gnu.crypto.util.Util;

import java.math.BigInteger;

/**
 * <p>A variation of the SRP-6 protocol as used in the SASL-SRP mechanism, for
 * the Host (server side).</p>
 * <p/>
 * <p>In this alternative, the exchange goes as follows:</p>
 * <pre>
 *    C -> S:  I                      (identifies self)
 *    S -> C:  N, g, s, B = 3v + g^b  (sends salt, b = random number)
 *    C -> S:  A = g^a                (a = random number)
 * </pre>
 * <p/>
 * <p>All elements are computed the same way as in the standard version.</p>
 * <p/>
 * <p>Reference:</p>
 * <ol>
 * <li><a href="http://www.ietf.org/internet-drafts/draft-burdis-cat-srp-sasl-09.txt">
 * Secure Remote Password Authentication Mechanism</a><br>
 * K. Burdis, R. Naffah.</li>
 * <li><a href="http://srp.stanford.edu/design.html">SRP Protocol Design</a><br>
 * Thomas J. Wu.</li>
 * </ol>
 *
 * @version $Revision: 1.3 $
 */
public class SRP6SaslServer extends SRP6TLSServer {

    // Constants and variables
    // -------------------------------------------------------------------------

    // Constructor(s)
    // -------------------------------------------------------------------------

    // default 0-arguments constructor

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    protected OutgoingMessage computeSharedSecret(final IncomingMessage in)
            throws KeyAgreementException {
        super.computeSharedSecret(in);

        final byte[] sBytes = Util.trim(K);
        final IMessageDigest hash = srp.newDigest();
        hash.update(sBytes, 0, sBytes.length);
        K = new BigInteger(1, hash.digest());

        return null;
    }
}
