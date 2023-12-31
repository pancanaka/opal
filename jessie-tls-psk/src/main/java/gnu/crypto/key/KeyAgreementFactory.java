package gnu.crypto.key;

// ----------------------------------------------------------------------------
// $Id: KeyAgreementFactory.java,v 1.2 2003/10/25 07:08:43 raif Exp $
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
import gnu.crypto.key.dh.DiffieHellmanReceiver;
import gnu.crypto.key.dh.DiffieHellmanSender;
import gnu.crypto.key.dh.ElGamalReceiver;
import gnu.crypto.key.dh.ElGamalSender;
import gnu.crypto.key.srp6.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>A <i>Factory</i> class to generate key agreement protocol handlers.</p>
 *
 * @version $Revision: 1.2 $
 */
public class KeyAgreementFactory {

    // Constants and variables
    // -------------------------------------------------------------------------

    // Constructor(s)
    // -------------------------------------------------------------------------

    /**
     * Trivial constructor to enforce <i>Singleton</i> pattern.
     */
    private KeyAgreementFactory() {
        super();
    }

    // Class methods
    // -------------------------------------------------------------------------

    /**
     * <p>Returns an instance of a key agreeent protocol handler, for party
     * <code>A</code> in a two-party <code>A..B</code> exchange, given the
     * canonical name of this protocol. Party <code>A</code> is usually the
     * initiator of the exchange.</p>
     *
     * @param name the case-insensitive key agreement protocol name.
     * @return an instance of the key agreement protocol handler for party
     *         <code>A</code>, or <code>null</code> if none found.
     */
    public static IKeyAgreementParty getPartyAInstance(String name) {
        if (name == null) {
            return null;
        }

        name = name.trim();
        IKeyAgreementParty result = null;
        if (name.equalsIgnoreCase(Registry.DH_KA)) {
            result = new DiffieHellmanSender();
        } else if (name.equalsIgnoreCase(Registry.ELGAMAL_KA)) {
            result = new ElGamalSender();
        } else if (name.equalsIgnoreCase(Registry.SRP6_KA)) {
            result = new SRP6User();
        } else if (name.equalsIgnoreCase(Registry.SRP_SASL_KA)) {
            result = new SRP6SaslClient();
        } else if (name.equalsIgnoreCase(Registry.SRP_TLS_KA)) {
            result = new SRP6TLSClient();
        }

        return result;
    }

    /**
     * <p>Returns an instance of a key agreeent protocol handler, for party
     * <code>B</code> in a two-party <code>A..B</code> exchange, given the
     * canonical name of this protocol.</p>
     *
     * @param name the case-insensitive key agreement protocol name.
     * @return an instance of the key agreement protocol handler for party
     *         <code>B</code>, or <code>null</code> if none found.
     */
    public static IKeyAgreementParty getPartyBInstance(String name) {
        if (name == null) {
            return null;
        }

        name = name.trim();
        IKeyAgreementParty result = null;
        if (name.equalsIgnoreCase(Registry.DH_KA)) {
            result = new DiffieHellmanReceiver();
        } else if (name.equalsIgnoreCase(Registry.ELGAMAL_KA)) {
            result = new ElGamalReceiver();
        } else if (name.equalsIgnoreCase(Registry.SRP6_KA)) {
            result = new SRP6Host();
        } else if (name.equalsIgnoreCase(Registry.SRP_SASL_KA)) {
            result = new SRP6SaslServer();
        } else if (name.equalsIgnoreCase(Registry.SRP_TLS_KA)) {
            result = new SRP6TLSServer();
        }

        return result;
    }

    /**
     * <p>Returns a {@link Set} of key agreement protocol names supported by this
     * <i>Factory</i>.</p>
     *
     * @return a {@link Set} of key agreement protocol names (Strings).
     */
    public static final Set getNames() {
        HashSet hs = new HashSet();
        hs.add(Registry.DH_KA);
        hs.add(Registry.ELGAMAL_KA);
        hs.add(Registry.SRP6_KA);
        hs.add(Registry.SRP_SASL_KA);
        hs.add(Registry.SRP_TLS_KA);

        return Collections.unmodifiableSet(hs);
    }
}
