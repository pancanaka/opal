/* SynchronizedRandom.java -- Thread-safe IRandom wrapper.
   Copyright (C) 2003  Casey Marshall <rsdio@metastatic.org>

This file is a part of Jessie.

Jessie is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2 of the License, or (at your
option) any later version.

Jessie is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with Jessie; if not, write to the

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

package org.metastatic.jessie.provider;

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;

import java.util.Map;

class SynchronizedRandom implements IRandom {

    // Field.
    // -------------------------------------------------------------------------

    private final IRandom random;

    // Constructor.
    // -------------------------------------------------------------------------

    SynchronizedRandom(IRandom random) {
        this.random = random;
    }

    // Instance methods.
    // -------------------------------------------------------------------------

    public String name() {
        return random.name();
    }

    public synchronized void init(Map attrib) {
        random.init(attrib);
    }

    public synchronized byte nextByte() throws IllegalStateException,
            LimitReachedException {
        return random.nextByte();
    }

    public synchronized void nextBytes(byte[] buf, int off, int len)
            throws IllegalStateException, LimitReachedException {
        random.nextBytes(buf, off, len);
    }

    public synchronized Object clone() {
        return new SynchronizedRandom((IRandom) random.clone());
    }

    // For future versions of GNU Crypto. No-ops.
    public void addRandomByte(byte b) {
    }

    public void addRandomBytes(byte[] buffer) {
        addRandomBytes(buffer, 0, buffer.length);
    }

    public void addRandomBytes(byte[] b, int i, int j) {
    }
}
