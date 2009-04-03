package fr.xlim.ssd.opal.library;

/**
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public enum GetStatusResponseMode {

    OLD_TYPE((byte) 0x00),
    NEW_TYPE((byte) 0x02);
    private byte value;

    private GetStatusResponseMode(byte val) {
        this.value = val;
    }

    public byte getVal() {
        return this.value;
    }
}
