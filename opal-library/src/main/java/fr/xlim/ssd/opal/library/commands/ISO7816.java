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
 * ISO7816 Constant values
 *
 * @author Guillaume Bouffard
 */
public enum ISO7816 {

    /* APDU Offsets */

    /// APDU command CLA : ISO 7816 = 0x00
    CLA_ISO7816(0x00),

    /// APDU command INS : EXTERNAL AUTHENTICATE = 0x82
    INS_EXTERNAL_AUTHENTICATE(0x82),

    /// APDU command INS : SELECT = 0xA4
    INS_SELECT(0xA4),

    /// APDU header offset : CLA = 0
    OFFSET_CLA(0x00),

    /// APDU header offset : INS = 1
    OFFSET_INS(0x01),

    /// APDU header offset : P1 = 2
    OFFSET_P1(0x02),

    /// APDU header offset : P1 = 3
    OFFSET_P2(0x03),

    /// APDU header offset : LC = 4
    OFFSET_LC(0x04),

    /// APDU command data offset : CDATA = 5
    OFFSET_CDATA(0x05),

    /// Response status : Applet selection failed
    SW_NO_ERROR(0x9000),

    /// Response status : Response bytes remaining
    SW_BYTES_REMAINING_00(0x6100),

    /// Response status: More Data Available
    SW_MORE_DATA_AVAILABLE(0x6310),

    /// Response status: Wrong length
    SW_WRONG_LENGTH(0x6700),

    /// Response status: Security condition not satisfied
    SW_SECURITY_STATUS_NOT_SATISFIED(0x6982),

    /// Response status: File invalid
    SW_FILE_INVALID(0x6983),

    /// Response status: Data invalid
    SW_DATA_INVALID(0x6984),

    /// Response status: Conditions of use not satisfied
    SW_CONDITIONS_NOT_SATISFIED(0x6985),

    /// Response status: Command not allowed (no current EF)
    SW_COMMAND_NOT_ALLOWED(0x6986),

    /// Response status: Applet selection failed
    SW_APPLET_SELECT_FAILED(0x6999),

    /// Response status: Wrong data
    SW_WRONG_DATA(0x6A80),

    /// Response status: File not found
    SW_FUNC_NOT_SUPPORTED(0x6A81),

    /// Response status: Record not found
    SW_FILE_NOT_FOUND(0x6A82),

    /// Response status: Record not found
    SW_RECORD_NOT_FOUND(0x6A83),

    /// Response status: Incorrect parameters (P1,P2)
    SW_INCORRECT_P1P2(0x6A86),

    /// Response status: Reference Data Not Found
    SW_WRONG_REFERENCE_DATA_NOT_FOUND(0x6A80),

    /// Response status: Incorrect parameters (P1,P2)
    SW_WRONG_P1P2(0x6B00),

    /// Response status: Correct Expected Length (Le)
    SW_CORRECT_LENGTH_00(0x6C00),

    /// Response status: INS value not supported
    SW_INS_NOT_SUPPORTED(0x6D00),

    /// Response status: CLA value not supported
    SW_CLA_NOT_SUPPORTED(0x6E00),

    /// Response status: No precise diagnosis
    SW_UNKNOWN(0x6F00),

    /// Response status: Not enough memory space in the file
    SW_FILE_FULL(0x6A84);

    /// Enumerate value
    private int value;

    /**
     * Link a value to an enumerate value
     *
     * @param value enumerate value
     */
    private ISO7816(int value) {
        if ((value & 0xFF00) != 0) {
            this.value = value & 0xFFFF;
        } else {
            this.value = value & 0x00FF;
        }
    }

    /**
     * Get enumerate value
     *
     * @return enumerate value
     */
    public int getValue() {
        return this.value;
    }
}
