package gnu.crypto.keyring;  // -*- c-basic-offset: 3 -*-

// ---------------------------------------------------------------------------
// $Id: PublicKeyEntry.java,v 1.4 2003/10/22 14:24:43 rsdio Exp $
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

import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.KeyPairCodecFactory;
import gnu.crypto.key.dh.GnuDHPublicKey;
import gnu.crypto.key.dss.DSSPublicKey;
import gnu.crypto.key.rsa.GnuRSAPublicKey;

import java.io.DataInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

public final class PublicKeyEntry extends PrimitiveEntry {

    // Constants and fields.
    // ------------------------------------------------------------------------

    public static final int TYPE = 6;

    private PublicKey key;

    // Constructor.
    // ------------------------------------------------------------------------

    public PublicKeyEntry(PublicKey key, Date creationDate,
                          Properties properties) {
        super(TYPE, creationDate, properties);

        if (key == null) {
            throw new IllegalArgumentException("no key specified");
        }
        this.key = key;
    }

    private PublicKeyEntry() {
    }

    // Class method.
    // ------------------------------------------------------------------------

    public static PublicKeyEntry decode(DataInputStream in)
            throws IOException {
        PublicKeyEntry entry = new PublicKeyEntry();
        entry.defaultDecode(in);
        String type = entry.properties.get("type");
        if (type == null) {
            throw new MalformedKeyringException("no key type");
        }
        if (type.equalsIgnoreCase("RAW-DSS")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dss");
            entry.key = coder.decodePublicKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW-RSA")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("rsa");
            entry.key = coder.decodePublicKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW-DH")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dh");
            entry.key = coder.decodePublicKey(entry.payload);
        } else if (type.equalsIgnoreCase("X.509")) {
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                entry.key = kf.generatePublic(new X509EncodedKeySpec(entry.payload));
            } catch (Exception x) {
            }
            if (entry.key == null) {
                try {
                    KeyFactory kf = KeyFactory.getInstance("DSA");
                    entry.key = kf.generatePublic(new X509EncodedKeySpec(entry.payload));
                } catch (Exception x) {
                }
                if (entry.key == null) {
                    throw new MalformedKeyringException("could not decode X.509 key");
                }
            }
        } else {
            throw new MalformedKeyringException("unsupported public key type: " + type);
        }
        return entry;
    }

    // Instance methods.
    // ------------------------------------------------------------------------

    /**
     * Returns the public key.
     *
     * @return The public key.
     */
    public PublicKey getKey() {
        return key;
    }

    protected void encodePayload() throws IOException {
        if (key instanceof DSSPublicKey) {
            properties.put("type", "RAW-DSS");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dss");
            payload = coder.encodePublicKey(key);
        } else if (key instanceof GnuRSAPublicKey) {
            properties.put("type", "RAW-RSA");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("rsa");
            payload = coder.encodePublicKey(key);
        } else if (key instanceof GnuDHPublicKey) {
            properties.put("type", "RAW-DH");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dh");
            payload = coder.encodePublicKey(key);
        } else if (key.getFormat() != null && key.getFormat().equals("X.509")) {
            properties.put("type", "X.509");
            payload = key.getEncoded();
        } else {
            throw new IllegalArgumentException("cannot encode public key");
        }
    }
}
