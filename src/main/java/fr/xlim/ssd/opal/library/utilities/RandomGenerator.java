package fr.xlim.ssd.opal.library.utilities;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Random generator. Just a wrapper for SHA1PRNG secure random.
 *
 * @author Julien Iguchi-Cartigny
 */
public class RandomGenerator {

    /**
     * This function is only available during test to set next random, and thus have tests which can be repeated.
     *
     * @param randomSequence the next random sequence
     * @throws UnsupportedOperationException if this function is not used during tests
     */
    public static void setRandomSequence(byte[] randomSequence) {
        throw new UnsupportedOperationException("This function is only called during tests");
    }

    /**
     * Generate a random byte array with a specific size.
     *
     * @param size the size of the random byte array
     * @return the random byte array
     * @throws UnsupportedOperationException if error when getting the instance of SecureRandom
     */
    public static byte[] generateRandom(int size) {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            throw new UnsupportedOperationException("Cannot create random sequence: " + ex.getMessage());
        }

        return secureRandom.generateSeed(size);
    }
}
