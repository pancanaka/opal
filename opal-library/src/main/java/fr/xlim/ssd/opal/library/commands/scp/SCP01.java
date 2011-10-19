/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a Java 6 library that implements Global Platform 2.x card
 * specification. OPAL is able to upload and manage Java Card applet lifecycle
 * on Java Card while dealing of the authentication of the user and encryption
 * of the communication between the user and the card. OPAL is also able
 * to manage different implementations of the specification via a pluggable
 * interface.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.library.commands.scp;

import fr.xlim.ssd.opal.library.config.SCGPKey;
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

            logger.debug("* staticKenc: " + Conversion.arrayToHex(staticKenc.getValue()));
            logger.debug("* staticKmac: " + Conversion.arrayToHex(staticKmac.getValue()));
            logger.debug("* staticKkek: " + Conversion.arrayToHex(staticKkek.getValue()));
            logger.debug("* SCP_Mode is SCP01");

            this.sessEnc = new byte[24];
            this.sessMac = new byte[24];
            this.sessKek = new byte[24];

            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");

            /* Calculating session encryption key */
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKenc.getValue(), "DESede"));
            session = myCipher.doFinal(this.derivationData);
            System.arraycopy(session, 0, this.sessEnc, 0, 16);
            System.arraycopy(session, 0, this.sessEnc, 16, 8);

            logger.debug("* sessEnc = " + Conversion.arrayToHex(this.sessEnc));

            /* Calculating session mac key */
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKmac.getValue(), "DESede"));
            session = myCipher.doFinal(this.derivationData);
            System.arraycopy(session, 0, this.sessMac, 0, 16);
            System.arraycopy(session, 0, this.sessMac, 16, 8);

            logger.debug("* sessMac = " + Conversion.arrayToHex(this.sessMac));

            /* Calculating session data encryption key */
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKkek.getValue(), "DESede"));
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
