package fr.xlim.ssd.opal.library;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author dede
 *
 */
public class SCGemVisa implements SCDerivableKey {

    /**
     *
     */
    private byte keySetVersion;
    /**
     *
     */
    private byte[] keyData;

    /**
     * @param keySetVersion
     * @param keyData
     */
    public SCGemVisa(byte keySetVersion, byte[] keyData) {
        this.keySetVersion = keySetVersion;
        this.keyData = keyData;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCDerivableKey#deriveKey(byte[])
     */
    @Override
    public SCGPKey[] deriveKey(byte[] keydata) {
        byte[] divDataStaticEnc = new byte[16];
        byte[] divDataStaticMac = new byte[16];
        byte[] divDataStaticKek = new byte[16];

        byte[] staticEncKey = new byte[24];
        byte[] staticMacKey = new byte[24];
        byte[] staticKekKey = new byte[24];

        // divDataStaticEnc = 'FF' 'FF' SN SN SN SN SN SN SN SN '01' '00' '00' '00' '00' '00'
        // where : SN SN SN SN SN SN SN SN) == Card Serial Number (last 8 bytes of the key diversification data)
        divDataStaticEnc[0] = (byte) 0xFF;
        divDataStaticEnc[1] = (byte) 0xFF;
        System.arraycopy(keydata, 2, divDataStaticEnc, 2, 8);
        divDataStaticEnc[10] = (byte) 0x01;
        divDataStaticEnc[11] = (byte) 0x00;
        divDataStaticEnc[12] = (byte) 0x00;
        divDataStaticEnc[13] = (byte) 0x00;
        divDataStaticEnc[14] = (byte) 0x00;
        divDataStaticEnc[15] = (byte) 0x00;

        // divDataStaticMac = '00' '00' SN SN SN SN SN SN SN SN '02' '00' '00' '00' '00' '00'
        // where : SN SN SN SN SN SN SN SN) == Card Serial Number (last 8 bytes of the key diversification data)
        divDataStaticMac[0] = (byte) 0x00;
        divDataStaticMac[1] = (byte) 0x00;
        System.arraycopy(keydata, 2, divDataStaticMac, 2, 8);
        divDataStaticMac[10] = (byte) 0x02;
        divDataStaticMac[11] = (byte) 0x00;
        divDataStaticMac[12] = (byte) 0x00;
        divDataStaticMac[13] = (byte) 0x00;
        divDataStaticMac[14] = (byte) 0x00;
        divDataStaticMac[15] = (byte) 0x00;

        // divDataStaticKek = 'F0' 'F0' SN SN SN SN SN SN SN SN '03' '00' '00' '00' '00' '00'
        // where : SN SN SN SN SN SN SN SN) == Card Serial Number (last 8 bytes of the key diversification data)
        divDataStaticKek[0] = (byte) 0xF0;
        divDataStaticKek[1] = (byte) 0xF0;
        System.arraycopy(keydata, 2, divDataStaticKek, 2, 8);
        divDataStaticKek[10] = (byte) 0x03;
        divDataStaticKek[11] = (byte) 0x00;
        divDataStaticKek[12] = (byte) 0x00;
        divDataStaticKek[13] = (byte) 0x00;
        divDataStaticKek[14] = (byte) 0x00;
        divDataStaticKek[15] = (byte) 0x00;

        try {
            Cipher myCipher;
            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.keyData, "DESede"));
            byte[] sEncKey = myCipher.doFinal(divDataStaticEnc);
            System.arraycopy(sEncKey, 0, staticEncKey, 0, 16);
            System.arraycopy(sEncKey, 0, staticEncKey, 16, 8);

            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.keyData, "DESede"));
            byte[] sMacKey = myCipher.doFinal(divDataStaticMac);
            System.arraycopy(sMacKey, 0, staticMacKey, 0, 16);
            System.arraycopy(sMacKey, 0, staticMacKey, 16, 8);

            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.keyData, "DESede"));
            byte[] sKekKey = myCipher.doFinal(divDataStaticMac);
            System.arraycopy(sKekKey, 0, staticKekKey, 0, 16);
            System.arraycopy(sKekKey, 0, staticKekKey, 16, 8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.exit(1);
        }
        SCGPKey[] res = new SCGPKey[3];
        res[0] = new SCGPKey(keySetVersion, (byte) 1, KeyType.DES_ECB, staticEncKey);
        res[1] = new SCGPKey(keySetVersion, (byte) 2, KeyType.DES_ECB, staticMacKey);
        res[2] = new SCGPKey(keySetVersion, (byte) 3, KeyType.DES_ECB, staticKekKey);
        return res;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getKeyData()
     */
    @Override
    public byte[] getKeyData() {
        return this.keyData.clone();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getSetVersion()
     */
    @Override
    public byte getSetVersion() {
        return this.keySetVersion;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getKeyId()
     */
    @Override
    public byte getKeyId() {
        return 1;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.SCKey#getKeyType()
     */
    @Override
    public KeyType getKeyType() {
        return KeyType.MOTHER_KEY;
    }
}
