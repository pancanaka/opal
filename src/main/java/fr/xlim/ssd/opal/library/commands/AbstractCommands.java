package fr.xlim.ssd.opal.library.commands;

import javax.smartcardio.CardChannel;

/**
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public abstract class AbstractCommands {

    /**
     *
     */
    protected CardChannel cc;

    /**
     *
     */
    protected AbstractCommands() {
        this.cc = null;
    }

    /**
     * @param cc
     */
    public final void setCc(CardChannel cc) {

        if(cc == null)
            throw new IllegalArgumentException("CardChannel must not be null");

        if (this.cc == null) {
            this.cc = cc;
        }
    }

    /**
     * @return
     */
    public final CardChannel getCc() {
        return this.cc;
    }

}