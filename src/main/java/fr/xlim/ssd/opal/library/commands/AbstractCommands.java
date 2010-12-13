package fr.xlim.ssd.opal.library.commands;

import javax.smartcardio.CardChannel;

/**
 * AbstractCommands defines a Card Channel structure used to communicate to the smartcard
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public abstract class AbstractCommands {

    /// Card Channel used to communicate with smart card
    protected CardChannel cc;

    /**
     * Class constructor
     */
    protected AbstractCommands() {
        this.cc = null;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#setCc(javax.smartcardio.CardChannel)
     */
    public final void setCc(CardChannel cc) {

        if (cc == null)
            throw new IllegalArgumentException("CardChannel must not be null");

        if (this.cc == null) {
            this.cc = cc;
        }
    }

    /**
     * Get the Card Channel
     *
     * @return the Card Channel
     */
    public final CardChannel getCc() {
        return this.cc;
    }

}
