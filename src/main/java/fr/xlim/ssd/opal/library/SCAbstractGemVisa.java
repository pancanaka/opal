package fr.xlim.ssd.opal.library;

public abstract class SCAbstractGemVisa implements SCKey,SCDerivableKey {

    protected byte setVersion;

    protected byte[] data;

    public SCAbstractGemVisa(byte setVersion, byte[] data) {
        this.setVersion = setVersion;

        if(data == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        if(data.length != 24) {
            throw new IllegalArgumentException("data must be 24 bytes long");
        }

        this.data = data;
    }

    @Override
    public abstract SCGPKey[] deriveKey(byte[] keydata);

    @Override
    public byte[] getData() {
        return this.data.clone();
    }

    @Override
    public byte getSetVersion() {
        return this.setVersion;
    }

    @Override
    public byte getId() {
        return 1;
    }

    @Override
    public KeyType getType() {
        return KeyType.MOTHER_KEY;
    }
}
