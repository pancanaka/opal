package gnu.crypto.jce.hash;

// ----------------------------------------------------------------------------
// $Id: Sha512Spi.java,v 1.1 2003/09/26 23:42:53 raif Exp $
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

/**
 * <p>The implementation of the SHA-2-3 (512-bit) <i>Service Provider Interface</i>
 * (<b>SPI</b>) adapter.</p>
 *
 * @version $Revision: 1.1 $
 */
public class Sha512Spi extends MessageDigestAdapter {

    // Constants and variables
    // -------------------------------------------------------------------------

    // Constructor(s)
    // -------------------------------------------------------------------------

    public Sha512Spi() {
        super(Registry.SHA512_HASH);
    }

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------
}
