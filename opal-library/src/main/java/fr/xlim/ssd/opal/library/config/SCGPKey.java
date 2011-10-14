package fr.xlim.ssd.opal.library.config;

import fr.xlim.ssd.opal.library.utilities.Conversion;

/**
 * Define a non-derivable Secure Communication key for global Platform
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public class SCGPKey implements SCKey {

    /// key version
    private byte setVersion;

    /// key id
    private byte id;

    /// key type
    private KeyType type;

    /// key value
    private byte[] data;


    /**
     * Constructor for the SCGPKey
     *
     * @param setVersion the key version
     * @param id         the key id
     * @param type       the key type
     * @param data       the key value
     */
    public SCGPKey(byte setVersion, byte id, KeyType type, byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data must be not null");
        }

        // TODO: test data key size

        this.setVersion = setVersion;
        this.id = id;
        this.type = type;
        this.data = data;
    }



    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getData()
     */
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


    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getType()
     */
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
