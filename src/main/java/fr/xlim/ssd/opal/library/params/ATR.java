package fr.xlim.ssd.opal.library.params;

import sun.font.TrueTypeFont;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Contains information about an card ATR
 * @author Guillaume Bouffard
 */
public class ATR {

    /// The ATR value
    private byte[] value;

    /**
     * The card ATR class constructor
     * @param atr ATR value
     */
    public ATR (byte[] atr){
        this.setValue(atr);
    }

    /**
     * Get card ATR value
     *
     * @return Get ATR value
     */
    public byte[] getValue() {
        return this.value;
    }

    /**
     * Set a card ATR value
     *
     * @param newATR The new ATR value
     */
    public void setValue (byte[] newATR) {
        //TODO: check ATR size
        this.value = newATR;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj ) return true;

        if (!(obj instanceof ATR)) return false;

        return (Arrays.equals(((ATR) obj).getValue(), this.getValue()));
    }
}
