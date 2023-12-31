package gnu.testlet.gnu.crypto.keyring;

// ----------------------------------------------------------------------------
// $Id: TestOfPublicKeyring.java,v 1.1 2003/12/25 02:21:39 uid66198 Exp $
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

// Tags: GNU-CRYPTO

import gnu.crypto.keyring.GnuPublicKeyring;
import gnu.crypto.keyring.IKeyring;
import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Conformance tests for the GNU (public) Keyring implementation.</p>
 *
 * @version $Revision: 1.1 $
 */
public class TestOfPublicKeyring implements Testlet {

    // Constants and variables
    // -------------------------------------------------------------------------

    private static final byte[] keyring = new byte[]{
//         (byte) 0x47,(byte) 0x4b,(byte) 0x52,(byte) 0x01,(byte) 0x04,(byte) 0x03,(byte) 0x00,(byte) 0x00,
            (byte) 0x47, (byte) 0x4b, (byte) 0x52, (byte) 0x01, (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x04, (byte) 0x73, (byte) 0x61, (byte) 0x6c, (byte) 0x74,
            (byte) 0x00, (byte) 0x10, (byte) 0x46, (byte) 0x34, (byte) 0x42, (byte) 0x35, (byte) 0x35, (byte) 0x43,
            (byte) 0x36, (byte) 0x34, (byte) 0x35, (byte) 0x46, (byte) 0x30, (byte) 0x33, (byte) 0x33, (byte) 0x35,
            (byte) 0x31, (byte) 0x43, (byte) 0x00, (byte) 0x0a, (byte) 0x61, (byte) 0x6c, (byte) 0x69, (byte) 0x61,
            (byte) 0x73, (byte) 0x2d, (byte) 0x6c, (byte) 0x69, (byte) 0x73, (byte) 0x74, (byte) 0x00, (byte) 0x10,
            (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0x69, (byte) 0x73, (byte) 0x69, (byte) 0x67, (byte) 0x6e,
            (byte) 0x63, (byte) 0x6c, (byte) 0x61, (byte) 0x73, (byte) 0x73, (byte) 0x31, (byte) 0x63, (byte) 0x61,
            (byte) 0x00, (byte) 0x06, (byte) 0x6d, (byte) 0x61, (byte) 0x63, (byte) 0x6c, (byte) 0x65, (byte) 0x6e,
            (byte) 0x00, (byte) 0x02, (byte) 0x32, (byte) 0x30, (byte) 0x00, (byte) 0x03, (byte) 0x6d, (byte) 0x61,
            (byte) 0x63, (byte) 0x00, (byte) 0x0a, (byte) 0x48, (byte) 0x4d, (byte) 0x41, (byte) 0x43, (byte) 0x2d,
            (byte) 0x53, (byte) 0x48, (byte) 0x41, (byte) 0x2d, (byte) 0x31, (byte) 0x00, (byte) 0x00, (byte) 0x02,
            (byte) 0x6e, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x0a,
            (byte) 0x61, (byte) 0x6c, (byte) 0x69, (byte) 0x61, (byte) 0x73, (byte) 0x2d, (byte) 0x6c, (byte) 0x69,
            (byte) 0x73, (byte) 0x74, (byte) 0x00, (byte) 0x10, (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0x69,
            (byte) 0x73, (byte) 0x69, (byte) 0x67, (byte) 0x6e, (byte) 0x63, (byte) 0x6c, (byte) 0x61, (byte) 0x73,
            (byte) 0x73, (byte) 0x31, (byte) 0x63, (byte) 0x61, (byte) 0x00, (byte) 0x09, (byte) 0x61, (byte) 0x6c,
            (byte) 0x67, (byte) 0x6f, (byte) 0x72, (byte) 0x69, (byte) 0x74, (byte) 0x68, (byte) 0x6d, (byte) 0x00,
            (byte) 0x07, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x4c, (byte) 0x41, (byte) 0x54, (byte) 0x45,
            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x1f, (byte) 0x78, (byte) 0x9c, (byte) 0x63, (byte) 0x65,
            (byte) 0x60, (byte) 0x60, (byte) 0x70, (byte) 0x61, (byte) 0x60, (byte) 0x29, (byte) 0xa9, (byte) 0x2c,
            (byte) 0x48, (byte) 0x65, (byte) 0x60, (byte) 0x8d, (byte) 0xd0, (byte) 0x33, (byte) 0x35, (byte) 0xb0,
            (byte) 0x64, (byte) 0xe0, (byte) 0x4d, (byte) 0x2e, (byte) 0x4a, (byte) 0x4d, (byte) 0x2c, (byte) 0xc9,
            (byte) 0xcc, (byte) 0xcf, (byte) 0xd3, (byte) 0x4d, (byte) 0x49, (byte) 0x2c, (byte) 0x49, (byte) 0x65,
            (byte) 0xe0, (byte) 0x35, (byte) 0x34, (byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x37, (byte) 0x31,
            (byte) 0xb3, (byte) 0x34, (byte) 0xb1, (byte) 0x30, (byte) 0xb4, (byte) 0x34, (byte) 0x60, (byte) 0x60,
            (byte) 0x4d, (byte) 0xcc, (byte) 0xc9, (byte) 0x4c, (byte) 0x2c, (byte) 0x66, (byte) 0x10, (byte) 0x28,
            (byte) 0x4b, (byte) 0x2d, (byte) 0xca, (byte) 0x2c, (byte) 0xce, (byte) 0x4c, (byte) 0xcf, (byte) 0x4b,
            (byte) 0xce, (byte) 0x49, (byte) 0x2c, (byte) 0x2e, (byte) 0x36, (byte) 0x4c, (byte) 0x4e, (byte) 0x64,
            (byte) 0x60, (byte) 0x60, (byte) 0x72, (byte) 0x30, (byte) 0x68, (byte) 0x62, (byte) 0xb2, (byte) 0x31,
            (byte) 0x68, (byte) 0x62, (byte) 0x5c, (byte) 0xca, (byte) 0x24, (byte) 0x60, (byte) 0x14, (byte) 0x60,
            (byte) 0x7c, (byte) 0x3e, (byte) 0xe0, (byte) 0x62, (byte) 0xd8, (byte) 0xe7, (byte) 0x98, (byte) 0xc6,
            (byte) 0xb5, (byte) 0xa9, (byte) 0x31, (byte) 0xfe, (byte) 0x27, (byte) 0x54, (byte) 0x0d, (byte) 0x78,
            (byte) 0xd9, (byte) 0x38, (byte) 0xb5, (byte) 0xda, (byte) 0x3c, (byte) 0xda, (byte) 0xbe, (byte) 0xf3,
            (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0xb1, (byte) 0x32, (byte) 0x18, (byte) 0xc4, (byte) 0x1b,
            (byte) 0x72, (byte) 0x1b, (byte) 0x70, (byte) 0xb2, (byte) 0x31, (byte) 0x87, (byte) 0xb2, (byte) 0xb0,
            (byte) 0x09, (byte) 0x33, (byte) 0x85, (byte) 0x06, (byte) 0x1b, (byte) 0x8a, (byte) 0x1b, (byte) 0x88,
            (byte) 0x82, (byte) 0x38, (byte) 0x5c, (byte) 0xc2, (byte) 0x7c, (byte) 0x61, (byte) 0x40, (byte) 0x63,
            (byte) 0x83, (byte) 0x81, (byte) 0xc6, (byte) 0xea, (byte) 0x28, (byte) 0x78, (byte) 0xe6, (byte) 0x25,
            (byte) 0xeb, (byte) 0x19, (byte) 0x9a, (byte) 0x1b, (byte) 0x98, (byte) 0x82, (byte) 0x24, (byte) 0xb8,
            (byte) 0x85, (byte) 0xf5, (byte) 0x9c, (byte) 0x41, (byte) 0xf6, (byte) 0x28, (byte) 0x18, (byte) 0x2a,
            (byte) 0x04, (byte) 0x94, (byte) 0x26, (byte) 0xe5, (byte) 0x64, (byte) 0x26, (byte) 0x2b, (byte) 0x04,
            (byte) 0x14, (byte) 0x65, (byte) 0xe6, (byte) 0x26, (byte) 0x16, (byte) 0x55, (byte) 0x2a, (byte) 0x38,
            (byte) 0xa7, (byte) 0x16, (byte) 0x95, (byte) 0x64, (byte) 0xa6, (byte) 0x65, (byte) 0x26, (byte) 0x83,
            (byte) 0x1d, (byte) 0xac, (byte) 0xe0, (byte) 0x58, (byte) 0x5a, (byte) 0x92, (byte) 0x91, (byte) 0x5f,
            (byte) 0x94, (byte) 0x59, (byte) 0x52, (byte) 0x69, (byte) 0x20, (byte) 0x27, (byte) 0xce, (byte) 0x6b,
            (byte) 0x69, (byte) 0x66, (byte) 0x60, (byte) 0x68, (byte) 0x64, (byte) 0x69, (byte) 0x00, (byte) 0x06,
            (byte) 0x51, (byte) 0xe2, (byte) 0xbc, (byte) 0x46, (byte) 0x06, (byte) 0x06, (byte) 0x40, (byte) 0x9f,
            (byte) 0x18, (byte) 0x19, (byte) 0x9b, (byte) 0x5a, (byte) 0x9a, (byte) 0x5a, (byte) 0x46, (byte) 0xd1,
            (byte) 0xde, (byte) 0x01, (byte) 0x8d, (byte) 0xf3, (byte) 0x91, (byte) 0xfd, (byte) 0xcc, (byte) 0xc8,
            (byte) 0xca, (byte) 0xc0, (byte) 0xdc, (byte) 0xd8, (byte) 0xcb, (byte) 0x60, (byte) 0xd0, (byte) 0xd8,
            (byte) 0xc9, (byte) 0xd4, (byte) 0xd8, (byte) 0xc8, (byte) 0xf0, (byte) 0x54, (byte) 0x72, (byte) 0x7f,
            (byte) 0xee, (byte) 0xe2, (byte) 0xb0, (byte) 0x44, (byte) 0xdd, (byte) 0x99, (byte) 0x1e, (byte) 0x85,
            (byte) 0xdf, (byte) 0xd2, (byte) 0xef, (byte) 0xed, (byte) 0xec, (byte) 0x7d, (byte) 0xbd, (byte) 0x7d,
            (byte) 0x5e, (byte) 0x5b, (byte) 0x03, (byte) 0xd7, (byte) 0x44, (byte) 0xbe, (byte) 0x5f, (byte) 0x16,
            (byte) 0xaa, (byte) 0xeb, (byte) 0xdd, (byte) 0x3a, (byte) 0x9a, (byte) 0x9e, (byte) 0x16, (byte) 0xaf,
            (byte) 0x58, (byte) 0x30, (byte) 0x5b, (byte) 0x25, (byte) 0x96, (byte) 0x57, (byte) 0xfe, (byte) 0x4c,
            (byte) 0x6a, (byte) 0x1e, (byte) 0xcf, (byte) 0x86, (byte) 0x0b, (byte) 0x61, (byte) 0x2d, (byte) 0x12,
            (byte) 0xed, (byte) 0xb3, (byte) 0xd8, (byte) 0x66, (byte) 0x0b, (byte) 0x2c, (byte) 0x2c, (byte) 0xbe,
            (byte) 0xbf, (byte) 0x25, (byte) 0xc2, (byte) 0x32, (byte) 0x3b, (byte) 0xef, (byte) 0xe0, (byte) 0x37,
            (byte) 0xd1, (byte) 0xab, (byte) 0x2b, (byte) 0x56, (byte) 0xd8, (byte) 0xaf, (byte) 0x12, (byte) 0x62,
            (byte) 0xeb, (byte) 0x35, (byte) 0x5c, (byte) 0x53, (byte) 0xbf, (byte) 0xc1, (byte) 0xe4, (byte) 0x7a,
            (byte) 0xbf, (byte) 0x49, (byte) 0x7a, (byte) 0x07, (byte) 0xe7, (byte) 0x59, (byte) 0x11, (byte) 0xc1,
            (byte) 0x47, (byte) 0x7e, (byte) 0xae, (byte) 0x61, (byte) 0x99, (byte) 0xf2, (byte) 0x15, (byte) 0x4c,
            (byte) 0x0d, (byte) 0xb7, (byte) 0xee, (byte) 0xb8, (byte) 0x4f, (byte) 0xd4, (byte) 0xdc, (byte) 0x6d,
            (byte) 0x76, (byte) 0x32, (byte) 0x39, (byte) 0xe6, (byte) 0xe8, (byte) 0x83, (byte) 0xeb, (byte) 0xba,
            (byte) 0xed, (byte) 0xd5, (byte) 0x0b, (byte) 0xb7, (byte) 0x1b, (byte) 0x6d, (byte) 0xa8, (byte) 0x36,
            (byte) 0xd8, (byte) 0xa5, (byte) 0xa5, (byte) 0x6f, (byte) 0xb8, (byte) 0xea, (byte) 0xdd, (byte) 0xe2,
            (byte) 0xf4, (byte) 0x5b, (byte) 0xb7, (byte) 0x99, (byte) 0x98, (byte) 0x19, (byte) 0x19, (byte) 0x18,
            (byte) 0xd1, (byte) 0xa2, (byte) 0x84, (byte) 0x19, (byte) 0xe8, (byte) 0x2e, (byte) 0x6f, (byte) 0x97,
            (byte) 0xb4, (byte) 0x84, (byte) 0x8c, (byte) 0x94, (byte) 0x27, (byte) 0x33, (byte) 0xa4, (byte) 0x3f,
            (byte) 0x6f, (byte) 0x28, (byte) 0x7a, (byte) 0x36, (byte) 0xb5, (byte) 0xb3, (byte) 0xe6, (byte) 0x6e,
            (byte) 0xf5, (byte) 0xe6, (byte) 0xa9, (byte) 0x07, (byte) 0x64, (byte) 0xf5, (byte) 0x6e, (byte) 0xdc,
            (byte) 0x90, (byte) 0xbc, (byte) 0xa0, (byte) 0x6b, (byte) 0x62, (byte) 0x7b, (byte) 0x2c, (byte) 0x60,
            (byte) 0x96, (byte) 0x40, (byte) 0x5b, (byte) 0xcf, (byte) 0x2a, (byte) 0x7b, (byte) 0xeb, (byte) 0x15,
            (byte) 0x2c, (byte) 0x7f, (byte) 0xcc, (byte) 0x83, (byte) 0xa6, (byte) 0x1e, (byte) 0xbe, (byte) 0x79,
            (byte) 0xf2, (byte) 0xf6, (byte) 0xd9, (byte) 0x4f, (byte) 0x6d, (byte) 0x6c, (byte) 0x47, (byte) 0x36,
            (byte) 0x4a, (byte) 0x7f, (byte) 0x68, (byte) 0xea, (byte) 0x30, (byte) 0x70, (byte) 0xea, (byte) 0x13,
            (byte) 0x0f, (byte) 0x90, (byte) 0x49, (byte) 0xa9, (byte) 0xda, (byte) 0x61, (byte) 0x37, (byte) 0xd3,
            (byte) 0xb3, (byte) 0x64, (byte) 0xfa, (byte) 0x9f, (byte) 0x35, (byte) 0x4c, (byte) 0xce, (byte) 0xbf,
            (byte) 0xa7, (byte) 0xf1, (byte) 0x84, (byte) 0xb1, (byte) 0xa8, (byte) 0xf2, (byte) 0xd4, (byte) 0xd4,
            (byte) 0xb4, (byte) 0xcf, (byte) 0x55, (byte) 0x59, (byte) 0x7e, (byte) 0xe3, (byte) 0x83, (byte) 0x91,
            (byte) 0xe6, (byte) 0xd6, (byte) 0x25, (byte) 0xf7, (byte) 0x63, (byte) 0x17, (byte) 0xf9, (byte) 0x1c,
            (byte) 0x15, (byte) 0x33, (byte) 0x5a, (byte) 0xe1, (byte) 0xf4, (byte) 0xcd, (byte) 0x75, (byte) 0xd9,
            (byte) 0x36, (byte) 0xb3, (byte) 0x9d, (byte) 0x0f, (byte) 0xf6, (byte) 0xa7, (byte) 0x9a, (byte) 0x4d,
            (byte) 0x3e, (byte) 0x74, (byte) 0xe9, (byte) 0x7a, (byte) 0xf6, (byte) 0x9d, (byte) 0x7b, (byte) 0x91,
            (byte) 0xd7, (byte) 0x16, (byte) 0x99, (byte) 0xfe, (byte) 0x70, (byte) 0x05, (byte) 0x00, (byte) 0xc4,
            (byte) 0x7a, (byte) 0xe5, (byte) 0xbb, (byte) 0x04, (byte) 0xf9, (byte) 0x0f, (byte) 0x26, (byte) 0x22,
            (byte) 0xfc, (byte) 0x61, (byte) 0xb6, (byte) 0xcf, (byte) 0x0c, (byte) 0xba, (byte) 0x43, (byte) 0xc1,
            (byte) 0xb9, (byte) 0xd6, (byte) 0xae, (byte) 0xb3, (byte) 0xd8, (byte) 0x21, (byte) 0x23
    };

    private static final String ALIAS = "verisignclass1ca";

    // Constructor(s)
    // -------------------------------------------------------------------------

    // default 0-arguments constructor

    // Class methods
    // -------------------------------------------------------------------------

    // Instance methods
    // -------------------------------------------------------------------------

    public void test(final TestHarness harness) {
        harness.checkPoint("TestOfPublicKeyring");
        final GnuPublicKeyring kr = new GnuPublicKeyring();
        try {
            final Map attributes = new HashMap();
            attributes.put(IKeyring.KEYRING_DATA_IN, new ByteArrayInputStream(keyring));
            attributes.put(IKeyring.KEYRING_PASSWORD, "password".toCharArray());

            // IMPORTANT:
            // the following relies on an X509 cert provider; which we're not.
            // GNU Classpath (gLibj.zip) is; you need to add it to the java
            // launcher classpath, and include the GNU provider ("GNU") before
            // exercising this code; eg:
            //
            //    Security.addProvider(new gnu.java.security.provider.Gnu());
            //
//         kr.load(attributes);
//         harness.check(true, "load(...)");
//
//         harness.check(kr.containsCertificate(ALIAS), "containsCertificate(...)");
//
//         final List list = kr.get(ALIAS);
//         harness.check(list.size() == 1, "get(...).size() == 1");
//
//         final Certificate cert = kr.getCertificate(ALIAS);
//         harness.check(cert != null, "getCertificate(...) != null");
//
////         System.out.println("cert="+cert);
        } catch (Exception x) {
            harness.debug(x);
            harness.fail("TestOfPublicKeyring");
        }
    }
}
