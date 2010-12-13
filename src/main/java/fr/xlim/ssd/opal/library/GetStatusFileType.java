package fr.xlim.ssd.opal.library;

/**
 * Used as GET STATUS parameters to select a subset of statuses to be included in the response message. Please see the
 * description of GET STATUS command in Section 9.4, global Platform Card Specification 2.1
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public enum GetStatusFileType {

    /// Issuer Security Domain only
    ISD((byte) 0x80),

    // Applications and Security Domain only
    APP_AND_SD((byte) 0x40),

    /// Executable Lod Files only
    LOAD_FILES((byte) 0x20),

    /// Executable Load Files and their Executable Modules only
    LOAD_FILES_AND_MODULES((byte) 0x10),

    /// Not documented but found working with some cards
    NOT_STANDARD_0x70((byte) 0x70),

    /// Not documented but found working with some cards
    NOT_STANDARD_0xF0((byte) 0xF0);

    /// The file type value
    private byte value;

    /**
     * Constructor for the file type
     *
     * @param value the file type value
     */
    private GetStatusFileType(byte value) {
        this.value = value;
    }

    /**
     * Get the file type value
     *
     * @return the file type value
     */
    public byte getValue() {
        return this.value;
    }
}
