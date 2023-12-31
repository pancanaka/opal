/* KeyUsage.java -- the key usage extension.
   Copyright (C) 2003  Casey Marshall <csm@metastatic.org>

This file is part of GNU PKI, a PKI library.

GNU PKI is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

GNU PKI is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with GNU PKI; see the file COPYING.  If not, write to the Free
Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package org.metastatic.jessie.pki.ext;

import org.metastatic.jessie.pki.der.*;
import org.metastatic.jessie.pki.io.ASN1ParsingException;

import java.io.IOException;

public class KeyUsage extends Extension.Value {

    // Constants and fields.
    // -------------------------------------------------------------------------

    public static final OID ID = new OID("2.5.29.15");
    public static final int DIGITAL_SIGNATURE = 0;
    public static final int NON_REPUDIATION = 1;
    public static final int KEY_ENCIPHERMENT = 2;
    public static final int DATA_ENCIPHERMENT = 3;
    public static final int KEY_AGREEMENT = 4;
    public static final int KEY_CERT_SIGN = 5;
    public static final int CRL_SIGN = 6;
    public static final int ENCIPHER_ONLY = 7;
    public static final int DECIPHER_ONLY = 8;

    private final BitString keyUsage;

    // Constructor.
    // -------------------------------------------------------------------------

    public KeyUsage(final byte[] encoded) throws IOException {
        super(encoded);
        DERValue val = DERReader.read(encoded);
        if (val.getTag() != DER.BIT_STRING)
            throw new ASN1ParsingException("malformed KeyUsage");
        keyUsage = (BitString) val.getValue();
    }

    // Instance methods.
    // -------------------------------------------------------------------------

    public BitString getKeyUsage() {
        return keyUsage;
    }
}
