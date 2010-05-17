package fr.xlim.ssd.opal.library;

/**
 * @author dede
 *
 */
public interface SCDerivableKey {

    public abstract SCGPKey[] deriveKey(byte[] keydata);
}
