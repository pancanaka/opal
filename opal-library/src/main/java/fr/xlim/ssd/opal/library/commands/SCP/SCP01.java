package fr.xlim.ssd.opal.library.commands.SCP;

import fr.xlim.ssd.opal.library.SCGPKey;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Secure Channel Protocol 01 implementation
 *
 * @author Guillaume Bouffard
 */
public class SCP01 implements SCP {

    /// Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(SCP01.class);

    /// Default PADDING to encrypt data for SCP 01
    protected static final byte[] PADDING = Conversion.hexToArray("80 00 00 00 00 00 00 00");

    /// Encryption session key
    protected byte[] sessEnc;

    /// C-MAC session key
    protected byte[] sessMac;

    /// Data Encryption session key
    protected byte[] sessKek;

    /// Derivation data used to calculate session keys
    protected byte[] derivationData;

    /// Initialized Cypher Vector used to initialized encryption steps
    protected byte[] icv;

    public SCP01() {
        this.initIcv();
    }

    /**
     * Generate session keys depending with SCP protocol used
     *
     * @param staticKenc Static Encrypt key
     * @param staticKmac Static Mac key
     * @param staticKkek Static data encryption key
     */
    @Override
    public void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek) {
        logger.debug("==> Generate Session Keys");

        try {

            byte[] session;

            Cipher myCipher = null;

            logger.debug("* staticKenc: " + Conversion.arrayToHex(staticKenc.getData()));
            logger.debug("* staticKmac: " + Conversion.arrayToHex(staticKmac.getData()));
            logger.debug("* staticKkek: " + Conversion.arrayToHex(staticKkek.getData()));
            logger.debug("* SCP_Mode is SCP01");

            this.sessEnc = new byte[24];
            this.sessMac = new byte[24];
            this.sessKek = new byte[24];

            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");

            /* Calculating session encryption key */
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKenc.getData(), "DESede"));
            session = myCipher.doFinal(this.derivationData);
            System.arraycopy(session, 0, this.sessEnc, 0, 16);
            System.arraycopy(session, 0, this.sessEnc, 16, 8);

            logger.debug("* sessEnc = " + Conversion.arrayToHex(this.sessEnc));

            /* Calculating session mac key */
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKmac.getData(), "DESede"));
            session = myCipher.doFinal(this.derivationData);
            System.arraycopy(session, 0, this.sessMac, 0, 16);
            System.arraycopy(session, 0, this.sessMac, 16, 8);

            logger.debug("* sessMac = " + Conversion.arrayToHex(this.sessMac));

            /* Calculating session data encryption key */
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKkek.getData(), "DESede"));
            session = myCipher.doFinal(this.derivationData);
            System.arraycopy(session, 0, this.sessKek, 0, 16);
            System.arraycopy(session, 0, this.sessKek, 16, 8);

            logger.debug("* sessKek = " + Conversion.arrayToHex(this.sessKek));


        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Cannot find algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("No such PADDING problem", e);
        } catch (InvalidKeyException e) {
            throw new UnsupportedOperationException("Key problem", e);
        } catch (IllegalBlockSizeException e) {
            throw new UnsupportedOperationException("Block size problem", e);
        } catch (BadPaddingException e) {
            throw new UnsupportedOperationException("Bad PADDING problem", e);
        }

        logger.debug("==> Generate Session Keys Data End");
    }

    /**
     * Generate mac value according input data
     *
     * @param data data used to generate Mac value
     * @return Mac value calculated
     */
    @Override
    public byte[] generateMac(byte[] data) {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Encrypt APDU command
     *
     * @param command command to encrypt
     * @return encrypted command
     */
    @Override
    public byte[] encryptCommand(byte[] command) {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Decrypt APDU response. This command may be unimplemented
     *
     * @param response response to decrypt
     * @return plain response
     */
    @Override
    public byte[] decryptCardResponseData(byte[] response) {
        return response;
    }

    /**
     * Calculate Derivation data.
     *
     * @param hostChallenge host challenge used to generate derivation data
     * @param cardChallenge card challenge used to generate derivation data
     */
    @Override
    public void calculateDerivationData(byte[] hostChallenge, byte[] cardChallenge) {

        logger.debug("==> Calculate Derivation Data");

        this.derivationData = new byte[16];


        /*
         * Derivation data in SCP 01 mode
         *
         * -0-------------------3-4------------------7--8----------------11-12-----------------15--
         * |   Card Challenge    |   Card Challenge   |   Card Challenge   |   Card Challenge    |
         * | (4 byte right half) | (4 byte left half) | (4 byte left half) | (4 byte right half) |
         * ----------------------------------------------------------------------------------------
         */
        System.arraycopy(hostChallenge, 0, this.derivationData, 4, 4);
        System.arraycopy(hostChallenge, 4, this.derivationData, 12, 4);
        System.arraycopy(cardChallenge, 0, this.derivationData, 8, 4);
        System.arraycopy(cardChallenge, 4, this.derivationData, 0, 4);

        logger.debug("* Derivation Data is " + Conversion.arrayToHex(this.derivationData));
        logger.debug("==> Calculate Derivation Data End");
    }

    /**
     * Calculate Cryptogramm
     *
     * @param challenge challenge to calculate
     */
    @Override
    public void calculateCryptogram(byte[] challenge) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * ICV Initialization. All values set to 0
     */
    protected void initIcv() {
        logger.debug("==> Init ICV begin");
        this.icv = new byte[8];
        for (int i = 0; i < this.icv.length; i++) {
            this.icv[i] = (byte) 0x00;
        }
        logger.debug("* New ICV is " + Conversion.arrayToHex(this.icv));
        logger.debug("==> Init ICV end");
    }
}
