package fr.xlim.ssd.opal.library;

/**
 * Used by GET STATUS to controls the number of consecutives GET STATUS command and incaites the format of the response
 * message. Please see the description of GET STATUS command in Section 9.4, global Platform Card Specification 2.1.
 *
 * TODO: create classes to format responses and extend various mode available
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public enum GetStatusResponseMode {

    /// Get first or all occurence(s) AND response data structure according to Table 9-22 and Table 9-23
    OLD_TYPE((byte) 0x00),

    /// Get first or all occurence(s) AND response data structure according to Table 9-24
    NEW_TYPE((byte) 0x02);

    private byte value;

    private GetStatusResponseMode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }
}
