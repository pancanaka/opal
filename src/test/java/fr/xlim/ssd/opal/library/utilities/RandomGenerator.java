package fr.xlim.ssd.opal.library.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomGenerator {

    private final static Logger logger = LoggerFactory.getLogger(RandomGenerator.class);
    private static byte[] randomSequence;

    public static void setRandomSequence(byte[] randomSequence) {
        RandomGenerator.randomSequence = randomSequence;
    }

    public static byte[] generateRandom(int size) {
        if (randomSequence == null) {
            throw new IllegalStateException("randomSequence has not been set");
        }

        if (randomSequence.length != size) {
            throw new IllegalArgumentException("size is different from randomSequence length");
        }

        logger.warn("Using a not random sequence: "
                + Conversion.arrayToHex(randomSequence));

        return randomSequence;
    }
}
