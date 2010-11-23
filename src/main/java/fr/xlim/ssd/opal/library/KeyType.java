package fr.xlim.ssd.opal.library;

/**
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public enum KeyType {

    DES_ECB((byte) 0x83),   // DES in ECB mode
    DES_CBC((byte) 0x84),   // DES in CBC mode
    MOTHER_KEY((byte) 0x00);
    private byte value;

    private KeyType(byte val) {
        this.value = val;
    }

    public byte getVal() {
        return this.value;
    }
}
