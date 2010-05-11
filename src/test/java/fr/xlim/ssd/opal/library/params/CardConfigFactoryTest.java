package fr.xlim.ssd.opal.library.params;

import org.junit.Test;
import static org.junit.Assert.*;

public class CardConfigFactoryTest {

    static private String[] code = {
        "GemXpresso211is",
        "GemXpresso211pkis",
        "GemXpresso211pkis",
        "GemCombiXpresso_Lite_R2_Std_Jcop30",
        "JCOP20",
        "JCOP30",
        "JCOP31",
        "OberthurCosmopolIC",
        "OberthurCosmoDual",
        "OberthurIdOneCosmo64RSA"
    };

    @Test
    public void testAllExistingCodeList() throws CardConfigNotFoundException {
        for (String s : code) {
            CardConfig config = CardConfigFactory.getCardConfig(s);
            assertNotNull(config);
        }
    }

    @Test(expected = CardConfigNotFoundException.class)
    public void testNotExistingCode() throws CardConfigNotFoundException {
        CardConfigFactory.getCardConfig("dummy");
    }
}
