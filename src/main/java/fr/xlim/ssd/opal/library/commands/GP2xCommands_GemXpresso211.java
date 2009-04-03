package fr.xlim.ssd.opal.library.commands;

import fr.xlim.ssd.opal.library.CommandsProvider;
import fr.xlim.ssd.opal.library.SCGPKey;

/**
 * @author dede
 *
 */
public class GP2xCommands_GemXpresso211 extends GP2xCommands {

    static {
        CommandsProvider.register(new GP2xCommands_GemXpresso211());
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.GP2xCommands#generateSessionKeys(fr.xlim.ssd.opal.SCGPKey, fr.xlim.ssd.opal.SCGPKey, fr.xlim.ssd.opal.SCGPKey)
     */
    @Override
    protected void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac,
            SCGPKey staticKkek) {
        super.generateSessionKeys(staticKenc, staticKmac, staticKkek);
        this.extraStep();
    }

    /**
     *
     */
    private void extraStep() {
        for (int i = 0; i < this.sessEnc.length; i++) {
            if (this.sessEnc[i] % 2 == (byte) 0x00) {
                this.sessEnc[i] = (byte) 0xCA;
            } else {
                this.sessEnc[i] = (byte) 0x2D;
            }
        }
        for (int i = 0; i < this.sessMac.length; i++) {
            if (this.sessMac[i] % 2 == (byte) 0x00) {
                this.sessMac[i] = (byte) 0xCA;
            } else {
                this.sessMac[i] = (byte) 0x2D;
            }
        }

        for (int i = 0; i < this.sessKek.length; i++) {
            if (this.sessKek[i] % 2 == (byte) 0x00) {
                this.sessKek[i] = (byte) 0xCA;
            } else {
                this.sessKek[i] = (byte) 0x2D;
            }
        }
    }
}
