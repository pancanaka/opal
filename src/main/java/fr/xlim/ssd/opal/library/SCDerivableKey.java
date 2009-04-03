package fr.xlim.ssd.opal.library;

/**
 * @author dede
 *
 */
public interface SCDerivableKey extends SCKey {

    /**
     * @param keydata
     * @return
     */
    public SCGPKey[] deriveKey(byte[] keydata);
}
