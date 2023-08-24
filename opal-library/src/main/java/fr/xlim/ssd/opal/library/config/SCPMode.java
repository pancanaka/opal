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

/**
 * Define the SCP protocol used. Please see the description Secure Channel Protocols 01 and 02 in Section D and E,
 * global Platform Card Specification 2.1.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @author Guillaume Bouffard
 * @author Jean Dubreuil
 */

public enum SCPMode {

    /// used when SCP used by the card is not know (before exchanges with the card)
    SCP_UNDEFINED(0, 0),

    /// Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, no ICV encryption, 3 Secure Channel Keys
    SCP_01_05(1, 0x05),

    /// Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, ICV encryption, 3 Secure Channel Keys
    SCP_01_15(1, 0x15),

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero, no ICV encryption, 1 Secure Channel base key
     *
     * TODO: implemented, Not tested
     */
    SCP_02_04(2, 0x04),

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero, no ICV encryption, 3 Secure Channel keys
     *
     * TODO: implemented, Not tested
     */
    SCP_02_05(2, 0x05),

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID, no ICV encryption, 1 Secure Channel base key
     *
     * TODO: implemented, Not tested
     */
    SCP_02_0A(2, 0x0A),

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID, no ICV encryption, 3 Secure Channel keys
     *
     * TODO: implemented, Not tested
     */
    SCP_02_0B(2, 0x0B),

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero, ICV encryption for C_MAC, 1 Secure Channel base key
     *
     * TODO: implemented, Not tested
     */
    SCP_02_14(2, 0x14),

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero, ICV encryption for C_MAC, 3 Secure Channel keys
     *)
     */
    SCP_02_15(2, 0x15),

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID, ICV encryption for C_MAC session, 1 Secure
     * Channel base key
     *
     * TODO: implemented, Not tested
     */
    SCP_02_1A(2, 0x1A),

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID, ICV encryption for C_MAC session, 3 Secure
     * Channel keys
     *
     * TODO: implemented, Not tested
     */
    SCP_02_1B(2, 0x1B),

    /*
     * Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, ICV encryption for C-MAC session, 3 Secure
     * Channel Keys, well-known pseudo-random algorithm (card challenge)
     *
     * TODO: implemented, Not tested
     */
    SCP_02_55(2, 0x55),

    /*
     * Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, no ICV encryption, 3 Secure
     * Channel Keys, well-known pseudo-random algorithm (card challenge)
     *
     * TODO: implemented, Not tested
     */
    SCP_02_45(2, 0x45),


    /*
     * Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, no ICV encryption, 1 Secure
     * Channel Keys, well-known pseudo-random algorithm (card challenge)
     *
     * TODO: implemented, Not tested
     */
    SCP_02_54(2, 0x54),

    /// TODO: Not implemented
    SCP_10(10, 0),

    /*
    * Random card challenge, no R-MAC support and no R-ENCRYPTION support
    * TODO: implemented, Not tested
    */
    SCP_03_00(3, 0x00),

    /*
    * Random card challenge, R-MAC support and no R-ENCRYPTION support
    * TODO: implemented, Not tested
    */
    SCP_03_20(3, 0x20),
    /*
     * Random card challenge, R-MAC support and R-ENCRYPTION support
     * TODO: implemented, Not tested
     */
    SCP_03_60(3, 0x60);
    
    // Enumerate value
    private final int value;
    
    /**
     * Link a value to an enumerate value.
     *
     * @param scp protocol number (1, 2, 3, 10).
     * @param iParam the i parameter indicating operations supported.
     */
    private SCPMode(int scp, int iParam) {
        this.value = (((scp & 0xFF) << 8) | (iParam & 0xFF));
    }
    
    /**
     * Get enumerate value.
     *
     * @return enumerate value.
     */
    public int getValue() {
        return this.value;
    }
    public int getProtocolNumber() {
        return (value >> 8) & 0xFF;
    }
    public int getIParameter() {
        return value & 0xFF;
    }
}
