package fr.xlim.ssd.opal.library;

/**
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public enum FileType {

    ISD((byte) 0x80),
    APP_AND_SD((byte) 0x40),
    LOAD_FILES((byte) 0x20),
    LOAD_FILES_AND_MODULES((byte) 0x10);
    private byte value;

    private FileType(byte val) {
        this.value = val;
    }

    public byte getVal() {
        return this.value;
    }
}
