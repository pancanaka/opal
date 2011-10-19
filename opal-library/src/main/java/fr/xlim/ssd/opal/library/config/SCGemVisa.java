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

import fr.xlim.ssd.opal.library.utilities.Conversion;

/**
 * Implementation of the mechanism of key derivation for Visa
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class SCGemVisa extends SCAbstractGemVisa {

    public SCGemVisa(byte setVersion, byte[] data) {
        super(setVersion, data);
    }

    @Override
    public String toString() {
        return "SCGemVisa(setVersion: " + getVersion()
                + ", data:" + Conversion.arrayToHex(getValue()) + ")";
    }

    @Override
    protected byte[] getDivDataStaticEnc(byte[] keydata) {
        byte[] divDataStaticEnc = new byte[16];

        // divDataStaticEnc = 'FF' 'FF' SN SN SN SN SN SN SN SN '01' '00' '00' '00' '00' '00'
        // where : SN SN SN SN SN SN SN SN) == Card Serial Number (last 8 bytes of the key diversification data)
        divDataStaticEnc[0] = (byte) 0xFF;
        divDataStaticEnc[1] = (byte) 0xFF;
        System.arraycopy(keydata, 2, divDataStaticEnc, 2, 8);
        divDataStaticEnc[10] = (byte) 0x01;
        divDataStaticEnc[11] = (byte) 0x00;
        divDataStaticEnc[12] = (byte) 0x00;
        divDataStaticEnc[13] = (byte) 0x00;
        divDataStaticEnc[14] = (byte) 0x00;
        divDataStaticEnc[15] = (byte) 0x00;

        return divDataStaticEnc;
    }

    @Override
    protected byte[] getDivDataStaticMac(byte[] keydata) {

        byte[] divDataStaticMac = new byte[16];

        // divDataStaticMac = '00' '00' SN SN SN SN SN SN SN SN '02' '00' '00' '00' '00' '00'
        // where : SN SN SN SN SN SN SN SN) == Card Serial Number (last 8 bytes of the key diversification data)
        divDataStaticMac[0] = (byte) 0x00;
        divDataStaticMac[1] = (byte) 0x00;
        System.arraycopy(keydata, 2, divDataStaticMac, 2, 8);
        divDataStaticMac[10] = (byte) 0x02;
        divDataStaticMac[11] = (byte) 0x00;
        divDataStaticMac[12] = (byte) 0x00;
        divDataStaticMac[13] = (byte) 0x00;
        divDataStaticMac[14] = (byte) 0x00;
        divDataStaticMac[15] = (byte) 0x00;

        return divDataStaticMac;
    }

    @Override
    protected byte[] getDivDataStaticKek(byte[] keydata) {

        byte[] divDataStaticKek = new byte[16];

        // divDataStaticKek = 'F0' 'F0' SN SN SN SN SN SN SN SN '03' '00' '00' '00' '00' '00'
        // where : SN SN SN SN SN SN SN SN) == Card Serial Number (last 8 bytes of the key diversification data)
        divDataStaticKek[0] = (byte) 0xF0;
        divDataStaticKek[1] = (byte) 0xF0;
        System.arraycopy(keydata, 2, divDataStaticKek, 2, 8);
        divDataStaticKek[10] = (byte) 0x03;
        divDataStaticKek[11] = (byte) 0x00;
        divDataStaticKek[12] = (byte) 0x00;
        divDataStaticKek[13] = (byte) 0x00;
        divDataStaticKek[14] = (byte) 0x00;
        divDataStaticKek[15] = (byte) 0x00;

        return divDataStaticKek;
    }
}