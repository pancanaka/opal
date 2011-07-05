package fr.xlim.ssd.opal.library.utilities;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

public class RandomGeneratorTest {

    @Before
    public void resetRandomGenerator() {
        RandomGenerator.setRandomSequence(null);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGenerateRandomFailedIfNotSetBefore() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Random sequence has not been set");
        RandomGenerator.generateRandom(10);
    }

    @Test
    public void testGenerateRandomFailedIfNotTheSameSize() {
        RandomGenerator.setRandomSequence(new byte[]{1, 2, 2});
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Size is different from "
                + "random sequence length");
        RandomGenerator.generateRandom(2);
    }

    @Test
    public void testGenerateRandom() {
        byte[] rs = new byte[]{1, 2, 2};
        RandomGenerator.setRandomSequence(rs);
        assertEquals(rs, RandomGenerator.generateRandom(3));
    }
}
