/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a Java 6 library that implements Global Platform 2.x card
 * specification. OPAL is able to upload and manage Java Card applet lifecycle
 * on Java Card while dealing of the authentication of the user and encryption
 * of the communication between the user and the card. OPAL is also able
 * to manage different implementations of the specification via a pluggable
 * interface.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.library.config;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SCGemVisa2Test {

    private SCGemVisa2 scgv;

    @Before
    public void createSCGemVisa2Key() {
        byte[] key = {
                0x47, 0x45, 0x4D, 0x58, 0x50, 0x52, 0x45, 0x53, 0x53, 0x4F, 0x53,
                0x41, 0x4D, 0x50, 0x4C, 0x45, 0x47, 0x45, 0x4D, 0x58, 0x50, 0x52,
                0x45, 0x53
        };

        scgv = new SCGemVisa2((byte) 0x32, key);

        assertEquals(0x32, scgv.getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailedIfDataNull() {
        scgv = new SCGemVisa2((byte) 0x32, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailedIfDataNotCorrectSize() {
        scgv = new SCGemVisa2((byte) 0x32, new byte[12]);
    }

    @Test
    public void testDerivateKey() {

        byte[] data = {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A
        };

        SCGPKey[] keys = scgv.deriveKey(data);

        // TODO: check why mac and kek key are identical...

        byte[] encKey = {
                0x7C, (byte) 0xF0, (byte) 0xA3, 0x31, (byte) 0xEC, (byte) 0xB5, 0x3F,
                (byte) 0x81, (byte) 0xDD, (byte) 0xB8, 0x6F, (byte) 0x93, (byte) 0xF1,
                0x74, (byte) 0xDC, 0x0B, 0x7C, (byte) 0xF0, (byte) 0xA3, 0x31,
                (byte) 0xEC, (byte) 0xB5, 0x3F, (byte) 0x81
        };

        assertArrayEquals(encKey, keys[0].getValue());
        assertEquals((byte) 0x32, keys[0].getVersion());
        assertEquals((byte) 0x01, keys[0].getId());
        assertEquals(KeyType.DES_ECB, keys[0].getType());

        byte[] macKey = {
                0x47, (byte) 0xA5, (byte) 0x9C, (byte) 0xEF, 0x20, 0x48, (byte) 0x99,
                0x38, 0x7F, 0x2B, 0x58, 0x39, (byte) 0xE7, (byte) 0xF3, (byte) 0x84,
                (byte) 0xAA, 0x47, (byte) 0xA5, (byte) 0x9C, (byte) 0xEF, 0x20,
                0x48, (byte) 0x99, 0x38
        };

        assertArrayEquals(macKey, keys[1].getValue());
        assertEquals((byte) 0x32, keys[1].getVersion());
        assertEquals((byte) 0x02, keys[1].getId());
        assertEquals(KeyType.DES_ECB, keys[1].getType());

        byte[] kekKey = {
                0x10, 0x29, 0x5B, 0x35, 0x4B, 0x5F, 0x7D, 0x63, (byte) 0xD1, 0x18,
                0x14, 0x62, 0x1D, (byte) 0xBC, (byte) 0xA9, 0x7B, 0x10, 0x29, 0x5B,
                0x35, 0x4B, 0x5F, 0x7D, 0x63
        };

        assertArrayEquals(kekKey, keys[2].getValue());
        assertEquals((byte) 0x32, keys[2].getVersion());
        assertEquals((byte) 0x03, keys[2].getId());
        assertEquals(KeyType.DES_ECB, keys[2].getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDerivateKeyFailedIfDataNull() {
        scgv.deriveKey(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDerivateKeyFailedIfDataNotCorrectSize() {
        scgv.deriveKey(new byte[9]);
    }

    @Test
    public void testGetId() {
        assertEquals(1, scgv.getId());
    }

    @Test
    public void testType() {
        assertEquals(KeyType.MOTHER_KEY, scgv.getType());
    }
}
