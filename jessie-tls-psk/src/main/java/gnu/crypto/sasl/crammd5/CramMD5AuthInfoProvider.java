package gnu.crypto.sasl.crammd5;

// ----------------------------------------------------------------------------
// $Id: CramMD5AuthInfoProvider.java,v 1.1 2003/05/10 18:53:57 raif Exp $
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

import gnu.crypto.Registry;
import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.NoSuchUserException;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The CRAM-MD5 mechanism authentication information provider implementation.
 *
 * @version $Revision: 1.1 $
 */
public class CramMD5AuthInfoProvider implements IAuthInfoProvider {

    // Constants and variables
    // -------------------------------------------------------------------------

    private PasswordFile passwordFile = null;

    // Constructor(s)
    // -------------------------------------------------------------------------

    // implicit 0-args constrcutor

    // Class methods
    // -------------------------------------------------------------------------

    // IAuthInfoProvider interface implementation
    // -------------------------------------------------------------------------

    public void activate(Map context) throws AuthenticationException {
        try {
            if (context == null) {
                passwordFile = new PasswordFile();
            } else {
                String pfn = (String) context.get(CramMD5Registry.PASSWORD_FILE);
                if (pfn == null) {
                    passwordFile = new PasswordFile();
                } else {
                    passwordFile = new PasswordFile(pfn);
                }
            }
        } catch (IOException x) {
            throw new AuthenticationException("activate()", x);
        }
    }

    public void passivate() throws AuthenticationException {
        passwordFile = null;
    }

    public boolean contains(String userName) throws AuthenticationException {
        if (passwordFile == null) {
            throw new AuthenticationException("contains()", new IllegalStateException());
        }
        boolean result = false;
        try {
            result = passwordFile.contains(userName);
        } catch (IOException x) {
            throw new AuthenticationException("contains()", x);
        }
        return result;
    }

    public Map lookup(Map userID) throws AuthenticationException {
        if (passwordFile == null) {
            throw new AuthenticationException("lookup()", new IllegalStateException());
        }
        Map result = new HashMap();
        try {
            String userName = (String) userID.get(Registry.SASL_USERNAME);
            if (userName == null) {
                throw new NoSuchUserException("");
            }
            String[] data = passwordFile.lookup(userName);
            result.put(Registry.SASL_USERNAME, data[0]);
            result.put(Registry.SASL_PASSWORD, data[1]);
            result.put(CramMD5Registry.UID_FIELD, data[2]);
            result.put(CramMD5Registry.GID_FIELD, data[3]);
            result.put(CramMD5Registry.GECOS_FIELD, data[4]);
            result.put(CramMD5Registry.DIR_FIELD, data[5]);
            result.put(CramMD5Registry.SHELL_FIELD, data[6]);
        } catch (Exception x) {
            if (x instanceof AuthenticationException) {
                throw (AuthenticationException) x;
            }
            throw new AuthenticationException("lookup()", x);
        }
        return result;
    }

    public void update(Map userCredentials) throws AuthenticationException {
        if (passwordFile == null) {
            throw new AuthenticationException("update()", new IllegalStateException());
        }
        try {
            String userName = (String) userCredentials.get(Registry.SASL_USERNAME);
            String password = (String) userCredentials.get(Registry.SASL_PASSWORD);
            String uid = (String) userCredentials.get(CramMD5Registry.UID_FIELD);
            String gid = (String) userCredentials.get(CramMD5Registry.GID_FIELD);
            String gecos = (String) userCredentials.get(CramMD5Registry.GECOS_FIELD);
            String dir = (String) userCredentials.get(CramMD5Registry.DIR_FIELD);
            String shell = (String) userCredentials.get(CramMD5Registry.SHELL_FIELD);
            if (uid == null || gid == null || gecos == null || dir == null || shell == null) {
                passwordFile.changePasswd(userName, password);
            } else {
                String[] attributes = new String[]{uid, gid, gecos, dir, shell};
                passwordFile.add(userName, password, attributes);
            }
        } catch (Exception x) {
            if (x instanceof AuthenticationException) {
                throw (AuthenticationException) x;
            }
            throw new AuthenticationException("update()", x);
        }
    }

    public Map getConfiguration(String mode) throws AuthenticationException {
        throw new AuthenticationException("", new UnsupportedOperationException());
    }
}
