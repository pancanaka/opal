package gnu.crypto.keyring;  // -*- c-basic-offset: 3 -*-

// ---------------------------------------------------------------------------
// $Id: AuthenticatedEntry.java,v 1.3 2003/10/23 09:54:22 rsdio Exp $
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

import gnu.crypto.Registry;
import gnu.crypto.mac.IMac;
import gnu.crypto.mac.MacFactory;
import gnu.crypto.mac.MacOutputStream;

import java.io.*;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public final class AuthenticatedEntry extends MaskableEnvelopeEntry
        implements Registry {

    // Constants and fields.
    // ------------------------------------------------------------------------

    public static final int TYPE = 2;

    // Constructor.
    // ------------------------------------------------------------------------

    public AuthenticatedEntry(String mac, int macLen, Properties properties) {
        super(TYPE, properties);

        if (macLen <= 0) {
            throw new IllegalArgumentException("invalid mac length");
        }
        this.properties.put("mac", mac);
        this.properties.put("maclen", String.valueOf(macLen));
        setMasked(false);
    }

    private AuthenticatedEntry() {
        setMasked(true);
    }

    // Class methods.
    // ------------------------------------------------------------------------

    public static AuthenticatedEntry decode(DataInputStream in)
            throws IOException {
        AuthenticatedEntry entry = new AuthenticatedEntry();
        entry.properties = new Properties();
        entry.properties.decode(in);
        if (!entry.properties.containsKey("mac")) {
            throw new MalformedKeyringException("no mac specified");
        }
        if (!entry.properties.containsKey("maclen")) {
            throw new MalformedKeyringException("no mac length specified");
        }
        return entry;
    }

    // Instance methods.
    // ------------------------------------------------------------------------

    /**
     * Computes the mac over this envelope's data. This method <b>must</b> be
     * called before this entry in encoded.
     *
     * @param key The key to authenticate with.
     * @throws IOException         If encoding fails.
     * @throws InvalidKeyException If the supplied key is bad.
     */
    public void authenticate(byte[] key) throws IOException, InvalidKeyException {
        if (isMasked()) {
            throw new IllegalStateException("entry is masked");
        }
        IMac m = getMac(key);

        ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
        MacOutputStream macout = new MacOutputStream(bout, m);
        DataOutputStream out2 = new DataOutputStream(macout);
        for (Iterator it = entries.iterator(); it.hasNext(); ) {
            Entry entry = (Entry) it.next();
            entry.encode(out2);
        }
        bout.write(m.digest());
        payload = bout.toByteArray();
    }

    /**
     * Verifies this entry's payload. This method will unmask this entry,
     * thus it must be called before accessing its contents.
     *
     * @param key The key to use to authenticate.
     * @throws InvalidKeyException If the given key is improper.
     */
    public void verify(byte[] key) throws InvalidKeyException {
        if (!isMasked() || payload == null) {
            return;
        }
        IMac m = getMac(key);

        m.update(payload, 0, payload.length - m.macSize());
        byte[] macValue = new byte[m.macSize()];
        System.arraycopy(payload, payload.length - macValue.length, macValue, 0, macValue.length);
        if (!Arrays.equals(macValue, m.digest())) {
            throw new IllegalArgumentException("MAC verification failed");
        }
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload, 0, payload.length - m.macSize()));
            decodeEnvelope(in);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("malformed keyring fragment");
        }
        setMasked(false);
        payload = null;
    }

    protected void encodePayload() throws IOException {
        if (payload == null) {
            throw new IllegalStateException("not authenticated");
        }
    }

    // Own methods.
    // ------------------------------------------------------------------------

    private IMac getMac(byte[] key) throws InvalidKeyException {
        IMac mac = MacFactory.getInstance(properties.get("mac"));
        if (mac == null) {
            throw new IllegalArgumentException("no such mac: " + properties.get("mac"));
        }
        int maclen = 0;
        if (!properties.containsKey("maclen")) {
            throw new IllegalArgumentException("no MAC length");
        }
        try {
            maclen = Integer.parseInt(properties.get("maclen"));
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("bad MAC length");
        }

        HashMap macAttr = new HashMap();
        macAttr.put(IMac.MAC_KEY_MATERIAL, key);
        macAttr.put(IMac.TRUNCATED_SIZE, new Integer(maclen));
        mac.init(macAttr);
        return mac;
    }
}
