/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.smartcardio.ResponseAPDU;

/**
 *
 * @author Guillaume Bouffard
 */
public class SCP02 {

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

    protected byte[] SCEncSessKeys ;
    protected byte[] C_MAC         ;
    protected byte[] R_MAC         ;
    protected byte[] SCDataEnc     ;

    protected SecurityDomain securityDomain ;
    protected CardConfig cardConfig ;

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


    public void Authentification (SecurityDomain securityDomain, CardConfig cardConfig) {

        /*
         * Moke Implementation:
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

        this.S_ENC = (this.cardConfig.getSCKeys()[0]).getData();
        this.S_MAC = (this.cardConfig.getSCKeys()[1]).getData();
        this.DEK   = (this.cardConfig.getSCKeys()[2]).getData();

        /* INIT UPDATE */
        this.hostChallenge        = Conversion.hexToArray ( "21 69 53 99 73 EC 89 3A" ) ;
        ResponseAPDU InitUpdateResp = new ResponseAPDU
                ( Conversion.hexToArray ( "00 00 81 58 03 12 20 91 38 27 FF 02 00 18 CC 28 2B D8 31 DB 76 D1 5E 08 A0 2D 92 20 90 00" ) );

        byte[] P = {cardConfig.getDefaultInitUpdateP1() , cardConfig.getDefaultInitUpdateP2()};

        logger.debug("INIT UPDATE Command "
                + "(-> 80 50 " + Conversion.arrayToHex( P ) + "08 " + Conversion.arrayToHex(hostChallenge) + ") "
                + "(<- " + Conversion.arrayToHex(InitUpdateResp.getBytes()) + ")");

        this.keyDivData      = new byte[10];
        this.keyinfo         = new byte[ 2];
        this.sequencecounter = new byte[ 2];
        this.cardchallenge   = new byte[ 6];
        this.cardcryptogram  = new byte[ 8];

        byte[] respBytes = InitUpdateResp.getBytes();

        System.arraycopy ( respBytes ,  0 , this.keyDivData      , 0 , 10 );
        System.arraycopy ( respBytes , 10 , this.keyinfo         , 0 ,  2 );
        System.arraycopy ( respBytes , 12 , this.sequencecounter , 0 ,  2 );
        System.arraycopy ( respBytes , 14 , this.cardchallenge   , 0 ,  6 );
        System.arraycopy ( respBytes , 20 , this.cardcryptogram  , 0 ,  8 );

        logger.debug ( "Host Challenge: "           + Conversion.arrayToHex( hostChallenge        ) );
        logger.debug ( "Key Diversification Data: " + Conversion.arrayToHex( this.keyDivData      ) );
        logger.debug ( "Key info: "                 + Conversion.arrayToHex( this.keyinfo         ) );
        logger.debug ( "Sequence counter: "         + Conversion.arrayToHex( this.sequencecounter ) );
        logger.debug ( "Card Challenge: "           + Conversion.arrayToHex( this.cardchallenge   ) );
        logger.debug ( "Card Cryptogram: "          + Conversion.arrayToHex( this.cardcryptogram  ) );
        
        System.arraycopy(this.sequencecounter, 0, this.derivationData, 2, this.sequencecounter.length);

        try {

            this.SCEncSessKeys = this.calculKey ( DESSessionKeys.SCEncSessKeys ) ;
            this.C_MAC         = this.calculKey ( DESSessionKeys.C_MAC         ) ;
            this.R_MAC         = this.calculKey ( DESSessionKeys.R_MAC         ) ;
            this.SCDataEnc     = this.calculKey ( DESSessionKeys.SCDataEnc     ) ;

            logger.info("SCEncSessKeys: " + Conversion.arrayToHex ( this.SCEncSessKeys ) );
            logger.info("C_MAC        : " + Conversion.arrayToHex ( this.C_MAC         ) );
            logger.info("R_MAC        : " + Conversion.arrayToHex ( this.R_MAC         ) );
            logger.info("SCDataEnc    : " + Conversion.arrayToHex ( this.SCDataEnc     ) );

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
            byte[] vHostCryptogram = new byte[8];
            System.arraycopy(calculedHostChallenge, calculedHostChallenge.length-8, vHostCryptogram, 0, 8);

            logger.info("Calculed Host Ctryptogram: " + Conversion.arrayToHex ( vHostCryptogram ) );

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

    private byte[] calculKey ( DESSessionKeys constant ) throws CardException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

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
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.SCEncSessKeys, "DESede"), ivSpec);
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

}
