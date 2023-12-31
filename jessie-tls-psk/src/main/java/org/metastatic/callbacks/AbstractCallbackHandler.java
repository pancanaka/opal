/* AbstractCallbackHandler.java -- base callback handler implementation.
   Copyright (C) 2003  Casey Marshall <rsdio@metastatic.org>

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2 of the License, or (at your
option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the

   Free Software Foundation, Inc.,
   59 Temple Place, Suite 330,
   Boston, MA  02111-1307
   USA

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under terms
of your choice, provided that you also meet, for each linked independent
module, the terms and conditions of the license of that module.  An
independent module is a module which is not derived from or based on
this library.  If you modify this library, you may extend this exception
to your version of the library, but you are not obligated to do so.  If
you do not wish to do so, delete this exception statement from your
version.  */

package org.metastatic.callbacks;

import javax.security.auth.callback.*;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public abstract class AbstractCallbackHandler implements CallbackHandler {

    // Fields.
    // -------------------------------------------------------------------------

    private static final String SERVICE = "CallbackHandler";

    private static final int NATIVE_NOT_LOADED = 0;
    private static final int NATIVE_LOAD_FAILED = 1;
    private static final int NATIVE_LOADED = 2;
    private int native_state = NATIVE_NOT_LOADED;

    protected final ResourceBundle messages;

    // Constructors.
    // -------------------------------------------------------------------------

    protected AbstractCallbackHandler() {
        super();
        messages = PropertyResourceBundle
                .getBundle("org/metastatic/callbacks/MessagesBundle");
    }

    // Class methods.
    // -------------------------------------------------------------------------

    // public static CallbackHandler getInstance(String type)
    // throws NoSuchAlgorithmException
    // {
    // Provider[] p = Security.getProviders();
    // for (int i = 0; i < p.length; i++)
    // {
    // try
    // {
    // return getInstance(type, p[i]);
    // }
    // catch (NoSuchAlgorithmException ignored)
    // {
    // }
    // }
    // throw new NoSuchAlgorithmException(type);
    // }

    // public static CallbackHandler getInstance(String type, String provider)
    // throws NoSuchAlgorithmException, NoSuchProviderException
    // {
    // Provider p = Security.getProvider(provider);
    // if (p == null)
    // {
    // throw new NoSuchProviderException(provider);
    // }
    // return getInstance(type, p);
    // }

    // public static CallbackHandler getInstance(String type, Provider provider)
    // throws NoSuchAlgorithmException
    // {
    // try
    // {
    // return (CallbackHandler) Engine.getInstance(SERVICE, type, provider);
    // }
    // catch (InvocationTargetException ite)
    // {
    // throw new NoSuchAlgorithmException(type);
    // }
    // catch (ClassCastException cce)
    // {
    // throw new NoSuchAlgorithmException(type);
    // }
    // }

    // Instance methods.
    // -------------------------------------------------------------------------

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        if (callbacks == null)
            throw new NullPointerException();
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] == null)
                continue;
            if (callbacks[i] instanceof ChoiceCallback)
                handleChoice((ChoiceCallback) callbacks[i]);
            else if (callbacks[i] instanceof ConfirmationCallback)
                handleConfirmation((ConfirmationCallback) callbacks[i]);
            else if (callbacks[i] instanceof LanguageCallback)
                handleLanguage((LanguageCallback) callbacks[i]);
            else if (callbacks[i] instanceof NameCallback)
                handleName((NameCallback) callbacks[i]);
            else if (callbacks[i] instanceof PasswordCallback)
                handlePassword((PasswordCallback) callbacks[i]);
            else if (callbacks[i] instanceof TextInputCallback)
                handleTextInput((TextInputCallback) callbacks[i]);
            else if (callbacks[i] instanceof TextOutputCallback)
                handleTextOutput((TextOutputCallback) callbacks[i]);
            else
                handleOther(callbacks[i]);
        }
    }

    // Abstract methods.
    // -------------------------------------------------------------------------

    /**
     * Handles a {@link ChoiceCallback}.
     *
     * @param callback The choice callback.
     * @throws IOException If an I/O error occurs.
     */
    protected abstract void handleChoice(ChoiceCallback callback)
            throws IOException;

    /**
     * Handles a {@link ConfirmationCallback}.
     *
     * @param callback The confirmation callback.
     * @throws IOException If an I/O error occurs.
     */
    protected abstract void handleConfirmation(ConfirmationCallback callback)
            throws IOException;

    /**
     * Handles a {@link LanguageCallback}.
     *
     * @param callback The language callback.
     * @throws IOException If an I/O error occurs.
     */
    protected abstract void handleLanguage(LanguageCallback callback)
            throws IOException;

    /**
     * Handles a {@link NameCallback}.
     *
     * @param callback The name callback.
     * @throws IOException If an I/O error occurs.
     */
    protected abstract void handleName(NameCallback callback)
            throws IOException;

    /**
     * Handles a {@link PasswordCallback}.
     *
     * @param callback The password callback.
     * @throws IOException If an I/O error occurs.
     */
    protected abstract void handlePassword(PasswordCallback callback)
            throws IOException;

    /**
     * Handles a {@link TextInputCallback}.
     *
     * @param callback The text input callback.
     * @throws IOException If an I/O error occurs.
     */
    protected abstract void handleTextInput(TextInputCallback callback)
            throws IOException;

    /**
     * Handles a {@link TextOutputCallback}.
     *
     * @param callback The text output callback.
     * @throws IOException If an I/O error occurs.
     */
    protected abstract void handleTextOutput(TextOutputCallback callback)
            throws IOException;

    /**
     * Handles an unknown callback. The default implementation simply throws an
     * {@link UnsupportedCallbackException}.
     *
     * @param callback The callback to handle.
     * @throws IOException                  If an I/O error occurs.
     * @throws UnsupportedCallbackException If the specified callback is not supported.
     */
    protected void handleOther(Callback callback) throws IOException,
            UnsupportedCallbackException {
        throw new UnsupportedCallbackException(callback);
    }
}
