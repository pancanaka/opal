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

import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import fr.xlim.ssd.opal.library.commands.SecLevel;
import fr.xlim.ssd.opal.library.config.SCDerivableKey;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCKey;
import fr.xlim.ssd.opal.library.config.SCPMode;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SCPImplementation implements SCP {

    /// Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(SCPImplementation.class);

    /// Default PADDING to encrypt data for SCP 02 and SCP 01
    protected static final byte[] PADDING = Conversion.hexToArray("80 00 00 00 00 00 00 00");


    private static byte[] iv_zero = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

    // used to calculate Encrypted counter ICV for R-ENC
    protected int RENC_counter = 01;


    // SCP 01 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP01 = (byte) 0x01;

    // SCP 02 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP02 = (byte) 0x02;

    // SCP 03 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP03 = (byte) 0x03;

    /// Default PADDING to encrypt data for SCP 03
    protected static final byte[] SCP03_PADDING = Conversion.hexToArray("80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    //Counter ICV padding for R-ENC computing
    protected static final byte[] SCP03_R_ENC_COUNTER_ICV_PADDING = Conversion.hexToArray("80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    // SCP 02 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.generateSessionKeys}, the C-Mac session key
    protected static final byte[] SCP02_DERIVATION4CMAC = {(byte) 0x01, (byte) 0x01};

    // SCP 02 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.generateSessionKeys}, the R-Mac session key
    protected static final byte[] SCP02_DERIVATION4RMAC = {(byte) 0x01, (byte) 0x02};

    // SCP 02 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.generateSessionKeys}, the encryption session key
    protected static final byte[] SCP02_DERIVATION4ENCKEY = {(byte) 0x01, (byte) 0x82};

    // SCP 02 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.generateSessionKeys}, the data encryption session key
    protected static final byte[] SCP02_DERIVATION4DATAENC = {(byte) 0x01, (byte) 0x81};

    // SCP 03 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.generateSessionKeys}, the C-Mac session key
    protected static final byte SCP03_DERIVATION4CMAC = (byte) 0x06;

    // SCP 03 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.generateSessionKeys}, the encryption session key
    protected static final byte SCP03_DERIVATION4DATAENC = (byte) 0x04;

    // SCP 03 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.generateSessionKeys}, the R-Mac session key
    protected static final byte SCP03_DERIVATION4RMAC = (byte) 0x07;

    // SCP 03 constant used to obtain the card cryptogram
    protected static final byte SCP03_DERIVATION4CardCryptogram = (byte) 0x00;

    // SCP 03 constant used to obtain the host cryptogram
    protected static final byte SCP03_DERIVATION4HostCryptogram = (byte) 0x01;

    // used to calculate Encrypted counter ICV for C-ENC
    protected int CENC_Counter = 01;

    //Counter ICV padding for R-ENC computing
    protected static final byte[] SCP03_C_ENC_COUNTER_ICV_PADDING = Conversion.hexToArray("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    private static byte[] iv_zero_scp03 = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

    /// Secure Channel Protocol used
    private SCPMode scpMode;

    /// Secure Level used to communicate
    private SecLevel secMode;

    /// Initialized Cypher Vector used to initialized encryption steps
    protected byte[] icv;

    /// Encryption session key
    protected byte[] sessEnc;

    /// C-MAC session key
    protected byte[] sessMac;

    /// R-MAC session key
    protected byte[] sessRMac;

    /// Data Encryption session key
    protected byte[] sessKek;

    /// Host challenge used to authenticate host in smartcard
    protected byte[] hostChallenge;

    /// Card challenge used to authenticate smartcard in host
    protected byte[] cardChallenge;

    /// Card response challenge
    protected byte[] cardCrypto;

    /// Derivation data used to calculate session keys
    protected byte[] derivationData;

    /// Host challenge result
    protected byte[] hostCrypto;

    /// Sequence counter used in SCP 02. Its value is the number of previous validate authentication
    protected byte[] sequenceCounter;

    @Override
    public SCPMode getScpMode() {
        return scpMode;
    }

    @Override
    public void setScpMode(SCPMode scpMode) {
        this.scpMode = scpMode;
    }

    @Override
    public SecLevel getSecMode() {
        return secMode;
    }

    @Override
    public void setSecMode(SecLevel secMode) {
        this.secMode = secMode;
    }

    @Override
    public byte[] getIcv() {
        return icv;
    }

    public void setIcv(byte[] icv) {
        this.icv = icv;
    }

    /**
     * ICV Initialization. All values set to 0
     */
    @Override
    public void initIcv() {
        if (getScpMode() == SCPMode.SCP_01_15
                || getScpMode() == SCPMode.SCP_01_05
                || getScpMode() == SCPMode.SCP_UNDEFINED
                || getScpMode() == SCPMode.SCP_02_04
                || getScpMode() == SCPMode.SCP_02_05
                || getScpMode() == SCPMode.SCP_02_0A
                || getScpMode() == SCPMode.SCP_02_0B
                || getScpMode() == SCPMode.SCP_02_14
                || getScpMode() == SCPMode.SCP_02_15
                || getScpMode() == SCPMode.SCP_02_1A
                || getScpMode() == SCPMode.SCP_02_1B
                || getScpMode() == SCPMode.SCP_02_45
                || getScpMode() == SCPMode.SCP_02_54
                || getScpMode() == SCPMode.SCP_02_55) {

            logger.debug("==> Init ICV begin");
            setIcv(new byte[8]);
            for (int i = 0; i < getIcv().length; i++) {
                getIcv()[i] = (byte) 0x00;
            }
            logger.debug("* New ICV is " + Conversion.arrayToHex(getIcv()));
            logger.debug("==> Init ICV end");
        }
        if (getScpMode() == SCPMode.SCP_03_05
                || getScpMode() == SCPMode.SCP_03_0D
                || getScpMode() == SCPMode.SCP_03_25
                || getScpMode() == SCPMode.SCP_03_2D
                || getScpMode() == SCPMode.SCP_03_65
                || getScpMode() == SCPMode.SCP_03_6D) {

            logger.debug("==> Init ICV begin");
            setIcv(new byte[16]);
            for (int i = 0; i < getIcv().length; i++) {
                getIcv()[i] = (byte) 0x00;
            }
            logger.debug("* New ICV is " + Conversion.arrayToHex(getIcv()));
            logger.debug("==> Init ICV end");

        }
    }

    @Override
    public byte[] getSessEnc() {
        return sessEnc;
    }

    @Override
    public void setSessEnc(byte[] sessEnc) {
        this.sessEnc = sessEnc;
    }

    @Override
    public byte[] getSessMac() {
        return sessMac;
    }

    @Override
    public void setSessMac(byte[] sessMac) {
        this.sessMac = sessMac;
    }

    @Override
    public byte[] getSessRMac() {
        return sessRMac;
    }

    @Override
    public void setSessRMac(byte[] sessRMac) {
        this.sessRMac = sessRMac;
    }

    @Override
    public byte[] getSessKek() {
        return sessKek;
    }

    @Override
    public void setSessKek(byte[] sessKek) {
        this.sessKek = sessKek;
    }

    @Override
    public byte[] getHostChallenge() {
        return hostChallenge;
    }

    @Override
    public void setHostChallenge(byte[] hostChallenge) {
        this.hostChallenge = hostChallenge;
    }

    @Override
    public byte[] getCardChallenge() {
        return cardChallenge;
    }

    @Override
    public void setCardChallenge(byte[] cardChallenge) {
        this.cardChallenge = cardChallenge;
    }

    @Override
    public byte[] getCardCrypto() {
        return cardCrypto;
    }

    @Override
    public void setCardCrypto(byte[] cardCrypto) {
        this.cardCrypto = cardCrypto;
    }

    @Override
    public byte[] getDerivationData() {
        return derivationData;
    }

    @Override
    public void setDerivationData(byte[] derivationData) {
        this.derivationData = derivationData;
    }

    @Override
    public byte[] getHostCrypto() {
        return hostCrypto;
    }

    @Override
    public void setHostCrypto(byte[] hostCrypto) {
        this.hostCrypto = hostCrypto;
    }

    @Override
    public byte[] getSequenceCounter() {
        return sequenceCounter;
    }

    @Override
    public void setSequenceCounter(byte[] sequenceCounter) {
        this.sequenceCounter = sequenceCounter;
    }

    /**
     * Calculate Derivation data. This step depending to the @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} card response.
     */
    @Override
    public void calculateDerivationData() {

        logger.debug("==> Calculate Derivation Data");

        if ((getScpMode() == SCPMode.SCP_UNDEFINED)
                || (getScpMode() == SCPMode.SCP_01_05)
                || (getScpMode() == SCPMode.SCP_01_15)) { // SCP 01_*

            setDerivationData(new byte[16]);

            System.arraycopy(getHostChallenge(), 0, getDerivationData(), 4, 4);
            System.arraycopy(getHostChallenge(), 4, getDerivationData(), 12, 4);
            System.arraycopy(getCardChallenge(), 0, getDerivationData(), 8, 4);
            System.arraycopy(getCardChallenge(), 4, getDerivationData(), 0, 4);

        } else if (getScpMode() == SCPMode.SCP_02_15
                || getScpMode() == SCPMode.SCP_02_04
                || getScpMode() == SCPMode.SCP_02_05
                || getScpMode() == SCPMode.SCP_02_14
                || getScpMode() == SCPMode.SCP_02_0A
                || getScpMode() == SCPMode.SCP_02_45
                || getScpMode() == SCPMode.SCP_02_55) { // SCP 02_*

            setDerivationData(new byte[16]);
            System.arraycopy(getSequenceCounter(), 0, getDerivationData(), 2, 2);

        } else if ((getScpMode() == SCPMode.SCP_03_65)
                || (getScpMode() == SCPMode.SCP_03_6D)
                || (getScpMode() == SCPMode.SCP_03_05)
                || (getScpMode() == SCPMode.SCP_03_0D)
                || (getScpMode() == SCPMode.SCP_03_2D)
                || (getScpMode() == SCPMode.SCP_03_25)) {


            /*
             * Derivation data in SCP 03 mode
             *
             * -0-----------------------10--11---12--13--14--15-
             * | label (11 byte of 00)    | dc | 00 |  L   | i  |
             * -------------------------------------------------
             *
             * --16------------23-24-------------31-
             *  | Host Challenge | Card Challenge |
             * -------------------------------------
             *
             * Definition of the derivation constant (dc):
             * - 00 : derivation data to calculate card cryptogram
             * - 01 : derivation data to calculate host cryptogram
             * - 04 : derivation of S-ENC
             * - 06 : derivation of S-MAC
             * - 07 : derivation of S-RMAC
             */


            setDerivationData(new byte[32]);
            byte[] label = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            System.arraycopy(label, 0, getDerivationData(), 0, label.length);
            System.arraycopy(getHostChallenge(), 0, getDerivationData(), 16, getHostChallenge().length);
            System.arraycopy(getCardChallenge(), 0, getDerivationData(), 24, getHostChallenge().length);
        }

        logger.debug("* Derivation Data is " + Conversion.arrayToHex(getDerivationData()));

        logger.debug("==> Calculate Derivation Data End");

    }


    @Override
    public byte[] decryptCardResponseData(byte[] response) throws CardException {

        try {
            byte[] encryptedData;
            if (response.length > 10)// the response contain data
            {
                byte[] res = null;
                if (getScpMode() == SCPMode.SCP_03_65) {
                    encryptedData = new byte[(response.length - 10)];
                    System.arraycopy(response, 0, encryptedData, 0, response.length - 10);
                    if ((encryptedData.length % 16) != 0) {
                        throw new CardException("The length of received encrypted data is invalid");
                    }
                    IvParameterSpec ivSpec = new IvParameterSpec(iv_zero_scp03);
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(getSessEnc(), "AES"), ivSpec);
                    res = cipher.doFinal(encryptedData);
                }
                if (getScpMode() == SCPMode.SCP_03_6D) {
                    byte[] icvCEnc = new byte[16];
                    String hexaCounter = Integer.toHexString(CENC_Counter);
                    if ((hexaCounter.length() % 2) == 1) {
                        hexaCounter = "0" + hexaCounter;
                    }
                    logger.debug("* icv counter = " + Conversion.arrayToHex(Conversion.hexToArray(hexaCounter)));
                    hexaCounter = Conversion.arrayToHex(Conversion.hexToArray(hexaCounter));

                    byte[] byteCounter = Conversion.hexToArray(hexaCounter);

                    System.arraycopy(SCP03_C_ENC_COUNTER_ICV_PADDING, 0, icvCEnc, 0, 16 - byteCounter.length);
                    System.arraycopy(byteCounter, 0, icvCEnc, 16 - byteCounter.length, byteCounter.length);
                    logger.debug("* data used to calculate icv = " + Conversion.arrayToHex(icvCEnc));

                    encryptedData = new byte[(response.length - 10)];
                    System.arraycopy(response, 0, encryptedData, 0, response.length - 10);
                    if ((encryptedData.length % 16) != 0) {
                        throw new CardException("The length of received encrypted data is invalid");
                    }
                    IvParameterSpec ivSpec = new IvParameterSpec(icvCEnc);
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(getSessEnc(), "AES"), ivSpec);
                    res = cipher.doFinal(encryptedData);
                    RENC_counter++;
                }
                if (getScpMode() == SCPMode.SCP_03_05) {// this mode don't support RMAC then the response contain only Crypted data and Statuts words
                    encryptedData = new byte[(response.length - 2)];
                    System.arraycopy(response, 0, encryptedData, 0, response.length - 2);
                    if ((encryptedData.length % 16) != 0) {
                        throw new CardException("The length of received encrypted data is invalid");
                    }
                    IvParameterSpec ivSpec = new IvParameterSpec(iv_zero_scp03);
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(getSessEnc(), "AES"), ivSpec);
                    res = cipher.doFinal(encryptedData);
                }
                if (getScpMode() == SCPMode.SCP_03_0D) {
                    byte[] icvCEnc = new byte[16];
                    String hexaCounter = Integer.toHexString(CENC_Counter);
                    if ((hexaCounter.length() % 2) == 1) {
                        hexaCounter = "0" + hexaCounter;
                    }
                    logger.debug("* icv counter = " + Conversion.arrayToHex(Conversion.hexToArray(hexaCounter)));
                    hexaCounter = Conversion.arrayToHex(Conversion.hexToArray(hexaCounter));

                    byte[] byteCounter = Conversion.hexToArray(hexaCounter);

                    System.arraycopy(SCP03_C_ENC_COUNTER_ICV_PADDING, 0, icvCEnc, 0, 16 - byteCounter.length);
                    System.arraycopy(byteCounter, 0, icvCEnc, 16 - byteCounter.length, byteCounter.length);
                    logger.debug("* data used to calculate icv = " + Conversion.arrayToHex(icvCEnc));

                    encryptedData = new byte[(response.length - 2)];
                    System.arraycopy(response, 0, encryptedData, 0, response.length - 2);
                    if ((encryptedData.length % 16) != 0) {
                        throw new CardException("The length of received encrypted data is invalid");
                    }
                    IvParameterSpec ivSpec = new IvParameterSpec(icvCEnc);
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(getSessEnc(), "AES"), ivSpec);
                    res = cipher.doFinal(encryptedData);
                    RENC_counter++;
                }


                return res;
            } else if (response.length == 10)   //the response does not contain data
            {
                return null;
            } else {
                throw new CardException("The length of received encrypted data is invalid");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Cannot find algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("No such PADDING problem", e);
        } catch (InvalidKeyException e) {
            throw new UnsupportedOperationException("Key problem", e);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Invalid Algorithm parameter", e);
        }
    }

    /**
     * Encrypt APDU command in a specific SCP version.
     * <p/>
     * Only SCP 01 protocol is yet implemented
     *
     * @param command command to encrypt
     *
     * @return encrypted command
     */
    @Override
    public byte[] encryptCommand(byte[] command) {

        logger.debug("==> Encrypt Command Begin");
        logger.debug("* Command to encrypt is " + Conversion.arrayToHex(command));

        byte[] datas = null;
        byte[] encryptedCmd = null;


        try {
            if (getScpMode() == SCPMode.SCP_01_05 || getScpMode() == SCPMode.SCP_01_15) {

                int dataLength = command.length - 4 - 8; // command without (CLA, INS, P1, P2) AND C-MAC
                if (dataLength % 8 == 0) { // don't need a PADDING
                    datas = new byte[dataLength];
                    System.arraycopy(command, 4, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    datas[0] = (byte) (datas.length - 1); // update the "pseudo" LC with the length of the original clear text
                } else { // need a PADDING
                    int nbBytes = 8 - (dataLength % 8); // bytes needed for the PADDING
                    logger.debug("- We need a PADDING (" + nbBytes + " bytes) ");
                    datas = new byte[dataLength + nbBytes];
                    System.arraycopy(command, 4, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    datas[0] = (byte) (datas.length - 1 - nbBytes); // update the "pseudo" LC with the length of the original clear text
                    System.arraycopy(PADDING, 0, datas, dataLength, nbBytes); // add necessary PADDING

                    logger.debug("- New data to encrypt is " + Conversion.arrayToHex(datas));
                }
                IvParameterSpec ivSpec = new IvParameterSpec(Conversion.hexToArray("00 00 00 00 00 00 00 00"));

                logger.debug("* SCP 01 Protocol used");
                logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));
                logger.debug("* sessEnc key is " + Conversion.arrayToHex(getSessEnc()));

                Cipher myCipher = Cipher.getInstance("DESede/CBC/NoPadding");

                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "DESede"), ivSpec);
                byte[] res = myCipher.doFinal(datas);
                encryptedCmd = new byte[5 + res.length + 8];
                System.arraycopy(command, 0, encryptedCmd, 0, 5);
                System.arraycopy(res, 0, encryptedCmd, 5, res.length);
                System.arraycopy(command, command.length - 8, encryptedCmd, res.length + 5, 8);
                encryptedCmd[4] = (byte) (encryptedCmd.length - 5);

                logger.debug("* Encrypted data is " + Conversion.arrayToHex(encryptedCmd));
            }

            if (getScpMode() == SCPMode.SCP_02_04
                    || getScpMode() == SCPMode.SCP_02_05
                    || getScpMode() == SCPMode.SCP_02_0A
                    || getScpMode() == SCPMode.SCP_02_0B
                    || getScpMode() == SCPMode.SCP_02_14
                    || getScpMode() == SCPMode.SCP_02_15
                    || getScpMode() == SCPMode.SCP_02_1A
                    || getScpMode() == SCPMode.SCP_02_1B
                    || getScpMode() == SCPMode.SCP_02_45
                    || getScpMode() == SCPMode.SCP_02_54
                    || getScpMode() == SCPMode.SCP_02_55) {

                int dataLength = command.length - 5 - 8; // command without (CLA, INS, P1, P2) AND C-MAC

                if (dataLength % 8 == 0) { // don't need a PADDING
                    datas = new byte[dataLength + 8];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(PADDING, 0, datas, dataLength, 8);
                    command[4] = (byte) (dataLength + 8);


                } else { // need a PADDING
                    int nbBytes = 8 - (dataLength % 8); // bytes needed for the PADDING
                    logger.debug("- We need a PADDING (" + nbBytes + " bytes) ");
                    datas = new byte[dataLength + nbBytes];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(PADDING, 0, datas, dataLength, nbBytes); // add necessary PADDING


                }


                logger.debug("- New data to encrypt is " + Conversion.arrayToHex(datas));
                logger.debug("* DATAFIELD from command is " + Conversion.arrayToHex(datas));


                IvParameterSpec ivSpec = new IvParameterSpec(iv_zero);

                logger.debug("* SCP 02 Protocol used");
                logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));
                logger.debug("* sessEnc key is " + Conversion.arrayToHex(getSessEnc()));

                Cipher myCipher = Cipher.getInstance("DESede/CBC/NoPadding", "SunJCE");
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "DESede"), ivSpec);
                byte[] res = myCipher.doFinal(datas);
                encryptedCmd = new byte[5 + res.length + 8];
                System.arraycopy(command, 0, encryptedCmd, 0, 5);
                System.arraycopy(res, 0, encryptedCmd, 5, res.length);
                System.arraycopy(command, command.length - 8, encryptedCmd, res.length + 5, 8);
                encryptedCmd[4] = (byte) (datas.length + 8);


                logger.debug("* Encrypted data is " + Conversion.arrayToHex(encryptedCmd));
            }


            if (getScpMode() == SCPMode.SCP_03_65
                    || getScpMode() == SCPMode.SCP_03_05
                    || getScpMode() == SCPMode.SCP_03_25) {

                int dataLength = command.length - 5; // command without (CLA, INS, P1, P2) AND C-MAC

                if (dataLength % 16 == 0) { // don't need a PADDING
                    datas = new byte[dataLength + 16];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(SCP03_PADDING, 0, datas, dataLength, 16);
                    command[4] = (byte) (dataLength + 8);


                } else { // need a PADDING
                    int nbBytes = 16 - (dataLength % 16); // bytes needed for the PADDING
                    logger.debug("- We need a PADDING (" + nbBytes + " bytes) ");
                    datas = new byte[dataLength + nbBytes];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(SCP03_PADDING, 0, datas, dataLength, nbBytes); // add necessary PADDING

                }
                logger.debug("- New data to encrypt is " + Conversion.arrayToHex(datas));
                logger.debug("* DATAFIELD from command is " + Conversion.arrayToHex(datas));


                IvParameterSpec ivSpec = new IvParameterSpec(iv_zero_scp03);
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "AES"), ivSpec);
                byte[] res = cipher.doFinal(datas);
                encryptedCmd = new byte[5 + res.length];
                System.arraycopy(command, 0, encryptedCmd, 0, 5);
                System.arraycopy(res, 0, encryptedCmd, 5, res.length);
                encryptedCmd[4] = (byte) (datas.length + 8);


                logger.debug("* Encrypted data is " + Conversion.arrayToHex(encryptedCmd));


            }
            if (getScpMode() == SCPMode.SCP_03_6D
                    || getScpMode() == SCPMode.SCP_03_0D
                    || getScpMode() == SCPMode.SCP_03_2D) {
                // compute the counter icv
                byte[] icvCEnc = new byte[16];
                String hexaCounter = Integer.toHexString(CENC_Counter);
                if ((hexaCounter.length() % 2) == 1) {
                    hexaCounter = "0" + hexaCounter;
                }
                logger.debug("* icv counter = " + Conversion.arrayToHex(Conversion.hexToArray(hexaCounter)));
                hexaCounter = Conversion.arrayToHex(Conversion.hexToArray(hexaCounter));

                byte[] byteCounter = Conversion.hexToArray(hexaCounter);

                System.arraycopy(SCP03_C_ENC_COUNTER_ICV_PADDING, 0, icvCEnc, 0, 16 - byteCounter.length);
                System.arraycopy(byteCounter, 0, icvCEnc, 16 - byteCounter.length, byteCounter.length);
                logger.debug("* data used to calculate icv = " + Conversion.arrayToHex(icvCEnc));

                IvParameterSpec ivSpec = new IvParameterSpec(iv_zero_scp03);
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "AES"), ivSpec);
                icvCEnc = cipher.doFinal(icvCEnc);
                logger.debug("* icv counter = " + Conversion.arrayToHex(icvCEnc));
                int dataLength = command.length - 5; // command without (CLA, INS, P1, P2) AND C-MAC

                if (dataLength % 16 == 0) { // don't need a PADDING
                    datas = new byte[dataLength + 16];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(SCP03_PADDING, 0, datas, dataLength, 16);
                    command[4] = (byte) (dataLength + 8);


                } else { // need a PADDING
                    int nbBytes = 16 - (dataLength % 16); // bytes needed for the PADDING
                    logger.debug("- We need a PADDING (" + nbBytes + " bytes) ");
                    datas = new byte[dataLength + nbBytes];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(SCP03_PADDING, 0, datas, dataLength, nbBytes); // add necessary PADDING

                }
                logger.debug("- New data to encrypt is " + Conversion.arrayToHex(datas));
                logger.debug("* DATAFIELD from command is " + Conversion.arrayToHex(datas));


                ivSpec = new IvParameterSpec(icvCEnc);
                cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "AES"), ivSpec);
                byte[] res = cipher.doFinal(datas);
                encryptedCmd = new byte[5 + res.length];
                System.arraycopy(command, 0, encryptedCmd, 0, 5);
                System.arraycopy(res, 0, encryptedCmd, 5, res.length);
                encryptedCmd[4] = (byte) (datas.length + 8);

                logger.debug("* Encrypted data " + Conversion.arrayToHex(res));

            }

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
        } catch (Exception e) {
            throw new UnsupportedOperationException("Invalid Algorithm parameter", e);
        }

        logger.debug("==> Encrypt Command End");

        return encryptedCmd;
    }

    /**
     * Generate mac value according input data in a specific SCP version
     *
     * @param data data used to generate Mac value
     *
     * @return Mac value calculated
     */
    @Override
    public byte[] generateMac(byte[] data) {

        logger.debug("==> Generate Mac");

        byte[] dataWithPadding = null;

        logger.debug("generateMac with data: " + Conversion.arrayToHex(data));

        if (data.length % 8 != 0) { // We need a PADDING

            logger.debug("- Data needs PADDING!");

            int nbBytes = 8 - (data.length % 8);
            dataWithPadding = new byte[data.length + nbBytes];
            System.arraycopy(data, 0, dataWithPadding, 0, data.length);
            System.arraycopy(PADDING, 0, dataWithPadding, data.length, nbBytes);
        } else {
            dataWithPadding = new byte[data.length + 8];
            System.arraycopy(data, 0, dataWithPadding, 0, data.length);
            System.arraycopy(PADDING, 0, dataWithPadding, data.length, PADDING.length);
        }

        logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));

        byte[] res = new byte[8];
        IvParameterSpec ivSpec = new IvParameterSpec(getIcv());
        try {
            logger.debug("SCP: " + getScpMode());
            if ((getScpMode() == SCPMode.SCP_UNDEFINED)    // TODO: Undefined SCPMode Here ?
                    || (getScpMode() == SCPMode.SCP_01_05)
                    || (getScpMode() == SCPMode.SCP_01_15)) {

                logger.debug("* SCP 01 Protocol (" + getScpMode() + ") used");
                logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));

                Cipher myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessMac(), "DESede"), ivSpec);
                byte[] cryptogram = myCipher.doFinal(dataWithPadding);
                System.arraycopy(cryptogram, cryptogram.length - 8, res, 0, 8);

                logger.debug("* Calculated cryptogram is " + Conversion.arrayToHex(res));

                switch (getScpMode()) {
                    case SCP_01_05:
                        setIcv(res); // update ICV with new C-MAC
                        break;
                    case SCP_01_15: // update ICV with new ENCRYPTED C-MAC
                        Cipher myCipher2 = Cipher.getInstance("DESede/ECB/NoPadding");
                        myCipher2.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessMac(), "DESede"));
                        setIcv(myCipher2.doFinal(res));
                        break;
                }

                logger.debug("* New ICV is " + Conversion.arrayToHex(getIcv()));

            } else if (getScpMode() == SCPMode.SCP_02_15
                    || getScpMode() == SCPMode.SCP_02_04
                    || getScpMode() == SCPMode.SCP_02_05
                    || getScpMode() == SCPMode.SCP_02_14
                    || getScpMode() == SCPMode.SCP_02_45
                    || getScpMode() == SCPMode.SCP_02_55) {

                logger.debug("* SCP 02 Protocol (" + getScpMode() + ") used");
                logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));

                SecretKeySpec desSingleKey = new SecretKeySpec(getSessMac(), 0, 8, "DES");
                Cipher singleDesCipher;
                singleDesCipher = Cipher.getInstance("DES/CBC/NoPadding", "SunJCE");

                // Calculate the first n - 1 block.
                int noOfBlocks = dataWithPadding.length / 8;
                byte ivForNextBlock[] = getIcv();
                int startIndex = 0;
                for (int i = 0; i < (noOfBlocks - 1); i++) {
                    singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
                    ivForNextBlock = singleDesCipher.doFinal(dataWithPadding, startIndex, 8);
                    startIndex += 8;
                    ivSpec = new IvParameterSpec(ivForNextBlock);
                    logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
                }


                SecretKeySpec desKey = new SecretKeySpec(getSessMac(), "DESede");
                Cipher myCipher;

                myCipher = Cipher.getInstance("DESede/CBC/NoPadding", "SunJCE");
                int offset = dataWithPadding.length - 8;

                // Generate C-MAC. Use 8-LSB
                // For the last block, you can use TripleDES EDE with ECB mode, now I select the CBC and
                // use the last block of the previous encryption result as ICV.
                //ivSpec = new IvParameterSpec(ivForLastBlock);
                myCipher.init(Cipher.ENCRYPT_MODE, desKey, ivSpec);
                res = myCipher.doFinal(dataWithPadding, offset, 8);
                if (getScpMode() == SCPMode.SCP_02_04
                        || getScpMode() == SCPMode.SCP_02_05
                        || getScpMode() == SCPMode.SCP_02_45) {
                    setIcv(res); // update ICV with new C-MAC //no ICV encryption
                } else if (getScpMode() == SCPMode.SCP_02_15
                        || getScpMode() == SCPMode.SCP_02_14
                        || getScpMode() == SCPMode.SCP_02_55) {
                    // update ICV with new ENCRYPTED C-MAC
                    ivSpec = new IvParameterSpec(new byte[8]);
                    singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
                    setIcv(singleDesCipher.doFinal(res));
                }

                logger.debug("* Calculated cryptogram is " + Conversion.arrayToHex(res));
                logger.debug("* New ICV is " + Conversion.arrayToHex(getIcv()));

            } else if (getScpMode() == SCPMode.SCP_03_65
                    || getScpMode() == SCPMode.SCP_03_6D
                    || getScpMode() == SCPMode.SCP_03_05
                    || getScpMode() == SCPMode.SCP_03_0D
                    || getScpMode() == SCPMode.SCP_03_2D
                    || getScpMode() == SCPMode.SCP_03_25) {


                if (data.length % 16 != 0) { // We need a PADDING

                    logger.debug("- Data needs PADDING!");

                    int nbBytes = 16 - (data.length % 16);
                    dataWithPadding = new byte[data.length + nbBytes];
                    System.arraycopy(data, 0, dataWithPadding, 0, data.length);
                    System.arraycopy(SCP03_PADDING, 0, dataWithPadding, data.length, nbBytes);
                } else {
                    dataWithPadding = new byte[data.length + 16];
                    System.arraycopy(data, 0, dataWithPadding, 0, data.length);
                    System.arraycopy(SCP03_PADDING, 0, dataWithPadding, data.length, SCP03_PADDING.length);
                }

                logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));


                byte[] dataToCalulateMac = new byte[dataWithPadding.length + 16];
                System.arraycopy(getIcv(), 0, dataToCalulateMac, 0, getIcv().length);
                System.arraycopy(dataWithPadding, 0, dataToCalulateMac, 16, dataWithPadding.length);

                logger.debug("data used to génerate mac : " + Conversion.arrayToHex(dataToCalulateMac));

                ivSpec = new IvParameterSpec(iv_zero_scp03);
                logger.debug("* SCP 03 Protocol (" + getScpMode() + ") used");
                logger.debug("* IV is " + Conversion.arrayToHex(getIcv()));

                SecretKeySpec skeySpec = new SecretKeySpec(getSessMac(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                byte[] encrypted = cipher.doFinal(dataToCalulateMac);

                logger.debug("encrypted command : " + Conversion.arrayToHex(encrypted));
                byte[] eightRightMostBit = new byte[8];
                System.arraycopy(encrypted, 0, res, 0, 8);
                System.arraycopy(encrypted, encrypted.length - 8, eightRightMostBit, 0, 8);

                //update ICV : 8 most significant bytes + 8 lest significant bytes
                System.arraycopy(res, 0, getIcv(), 0, 8);
                System.arraycopy(eightRightMostBit, 0, getIcv(), 8, 8);


            }
        } catch (NoSuchProviderException e) {
            throw new UnsupportedOperationException("no such crypto provider", e);
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
        } catch (InvalidAlgorithmParameterException e) {
            throw new UnsupportedOperationException("Invalid Algorithm parameter", e);
        }

        logger.debug("==> Generate Mac End");

        return res.clone();
    }


    /**
     * Initialization ICV to mac ovec AID of selected application, this function is used in
     * SCP 02_1A and SCP 02_0A and SCP 02_1B which use the implicit initiation mode
     */
    @Override
    public void initIcvToMacOverAid(byte[] aid) {

        logger.info("==> init ICV to mac over AID");
        logger.info("* SCP 02 Protocol (" + getScpMode() + ") used");
        logger.info("* IV is " + Conversion.arrayToHex(iv_zero));

        IvParameterSpec ivSpec = new IvParameterSpec(iv_zero);
        byte[] dataWithPadding;

        byte[] res;

        if (aid.length % 8 != 0) { // We need a PADDING

            logger.debug("- Data needs PADDING!");

            int nbBytes = 8 - (aid.length % 8);
            dataWithPadding = new byte[aid.length + nbBytes];
            System.arraycopy(aid, 0, dataWithPadding, 0, aid.length);
            System.arraycopy(PADDING, 0, dataWithPadding, aid.length, nbBytes);
        } else {
            dataWithPadding = new byte[aid.length + 8];
            System.arraycopy(aid, 0, dataWithPadding, 0, aid.length);
            System.arraycopy(PADDING, 0, dataWithPadding, aid.length, PADDING.length);
        }

        logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));

        try {

            SecretKeySpec desSingleKey = new SecretKeySpec(getSessMac(), 0, 8, "DES");
            Cipher singleDesCipher;
            singleDesCipher = Cipher.getInstance("DES/CBC/NoPadding", "SunJCE");

            // Calculate the first n - 1 block.
            int noOfBlocks = dataWithPadding.length / 8;
            byte ivForNextBlock[] = getIcv();
            int startIndex = 0;
            for (int i = 0; i < (noOfBlocks - 1); i++) {
                singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
                ivForNextBlock = singleDesCipher.doFinal(dataWithPadding, startIndex, 8);
                startIndex += 8;
                ivSpec = new IvParameterSpec(ivForNextBlock);
                logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
            }


            SecretKeySpec desKey = new SecretKeySpec(getSessMac(), "DESede");
            Cipher myCipher;

            myCipher = Cipher.getInstance("DESede/CBC/NoPadding", "SunJCE");
            int offset = dataWithPadding.length - 8;

            // Generate C-MAC. Use 8-LSB
            // For the last block, you can use TripleDES EDE with ECB mode, now I select the CBC and
            // use the last block of the previous encryption result as ICV.
            //ivSpec = new IvParameterSpec(ivForLastBlock);
            myCipher.init(Cipher.ENCRYPT_MODE, desKey, ivSpec);
            res = myCipher.doFinal(dataWithPadding, offset, 8);
            logger.info("New ICV is " + Conversion.arrayToHex(res));
        } catch (NoSuchProviderException e) {
            throw new UnsupportedOperationException("no such provider exception", e);
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
        } catch (InvalidAlgorithmParameterException e) {
            throw new UnsupportedOperationException("Invalid Algorithm parameter", e);
        }
    }


    /**
     * Calculate Cryptogramms in SCP01 et SCP02 protocol
     */
    @Override
    public void calculateCryptograms() {

        logger.debug("==> Calculate Cryptograms");

        byte[] data = new byte[24];
        Cipher myCipher;
        try {

            myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
            IvParameterSpec ivSpec = new IvParameterSpec(getIcv());

            logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));

            if ((getScpMode() == SCPMode.SCP_UNDEFINED)
                    || (getScpMode() == SCPMode.SCP_01_05)
                    || (getScpMode() == SCPMode.SCP_01_15)) {

                logger.debug("* SCP 01 protocol used");

                /* Calculing Cryptogram */
                System.arraycopy(getHostChallenge(), 0, data, 0, 8);
                System.arraycopy(getCardChallenge(), 0, data, 8, 8);
                System.arraycopy(PADDING, 0, data, 16, 8);

                logger.debug("* Data to encrypt: " + Conversion.arrayToHex(data));

                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "DESede"), ivSpec);
                byte[] cardcryptogram = myCipher.doFinal(data);
                setCardCrypto(new byte[8]);
                System.arraycopy(cardcryptogram, 16, getCardCrypto(), 0, 8);

                logger.debug("* Calculated Card Crypto: " + Conversion.arrayToHex(getCardCrypto()));

                System.arraycopy(getCardChallenge(), 0, data, 0, 8);
                System.arraycopy(getHostChallenge(), 0, data, 8, 8);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "DESede"), ivSpec);
                byte[] hostcryptogram = myCipher.doFinal(data);
                setHostCrypto(new byte[8]);
                System.arraycopy(hostcryptogram, 16, getHostCrypto(), 0, 8);

                logger.debug("* Calculated Host Crypto: " + Conversion.arrayToHex(getHostCrypto()));

            } else if (getScpMode() == SCPMode.SCP_02_15
                    || getScpMode() == SCPMode.SCP_02_04
                    || getScpMode() == SCPMode.SCP_02_05
                    || getScpMode() == SCPMode.SCP_02_14
                    || getScpMode() == SCPMode.SCP_02_45
                    || getScpMode() == SCPMode.SCP_02_55) {

                logger.debug("* SCP 02 protocol used");

                /* Calculing Card Cryptogram */
                System.arraycopy(getHostChallenge(), 0, data, 0, 8);
                System.arraycopy(getSequenceCounter(), 0, data, 8, 2);
                System.arraycopy(getCardChallenge(), 0, data, 10, 6);
                System.arraycopy(PADDING, 0, data, 16, PADDING.length);

                logger.debug("* Data to encrypt: " + Conversion.arrayToHex(data));

                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "DESede"), ivSpec);
                byte[] cardcryptogram = myCipher.doFinal(data);
                setCardCrypto(new byte[8]);
                System.arraycopy(cardcryptogram, 16, getCardCrypto(), 0, 8);

                logger.debug("* Calculated Card Crypto: " + Conversion.arrayToHex(getCardCrypto()));

                /* Calculing Host Cryptogram */
                System.arraycopy(getSequenceCounter(), 0, data, 0, 2);
                System.arraycopy(getCardChallenge(), 0, data, 2, 6);
                System.arraycopy(getHostChallenge(), 0, data, 8, 8);
                System.arraycopy(PADDING, 0, data, 16, PADDING.length);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSessEnc(), "DESede"), ivSpec);
                byte[] hostcryptogram = myCipher.doFinal(data);
                setHostCrypto(new byte[8]);
                System.arraycopy(hostcryptogram, 16, getHostCrypto(), 0, 8);

                logger.debug("* Calculated Host Crypto: " + Conversion.arrayToHex(getHostCrypto()));

            } else if (getScpMode() == SCPMode.SCP_03_65
                    || getScpMode() == SCPMode.SCP_03_6D
                    || getScpMode() == SCPMode.SCP_03_05
                    || getScpMode() == SCPMode.SCP_03_0D
                    || getScpMode() == SCPMode.SCP_03_2D
                    || getScpMode() == SCPMode.SCP_03_25) {

                logger.debug("* SCP 03 protocol used");

                /* Calculing Card Cryptogram */
                getDerivationData()[11] = SCP03_DERIVATION4CardCryptogram;
                getDerivationData()[13] = (byte) 0x00;
                getDerivationData()[14] = (byte) 0x40;
                getDerivationData()[15] = (byte) 0x01;

                logger.debug("* derivation data : " + Conversion.arrayToHex(getDerivationData()));


                SecretKeySpec skeySpec = new SecretKeySpec(getSessMac(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

                byte icvNextBloc[] = new byte[16];
                int noOfBlocks = getDerivationData().length / 16;

                int startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(getDerivationData());
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                setCardCrypto(new byte[8]);
                System.arraycopy(icvNextBloc, 0, getCardCrypto(), 0, 8);
                logger.debug("Calculated Card Cryptogram = " + Conversion.arrayToHex(getCardCrypto()));

                ivSpec = new IvParameterSpec(getIcv());

                /* Calculing Card Cryptogram */
                getDerivationData()[11] = SCP03_DERIVATION4HostCryptogram;
                getDerivationData()[13] = (byte) 0x00;
                getDerivationData()[14] = (byte) 0x40;
                getDerivationData()[15] = (byte) 0x01;

                logger.debug("* derivation data : " + Conversion.arrayToHex(getDerivationData()));


                skeySpec = new SecretKeySpec(getSessMac(), "AES");
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

                noOfBlocks = getDerivationData().length / 16;

                startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(getDerivationData(), startIndex, 16);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                setHostCrypto(new byte[8]);
                System.arraycopy(icvNextBloc, 0, getHostCrypto(), 0, 8);
                logger.debug("Calculated Host Cryptogram = " + Conversion.arrayToHex(getHostCrypto()));


            }

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
        } catch (InvalidAlgorithmParameterException e) {
            throw new UnsupportedOperationException("Invalid Algorithm parameter", e);
        }

        logger.debug("==> Calculate Cryptograms End");
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
            logger.debug("* SCP_Mode is " + getScpMode());

            if ((getScpMode() == SCPMode.SCP_UNDEFINED)
                    || (getScpMode() == SCPMode.SCP_01_05)
                    || (getScpMode() == SCPMode.SCP_01_15)) {  // TODO: SCPMode.SCP_UNDEFINED Here ?

                setSessEnc(new byte[24]);
                setSessMac(new byte[24]);
                setSessKek(new byte[24]);

                myCipher = Cipher.getInstance("DESede/ECB/NoPadding");

                /* Calculating session encryption key */
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKenc.getValue(), "DESede"));
                session = myCipher.doFinal(getDerivationData());
                System.arraycopy(session, 0, getSessEnc(), 0, 16);
                System.arraycopy(session, 0, getSessEnc(), 16, 8);

                logger.debug("* sessEnc = " + Conversion.arrayToHex(getSessEnc()));

                /* Calculating session mac key */
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKmac.getValue(), "DESede"));
                session = myCipher.doFinal(getDerivationData());
                System.arraycopy(session, 0, getSessMac(), 0, 16);
                System.arraycopy(session, 0, getSessMac(), 16, 8);

                logger.debug("* sessMac = " + Conversion.arrayToHex(getSessMac()));

                /* Calculating session data encryption key */
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKkek.getValue(), "DESede"));
                session = myCipher.doFinal(getDerivationData());
                System.arraycopy(session, 0, getSessKek(), 0, 16);
                System.arraycopy(session, 0, getSessKek(), 16, 8);

                logger.debug("* sessKek = " + Conversion.arrayToHex(getSessKek()));

            } else if (getScpMode() == SCPMode.SCP_02_15
                    || getScpMode() == SCPMode.SCP_02_04
                    || getScpMode() == SCPMode.SCP_02_05
                    || getScpMode() == SCPMode.SCP_02_14
                    || getScpMode() == SCPMode.SCP_02_45
                    || getScpMode() == SCPMode.SCP_02_55) {

                setSessEnc(new byte[24]);
                setSessMac(new byte[24]);
                setSessRMac(new byte[24]);
                setSessKek(new byte[24]);

                myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
                IvParameterSpec ivSpec = new IvParameterSpec(getIcv());

                logger.debug("*** Initialize IV : " + Conversion.arrayToHex(getSessEnc()));

                // Calculing Encryption Session Keys
                System.arraycopy(SCP02_DERIVATION4ENCKEY, 0, getDerivationData(), 0, 2);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKenc.getValue(), "DESede"), ivSpec);
                session = myCipher.doFinal(getDerivationData());
                System.arraycopy(session, 0, getSessEnc(), 0, 16);
                System.arraycopy(session, 0, getSessEnc(), 16, 8);

                logger.debug("* sessEnc = " + Conversion.arrayToHex(getSessEnc()));

                // Calculing C_Mac Session Keys
                System.arraycopy(SCP02_DERIVATION4CMAC, 0, getDerivationData(), 0, 2);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKmac.getValue(), "DESede"), ivSpec);
                session = myCipher.doFinal(getDerivationData());
                System.arraycopy(session, 0, getSessMac(), 0, 16);
                System.arraycopy(session, 0, getSessMac(), 16, 8);

                logger.debug("* sessMac = " + Conversion.arrayToHex(getSessMac()));

                // Calculing R_Mac Session Keys
                System.arraycopy(SCP02_DERIVATION4RMAC, 0, getDerivationData(), 0, 2);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKmac.getValue(), "DESede"), ivSpec);
                session = myCipher.doFinal(getDerivationData());
                System.arraycopy(session, 0, getSessRMac(), 0, 16);
                System.arraycopy(session, 0, getSessRMac(), 16, 8);

                logger.debug("* sessRMac = " + Conversion.arrayToHex(getSessRMac()));

                // Calculing Data Encryption Session Keys
                System.arraycopy(SCP02_DERIVATION4DATAENC, 0, getDerivationData(), 0, 2);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKkek.getValue(), "DESede"), ivSpec);
                session = myCipher.doFinal(getDerivationData());
                System.arraycopy(session, 0, getSessKek(), 0, 16);
                System.arraycopy(session, 0, getSessKek(), 16, 8);

                logger.debug("* sessKek = " + Conversion.arrayToHex(getSessRMac()));

            } else if ((getScpMode() == SCPMode.SCP_03_65)
                    || (getScpMode() == SCPMode.SCP_03_6D)
                    || (getScpMode() == SCPMode.SCP_03_05)
                    || (getScpMode() == SCPMode.SCP_03_0D)
                    || (getScpMode() == SCPMode.SCP_03_2D)
                    || (getScpMode() == SCPMode.SCP_03_25)) {

                logger.debug("derivation data : " + Conversion.arrayToHex(getDerivationData()));

                setSessEnc(new byte[16]);
                setSessMac(new byte[16]);
                setSessRMac(new byte[16]);

                IvParameterSpec ivSpec = new IvParameterSpec(getIcv());
                logger.debug("*** Initialize IV : " + Conversion.arrayToHex(getIcv()));


                SecretKeySpec skeySpec = new SecretKeySpec(staticKenc.getValue(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                logger.debug("key : " + Conversion.arrayToHex(skeySpec.getEncoded()));

                // Calculing Encryption Session Keys
                getDerivationData()[11] = SCP03_DERIVATION4DATAENC;
                getDerivationData()[13] = (byte) 0x00;
                getDerivationData()[14] = (byte) 0xC0;
                getDerivationData()[15] = (byte) 0x02;

                byte icvNextBloc[] = new byte[16];
                int noOfBlocks = getDerivationData().length / 16;

                int startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(getDerivationData(), startIndex, 16);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
//                    logger.debug("derivation data : " + Conversion.arrayToHex(derivationData));
//                    logger.debug("Icv for block = " + Conversion.arrayToHex(icvNextBloc));
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                System.arraycopy(icvNextBloc, 0, getSessEnc(), 0, icvNextBloc.length);
                logger.debug("sessEnc = " + Conversion.arrayToHex(getSessEnc()));

                ivSpec = new IvParameterSpec(getIcv());


                // Calculing C_Mac Session Keys
                getDerivationData()[11] = SCP03_DERIVATION4CMAC;
                getDerivationData()[13] = (byte) 0x00;
                getDerivationData()[14] = (byte) 0xC0;
                getDerivationData()[15] = (byte) 0x02;


                noOfBlocks = getDerivationData().length / 16;

                startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(getDerivationData(), startIndex, 16);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
//                    logger.debug("derivation data : " + Conversion.arrayToHex(derivationData));
//                    logger.debug("Icv for block = " + Conversion.arrayToHex(icvNextBloc));
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                System.arraycopy(icvNextBloc, 0, getSessMac(), 0, icvNextBloc.length);
                logger.debug("sessMac = " + Conversion.arrayToHex(getSessMac()));

                ivSpec = new IvParameterSpec(getIcv());

                // Calculing R_Mac Session Keys
                getDerivationData()[11] = SCP03_DERIVATION4RMAC;
                getDerivationData()[13] = (byte) 0x00;
                getDerivationData()[14] = (byte) 0xC0;
                getDerivationData()[15] = (byte) 0x02;

                noOfBlocks = getDerivationData().length / 16;

                startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(getDerivationData(), startIndex, 16);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
//                    logger.debug("derivation data : " + Conversion.arrayToHex(derivationData));
//                    logger.debug("Icv for block = " + Conversion.arrayToHex(icvNextBloc));
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                System.arraycopy(icvNextBloc, 0, getSessRMac(), 0, icvNextBloc.length);
                logger.debug("sessRMac = " + Conversion.arrayToHex(getSessRMac()));


            }


        } catch (InvalidAlgorithmParameterException ex) {
            throw new UnsupportedOperationException("invalid algorithm parameter", ex);
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

    // SECURITY DOMAIN

    /**
     * Generate the pseudo Random Card Challenge to compare with the Challenge
     * of the card obtained with initializeUpdate Command
     *
     * @param aid AID of the current selected application
     */

    private byte[] pseudoRandomGenerationCardChallenge(byte[] aid) {

        logger.info("==> pseudo Random Generation CardChallenge");
        logger.info("* SCP 02 Protocol (" + getScpMode() + ") used");
        logger.info("* IV is " + Conversion.arrayToHex(iv_zero));

        byte[] computedCardChallenge = null;
        IvParameterSpec ivSpec = new IvParameterSpec(iv_zero);
        byte[] dataWithPadding;
        byte[] res;
        if (aid.length % 8 != 0) { // We need a PADDING

            logger.debug("- Data needs PADDING!");

            int nbBytes = 8 - (aid.length % 8);
            dataWithPadding = new byte[aid.length + nbBytes];
            System.arraycopy(aid, 0, dataWithPadding, 0, aid.length);
            System.arraycopy(PADDING, 0, dataWithPadding, aid.length, nbBytes);
        } else {
            dataWithPadding = new byte[aid.length + 8];
            System.arraycopy(aid, 0, dataWithPadding, 0, aid.length);
            System.arraycopy(PADDING, 0, dataWithPadding, aid.length, PADDING.length);
        }
        logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));
        try {

            SecretKeySpec desSingleKey = new SecretKeySpec(getSessMac(), 0, 8, "DES");
            Cipher singleDesCipher;
            singleDesCipher = Cipher.getInstance("DES/CBC/NoPadding", "SunJCE");

            // Calculate the first n - 1 block.
            int noOfBlocks = dataWithPadding.length / 8;
            byte ivForNextBlock[] = getIcv();
            int startIndex = 0;
            for (int i = 0; i < (noOfBlocks - 1); i++) {
                singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
                ivForNextBlock = singleDesCipher.doFinal(dataWithPadding, startIndex, 8);
                startIndex += 8;
                ivSpec = new IvParameterSpec(ivForNextBlock);
                logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
            }


            SecretKeySpec desKey = new SecretKeySpec(getSessMac(), "DESede");
            Cipher myCipher;

            myCipher = Cipher.getInstance("DESede/CBC/NoPadding", "SunJCE");
            int offset = dataWithPadding.length - 8;

            // Generate C-MAC. Use 8-LSB
            // For the last block, you can use TripleDES EDE with ECB mode, now I select the CBC and
            // use the last block of the previous encryption result as ICV.
            //ivSpec = new IvParameterSpec(ivForLastBlock);
            myCipher.init(Cipher.ENCRYPT_MODE, desKey, ivSpec);
            res = myCipher.doFinal(dataWithPadding, offset, 8);

            // the CardChallenge is the six MSB of the result(MAC Over AID)
            computedCardChallenge = new byte[6];
            System.arraycopy(res, 0, computedCardChallenge, 0, 6);
            logger.info("pseudo Random Generation CardChallenge computed is " + Conversion.arrayToHex(computedCardChallenge));

        } catch (NoSuchProviderException ex) {
            throw new UnsupportedOperationException("No such provider", ex);
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
        } catch (InvalidAlgorithmParameterException e) {
            throw new UnsupportedOperationException("Invalid Algorithm parameter", e);
        }
        return computedCardChallenge;
    }

    @Override
    public void detectAndInitSCP(byte keyId, SCPMode desiredScp, ResponseAPDU resp, GP2xCommands commands) throws SCPException {

        byte keyVersNumRec = resp.getData()[10];
        byte scpRec = resp.getData()[11];

        byte[] cardCryptoResp = new byte[8];
        byte[] keyDivData = new byte[10];

        if (scpRec == SCP01) {
            if (desiredScp == SCPMode.SCP_UNDEFINED) {
                setScpMode(SCPMode.SCP_01_05);
                logger.trace("Change " + SCPMode.SCP_UNDEFINED + " to " + SCPMode.SCP_01_05);
            } else if (desiredScp == SCPMode.SCP_01_05 || desiredScp == SCPMode.SCP_01_15) {
                setScpMode(desiredScp);
            } else {
                throw new SCPException("Desired SCP does not match with card SCP value (" + scpRec + ")");
            }

            logger.debug("SCPMode is " + getScpMode());

            setCardChallenge(new byte[8]);

            /*
             * INITIALIZE UPDATE response in SCP 01 mode
             * -0-----------------------09-10------11-12------------19-20-------------27-
             * | Key Diversification Data | Key Info | Card Challenge | Card Cryptogram |
             * --------------------------------------------------------------------------
             */

            System.arraycopy(resp.getData(), 0, keyDivData, 0, 10);
            System.arraycopy(resp.getData(), 12, getCardChallenge(), 0, 8);
            System.arraycopy(resp.getData(), 20, cardCryptoResp, 0, 8);

            logger.debug("* Key Diversification Data is " + Conversion.arrayToHex(keyDivData));
            logger.debug("* Host Challenge is " + Conversion.arrayToHex(getHostChallenge()));
            logger.debug("* Card Challenge is " + Conversion.arrayToHex(getCardChallenge()));
            logger.debug("* Card Crypto Resp is " + Conversion.arrayToHex(cardCryptoResp));

        } else if (scpRec == SCP02) {

            if (desiredScp == SCPMode.SCP_UNDEFINED) {
                setScpMode(SCPMode.SCP_02_15);
                logger.trace("Change " + SCPMode.SCP_UNDEFINED + " to " + SCPMode.SCP_02_15);
            } else if (desiredScp == SCPMode.SCP_02_15) {
                setScpMode(desiredScp);
            } else if (desiredScp == SCPMode.SCP_02_14) {
                setScpMode(desiredScp);
            } else if (desiredScp == SCPMode.SCP_02_04) {
                setScpMode(desiredScp);
            } else if (desiredScp == SCPMode.SCP_02_05) {
                setScpMode(desiredScp);
            } else if (desiredScp == SCPMode.SCP_02_45) {
                setScpMode(desiredScp);
            } else if (desiredScp == SCPMode.SCP_02_55) {
                setScpMode(desiredScp);
            } else {
                throw new SCPException("Desired SCP does not match with card SCP value (" + scpRec + ")");
            }

            logger.debug("SCPMode is " + getScpMode());

            setCardChallenge(new byte[6]);
            setSequenceCounter(new byte[2]);

            /*
             * INITIALIZE UPDATE response in SCP 02 mode
             *
             * -0-----------------------09-10------11-12------------- 13-
             * | Key Diversification Data | Key Info | Sequence Counter |
             * ----------------------------------------------------------
             *
             * --14------------19-20-------------27-
             *  | Card Challenge | Card Cryptogram |
             * -------------------------------------
             */

            System.arraycopy(resp.getData(), 0, keyDivData, 0, 10);
            System.arraycopy(resp.getData(), 12, getSequenceCounter(), 0, 2);
            System.arraycopy(resp.getData(), 14, getCardChallenge(), 0, 6);
            System.arraycopy(resp.getData(), 20, cardCryptoResp, 0, 8);

            logger.debug("* Key Diversification Data is " + Conversion.arrayToHex(keyDivData));
            logger.debug("* Sequence Counter is " + Conversion.arrayToHex(getSequenceCounter()));
            logger.debug("* Host Challenge is " + Conversion.arrayToHex(getHostChallenge()));
            logger.debug("* Card Challenge is " + Conversion.arrayToHex(getCardChallenge()));
            logger.debug("* Card Crypto Resp is " + Conversion.arrayToHex(cardCryptoResp));


        } else if (scpRec == SCP03) {


            if (desiredScp == SCPMode.SCP_03_65) {
                setScpMode(desiredScp);
            }
            if (desiredScp == SCPMode.SCP_03_6D) {
                setScpMode(desiredScp);
            }
            if (desiredScp == SCPMode.SCP_03_05) {
                setScpMode(desiredScp);
            }
            if (desiredScp == SCPMode.SCP_03_0D) {
                setScpMode(desiredScp);
            }
            if (desiredScp == SCPMode.SCP_03_2D) {
                setScpMode(desiredScp);
            }
            if (desiredScp == SCPMode.SCP_03_25) {
                setScpMode(desiredScp);
            }


            setSequenceCounter(new byte[3]);
            setCardChallenge(new byte[8]);
            /*
             * INITIALIZE UPDATE response in SCP 03 mode
             *
             * -0-----------------------09-10------12-13-------------20--
             * | Key Diversification Data | Key Info | Card Challenge   |
             * ----------------------------------------------------------
             *
             * --21------------28-29-------------31-
             *  |Card Cryptogram | Sequence Counter|
             * -------------------------------------
             * Sequence Counter is only present when SCP03 is configured for
             * pseudo-random challenge generation.
             */


            System.arraycopy(resp.getData(), 0, keyDivData, 0, 10);
            System.arraycopy(resp.getData(), 13, getCardChallenge(), 0, 8);
            System.arraycopy(resp.getData(), 21, cardCryptoResp, 0, 8);

            if (resp.getData().length == 32) {
                System.arraycopy(resp.getData(), 29, getSequenceCounter(), 0, 3);
                logger.debug("* Sequence Counter is " + Conversion.arrayToHex(getSequenceCounter()));
            }

            logger.debug("* Key Diversification Data is " + Conversion.arrayToHex(keyDivData));
            logger.debug("* Host Challenge is " + Conversion.arrayToHex(getHostChallenge()));
            logger.debug("* Card Challenge is " + Conversion.arrayToHex(getCardChallenge()));
            logger.debug("* Card Crypto Resp is " + Conversion.arrayToHex(cardCryptoResp));

        } else {
            throw new SCPException("SCP version not available (" + scpRec + ")");
        }

        if (keyId == (byte) 0) {
            keyId = (byte) 1;
            logger.trace("key id switchs from 0 to 1");
        }

        SCKey key = commands.getKey(keyVersNumRec, keyId);
        if (key == null) {
            throw new SCPException("Selected key not found in local repository (keySetVersion: "
                    + (keyVersNumRec & 0xff) + ", keyId: " + keyId + ")");
        }

        SCGPKey kEnc = null;
        SCGPKey kMac = null;
        SCGPKey kKek = null;


        if (getScpMode() == SCPMode.SCP_01_15 || getScpMode() == SCPMode.SCP_01_05) {
            if (key instanceof SCDerivableKey) {
                SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
                kEnc = keysFromDerivableKey[0];
                kMac = keysFromDerivableKey[1];
                kKek = keysFromDerivableKey[2];
            } else {
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) commands.getKey(keyVersNumRec, (byte) (++keyId));
                if (kMac == null) {
                    throw new SCPException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) commands.getKey(keyVersNumRec, (byte) (++keyId));
                if (kKek == null) {
                    throw new SCPException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
            }
        }


        if (getScpMode() == SCPMode.SCP_02_15
                || getScpMode() == SCPMode.SCP_02_45
                || getScpMode() == SCPMode.SCP_02_05
                || getScpMode() == SCPMode.SCP_02_55) {
            if (key instanceof SCDerivableKey) {
                SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
                kEnc = keysFromDerivableKey[0];
                kMac = keysFromDerivableKey[1];
                kKek = keysFromDerivableKey[2];
            } else {
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) commands.getKey(keyVersNumRec, (byte) (++keyId));
                if (kMac == null) {
                    throw new SCPException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) commands.getKey(keyVersNumRec, (byte) (++keyId));
                if (kKek == null) {
                    throw new SCPException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
            }

        } else if (getScpMode() == SCPMode.SCP_02_04
                || getScpMode() == SCPMode.SCP_02_14) {
            if (key instanceof SCDerivableKey) {
                SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
                kEnc = keysFromDerivableKey[0];
                kMac = keysFromDerivableKey[0];
                kKek = keysFromDerivableKey[0];
            } else {
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) commands.getKey(keyVersNumRec, (byte) (keyId));
                if (kMac == null) {
                    throw new SCPException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) commands.getKey(keyVersNumRec, (byte) (keyId));
                if (kKek == null) {
                    throw new SCPException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
            }
        }

        if ((getScpMode() == SCPMode.SCP_03_65)
                || (getScpMode() == SCPMode.SCP_03_6D)
                || (getScpMode() == SCPMode.SCP_03_05)
                || (getScpMode() == SCPMode.SCP_03_0D)
                || (getScpMode() == SCPMode.SCP_03_2D)
                || (getScpMode() == SCPMode.SCP_03_25)) {
            initIcv();
            if (key instanceof SCDerivableKey) {
                SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
                kEnc = keysFromDerivableKey[0];
                kMac = keysFromDerivableKey[0];
                kKek = keysFromDerivableKey[0];
            } else {
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) commands.getKey(keyVersNumRec, (byte) (keyId));
                if (kMac == null) {
                    throw new SCPException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) commands.getKey(keyVersNumRec, (byte) (keyId));
                if (kKek == null) {
                    throw new SCPException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
            }
        }

        calculateDerivationData();
        generateSessionKeys(kEnc, kMac, kKek);
        calculateCryptograms();


        if (getScpMode() == SCPMode.SCP_02_45 || getScpMode() == SCPMode.SCP_02_55) {
            byte[] computedCardChallenge = new byte[6];
            computedCardChallenge = pseudoRandomGenerationCardChallenge(commands.getAid());
            if (!Arrays.equals(getCardChallenge(), computedCardChallenge)) {
                logger.debug("Card challege is " + Conversion.arrayToHex(getCardChallenge()) + "   " + getCardChallenge().length);
                throw new SCPException("Error verifying Card Challenge");
            }
        }


        if (!Arrays.equals(cardCryptoResp, getCardCrypto())) {
            throw new SCPException("Error verifying Card Cryptogram");
        }
    }

    /**
     * Compute and verify the Rmac recieved.
     *
     * @response Response Message receved by the off card entity
     */
    public void compudeAndVerifyRMac(byte[] response) throws SCPException {
        if (getScpMode() == SCPMode.SCP_03_65
                || getScpMode() == SCPMode.SCP_03_6D
                || getScpMode() == SCPMode.SCP_03_25) {
            if ((this.getSecMode() == SecLevel.R_MAC) ||
                    (this.getSecMode() == SecLevel.C_MAC_AND_R_MAC) ||
                    (this.getSecMode() == SecLevel.C_ENC_AND_C_MAC_AND_R_MAC) ||
                    (this.getSecMode() == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC)) {
                byte[] recevedRmac = new byte[8];
                byte[] data;
                if (response.length > 10)// the response contain data
                {
                    data = new byte[16 + (response.length - 10) + 2];
                    System.arraycopy(getIcv(), 0, data, 0, getIcv().length);
                    System.arraycopy(response, 0, data, 16, response.length - 10);
                    System.arraycopy(response, response.length - 2, data, 16 + response.length - 10, 2);

                } else    //the response does not contain data
                {
                    data = new byte[18];
                    System.arraycopy(getIcv(), 0, data, 0, getIcv().length);
                    System.arraycopy(response, response.length - 2, data, 16, 2);
                }

                logger.debug("* data used to  calculate RMac: " + Conversion.arrayToHex(data));

                byte[] copyOfIcv = new byte[16];
                System.arraycopy(getIcv(), 0, copyOfIcv, 0, getIcv().length);


                byte[] Rmac = generateMac(data);

                logger.debug("* Computed RMac is : " + Conversion.arrayToHex(Rmac));

                System.arraycopy(response, response.length - 10, recevedRmac, 0, recevedRmac.length);

                logger.debug("* Receved RMac is : " + Conversion.arrayToHex(recevedRmac));

                System.arraycopy(copyOfIcv, 0, getIcv(), 0, copyOfIcv.length);

                boolean eq = Arrays.equals(Rmac, recevedRmac);
                if (!eq) {
                    throw new SCPException("Response APDU error - RMAC Not verification error: ");
                }


            }
        }
    }

    @Override
    public void setRENC_counter(int RENC_counter) {
        this.RENC_counter = RENC_counter;
    }

    @Override
    public int getCENC_Counter() {
        return CENC_Counter;
    }

    @Override
    public void setCENC_Counter(int CENC_Counter) {
        this.CENC_Counter = CENC_Counter;
    }
}
