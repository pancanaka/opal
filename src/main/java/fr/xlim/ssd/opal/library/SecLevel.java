package fr.xlim.ssd.opal.library;

/**
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public enum SecLevel {

    NO_SECURITY_LEVEL((byte) 0x00),
    C_MAC((byte) 0x01),
    C_ENC_AND_MAC((byte) 0x03);
    private byte value;

    private SecLevel(byte val) {
        this.value = val;
    }

    public byte getVal() {
        return this.value;
    }
}

