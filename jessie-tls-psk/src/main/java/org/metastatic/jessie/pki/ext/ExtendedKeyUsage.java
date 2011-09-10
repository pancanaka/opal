/* ExtendedKeyUsage.java -- the extended key usage extension.
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

import org.metastatic.jessie.pki.der.DER;
import org.metastatic.jessie.pki.der.DERReader;
import org.metastatic.jessie.pki.der.DERValue;
import org.metastatic.jessie.pki.der.OID;
import org.metastatic.jessie.pki.io.ASN1ParsingException;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ExtendedKeyUsage extends Extension.Value {

    // Constants and fields.
    // -------------------------------------------------------------------------

    public static final OID ID = new OID("2.5.29.37");

    private final List purposeIds;

    // Constructor.
    // -------------------------------------------------------------------------

    public ExtendedKeyUsage(final byte[] encoded) throws IOException {
        super(encoded);
        DERReader der = new DERReader(encoded);
        DERValue usageList = der.read();
        if (!usageList.isConstructed())
            throw new ASN1ParsingException("malformed ExtKeyUsageSyntax");
        int len = 0;
        purposeIds = new LinkedList();
        while (len < usageList.getLength()) {
            DERValue val = der.read();
            if (val.getTag() != DER.OBJECT_IDENTIFIER)
                throw new ASN1ParsingException("malformed KeyPurposeId");
            purposeIds.add(val.getValue());
            len += val.getEncodedLength();
        }
    }

    // Instance method.
    // -------------------------------------------------------------------------

    public List getPurposeIds() {
        return Collections.unmodifiableList(purposeIds);
    }
}
