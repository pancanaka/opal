package gnu.crypto.sasl.anonymous;

// ----------------------------------------------------------------------------
// $Id: AnonymousUtil.java,v 1.1 2003/05/10 18:53:57 raif Exp $
//
// Copyright (C) 2003, Free Software Foundation, Inc.
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

import gnu.crypto.sasl.SaslUtil;

/**
 * An ANONYMOUS-specific utility class.
 *
 * @version $Revision: 1.1 $
 */
public class AnonymousUtil {

    // Constants and variables
    // -------------------------------------------------------------------------

    // Constructor(s)
    // -------------------------------------------------------------------------

    /**
     * Trivial private constructor to enforce Singleton pattern.
     */
    private AnonymousUtil() {
        super();
    }

    // Class methods
    // -------------------------------------------------------------------------

    static boolean isValidTraceInformation(String traceInformation) {
        if (traceInformation == null) {
            return false;
        }
        if (traceInformation.length() == 0) {
            return true;
        }
        if (SaslUtil.validEmailAddress(traceInformation)) {
            return true;
        }
        return isValidToken(traceInformation);
    }

    static boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        if (token.length() == 0) {
            return false;
        }
        if (token.length() > 255) {
            return false;
        }
        if (token.indexOf('@') != -1) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c < 0x20 || c > 0x7E) {
                return false;
            }
        }
        return true;
    }
}
