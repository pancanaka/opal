package fr.xlim.ssd.opal.library.utilities;

import java.io.IOException;

/**
 * TLV - Type Length Value.
 * <p/>
 * -0----1-2------3-3--------LENGTH-
 * | tag | LENGTH |     VALUE     |
 * ---------------------------------
 *
 * @author Guillaume Bouffard
 */
public class TLV {
    /// The tag of TLV
    private byte tag;
    /// The length of the TLV
    private byte length;
    /// The value of the TLV
    private byte[] value;

    /**
     * Creates a new TLV.
     *
     * @param data all data of TLV
     * @throws IOException if an error occurs
     */
    public TLV(byte[] data) throws IOException {
        setTLV(data);
    }

    /**
     * Creates a new TLV.
     *
     * @param value TLV value
     * @throws IOException if an error occurs
     * @apram type TLV tag
     */
    public TLV(byte tag, byte[] value) throws IOException {
        if (value.length > 0x00FF) {
            throw new IOException("Value is too long (" + value.length + ")");
        }

        this.tag = tag;
        this.length = (byte) value.length;
        this.value = value.clone();
    }

    /**
     * Set TLV with a array byte
     *
     * @param data TLV data
     * @throws IOException Invalid TLV length
     */
    public void setTLV(byte[] data) throws IOException {
        if ((data.length < 3) // Minimal size of a TLV
                || ((data[1] & 0xFF) != (data.length - 2))) { // Check Length value
            throw new IOException("Invalid TLV length");
        }

        this.tag = data[0];
        this.length = data[1];
        this.value = new byte[this.length];
        System.arraycopy(data, 2, this.value, 0, this.length);
    }

    /**
     * Get the value of tag.
     *
     * @return value of tag.
     */
    public int getTag() {
        return (int) (this.tag) & 0x00FF;
    }

    /**
     * Set the value of tag.
     *
     * @param tag Value to assign to tag.
     */
    public void setTag(byte tag) {
        this.tag = tag;
    }

    /**
     * Get the value of length.
     *
     * @return value of length.
     */
    public int getLength() {
        return this.length & 0xFF;
    }

    /**
     * Set the value of length.
     *
     * @param length Value to assign to length.
     */
    public void setLength(byte length) {
        this.length = length;
    }

    /**
     * Get the value of value.
     *
     * @return value of value.
     */
    public byte[] getValue() {
        return this.value;
    }

    /**
     * Set the value of value.
     *
     * @param value Value to assign to value.
     */
    public void setValue(byte[] value) {
        this.length = (byte) value.length;
        this.value = value;
    }

    /**
     * A byte array containing the representation of this TLV instance.
     *
     * @return A byte array containing the representation of this TLV instance
     */
    public byte[] toBinary() {
        byte[] ret = new byte[this.length + 2];

        ret[0] = (byte) (this.getTag() & 0xFF);
        ret[1] = (byte) (this.getLength() & 0xFF);
        System.arraycopy(this.value, 0, ret, 2, this.getLength());

        return ret.clone();
    }


    /**
     * Return a string representation of this TLV.
     *
     * @return the string representation of this TLV
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("TLV [ 0x");
        sb.append(Integer.toHexString(tag));
        sb.append(", ");
        sb.append(length);
        sb.append(", ");

        if (value == null) {
            sb.append("null");
        } else {
            sb.append(Conversion.arrayToHex(this.getValue()));
        }

        sb.append("]");

        return sb.toString();
    }
}
