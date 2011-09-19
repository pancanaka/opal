package fr.xlim.ssd.opal.library.params;

import java.util.Arrays;

/**
 * Contains information about an card ATR
 *
 * @author Guillaume Bouffard
 */
public class ATR {

    /// The ATR value
    private byte[] value;

    /**
     * The card ATR class constructor
     *
     * @param atr ATR value
     */
    public ATR(byte[] atr) {
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
    public void setValue(byte[] newATR) {
        this.value = newATR.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ATR atr = (ATR) o;

        if (!Arrays.equals(value, atr.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
