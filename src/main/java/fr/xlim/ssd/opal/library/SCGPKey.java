package fr.xlim.ssd.opal.library;

/**
 * @author dede
 *
 */
public class SCGPKey implements SCKey {

    /**
     * 
     */
    private byte keySetVersion;
    /**
     *
     */
    private byte keyId;
    /**
     *
     */
    private KeyType keyType;
    /**
     *
     */
    private byte[] keyData;

    /**
     * @param keySetVersion
     * @param keyId
     * @param keyType
     * @param keyData
     */
    public SCGPKey(byte keySetVersion, byte keyId, KeyType keyType, byte[] keyData) {
        this.keySetVersion = keySetVersion;
        this.keyId = keyId;
        this.keyType = keyType;
        this.keyData = keyData;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getKeyData()
     */
    @Override
    public byte[] getKeyData() {
        return this.keyData.clone();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getKeyId()
     */
    @Override
    public byte getKeyId() {
        return this.keyId;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getSetVersion()
     */
    @Override
    public byte getSetVersion() {
        return this.keySetVersion;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getKeyType()
     */
    @Override
    public KeyType getKeyType() {
        return this.keyType;
    }
}
