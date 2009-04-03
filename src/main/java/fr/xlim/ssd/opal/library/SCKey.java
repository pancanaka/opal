package fr.xlim.ssd.opal.library;

/**
 * @author dede
 *
 */
public interface SCKey {

    /**
     * @return
     */
    public byte[] getKeyData();

    /**
     * @return
     */
    public byte getSetVersion();

    /**
     * @return
     */
    public byte getKeyId();

    /**
     * @return
     */
    public KeyType getKeyType();
}
