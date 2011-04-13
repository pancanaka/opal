package fr.xlim.ssd.opal.library;

/**
 * Define the key type used.
 *
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 * @see SCKey
 */
public enum KeyType {

    /// DES in ECB mode
    DES_ECB((byte) 0x83),

    /// DES in CBC mode
    DES_CBC((byte) 0x84),

    /// AES in CBC mode
    AES_CBC((byte) 0x88),

    /// mother key used for derivation
    MOTHER_KEY((byte) 0x00);

    private byte value;

    private KeyType(byte val) {
        this.value = val;
    }

    public byte getValue() {
        return this.value;
    }
}