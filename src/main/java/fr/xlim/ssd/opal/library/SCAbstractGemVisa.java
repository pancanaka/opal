package fr.xlim.ssd.opal.library;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract class to define common methods between the two version of Visa Global Platform implementations.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public abstract class SCAbstractGemVisa implements SCKey, SCDerivableKey {

    protected byte setVersion;

    protected byte[] data;

    public SCAbstractGemVisa(byte setVersion, byte[] data) {
        this.setVersion = setVersion;

        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        if (data.length != 24) {
            throw new IllegalArgumentException("data must be 24 bytes long");
        }

        this.data = data;
    }

    @Override
    public SCGPKey[] deriveKey(byte[] keydata) {

        if (keydata == null) {
            throw new IllegalArgumentException("keydata must not be null");
        }

        if (keydata.length != 10) {
            throw new IllegalArgumentException("keydata must be 10 bytes long");
        }

        byte[] divDataStaticEnc = getDivDataStaticEnc(keydata);
        byte[] divDataStaticMac = getDivDataStaticMac(keydata);
        byte[] divDataStaticKek = getDivDataStaticKek(keydata);

        byte[] staticEncKey = new byte[24];
        byte[] staticMacKey = new byte[24];
        byte[] staticKekKey = new byte[24];

        try {
            Cipher myCipher;
            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.data, "DESede"));
            byte[] sEncKey = myCipher.doFinal(divDataStaticEnc);
            System.arraycopy(sEncKey, 0, staticEncKey, 0, 16);
            System.arraycopy(sEncKey, 0, staticEncKey, 16, 8);

            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.data, "DESede"));
            byte[] sMacKey = myCipher.doFinal(divDataStaticMac);
            System.arraycopy(sMacKey, 0, staticMacKey, 0, 16);
            System.arraycopy(sMacKey, 0, staticMacKey, 16, 8);

            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.data, "DESede"));
            byte[] sKekKey = myCipher.doFinal(divDataStaticKek);
            System.arraycopy(sKekKey, 0, staticKekKey, 0, 16);
            System.arraycopy(sKekKey, 0, staticKekKey, 16, 8);

        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Cannot find algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("No such padding problem", e);
        } catch (InvalidKeyException e) {
            throw new UnsupportedOperationException("Key problem", e);
        } catch (IllegalBlockSizeException e) {
            throw new UnsupportedOperationException("Block size problem", e);
        } catch (BadPaddingException e) {
            throw new UnsupportedOperationException("Bad padding problem", e);
        }

        SCGPKey[] res = new SCGPKey[3];
        res[0] = new SCGPKey(setVersion, (byte) 1, KeyType.DES_ECB, staticEncKey);
        res[1] = new SCGPKey(setVersion, (byte) 2, KeyType.DES_ECB, staticMacKey);
        res[2] = new SCGPKey(setVersion, (byte) 3, KeyType.DES_ECB, staticKekKey);
        return res;
    }

    protected abstract byte[] getDivDataStaticEnc(byte[] keydata);

    protected abstract byte[] getDivDataStaticMac(byte[] keydata);

    protected abstract byte[] getDivDataStaticKek(byte[] keydata);

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
