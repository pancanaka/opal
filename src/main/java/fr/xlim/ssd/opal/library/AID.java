package fr.xlim.ssd.opal.library;

public class AID {

    private byte[] value;

    public AID(byte[] value) {

        if(value == null) {
            throw new IllegalArgumentException("value must be not null");
        }

        if(value.length < 5 || value.length > 16) {
            throw new IllegalArgumentException("value array length must be between 5 and 16");
        }

        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }
}
