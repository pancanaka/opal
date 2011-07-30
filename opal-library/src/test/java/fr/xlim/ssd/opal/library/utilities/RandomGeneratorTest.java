package fr.xlim.ssd.opal.library.utilities;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RandomGeneratorTest {

    @Before
    public void resetRandomGenerator() {
        RandomGenerator.setRandomSequence(null);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGenerateRandomForTestFailedIfNotTheSameSize() {
        RandomGenerator.setRandomSequence(new byte[]{1, 2, 2});
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Size is different from "
                + "random sequence length");
        RandomGenerator.generateRandom(2);
    }

    @Test
    public void testGenerateRandomForTest() {
        byte[] rs = new byte[]{1, 2, 2};
        RandomGenerator.setRandomSequence(rs);
        assertEquals(rs, RandomGenerator.generateRandom(3));
    }

    @Test
    public void testGenerateRandom() {
        byte[] t = RandomGenerator.generateRandom(3);
        assertNotNull(t);
        assertEquals(3, t.length);
    }

}
