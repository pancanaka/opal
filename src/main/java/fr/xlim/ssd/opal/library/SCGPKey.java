package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.utilities.Conversion;

/**
 * Define a non-derivable Secure Communication key for global Platform
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public class SCGPKey implements SCKey {

    private byte setVersion;

    private byte id;

    private KeyType type;

    private byte[] data;

    // TODO: test data key size and not null
    public SCGPKey(byte setVersion, byte id, KeyType type, byte[] data) {
        this.setVersion = setVersion;
        this.id = id;
        this.type = type;
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return this.data.clone();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getKeyId()
     */
    @Override
    public byte getId() {
        return this.id;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getSetVersion()
     */
    @Override
    public byte getSetVersion() {
        return this.setVersion;
    }

    @Override
    public KeyType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "SCGPKey(setVersion: " + getSetVersion() + ", id:" + getId()
                + ", type:" + getType() + ", data:"
                + Conversion.arrayToHex(getData()) + ")";
    }
}
