/* DefaultHandler.java -- non-interactive default callback handler.
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
import java.util.Locale;

/**
 * This trivial implementation of {@link CallbackHandler} sets its
 * {@link Callback} arguments to default values, with no user interaction.
 */
public class DefaultCallbackHandler extends AbstractCallbackHandler {

    // Constructor.
    // -------------------------------------------------------------------------

    public DefaultCallbackHandler() {
        super();
    }

    // Instance methods.
    // -------------------------------------------------------------------------

    protected void handleChoice(ChoiceCallback c) {
        c.setSelectedIndex(c.getDefaultChoice());
    }

    protected void handleConfirmation(ConfirmationCallback c) {
        if (c.getOptionType() == ConfirmationCallback.YES_NO_OPTION)
            c.setSelectedIndex(ConfirmationCallback.NO);
        else if (c.getOptionType() == ConfirmationCallback.YES_NO_CANCEL_OPTION)
            c.setSelectedIndex(ConfirmationCallback.NO);
        else if (c.getOptionType() == ConfirmationCallback.OK_CANCEL_OPTION)
            c.setSelectedIndex(ConfirmationCallback.OK);
        else
            c.setSelectedIndex(c.getDefaultOption());
    }

    protected void handleLanguage(LanguageCallback c) {
        c.setLocale(Locale.getDefault());
    }

    protected void handleName(NameCallback c) {
        c.setName(System.getProperty("user.name"));
    }

    protected void handlePassword(PasswordCallback c) {
        c.setPassword(new char[0]);
    }

    protected void handleTextInput(TextInputCallback c) {
        c.setText("");
    }

    protected void handleTextOutput(TextOutputCallback c) {
    }
}
