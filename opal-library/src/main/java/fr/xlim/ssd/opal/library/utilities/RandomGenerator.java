package fr.xlim.ssd.opal.library.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Random generator. Just a wrapper for SHA1PRNG secure random.
 *
 * @author Julien Iguchi-Cartigny
 */
public class RandomGenerator {

    private final static Logger logger = LoggerFactory.getLogger(RandomGenerator.class);

    // for testing purpose
    private static byte[] randomSequence;

    /**
     * This function is only available during test to set next random, and thus have tests which can be repeated.
     *
     * @param randomSequence the next random sequence
     * @throws UnsupportedOperationException if this function is not used during tests
     */
    public static void setRandomSequence(byte[] randomSequence) {
        logger.warn("setting a fixed value for random (this feature is only used" +
                "for testing purpose)");

        RandomGenerator.randomSequence = randomSequence;
    }

    /**
     * Generate a random byte array with a specific size.
     *
     * @param size the size of the random byte array
     * @return the random byte array
     * @throws UnsupportedOperationException if error when getting the instance of SecureRandom
     */
    public static byte[] generateRandom(int size) {

        // *************** FOR TEST **********************
        if (randomSequence != null) {

            if (randomSequence.length != size) {
                throw new IllegalArgumentException("Size is different from " +
                        "random sequence length");
            }

            logger.warn("Using a not random sequence: "
                    + Conversion.arrayToHex(randomSequence));

            return randomSequence;
        }

        // classical random
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            throw new UnsupportedOperationException("Cannot create random sequence: " + ex.getMessage());
        }

        return secureRandom.generateSeed(size);
    }
}
