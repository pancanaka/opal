package fr.xlim.ssd.opal;

import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import junit.framework.TestCase;

public class TestCardConfigFactory extends TestCase {

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

    public void testSimpleAccess() throws CardConfigNotFoundException {

        for(String s : code) {
            CardConfig config = CardConfigFactory.getCardConfig(s);
            assertNotNull(config);
        }
    }
}
