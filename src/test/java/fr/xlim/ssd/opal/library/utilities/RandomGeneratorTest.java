package fr.xlim.ssd.opal.library.utilities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RandomGeneratorTest {

    @Before
    public void resetRandomGenerator() {
        RandomGenerator.setRandomSequence(null);
    }

    @Test(expected=IllegalStateException.class)
    public void testGenerateRandomFailedIfNotSetBefore() {
        RandomGenerator.generateRandom(10);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGenerateRandomFailedIfNotTheSameSize() {
        RandomGenerator.setRandomSequence(new byte[]{1,2,2});
        RandomGenerator.generateRandom(2);
    }

    @Test
    public void testGenerateRandom() {
        byte[] rs = new byte[]{1,2,2};
        RandomGenerator.setRandomSequence(rs);
        assertEquals(rs,RandomGenerator.generateRandom(3));
    }
}
