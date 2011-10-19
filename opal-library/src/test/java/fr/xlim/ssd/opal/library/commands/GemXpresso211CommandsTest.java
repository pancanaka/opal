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
package fr.xlim.ssd.opal.library.commands;

import fr.xlim.ssd.opal.library.config.KeyType;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class GemXpresso211CommandsTest {

    @Test
    public void testGenerateSessionKeys() {
        GemXpresso211Commands commands = new GemXpresso211Commands();

        byte[] encData = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17
        };

        SCGPKey enc = new SCGPKey((byte) -1, (byte) -1, KeyType.DES_ECB, encData);

        byte[] macData = new byte[]{
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
        };

        SCGPKey mac = new SCGPKey((byte) -1, (byte) -1, KeyType.DES_ECB, macData);

        byte[] kekData = new byte[]{
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
        };

        SCGPKey kek = new SCGPKey((byte) -1, (byte) -1, KeyType.DES_ECB, kekData);

        commands.derivationData = new byte[]{
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,};

        commands.generateSessionKeys(enc, mac, kek);

        byte[] expectedSessEnc = {
                0x2D, 0x2D, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, 0x2D, 0x2D, (byte) 0xCA,
                (byte) 0xCA, 0x2D, (byte) 0xCA, (byte) 0xCA, 0x2D, (byte) 0xCA, (byte) 0xCA, 0x2D,
                0x2D, 0x2D, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, 0x2D, 0x2D, (byte) 0xCA
        };

        byte[] expectedSessMac = {
                0x2D, (byte) 0xCA, 0x2D, (byte) 0xCA, 0x2D, 0x2D, 0x2D, 0x2D, (byte) 0xCA,
                (byte) 0xCA, (byte) 0xCA, 0x2D, 0x2D, 0x2D, 0x2D, (byte) 0xCA, 0x2D, (byte) 0xCA,
                0x2D, (byte) 0xCA, 0x2D, 0x2D, 0x2D, 0x2D
        };

        byte[] expectedSessKek = {
                0x2D, 0x2D, (byte) 0xCA, 0x2D, 0x2D, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, 0x2D,
                0x2D, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA, 0x2D, (byte) 0xCA, (byte) 0xCA, 0x2D,
                0x2D, (byte) 0xCA, 0x2D, 0x2D, (byte) 0xCA, (byte) 0xCA, (byte) 0xCA
        };

        assertArrayEquals(expectedSessEnc, commands.sessEnc);
        assertArrayEquals(expectedSessMac, commands.sessMac);
        assertArrayEquals(expectedSessKek, commands.sessKek);
    }
}