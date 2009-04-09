package fr.xlim.ssd.opal.library;

/**
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 * @author David Pequegnot, Julie Rispal
 */
public enum SecLevel {

    NO_SECURITY_LEVEL((byte) 0x00),
    C_MAC((byte) 0x01),
    C_ENC_AND_MAC((byte) 0x03),
    
    // Added by the OPALGUI Team but not yet implemented
    R_MAC((byte) 0x07),
    C_MAC_AND_R_MAC((byte) 0x0F),
    ENC_AND_C_MAC_AND_R_MAC((byte) 0x1F);

    private byte value;

    private SecLevel(byte val) {
        this.value = val;
    }

    public byte getVal() {
        return this.value;
    }
}