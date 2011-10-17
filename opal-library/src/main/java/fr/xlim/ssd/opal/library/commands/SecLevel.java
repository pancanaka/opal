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
