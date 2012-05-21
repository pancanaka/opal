package fr.xlim.ssd.opal.library.commands.scp;

import fr.xlim.ssd.opal.library.config.SCGPKey;

public class GemXpresso211SCP extends SCPImplementation {

    @Override
    public void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac,
                                    SCGPKey staticKkek) {
        super.generateSessionKeys(staticKenc, staticKmac, staticKkek);
        this.extraStep();
    }

    /**
     * Special step after Generate Session Keys.
     */
    private void extraStep() {
        for (int i = 0; i < getSessEnc().length; i++) {
            if (getSessEnc()[i] % 2 == (byte) 0x00) {
                getSessEnc()[i] = (byte) 0xCA;
            } else {
                getSessEnc()[i] = (byte) 0x2D;
            }
        }
        for (int i = 0; i < getSessMac().length; i++) {
            if (getSessMac()[i] % 2 == (byte) 0x00) {
                getSessMac()[i] = (byte) 0xCA;
            } else {
                getSessMac()[i] = (byte) 0x2D;
            }
        }

        for (int i = 0; i < getSessKek().length; i++) {
            if (getSessKek()[i] % 2 == (byte) 0x00) {
                getSessKek()[i] = (byte) 0xCA;
            } else {
                getSessKek()[i] = (byte) 0x2D;
            }
        }
    }
}
