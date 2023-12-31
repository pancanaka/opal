package gnu.crypto.keyring;  // -*- c-basic-offset: 3 -*-

// ---------------------------------------------------------------------------
// $Id: PrivateKeyEntry.java,v 1.5 2003/12/03 19:27:10 rsdio Exp $
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

import gnu.crypto.key.GnuSecretKey;
import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.KeyPairCodecFactory;
import gnu.crypto.key.dh.GnuDHPrivateKey;
import gnu.crypto.key.dss.DSSPrivateKey;
import gnu.crypto.key.rsa.GnuRSAPrivateKey;

import java.io.DataInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

/**
 * <p>An immutable class representing a private or secret key entry.</p>
 *
 * @version $Revision: 1.5 $
 */
public final class PrivateKeyEntry extends PrimitiveEntry {

    // Constants and variables
    // -------------------------------------------------------------------------

    public static final int TYPE = 7;

    /**
     * The key.
     */
    private Key key;

    // Constructor(s)
    // -------------------------------------------------------------------------

    /**
     * <p>Creates a new key entry.</p>
     *
     * @param key          The key.
     * @param creationDate The entry creation date.
     * @param properties   The entry properties.
     * @throws IllegalArgumentException If any parameter is null.
     */
    public PrivateKeyEntry(Key key, Date creationDate, Properties properties) {
        super(TYPE, creationDate, properties);

        if (key == null) {
            throw new IllegalArgumentException("no private key");
        }
        if (!(key instanceof PrivateKey) && !(key instanceof GnuSecretKey)) {
            throw new IllegalArgumentException("not a private or secret key");
        }
        this.key = key;
    }

    private PrivateKeyEntry() {
        super();
    }

    // Class methods
    // -------------------------------------------------------------------------

    public static PrivateKeyEntry decode(DataInputStream in) throws IOException {
        PrivateKeyEntry entry = new PrivateKeyEntry();
        entry.defaultDecode(in);
        String type = entry.properties.get("type");
        if (type == null) {
            throw new MalformedKeyringException("no key type");
        }
        if (type.equalsIgnoreCase("RAW-DSS")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dss");
            entry.key = coder.decodePrivateKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW-RSA")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("rsa");
            entry.key = coder.decodePrivateKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW-DH")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dh");
            entry.key = coder.decodePrivateKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW")) {
            entry.key = new GnuSecretKey(entry.payload, null);
        } else if (type.equalsIgnoreCase("PKCS8")) {
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                entry.key = kf.generatePrivate(new PKCS8EncodedKeySpec(entry.payload));
            } catch (Exception x) {
            }
            if (entry.key == null) {
                try {
                    KeyFactory kf = KeyFactory.getInstance("DSA");
                    entry.key = kf.generatePrivate(new PKCS8EncodedKeySpec(entry.payload));
                } catch (Exception x) {
                }
                if (entry.key == null) {
                    throw new MalformedKeyringException("could not decode PKCS#8 key");
                }
            }
        } else {
            throw new MalformedKeyringException("unsupported key type " + type);
        }
        return entry;
    }

    // Instance methods
    // -------------------------------------------------------------------------

    /**
     * <p>Returns this entry's key.</p>
     *
     * @return The key.
     */
    public Key getKey() {
        return key;
    }

    protected void encodePayload() throws IOException {
        String format = key.getFormat();
        if (key instanceof DSSPrivateKey) {
            properties.put("type", "RAW-DSS");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dss");
            payload = coder.encodePrivateKey((PrivateKey) key);
        } else if (key instanceof GnuRSAPrivateKey) {
            properties.put("type", "RAW-RSA");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("rsa");
            payload = coder.encodePrivateKey((PrivateKey) key);
        } else if (key instanceof GnuDHPrivateKey) {
            properties.put("type", "RAW-DH");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dh");
            payload = coder.encodePrivateKey((PrivateKey) key);
        } else if (key instanceof GnuSecretKey) {
            properties.put("type", "RAW");
            payload = key.getEncoded();
        } else if (format != null && format.equals("PKCS#8")) {
            properties.put("type", "PKCS8");
            payload = key.getEncoded();
        } else {
            throw new IllegalArgumentException("unsupported private key");
        }
    }
}
