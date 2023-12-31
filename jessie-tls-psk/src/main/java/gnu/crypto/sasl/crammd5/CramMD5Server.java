package gnu.crypto.sasl.crammd5;

// ----------------------------------------------------------------------------
// $Id: CramMD5Server.java,v 1.3 2003/12/25 01:59:44 uid66198 Exp $
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
import gnu.crypto.sasl.NoSuchUserException;
import gnu.crypto.sasl.ServerMechanism;
import gnu.crypto.util.Util;

import javax.security.sasl.AuthenticationException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The CRAM-MD5 SASL server-side mechanism.</p>
 *
 * @version $Revision: 1.3 $
 */
public class CramMD5Server extends ServerMechanism implements SaslServer {

    // Constants and variables
    // -------------------------------------------------------------------------

    private byte[] msgID;

    // Constructor(s)
    // -------------------------------------------------------------------------

    public CramMD5Server() {
        super(Registry.SASL_CRAM_MD5_MECHANISM);
    }

    // Class methods
    // -------------------------------------------------------------------------

    // abstract methods implementation -----------------------------------------

    protected void initMechanism() throws SaslException {
    }

    protected void resetMechanism() throws SaslException {
    }

    // javax.security.sasl.SaslServer interface implementation -----------------

    public byte[] evaluateResponse(final byte[] response) throws SaslException {
        if (state == 0) {
            msgID = CramMD5Util.createMsgID();
            state++;
            return msgID;
        }

        final String responseStr = new String(response);
        final int index = responseStr.lastIndexOf(" ");
        final String username = responseStr.substring(0, index);
        final byte[] responseDigest;
        try {
            responseDigest = responseStr.substring(index + 1).getBytes("UTF-8");
        } catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("evaluateResponse()", x);
        }

        // Look up the password
        final char[] password = lookupPassword(username);

        // Compute the digest
        byte[] digest;
        try {
            digest = CramMD5Util.createHMac(password, msgID);
        } catch (InvalidKeyException x) {
            throw new AuthenticationException("evaluateResponse()", x);
        }
        try {
//         digest = (new String(Util.toString(digest).toLowerCase())).getBytes("UTF-8");
            digest = Util.toString(digest).toLowerCase().getBytes("UTF-8");
        } catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("evaluateResponse()", x);
        }

        // Compare the received and computed digests
        if (!Arrays.equals(digest, responseDigest)) {
            throw new AuthenticationException("Digest mismatch");
        }
        state++;
        return null;
    }

    public boolean isComplete() {
        return (state == 2);
    }

    protected String getNegotiatedQOP() {
        return Registry.QOP_AUTH;
    }

    // Other instance methods --------------------------------------------------

    private char[] lookupPassword(final String userName) throws SaslException {
        try {
            if (!authenticator.contains(userName)) {
                throw new NoSuchUserException(userName);
            }
            final Map userID = new HashMap();
            userID.put(Registry.SASL_USERNAME, userName);
            final Map credentials = authenticator.lookup(userID);
            final String password = (String) credentials.get(Registry.SASL_PASSWORD);
            if (password == null) {
                throw new AuthenticationException("lookupPassword()", new InternalError());
            }
            return password.toCharArray();
        } catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException) x;
            }
            throw new AuthenticationException("lookupPassword()", x);
        }
    }
}
