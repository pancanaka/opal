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
package fr.xlim.ssd.opal.library.utilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConversionTest {

    @Test
    public void testArrayToHex() {
        byte[] tab = new byte[]{(byte) 0xAB, 0x07, 0x13, 0x56};
        String res = Conversion.arrayToHex(tab);
        assertEquals("AB 07 13 56 ", res);
    }

    @Test
    public void testEmptyArrayToHex() {
        byte[] tab = new byte[0];
        String res = Conversion.arrayToHex(tab);
        assertEquals("", res);
    }

    @Test
    public void testHexToArray() {
        String s = "AB 12 F7 65 23";
        byte[] res = Conversion.hexToArray(s);
        byte[] expected = new byte[]{(byte) 0xAB, 0x12, (byte) 0xF7, 0x65, 0x23};
        assertEquals(expected.length, res.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res[i]);
        }
    }

    @Test
    public void testEmptyHexToArray() {
        String s = "";
        byte[] res = Conversion.hexToArray(s);
        assertEquals(0, res.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToArrayFailedWithIllegalHexadecimalLetter() {
        String s = "AG";
        byte[] res = Conversion.hexToArray(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToArrayFailedWithOnlyOneLetter() {
        String s = "A";
        byte[] res = Conversion.hexToArray(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToArrayFailedWithOnlyThreeLetters() {
        String s = "A15";
        byte[] res = Conversion.hexToArray(s);
    }
}
