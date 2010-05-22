package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.utilities.Conversion;

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
    public String toString() {
        return "SCGemVisa(setVersion: " + getSetVersion()
                + ", data:" + Conversion.arrayToHex(getData()) + ")";
    }

    @Override
    protected byte[] getDivDataStaticEnc(byte[] keydata) {
        byte[] divDataStaticEnc = new byte[16];

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

        return divDataStaticEnc;
    }

    @Override
    protected byte[] getDivDataStaticMac(byte[] keydata) {

        byte[] divDataStaticMac = new byte[16];

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

        return divDataStaticMac;
    }

    @Override
    protected byte[] getDivDataStaticKek(byte[] keydata) {

        byte[] divDataStaticKek = new byte[16];

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

        return divDataStaticKek;
    }
}