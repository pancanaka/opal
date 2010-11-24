/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.commands.AbstractCommands;
import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.library.utilities.RandomGenerator;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.smartcardio.ResponseAPDU;

/**
 * Implementation Of SCP 02
 * @author Guillaume Bouffard
 */
public class SCP02 extends AbstractCommands {

    protected static final Logger logger = LoggerFactory.getLogger(SCP02.class);
    protected static final byte[] padding = Conversion.hexToArray("80 00 00 00 00 00 00 00");

    protected byte[] keyDivData      ;
    protected byte[] keyinfo         ;
    protected byte[] sequencecounter ;
    protected byte[] hostChallenge   ;
    protected byte[] cardchallenge   ;
    protected byte[] cardcryptogram  ;
    protected byte[] derivationData = Conversion.hexToArray("01 82 00 00 00 00 00 00 00 00 00 00 00 00 00 00" );
    protected byte[] icv;

    /* Keys */
    protected byte[] S_ENC ;
    protected byte[] S_MAC ;
    protected byte[] DEK   ;

    protected byte[] kSCEncSess ;
    protected byte[] kC_MAC         ;
    protected byte[] kR_MAC         ;
    protected byte[] SCDataEnc     ;

    protected SecurityDomain securityDomain ;
    protected CardConfig cardConfig ;
    protected SecLevel secMode;
    private byte[] hostCrypto;
    private SCPMode scp;

    public enum DESSessionKeys {
        C_MAC ((short) 0x0101),
        R_MAC ((short) 0x0102),
        SCEncSessKeys ((short) 0x0182),
        SCDataEnc   ((short) 0x0181);

        private short value;

        private DESSessionKeys(short value) {
            this.value = value;
        }

        public short getVal() {
            return this.value;
        }
    } ;

    public SCP02() {
        this.initIcv();
    }

     /**
     *
     */
    protected void initIcv() {
        this.icv = new byte[8];
        for (int i = 0; i < this.icv.length; i++) {
            this.icv[i] = (byte) 0x00;
        }
    }


    public ResponseAPDU initializeUpdate (SecurityDomain securityDomain, CardConfig cardConfig) throws CardException {

        /*
         * Mock Implementation:
         *
         * HostChallenge: 21 69 53 99 73 EC 89 3A
         * CardChallenge: CC 28 2B D8 31 DB
         * Card Calculated Card Cryptogram: 76 D1 5E 08 A0 2D 92 20
         * Derivation Data is 01 82 00 18 00 00 00 00 00 00 00 00 00 00 00 00
         * Host Cryptogram Data (to encrypt) 00 18 CC 28 2B D8 31 DB 21 69 53 99 73 EC 89 3A 80 00 00 00 00 00 00 00
         * Card Cryptogram Data (to encrypt for verification) 21 69 53 99 73 EC 89 3A 00 18 CC 28 2B D8 31 DB 80 00 00 00 00 00 00 00
         * S_ENC: DE B2 51 3D 40 5A 94 CA 19 62 6C 58 D5 19 DA 4B DE B2 51 3D 40 5A 94 CA
         * The Current session MAC key is 12 C0 D5 A7 67 2F 3E 68 E7 8A 88 08 2D 3C B1 64
         * The Current session DEK key is 88 0D 95 BA 90 44 E6 A9 19 52 18 FA BF 76 6A E1
         * Encrypted CardCryptoGram is 7E A7 C5 58 C4 8A 50 46 92 D2 4F FB 17 67 38 0E 76 D1 5E 08 A0 2D 92 20
         * Encrypted HostCryptoGram is 2E 4F 07 49 61 7F 66 A1 D8 39 55 9D 88 C4 57 5B B0 D4 6C 83 C2 33 CD CE
         *
         */

        this.securityDomain = securityDomain;
        this.cardConfig     = cardConfig;
        this.scp            = cardConfig.getScpMode();
        this.cc             = securityDomain.getCc();

        this.S_ENC = (this.cardConfig.getSCKeys()[0]).getData();
        this.S_MAC = (this.cardConfig.getSCKeys()[1]).getData();
        this.DEK   = (this.cardConfig.getSCKeys()[2]).getData();

        /* INIT UPDATE */

        /*
        this.hostChallenge        = Conversion.hexToArray ( "21 69 53 99 73 EC 89 3A" ) ;
        ResponseAPDU InitUpdateResp = new ResponseAPDU
                ( Conversion.hexToArray ( "00 00 81 58 03 12 20 91 38 27 FF 02 00 18 CC 28 2B D8 31 DB 76 D1 5E 08 A0 2D 92 20 90 00" ) );

        byte[] P = {cardConfig.getDefaultInitUpdateP1() , cardConfig.getDefaultInitUpdateP2()};

        logger.debug("INIT UPDATE Command "
                + "(-> 80 50 " + Conversion.arrayToHex( P ) + "08 " + Conversion.arrayToHex(hostChallenge) + ") "
                + "(<- " + Conversion.arrayToHex(InitUpdateResp.getBytes()) + ")");
         */
        this.hostChallenge = RandomGenerator.generateRandom(8);

        byte[] initUpdCmd = new byte[13];
        initUpdCmd[0] = (byte) 0x80;
        initUpdCmd[1] = (byte) 0x50;
        initUpdCmd[2] = cardConfig.getDefaultInitUpdateP1();
        initUpdCmd[3] = cardConfig.getDefaultInitUpdateP2();
        initUpdCmd[4] = (byte) this.hostChallenge.length;

        System.arraycopy(this.hostChallenge, 0, initUpdCmd, 5, this.hostChallenge.length);

        CommandAPDU cmdInitUpd = new CommandAPDU(initUpdCmd);
        ResponseAPDU resp = this.cc.transmit(cmdInitUpd);

        logger.debug("INIT UPDATE command "
                + "(-> " + Conversion.arrayToHex(cmdInitUpd.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        this.keyDivData      = new byte[10];
        this.keyinfo         = new byte[ 2];
        this.sequencecounter = new byte[ 2];
        this.cardchallenge   = new byte[ 6];
        this.cardcryptogram  = new byte[ 8];

        System.arraycopy ( resp.getBytes() ,  0 , this.keyDivData      , 0 , 10 );
        System.arraycopy ( resp.getBytes() , 10 , this.keyinfo         , 0 ,  2 );
        System.arraycopy ( resp.getBytes() , 12 , this.sequencecounter , 0 ,  2 );
        System.arraycopy ( resp.getBytes() , 14 , this.cardchallenge   , 0 ,  6 );
        System.arraycopy ( resp.getBytes() , 20 , this.cardcryptogram  , 0 ,  8 );

        logger.debug ( "Host Challenge: "           + Conversion.arrayToHex( hostChallenge        ) );
        logger.debug ( "Key Diversification Data: " + Conversion.arrayToHex( this.keyDivData      ) );
        logger.debug ( "Key info: "                 + Conversion.arrayToHex( this.keyinfo         ) );
        logger.debug ( "Sequence counter: "         + Conversion.arrayToHex( this.sequencecounter ) );
        logger.debug ( "Card Challenge: "           + Conversion.arrayToHex( this.cardchallenge   ) );
        logger.debug ( "Card Cryptogram: "          + Conversion.arrayToHex( this.cardcryptogram  ) );
        
        System.arraycopy(this.sequencecounter, 0, this.derivationData, 2, this.sequencecounter.length);

        /* GENERATING SESSION KEYS */
        this.generateSessionKeys();

        /* Verifing Card Cryptogram */
        byte[] cardCryptoData = new byte[24];
        System.arraycopy( this.hostChallenge   , 0, cardCryptoData,  0, this.hostChallenge.length   );
        System.arraycopy( this.sequencecounter , 0, cardCryptoData,  8, this.sequencecounter.length );
        System.arraycopy( this.cardchallenge   , 0, cardCryptoData, 10, this.cardchallenge.length   );
        System.arraycopy( SCP02.padding        , 0, cardCryptoData, 16, SCP02.padding.length        );

        byte[] calculedCardChallenge = calculCryptogram(cardCryptoData);
        byte[] vCardCryptogram = new byte[8];
        System.arraycopy(calculedCardChallenge, calculedCardChallenge.length-8, vCardCryptogram, 0, 8);

        logger.info("Calculed Card Challenge: " + Conversion.arrayToHex ( vCardCryptogram     ) );
        logger.info("Card Cryptogram        : " + Conversion.arrayToHex ( this.cardcryptogram ) );

        if (Arrays.equals(vCardCryptogram, this.cardcryptogram)) {
            logger.info("Card Cryptogram OK" );
        } else {
            logger.info("Card Cryptogram FAIL" );
        }

        /* Calculing Host Cryptogram */
        byte[] hostCryptoData = new byte[24];
        System.arraycopy( this.sequencecounter , 0, hostCryptoData,  0, this.sequencecounter.length );
        System.arraycopy( this.cardchallenge   , 0, hostCryptoData,  2, this.cardchallenge.length   );
        System.arraycopy( this.hostChallenge   , 0, hostCryptoData,  8, this.hostChallenge.length   );
        System.arraycopy( SCP02.padding        , 0, hostCryptoData, 16, SCP02.padding.length        );

        byte[] calculedHostChallenge = calculCryptogram(hostCryptoData);
        this.hostCrypto = new byte[8];
        System.arraycopy(calculedHostChallenge, calculedHostChallenge.length-8, this.hostCrypto, 0, 8);

        logger.info("Calculed Host Ctryptogram: " + Conversion.arrayToHex ( this.hostCrypto ) );

        return resp;

    }

    protected void generateSessionKeys () {
        try {

            this.kSCEncSess = this.calculKey ( DESSessionKeys.SCEncSessKeys ) ;
            this.kC_MAC     = this.calculKey ( DESSessionKeys.C_MAC         ) ;
            this.kR_MAC     = this.calculKey ( DESSessionKeys.R_MAC         ) ;
            this.SCDataEnc  = this.calculKey ( DESSessionKeys.SCDataEnc     ) ;

            logger.info("SCEncSessKeys: " + Conversion.arrayToHex ( this.kSCEncSess ) );
            logger.info("C_MAC        : " + Conversion.arrayToHex ( this.kC_MAC     ) );
            logger.info("R_MAC        : " + Conversion.arrayToHex ( this.kR_MAC     ) );
            logger.info("SCDataEnc    : " + Conversion.arrayToHex ( this.SCDataEnc  ) );

        } catch (CardException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected byte[] calculKey ( DESSessionKeys constant ) throws CardException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte [] cipherKey    = null ;
        byte [] cipherKey_24 = null ;

        try {
            this.derivationData[0] = (byte)( constant.getVal()>>8   ) ;
            this.derivationData[1] = (byte)( constant.getVal()&0xFF ) ;
            //logger.debug ( "Derivation Data: "         + Conversion.arrayToHex( this.derivationData   ) );

            byte[] skey;
            switch (constant) {
                case SCEncSessKeys:
                    skey = this.S_ENC;
                    break;
                case C_MAC:
                case R_MAC:
                    skey = this.S_MAC;
                    break;
                case SCDataEnc:
                    skey = this.DEK;
                    break;
                default:
                    throw new CardException("DES Session Key not available (" + constant.getVal() + ")");
            }

            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            IvParameterSpec ivSpec = new IvParameterSpec(this.icv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(skey, "DESede"), ivSpec);

            cipherKey = cipher.doFinal(this.derivationData);
            cipherKey_24 = new byte[24];

            System.arraycopy(cipherKey, 0, cipherKey_24, 0, cipherKey.length);
            System.arraycopy(cipherKey, 0, cipherKey_24, cipherKey.length, cipherKey.length / 2);

            return cipherKey_24;

        } catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private byte[] calculCryptogram (byte[] cryptogram) {

        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            IvParameterSpec ivSpec = new IvParameterSpec(this.icv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.kSCEncSess, "DESede"), ivSpec);
            return cipher.doFinal(cryptogram);

        } catch (IllegalBlockSizeException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResponseAPDU externalAuthenticate(SecLevel secLevel) throws CardException {

        if (secLevel == null) {
            throw new IllegalArgumentException("secLevel must be not null");
        }

        // TODO: Check session state here!

        this.secMode = secLevel;
        byte[] extAuthCmd = new byte[21];
        extAuthCmd[0] = (byte) 0x84;
        extAuthCmd[1] = (byte) 0x82;
        extAuthCmd[2] = (byte) 0x03; //this.secMode.getVal();
        extAuthCmd[3] = (byte) 0x00;
        extAuthCmd[4] = (byte) 0x10;

        System.arraycopy(this.hostCrypto, 0, extAuthCmd, 5, this.hostCrypto.length);
        byte[] data = new byte[5 + this.hostCrypto.length];
        System.arraycopy(extAuthCmd, 0, data, 0, data.length);
        byte[] mac = this.generateC_MAC(data);
        extAuthCmd[4] = 0x10;
        System.arraycopy(mac, 0, extAuthCmd, 13, 8);

        CommandAPDU cmd_extauth = new CommandAPDU(extAuthCmd);
        ResponseAPDU resp = this.cc.transmit(cmd_extauth);

        logger.debug("EXTERNAL AUTHENTICATE command "
                + "(-> " + Conversion.arrayToHex(cmd_extauth.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != 0x9000) {
         //   this.resetParams();
            throw new CardException("Error in External Authenticate : " + Integer.toHexString(resp.getSW()));
        }
        return resp;
    }

    protected byte[] generateC_MAC(byte[] data) {
        byte[] dataWithPadding = null;

        logger.debug("data: " + Conversion.arrayToHex(data));

        //padding
        int tmpL = data.length + 1;
        while (tmpL % 8 != 0) {
            tmpL++;
        }

        dataWithPadding = new byte[tmpL];
        System.arraycopy(data, 0, dataWithPadding, 0, data.length);
        System.arraycopy(SCP02.padding, 0, dataWithPadding, data.length, tmpL - data.length);

        logger.debug("data with padding: " + Conversion.arrayToHex(dataWithPadding));

        try {
            switch (this.scp) {
                case SCP_02_15:
                    SecretKeySpec desSingleKey = new SecretKeySpec(this.kC_MAC,0, 8,"DES");
                    Cipher singleDesCipher;
                    singleDesCipher = Cipher.getInstance("DES/CBC/NoPadding", "SunJCE");
            

                    // Calculate the first n - 1 block.
                    int noOfBlocks = dataWithPadding.length / 8;
                    byte ivForNextBlock[] = this.icv;
                    IvParameterSpec ivSpec = new IvParameterSpec(this.icv);
                    int startIndex = 0;
                    for (int i = 0; i < (noOfBlocks - 1); i++) {
                        singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
			ivForNextBlock = singleDesCipher.doFinal(dataWithPadding, startIndex, 8);
			startIndex +=8;
			ivSpec = new IvParameterSpec(ivForNextBlock);
                    }

                    byte ivForLastBlock[] = singleDesCipher.doFinal(dataWithPadding, 0, 8);

                    SecretKeySpec desKey = new SecretKeySpec(this.kC_MAC, "DESede");
                    Cipher cipher;

                    cipher = Cipher.getInstance("DESede/CBC/NoPadding", "SunJCE");
                    int offset = dataWithPadding.length - 8;

                    // Generate C-MAC. Use 8-LSB
                    // For the last block, you can use TripleDES EDE with ECB mode, now I select the CBC and
                    // use the last block of the previous encryption result as ICV.
                    ivSpec = new IvParameterSpec(ivForLastBlock);
                    cipher.init(Cipher.ENCRYPT_MODE, desKey, ivSpec);
                    return cipher.doFinal(dataWithPadding, offset, 8);

                default:
                    throw new IllegalArgumentException("scp mode not implemented (" + this.scp + ")");
            }
        } catch (IllegalBlockSizeException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            java.util.logging.Logger.getLogger(SCP02.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    protected byte[] modifyAPDU (byte[] apdu) {
        // set CLA bit 3
        apdu[0] |= 0x03; // 0b0000_0100
        return apdu;
    }

}
