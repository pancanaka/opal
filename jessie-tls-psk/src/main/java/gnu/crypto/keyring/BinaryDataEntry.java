package gnu.crypto.keyring;  // -*- c-basic-offset: 3 -*-

// ---------------------------------------------------------------------------
// $Id: BinaryDataEntry.java,v 1.1 2003/10/23 09:52:58 rsdio Exp $
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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * A binary data entry is a primitive entry that simply contains some amount
 * of arbitrary binary data and an optional content type.
 * <p/>
 * $Revision: 1.1 $
 */
public class BinaryDataEntry extends PrimitiveEntry {

    // Fields.
    // ------------------------------------------------------------------------

    public static final int TYPE = 9;

    // Constructors.
    // ------------------------------------------------------------------------

    /**
     * Creates a new binary data entry.
     *
     * @param contentType  The content type of this entry. This parameter can
     *                     be <code>null</code> if no content type is needed.
     * @param data         The data.
     * @param creationDate The creation date.
     * @param properties   This entry's properties.
     */
    public BinaryDataEntry(String contentType, byte[] data, Date creationDate,
                           Properties properties) {
        super(TYPE, creationDate, properties);
        if (data == null) {
            throw new IllegalArgumentException("no data");
        }
        payload = (byte[]) data.clone();
        if (contentType != null) {
            this.properties.put("content-type", contentType);
        }
    }

    private BinaryDataEntry() {
    }

    // Class methods.
    // ------------------------------------------------------------------------

    public static BinaryDataEntry decode(DataInputStream in) throws IOException {
        BinaryDataEntry entry = new BinaryDataEntry();
        entry.defaultDecode(in);
        return entry;
    }

    // Instance methods.
    // ------------------------------------------------------------------------

    /**
     * Returns the content type of this entry, or <code>null</code> if this
     * property is not set.
     *
     * @return The content type.
     */
    public String getContentType() {
        return properties.get("content-type");
    }

    /**
     * Returns this object's data field.
     *
     * @return The data.
     */
    public byte[] getData() {
        return getPayload();
    }

    protected void encodePayload() {
        // Empty.
    }
}
