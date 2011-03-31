package fr.xlim.ssd.opal.library.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This generator of random sequence is used only during test. It replaces
 * the original RandomGenerator to be able to replay sequence with predefined
 * random sequences. <b>IT MUST NOT BE USED IN PRODUCTION !!!</b> (as it is
 * "slightly" not random).
 * 
 * @author Julien Iguchi-Cartigny
 */
public class RandomGenerator {

    private final static Logger logger = LoggerFactory.getLogger(RandomGenerator.class);

    private static byte[] randomSequence;

    /**
     * Set the next random sequences returned by next calls 
     * of {@link #generateRandom(int)} until new call to this function
     *
     * @param randomSequence the next random sequences
     */
    public static void setRandomSequence(byte[] randomSequence) {
        RandomGenerator.randomSequence = randomSequence;
    }

    /**
     * Generate and return a byte array with random value with a specific size.
     * Theis specific implementation return the byte array set by the previous
     * call of {@link #setRandomSequence(randomSequence)}.
     *
     * @param size the byte array size
     * @return the random sequence as a byte array
     */
    public static byte[] generateRandom(int size) {
        if (randomSequence == null) {
            throw new IllegalStateException("Random sequence has not been set");
        }

        if (randomSequence.length != size) {
            throw new IllegalArgumentException("Size is different from " +
                    "random sequence length");
        }

        logger.warn("Using a not random sequence: "
                + Conversion.arrayToHex(randomSequence));

        return randomSequence;
    }
}