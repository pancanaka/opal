package gnu.crypto.sasl;

// ----------------------------------------------------------------------------
// $Id: ServerMechanism.java,v 1.2 2003/11/21 09:26:14 raif Exp $
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

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A base class to facilitate implementing SASL server-side mechanisms.</p>
 *
 * @version $Revision: 1.2 $
 */
public abstract class ServerMechanism implements SaslServer {

    // Constants and variables
    // -------------------------------------------------------------------------

    /**
     * Name of this mechanism.
     */
    protected String mechanism;

    /**
     * Name of protocol using this mechanism.
     */
    protected String protocol;

    /**
     * Name of server to authenticate to.
     */
    protected String serverName;

    /**
     * Properties of qualities desired for this mechanism.
     */
    protected Map properties;

    /**
     * Callback handler to use with this mechanism instance.
     */
    protected CallbackHandler handler;

    /**
     * Whether authentication phase is completed (true) or not (false).
     */
    protected boolean complete = false;

    /**
     * The authorisation identity.
     */
    protected String authorizationID;

    /**
     * Channel binding data to use with this mechanism instance.
     */
    protected byte[] channelBinding;

    /**
     * The state of the authentication automaton. -1 means uninitialised.
     */
    protected int state = -1;

    /**
     * The provider for authentication information.
     */
    protected IAuthInfoProvider authenticator;

    // Constructor(s)
    // -------------------------------------------------------------------------

    protected ServerMechanism(final String mechanism) {
        super();

        this.mechanism = mechanism;
        this.authenticator = AuthInfo.getProvider(mechanism);
        this.state = -1;
    }

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    // abstract methods to be implemented by concrete subclasses ---------------

    protected abstract void initMechanism() throws SaslException;

    protected abstract void resetMechanism() throws SaslException;

    // javax.security.sasl.SaslServer interface implementation -----------------

    public abstract byte[] evaluateResponse(byte[] response) throws SaslException;

    public boolean isComplete() {
        return complete;
    }

    public byte[] unwrap(final byte[] incoming, final int offset, final int len)
            throws SaslException {
        if (!isComplete()) {
            throw new IllegalMechanismStateException();
        }
        return this.engineUnwrap(incoming, offset, len);
    }

    public byte[] wrap(final byte[] outgoing, final int offset, final int len)
            throws SaslException {
        if (!isComplete()) {
            throw new IllegalMechanismStateException();
        }
        return this.engineWrap(outgoing, offset, len);
    }

    public String getMechanismName() {
        return this.mechanism;
    }

    public String getAuthorizationID() {
        return this.authorizationID;
    }

    public Object getNegotiatedProperty(final String propName) {
        if (!isComplete()) {
            throw new IllegalStateException();
        }
        if (Sasl.QOP.equals(propName)) {
            return getNegotiatedQOP();
        }
        if (Sasl.STRENGTH.equals(propName)) {
            return getNegotiatedStrength();
        }
        if (Sasl.SERVER_AUTH.equals(propName)) {
            return getNegotiatedServerAuth();
        }
        if (Sasl.MAX_BUFFER.equals(propName)) {
            return getNegotiatedMaxBuffer();
        }
        if (Sasl.RAW_SEND_SIZE.equals(propName)) {
            return getNegotiatedRawSendSize();
        }
        if (Sasl.POLICY_NOPLAINTEXT.equals(propName)) {
            return getNegotiatedPolicyNoPlainText();
        }
        if (Sasl.POLICY_NOACTIVE.equals(propName)) {
            return getNegotiatedPolicyNoActive();
        }
        if (Sasl.POLICY_NODICTIONARY.equals(propName)) {
            return getNegotiatedPolicyNoDictionary();
        }
        if (Sasl.POLICY_NOANONYMOUS.equals(propName)) {
            return getNegotiatedPolicyNoAnonymous();
        }
        if (Sasl.POLICY_FORWARD_SECRECY.equals(propName)) {
            return getNegotiatedPolicyForwardSecrecy();
        }
        if (Sasl.POLICY_PASS_CREDENTIALS.equals(propName)) {
            return getNegotiatedPolicyPassCredentials();
        }
        if (Sasl.REUSE.equals(propName)) {
            return getReuse();
        }
        return null;
    }

    public void dispose() throws SaslException {
        reset();
    }

    // other Instance methods --------------------------------------------------

    protected String getNegotiatedQOP() {
        return Registry.QOP_AUTH;
    }

    protected String getNegotiatedStrength() {
        return Registry.STRENGTH_LOW;
    }

    protected String getNegotiatedServerAuth() {
        return Registry.SERVER_AUTH_FALSE;
    }

    protected String getNegotiatedMaxBuffer() {
        return null;
    }

    protected String getNegotiatedPolicyNoPlainText() {
        return null;
    }

    protected String getNegotiatedPolicyNoActive() {
        return null;
    }

    protected String getNegotiatedPolicyNoDictionary() {
        return null;
    }

    protected String getNegotiatedPolicyNoAnonymous() {
        return null;
    }

    protected String getNegotiatedPolicyForwardSecrecy() {
        return null;
    }

    protected String getNegotiatedPolicyPassCredentials() {
        return null;
    }

    protected String getNegotiatedRawSendSize() {
        return String.valueOf(Registry.SASL_BUFFER_MAX_LIMIT);
    }

    protected String getReuse() {
        return Registry.REUSE_FALSE;
    }

    protected byte[]
    engineUnwrap(final byte[] incoming, final int offset, final int len)
            throws SaslException {
        final byte[] result = new byte[len];
        System.arraycopy(incoming, offset, result, 0, len);
        return result;
    }

    protected byte[]
    engineWrap(final byte[] outgoing, final int offset, final int len)
            throws SaslException {
        final byte[] result = new byte[len];
        System.arraycopy(outgoing, offset, result, 0, len);
        return result;
    }

    /**
     * <p>Initialises the mechanism with designated attributes. Permissible names
     * and values are mechanism specific.</p>
     *
     * @param attributes a set of name-value pairs that describes the desired
     *                   future behaviour of this instance.
     * @throws IllegalMechanismStateException if the instance is already
     *                                        initialised.
     * @throws SaslException                  if an exception occurs during the process.
     */
    public void init(final Map attributes) throws SaslException {
        if (state != -1) {
            throw new IllegalMechanismStateException("init()");
        }

        if (properties == null) {
            properties = new HashMap();
        } else {
            properties.clear();
        }
        if (attributes != null) {
            protocol = (String) attributes.get(Registry.SASL_PROTOCOL);
            serverName = (String) attributes.get(Registry.SASL_SERVER_NAME);
            handler = (CallbackHandler) attributes.get(Registry.SASL_CALLBACK_HANDLER);
            channelBinding = (byte[]) attributes.get(Registry.SASL_CHANNEL_BINDING);
            properties.putAll(attributes);
        } else {
            handler = null;
        }

        if (protocol == null) {
            protocol = "";
        }
        if (serverName == null) {
            serverName = "";
        }
        if (authenticator != null) {
            authenticator.activate(properties);
        }
        if (channelBinding == null) {
            channelBinding = new byte[0];
        }
        initMechanism();
        complete = false;
        state = 0;
    }

    /**
     * <p>Resets the mechanism instance for re-initialisation and use with other
     * characteristics.</p>
     *
     * @throws SaslException if an exception occurs during the process.
     */
    public void reset() throws SaslException {
        resetMechanism();
        properties.clear();
        if (authenticator != null) {
            authenticator.passivate();
        }
        protocol = serverName = null;
        channelBinding = null;
        complete = false;
        state = -1;
    }
}
