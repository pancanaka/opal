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

/**
 * Define the security level for the communication.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @author David Pequegnot
 * @author Julie Rispal
 */
public enum SecLevel {

    /// No encryption or authentication
    NO_SECURITY_LEVEL((byte) 0x00),

    /// MAC Authentication of each APDU
    C_MAC((byte) 0x01),

    /// Encryption and MAC authentication of each APDU
    C_ENC_AND_MAC((byte) 0x03),

    /// implemented, Not tested
    R_MAC((byte) 0x10),

    /// implemented, Not tested
    C_MAC_AND_R_MAC((byte) 0x11),

    /// implemented, Not tested
    C_ENC_AND_C_MAC_AND_R_MAC((byte) 0x13),

    // implemented, Not tested
    C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC((byte) 0x33);

    private byte value;

    private SecLevel(byte val) {
        this.value = val;
    }

    public byte getVal() {
        return this.value;
    }
}