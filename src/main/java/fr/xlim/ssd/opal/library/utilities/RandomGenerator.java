package fr.xlim.ssd.opal.library.utilities;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomGenerator {

    private final static Logger logger = LoggerFactory.getLogger(RandomGenerator.class);

    public static byte[] generateRandom(int size) {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Cannot instantiate random generator",ex);
            throw new UnsupportedOperationException("Cannot create random sequence");

        }
        
        return secureRandom.generateSeed(size);
    }

}
