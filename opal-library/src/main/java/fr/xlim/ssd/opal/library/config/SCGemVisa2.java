package fr.xlim.ssd.opal.library.config;

import fr.xlim.ssd.opal.library.utilities.Conversion;

/**
 * Implementation of the mechanism of key derivation for Visa 2
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class SCGemVisa2 extends SCAbstractGemVisa {

    public SCGemVisa2(byte setVersion, byte[] data) {
        super(setVersion, data);
    }

    @Override
    public String toString() {
        return "SCGemVisa2(setVersion: " + getSetVersion()
                + ", data:" + Conversion.arrayToHex(getData()) + ")";
    }

    @Override
    protected byte[] getDivDataStaticEnc(byte[] keydata) {

        byte[] divDataStaticEnc = new byte[16];

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

        return divDataStaticEnc;
    }

    @Override
    protected byte[] getDivDataStaticMac(byte[] keydata) {

        byte[] divDataStaticMac = new byte[16];

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

        return divDataStaticMac;
    }

    @Override
    protected byte[] getDivDataStaticKek(byte[] keydata) {

        byte[] divDataStaticKek = new byte[16];

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

        return divDataStaticKek;
    }
}
