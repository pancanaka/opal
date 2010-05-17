package fr.xlim.ssd.opal.library;

/**
 * Define the security level for the communication.
 *
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 * @author David Pequegnot, Julie Rispal
 */
public enum SecLevel {

    /**
     * No encryption or authentication
     */
    NO_SECURITY_LEVEL((byte) 0x00),

    /**
     * MAC Authentication of each APDU
     */
    C_MAC((byte) 0x01),

    /**
     * Encryption and MAC authentication of each APDU
     */
    C_ENC_AND_MAC((byte) 0x03),

    /**
     * TODO: Not implemented
     */
    R_MAC((byte) 0x10),

    /**
     * TODO: Not implemented
     */
    C_MAC_AND_R_MAC((byte) 0x11),

    /**
     * TODO: Not implemented
     */
    C_ENC_AND_C_MAC_AND_R_MAC((byte) 0x13);

    private byte value;

    private SecLevel(byte val) {
        this.value = val;
    }

    public byte getVal() {
        return this.value;
    }
}