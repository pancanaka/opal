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
public class SCGemVisa2 implements SCDerivableKey {

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
    public SCGemVisa2(byte keySetVersion, byte[] keyData) {
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

        // divDataStaticEnc = CC CC SN SN SN SN 'F0' '01' CC CC SN SN SN SN '0F' '01'
        // where : (CC CC)       == last two bytes of the card manager AID (2 first bytes of the initialize update response)
        //         (SN SN SN SN) == IC Serial Number
        System.arraycopy(keydata, 0, divDataStaticEnc, 0, 2);
        System.arraycopy(keydata, 4, divDataStaticEnc, 2, 4);
        divDataStaticEnc[6] = (byte) 0xF0;
        divDataStaticEnc[7] = (byte) 0x01;
        System.arraycopy(keydata, 0, divDataStaticEnc, 8, 2);
        System.arraycopy(keydata, 4, divDataStaticEnc, 10, 4);
        divDataStaticEnc[14] = (byte) 0x0F;
        divDataStaticEnc[15] = (byte) 0x01;

        // divDataStaticMac = CC CC SN SN SN SN 'F0' '02' CC CC SN SN SN SN '0F' '02'
        // where : (CC CC)       == last two bytes of the card manager AID (2 first bytes of the initialize update response)
        //         (SN SN SN SN) == IC Serial Number
        System.arraycopy(keydata, 0, divDataStaticMac, 0, 2);
        System.arraycopy(keydata, 4, divDataStaticMac, 2, 4);
        divDataStaticMac[6] = (byte) 0xF0;
        divDataStaticMac[7] = (byte) 0x02;
        System.arraycopy(keydata, 0, divDataStaticMac, 8, 2);
        System.arraycopy(keydata, 4, divDataStaticMac, 10, 4);
        divDataStaticMac[14] = (byte) 0x0F;
        divDataStaticMac[15] = (byte) 0x02;

        // divDataStaticKek = CC CC SN SN SN SN 'F0' '03' CC CC SN SN SN SN '0F' '03'
        // where : (CC CC)       == last two bytes of the card manager AID (2 first bytes of the initialize update response)
        //         (SN SN SN SN) == IC Serial Number
        System.arraycopy(keydata, 0, divDataStaticKek, 0, 2);
        System.arraycopy(keydata, 4, divDataStaticKek, 2, 4);
        divDataStaticKek[6] = (byte) 0xF0;
        divDataStaticKek[7] = (byte) 0x03;
        System.arraycopy(keydata, 0, divDataStaticKek, 8, 2);
        System.arraycopy(keydata, 4, divDataStaticKek, 10, 4);
        divDataStaticKek[14] = (byte) 0x0F;
        divDataStaticKek[15] = (byte) 0x03;

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