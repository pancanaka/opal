package gnu.crypto.jce.mac;

// --------------------------------------------------------------------------
// $Id: MacAdapter.java,v 1.2 2003/01/11 06:12:45 raif Exp $
//
// Copyright (C) 2002, 2003 Free Software Foundation, Inc.
//
// This file is part of GNU Crypto.
//
// GNU Crypto is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2 of the License, or (at
// your option) any later version.
//
// GNU Crypto is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the
//
//    Free Software Foundation, Inc.,
//    59 Temple Place, Suite 330,
//    Boston, MA  02111-1307
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
// --------------------------------------------------------------------------

import gnu.crypto.mac.IMac;
import gnu.crypto.mac.MacFactory;

import javax.crypto.MacSpi;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The implementation of a generic {@link javax.crypto.Mac} adapter class
 * to wrap GNU Crypto MAC instances.</p>
 * <p/>
 * <p>This class defines the <i>Service Provider Interface</i> (<b>SPI</b>) for
 * the {@link javax.crypto.Mac} class, which provides the functionality of a
 * message authentication code algorithm, such as the <i>Hashed Message
 * Authentication Code</i> (<b>HMAC</b>) algorithms.</p>
 *
 * @version $Revision: 1.2 $
 */
class MacAdapter extends MacSpi {

    // Constants and variables
    // -----------------------------------------------------------------------

    /**
     * Our MAC instance.
     */
    protected IMac mac;

    /**
     * Our MAC attributes.
     */
    protected Map attributes;

    // Constructor(s)
    // -----------------------------------------------------------------------

    /**
     * <p>Private, "copying" constructor for cloning.</p>
     *
     * @param that The instance being cloned.
     */
    private MacAdapter(MacAdapter that) {
        this.mac = (IMac) that.mac.clone();
        this.attributes = new HashMap(that.attributes);
    }

    /**
     * <p>Creates a new Mac instance for the given name.</p>
     *
     * @param name The name of the mac to create.
     */
    protected MacAdapter(String name) {
        mac = MacFactory.getInstance(name);
        attributes = new HashMap();
    }

    // Class methods
    // -----------------------------------------------------------------------

    // Instance methods
    // -----------------------------------------------------------------------

    // Cloneable interface implementation ------------------------------------

    public Object clone() {
        return new MacAdapter(this);
    }

    // Instance methods implementing javax.crypto.MacSpi ---------------------

    protected byte[] engineDoFinal() {
        byte[] result = mac.digest();
        engineReset();
        return result;
    }

    protected int engineGetMacLength() {
        return mac.macSize();
    }

    protected void engineInit(Key key, AlgorithmParameterSpec params)
            throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!key.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("unknown key format " + key.getFormat());
        }
        attributes.put(IMac.MAC_KEY_MATERIAL, key.getEncoded());
        mac.reset();
        mac.init(attributes);
    }

    protected void engineReset() {
        mac.reset();
    }

    protected void engineUpdate(byte b) {
        mac.update(b);
    }

    protected void engineUpdate(byte[] in, int off, int len) {
        mac.update(in, off, len);
    }
}
