package fr.xlim.ssd.opal.library.config;

/**
 * Define the fact that the key is derivable.
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public interface SCDerivableKey {

    /**
     * Compute a set of key based on the mother key and the keydata (usually a random number sends by the other part).
     * It returns an array of 3 keys: the encryption key, the MAC key and the data encryption key.
     *
     * @param keydata data used to derivate mother key
     * @return an array of 3 keys (enc, mac, kek)
     */
    public abstract SCGPKey[] deriveKey(byte[] keydata);
}
