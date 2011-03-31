package fr.xlim.ssd.opal.library;

/**
 * Represent an Application ID (AID)
 *
 * @author Julien Iguchi-Cartigny
 */
public class AID {

    /// the AID value
    private byte[] value;

    /**
     * Create an AID
     *
     * @param value the AID value
     * @throws IllegalArgumentException if value is null
     * @throws IllegalArgumentException if value length is < 5 or > 16
     */
    public AID(byte[] value) {

        if (value == null) {
            throw new IllegalArgumentException("value must be not null");
        }

        if (value.length < 5 || value.length > 16) {
            throw new IllegalArgumentException("value array length must be between 5 and 16");
        }

        this.value = value;
    }

    /**
     * Return the AID byte array
     *
     * @return the AID byte array
     */
    public byte[] getValue() {
        return value;
    }
}
