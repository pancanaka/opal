package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 *
 */
public class SCGemVisa extends SCAbstractGemVisa {

    public SCGemVisa(byte setVersion, byte[] data) {
        super(setVersion, data);
    }

    @Override
    public SCGPKey[] deriveKey(byte[] keydata) {

        if(keydata == null) {
            throw new IllegalArgumentException("keydata must not be null");
        }

        if(keydata.length != 10) {
            throw new IllegalArgumentException("keydata must be 10 bytes long");
        }

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
            throw new UnsupportedOperationException("Cannot find algorithm",e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("No such padding problem",e);
        } catch (InvalidKeyException e) {
            throw new UnsupportedOperationException("Key problem",e);
        } catch (IllegalBlockSizeException e) {
            throw new UnsupportedOperationException("Block size problem",e);
        } catch (BadPaddingException e) {
            throw new UnsupportedOperationException("Bad padding problem",e);
        }

        SCGPKey[] res = new SCGPKey[3];
        res[0] = new SCGPKey(setVersion, (byte) 1, KeyType.DES_ECB, staticEncKey);
        res[1] = new SCGPKey(setVersion, (byte) 2, KeyType.DES_ECB, staticMacKey);
        res[2] = new SCGPKey(setVersion, (byte) 3, KeyType.DES_ECB, staticKekKey);
        return res;
    }

    @Override
    public String toString() {
        return "SCGemVisa(setVersion: " + getSetVersion()
                + ", data:" + Conversion.arrayToHex(getData()) + ")";
    }
}