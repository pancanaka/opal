/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a Java 6 library that implements Global Platform 2.x card
 * specification. OPAL is able to upload and manage Java Card applet lifecycle
 * on Java Card while dealing of the authentication of the user and encryption
 * of the communication between the user and the card. OPAL is also able
 * to manage different implementations of the specification via a pluggable
 * interface.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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

            return randomSequence.clone();
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
