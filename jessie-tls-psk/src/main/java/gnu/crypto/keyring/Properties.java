package gnu.crypto.keyring;  // -*- c-basic-offset: 3 -*-

// ---------------------------------------------------------------------------
// $Id: Properties.java,v 1.3 2003/10/22 12:15:55 rsdio Exp $
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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A set of <code>(name =&gt; value)</code> pairs used in keyring entries.
 * Keys and values are simple strings, with the key never being empty and
 * always treated case-insensitively.
 */
public class Properties implements Cloneable {

    // Field.
    // ------------------------------------------------------------------------

    private HashMap props;

    // Constructor.
    // ------------------------------------------------------------------------

    /**
     * Creates a new properties object.
     */
    public Properties() {
        props = new HashMap();
    }

    // Instance methods.
    // ------------------------------------------------------------------------

    /**
     * Removes all properties from this object.
     */
    public void clear() {
        props.clear();
    }

    /**
     * Creates a copy of this properties object.
     *
     * @return The copy.
     */
    public Object clone() {
        Properties result = new Properties();
        result.props.putAll(props);
        return result;
    }

    /**
     * Tests if this object contains a given property name.
     *
     * @param key The key to test.
     * @return True if this object contains the given key.
     */
    public boolean containsKey(String key) {
        if (key == null || key.length() == 0) {
            return false;
        }
        return props.containsKey(canonicalize(key));
    }

    /**
     * Tests if this object contains a given property value.
     *
     * @param value The value to test.
     * @return True if this object contains the given value.
     */
    public boolean containsValue(String value) {
        if (value == null) {
            return false;
        }
        return props.containsValue(value);
    }

    /**
     * Adds a new property to this object.
     *
     * @param key   The key, which can neither be null nor empty.
     * @param value The value, which cannot be null.
     * @return The old value mapped by the key, if any.
     * @throws IllegalArgumentException If either the key or value parameter
     *                                  <p/>
     *                                  is null, or if the key is empty.
     */
    public String put(String key, String value) {
        if (key == null || value == null || key.length() == 0) {
            throw new IllegalArgumentException("key nor value can be null");
        }
        return (String) props.put(canonicalize(key), value);
    }

    /**
     * Returns the value mapped by the given key, or null if there is no
     * such mapping.
     *
     * @param key
     */
    public String get(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        return (String) props.get(canonicalize(key));
    }

    /**
     * Removes a key and its value from this object.
     *
     * @param key The key of the property to remove.
     * @return The old value mapped by the key, if any.
     */
    public String remove(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        return (String) props.remove(canonicalize(key));
    }

    /**
     * Decodes a set of properties from the given input stream.
     *
     * @param in The input stream.
     * @throws IOException If an I/O error occurs.
     */
    public void decode(DataInputStream in) throws IOException {
        int len = in.readInt();
        MeteredInputStream min = new MeteredInputStream(in, len);
        DataInputStream in2 = new DataInputStream(min);
        while (!min.limitReached()) {
            String name = in2.readUTF();
            String value = in2.readUTF();
            put(name, value);
        }
    }

    /**
     * Encodes this set of properties to the given output stream.
     *
     * @param out The output stream to encode to.
     * @throws IOException If an I/O error occurs.
     */
    public void encode(DataOutputStream out) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out2 = new DataOutputStream(buf);
        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            out2.writeUTF((String) entry.getKey());
            out2.writeUTF((String) entry.getValue());
        }
        out.writeInt(buf.size());
        buf.writeTo(out);
    }

    public String toString() {
        return props.toString();
    }

    // Own methods.
    // ------------------------------------------------------------------------

    private String canonicalize(String key) {
        return key.toLowerCase();
    }
}
