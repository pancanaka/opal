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
package fr.xlim.ssd.opal.library.commands;

import fr.xlim.ssd.opal.library.config.SCDerivableKey;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCKey;
import fr.xlim.ssd.opal.library.config.SCPMode;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.library.utilities.RandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Implementation of Global Platform specification
 *
 * @author Damien Arcuset, Eric Linke
 * @author Guillaume Bouffard
 */
public class GP2xCommands extends AbstractCommands implements Commands {

    /// Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(GP2xCommands.class);

    /// Default PADDING to encrypt data for SCP 02 and SCP 01
    protected static final byte[] PADDING = Conversion.hexToArray("80 00 00 00 00 00 00 00");

    /// Default PADDING to encrypt data for SCP 03
    protected static final byte[] SCP03_PADDING = Conversion.hexToArray("80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    // SCP 01 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP01 = (byte) 0x01;

    // SCP 02 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP02 = (byte) 0x02;

    // SCP 03 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP03 = (byte) 0x03;

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

    /// Static Keys
    protected List<SCKey> keys = new LinkedList<SCKey>();

    /// Secure Channel Protocol used
    protected SCPMode scp;

    /// Secure Level used to communicate
    protected SecLevel secMode;

    /// State of smart card session (NO_SESSION, SESSION_INIT or SESSION_AUTH)
    protected SessionState sessState;

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

    /// Initialized Cypher Vector used to initialized encryption steps
    protected byte[] icv;

    /// Sequence counter used in SCP 02. Its value is the number of previous validate authentication
    protected byte[] sequenceCounter;

    protected byte[] aid;

    private static byte[] iv_zero = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

    private static byte[] iv_zero_scp03 = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

    // used to calculate Encrypted counter ICV for C-ENC
    protected int CENC_Counter = 01;

    // used to calculate Encrypted counter ICV for R-ENC
    protected int RENC_counter = 01;

    //Counter ICV padding for R-ENC computing
    protected static final byte[] SCP03_R_ENC_COUNTER_ICV_PADDING = Conversion.hexToArray("80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    //Counter ICV padding for R-ENC computing
    protected static final byte[] SCP03_C_ENC_COUNTER_ICV_PADDING = Conversion.hexToArray("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    /**
     * Default constructor
     */
    public GP2xCommands() {
        resetParams();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#getScp()
     */
    @Override
    public SCPMode getScp() {
        return this.scp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#getSessState()
     */
    @Override
    public SessionState getSessState() {
        return this.sessState;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#getSecMode()
     */
    @Override
    public SecLevel getSecMode() {
        return this.secMode;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#getKeys()
     */
    @Override
    public SCKey[] getKeys() {
        return this.keys.toArray(new SCKey[this.keys.size()]);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#getKey(byte, byte)
     */
    @Override
    public SCKey getKey(byte keySetVersion, byte keyId) {
        for (SCKey currKey : this.keys) {
            if (currKey.getVersion() == keySetVersion && currKey.getId() == keyId) {
                return currKey;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#setOffCardKey(fr.xlim.ssd.opal.library.SCKey)
     */
    @Override
    public SCKey setOffCardKey(SCKey key) {
        for (SCKey currKey : this.keys) {
            if (currKey.getVersion() == key.getVersion() && currKey.getId() == key.getId()) {
                this.keys.remove(currKey);
                this.keys.add(key);
                return currKey;
            }
        }
        this.keys.add(key);
        return key;     // Todo: return input key is useless
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#setOffCardKeys(fr.xlim.ssd.opal.library.SCKey[])
     */
    @Override
    public void setOffCardKeys(SCKey[] keys) {
        for (SCKey key : keys) {
            this.setOffCardKey(key);
        }
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#deleteOffCardKey(int, int)
     */
    // TODO: why int insted of byte in parameter ?

    @Override
    public SCKey deleteOffCardKey(byte keySetVersion, byte keyId) {
        for (SCKey currKey : this.keys) {
            if (currKey.getVersion() == keySetVersion && currKey.getId() == keyId) {
                this.keys.remove(currKey);
                return currKey;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#select(byte[])
     */
    @Override
    public ResponseAPDU select(byte[] aid) throws CardException {

        this.aid = new byte[aid.length];
        System.arraycopy(aid, 0, this.aid, 0, aid.length);

        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC
        byte[] selectComm = new byte[headerSize + aid.length];

        selectComm[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x00; // (CLA) command class
        selectComm[ISO7816.OFFSET_INS.getValue()] = (byte) 0xA4; // (INS) SELECT command
        selectComm[ISO7816.OFFSET_P1.getValue()] = (byte) 0x04; // (P1) SELECT by name
        selectComm[ISO7816.OFFSET_P2.getValue()] = (byte) 0x00; // (P2) first or only occurrence
        selectComm[ISO7816.OFFSET_LC.getValue()] = (byte) aid.length; // (LC) data length

        System.arraycopy(aid, 0, selectComm, 5, aid.length); // put the AID into selectComm

        CommandAPDU cmdSelect = new CommandAPDU(selectComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdSelect);
        logger.debug("SELECT Command "
                + "(-> " + Conversion.arrayToHex(cmdSelect.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");
        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            this.resetParams();
            throw new CardException("Invalid response SW after SELECT command (" + Integer.toHexString(resp.getSW()) + ")");
        }
        return resp;

    }


    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#select(byte[],SCPMode)
     */
    @Override
    public ResponseAPDU select(byte[] aid, SCPMode desiredScp) throws CardException {

        this.sequenceCounter = new byte[2];
        if (true) {
            this.scp = desiredScp;

            ResponseAPDU resp = select(aid);

            // get the value of Secure Channel Sequence Counter,Calculate derivation data
            getData();
            calculateDerivationData();
            return resp;
        } else {
            this.resetParams();
            throw new CardException("this SELECT Command is used for card which implement SCPMode (SCP 02_0A or SCP_02_0B) ");
        }


    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#resetParams()
     */
    @Override
    public void resetParams() {
        this.initIcv();
        this.scp = SCPMode.SCP_UNDEFINED;
        this.secMode = SecLevel.NO_SECURITY_LEVEL;
        this.sessState = SessionState.NO_SESSION;
        this.sessEnc = null;
        this.sessMac = null;
        this.sessRMac = null;
        this.sessKek = null;
        this.derivationData = null;
        this.hostCrypto = null;
        this.cardCrypto = null;
        this.cardChallenge = null;
        this.hostChallenge = null;

    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#initializeUpdate(byte, byte, fr.xlim.ssd.opal.library.SCP_Mode)
     */
    @Override
    public ResponseAPDU initializeUpdate(byte keySetVersion, byte keyId, SCPMode desiredScp) throws CardException {

        logger.debug("=> Initialize Update");
        this.scp = desiredScp;
        this.resetParams();
        this.hostChallenge = RandomGenerator.generateRandom(8);

        byte[] initUpdCmd = new byte[13];
        initUpdCmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80;
        initUpdCmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0x50;
        initUpdCmd[ISO7816.OFFSET_P1.getValue()] = keySetVersion;
        initUpdCmd[ISO7816.OFFSET_P2.getValue()] = keyId;
        initUpdCmd[ISO7816.OFFSET_LC.getValue()] = (byte) this.hostChallenge.length;

        System.arraycopy(this.hostChallenge, 0, initUpdCmd, 5, this.hostChallenge.length);

        CommandAPDU cmdInitUpd = new CommandAPDU(initUpdCmd);

        ResponseAPDU resp = this.getCardChannel().transmit(cmdInitUpd);

        logger.debug("INIT UPDATE command "
                + "(-> " + Conversion.arrayToHex(cmdInitUpd.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            this.resetParams();
            throw new CardException("Invalid response SW after first INIT UPDATE command (" + resp.getSW() + ")");
        }

        /*
         * Verify the length of response data
         * 28 => SCP02 and SCP03
         * 32 => SCP03 configured for pseudo-random challenge generation.
         * 29 => SCP03
         */
        if (resp.getData().length != 28 && resp.getData().length != 32 && resp.getData().length != 29) {
            this.resetParams();
            throw new CardException("Invalid response size after first INIT UPDATE command ("
                    + resp.getData().length + ")");
        }

        byte[] cardCryptoResp = new byte[8];
        byte[] keyDivData = new byte[10];

        byte keyVersNumRec = resp.getData()[10];
        byte scpRec = resp.getData()[11];

        if (scpRec == SCP01) {
            if (desiredScp == SCPMode.SCP_UNDEFINED) {
                this.scp = SCPMode.SCP_01_05;
                logger.trace("Change " + SCPMode.SCP_UNDEFINED + " to " + SCPMode.SCP_01_05);
            } else if (desiredScp == SCPMode.SCP_01_05 || desiredScp == SCPMode.SCP_01_15) {
                this.scp = desiredScp;
            } else {
                this.resetParams();
                throw new CardException("Desired SCP does not match with card SCP value (" + scpRec + ")");
            }

            logger.debug("SCPMode is " + this.scp);

            this.cardChallenge = new byte[8];

            /*
             * INITIALIZE UPDATE response in SCP 01 mode
             * -0-----------------------09-10------11-12------------19-20-------------27-
             * | Key Diversification Data | Key Info | Card Challenge | Card Cryptogram |
             * --------------------------------------------------------------------------
             */

            System.arraycopy(resp.getData(), 0, keyDivData, 0, 10);
            System.arraycopy(resp.getData(), 12, this.cardChallenge, 0, 8);
            System.arraycopy(resp.getData(), 20, cardCryptoResp, 0, 8);

            logger.debug("* Key Diversification Data is " + Conversion.arrayToHex(keyDivData));
            logger.debug("* Host Challenge is " + Conversion.arrayToHex(this.hostChallenge));
            logger.debug("* Card Challenge is " + Conversion.arrayToHex(this.cardChallenge));
            logger.debug("* Card Crypto Resp is " + Conversion.arrayToHex(cardCryptoResp));

        } else if (scpRec == SCP02) {

            if (desiredScp == SCPMode.SCP_UNDEFINED) {
                this.scp = SCPMode.SCP_02_15;
                logger.trace("Change " + SCPMode.SCP_UNDEFINED + " to " + SCPMode.SCP_02_15);
            } else if (desiredScp == SCPMode.SCP_02_15) {
                this.scp = desiredScp;
            } else if (desiredScp == SCPMode.SCP_02_14) {
                this.scp = desiredScp;
            } else if (desiredScp == SCPMode.SCP_02_04) {
                this.scp = desiredScp;
            } else if (desiredScp == SCPMode.SCP_02_05) {
                this.scp = desiredScp;
            } else if (desiredScp == SCPMode.SCP_02_45) {
                this.scp = desiredScp;
            } else if (desiredScp == SCPMode.SCP_02_55) {
                this.scp = desiredScp;
            } else {
                this.resetParams();
                throw new CardException("Desired SCP does not match with card SCP value (" + scpRec + ")");
            }

            logger.debug("SCPMode is " + this.scp);

            this.cardChallenge = new byte[6];
            this.sequenceCounter = new byte[2];

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
            System.arraycopy(resp.getData(), 12, this.sequenceCounter, 0, 2);
            System.arraycopy(resp.getData(), 14, this.cardChallenge, 0, 6);
            System.arraycopy(resp.getData(), 20, cardCryptoResp, 0, 8);

            logger.debug("* Key Diversification Data is " + Conversion.arrayToHex(keyDivData));
            logger.debug("* Sequence Counter is " + Conversion.arrayToHex(this.sequenceCounter));
            logger.debug("* Host Challenge is " + Conversion.arrayToHex(this.hostChallenge));
            logger.debug("* Card Challenge is " + Conversion.arrayToHex(this.cardChallenge));
            logger.debug("* Card Crypto Resp is " + Conversion.arrayToHex(cardCryptoResp));


        } else if (scpRec == SCP03) {


            if (desiredScp == SCPMode.SCP_03_65) {
                this.scp = desiredScp;
            }
            if (desiredScp == SCPMode.SCP_03_6D) {
                this.scp = desiredScp;
            }
            if (desiredScp == SCPMode.SCP_03_05) {
                this.scp = desiredScp;
            }
            if (desiredScp == SCPMode.SCP_03_0D) {
                this.scp = desiredScp;
            }
            if (desiredScp == SCPMode.SCP_03_2D) {
                this.scp = desiredScp;
            }
            if (desiredScp == SCPMode.SCP_03_25) {
                this.scp = desiredScp;
            }


            this.sequenceCounter = new byte[3];
            this.cardChallenge = new byte[8];
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
            System.arraycopy(resp.getData(), 13, this.cardChallenge, 0, 8);
            System.arraycopy(resp.getData(), 21, cardCryptoResp, 0, 8);

            if (resp.getData().length == 32) {
                System.arraycopy(resp.getData(), 29, this.sequenceCounter, 0, 3);
                logger.debug("* Sequence Counter is " + Conversion.arrayToHex(this.sequenceCounter));
            }

            logger.debug("* Key Diversification Data is " + Conversion.arrayToHex(keyDivData));
            logger.debug("* Host Challenge is " + Conversion.arrayToHex(this.hostChallenge));
            logger.debug("* Card Challenge is " + Conversion.arrayToHex(this.cardChallenge));
            logger.debug("* Card Crypto Resp is " + Conversion.arrayToHex(cardCryptoResp));

        } else {
            this.resetParams();
            throw new CardException("SCP version not available (" + scpRec + ")");
        }

        if (keyId == (byte) 0) {
            keyId = (byte) 1;
            logger.trace("key id switchs from 0 to 1");
        }

        SCKey key = this.getKey(keyVersNumRec, keyId);
        if (key == null) {
            this.resetParams();
            throw new CardException("Selected key not found in local repository (keySetVersion: "
                    + (keyVersNumRec & 0xff) + ", keyId: " + keyId + ")");
        }

        SCGPKey kEnc = null;
        SCGPKey kMac = null;
        SCGPKey kKek = null;


        if (scp == SCPMode.SCP_01_15 || scp == SCPMode.SCP_01_05) {
            if (key instanceof SCDerivableKey) {
                SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
                kEnc = keysFromDerivableKey[0];
                kMac = keysFromDerivableKey[1];
                kKek = keysFromDerivableKey[2];
            } else {
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) this.getKey(keyVersNumRec, (byte) (++keyId));
                if (kMac == null) {
                    this.resetParams();
                    throw new CardException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) this.getKey(keyVersNumRec, (byte) (++keyId));
                if (kKek == null) {
                    this.resetParams();
                    throw new CardException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
            }
        }


        if (scp == SCPMode.SCP_02_15
                || scp == SCPMode.SCP_02_45
                || scp == SCPMode.SCP_02_05
                || scp == SCPMode.SCP_02_55) {
            if (key instanceof SCDerivableKey) {
                SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
                kEnc = keysFromDerivableKey[0];
                kMac = keysFromDerivableKey[1];
                kKek = keysFromDerivableKey[2];
            } else {
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) this.getKey(keyVersNumRec, (byte) (++keyId));
                if (kMac == null) {
                    this.resetParams();
                    throw new CardException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) this.getKey(keyVersNumRec, (byte) (++keyId));
                if (kKek == null) {
                    this.resetParams();
                    throw new CardException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
            }

        } else if (scp == SCPMode.SCP_02_04
                || scp == SCPMode.SCP_02_14) {
            if (key instanceof SCDerivableKey) {
                SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
                kEnc = keysFromDerivableKey[0];
                kMac = keysFromDerivableKey[0];
                kKek = keysFromDerivableKey[0];
            } else {
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) this.getKey(keyVersNumRec, (byte) (keyId));
                if (kMac == null) {
                    this.resetParams();
                    throw new CardException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) this.getKey(keyVersNumRec, (byte) (keyId));
                if (kKek == null) {
                    this.resetParams();
                    throw new CardException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
            }
        }

        if ((scp == SCPMode.SCP_03_65)
                || (scp == SCPMode.SCP_03_6D)
                || (scp == SCPMode.SCP_03_05)
                || (scp == SCPMode.SCP_03_0D)
                || (scp == SCPMode.SCP_03_2D)
                || (scp == SCPMode.SCP_03_25)) {
            this.initIcv();
            if (key instanceof SCDerivableKey) {
                SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
                kEnc = keysFromDerivableKey[0];
                kMac = keysFromDerivableKey[0];
                kKek = keysFromDerivableKey[0];
            } else {
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) this.getKey(keyVersNumRec, (byte) (keyId));
                if (kMac == null) {
                    this.resetParams();
                    throw new CardException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) this.getKey(keyVersNumRec, (byte) (keyId));
                if (kKek == null) {
                    this.resetParams();
                    throw new CardException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
            }
        }

        this.calculateDerivationData();
        this.generateSessionKeys(kEnc, kMac, kKek);
        this.calculateCryptograms();


        if (this.scp == SCPMode.SCP_02_45 || this.scp == SCPMode.SCP_02_55) {
            byte[] computedCardChallenge = new byte[6];
            computedCardChallenge = this.pseudoRandomGenerationCardChallenge(this.aid);
            if (!Arrays.equals(this.cardChallenge, computedCardChallenge)) {
                logger.debug("Card challege is " + Conversion.arrayToHex(cardChallenge) + "   " + cardChallenge.length);
                throw new CardException("Error verifying Card Challenge");
            }
        }


        if (!Arrays.equals(cardCryptoResp, this.cardCrypto)) {
            this.resetParams();
            throw new CardException("Error verifying Card Cryptogram");
        }

        this.sessState = SessionState.SESSION_INIT;

        logger.debug("Session State is now " + SessionState.SESSION_INIT);

        logger.debug("=> Initialize Update end");

        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#externalAuthenticate(fr.xlim.ssd.opal.library.SecLevel)
     */
    @Override

    public ResponseAPDU externalAuthenticate(SecLevel secLevel) throws CardException {

        logger.debug("=> External Authenticate begin");

        if (secLevel == null) {
            throw new IllegalArgumentException("secLevel must be not null");
        }

        if (this.sessState != SessionState.SESSION_INIT) {
            this.resetParams();
            throw new CardException("Session is not initialized");
        }

        this.secMode = secLevel;

        logger.debug("* Sec Mode is" + this.secMode);

        byte[] extAuthCmd = new byte[21];
        extAuthCmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x84;
        extAuthCmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0x82;
        extAuthCmd[ISO7816.OFFSET_P1.getValue()] = this.secMode.getVal();
        extAuthCmd[ISO7816.OFFSET_P2.getValue()] = (byte) 0x00;
        extAuthCmd[ISO7816.OFFSET_LC.getValue()] = (byte) 0x10;

        System.arraycopy(this.hostCrypto, 0, extAuthCmd, 5, this.hostCrypto.length);
        byte[] data = new byte[5 + this.hostCrypto.length];
        System.arraycopy(extAuthCmd, 0, data, 0, data.length);

        logger.debug("* Data uses to calculate mac value is" + Conversion.arrayToHex(data));

        byte[] mac = this.generateMac(data);

        logger.debug("* mac value obtains" + Conversion.arrayToHex(mac));

        System.arraycopy(mac, 0, extAuthCmd, extAuthCmd.length - mac.length, mac.length);
        CommandAPDU cmdExtauth = new CommandAPDU(extAuthCmd);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdExtauth);

        logger.debug("EXTERNAL AUTHENTICATE command "
                + "(-> " + Conversion.arrayToHex(cmdExtauth.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            this.resetParams();
            throw new CardException("Error in External Authenticate : " + Integer.toHexString(resp.getSW()));
        }
        this.sessState = SessionState.SESSION_AUTH;

        //Protocol SCP03 - intitialisation of counters for computing of counter ICV for C/R-Enc.
        CENC_Counter = 1;
        RENC_counter = 1;

        logger.debug("=> External Authenticate end");

        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getStatus(fr.xlim.ssd.opal.Constant.FileType, fr.xlim.ssd.opal.Constant.GetStatusResponseMode, byte[])
     */

    @Override
    public ResponseAPDU[] getStatus(GetStatusFileType fileType, GetStatusResponseMode responseMode, byte[] searchQualifier) throws CardException {

        logger.debug("=> Get Status begin");

        logger.debug("+ file type is " + fileType);
        logger.debug("+ response mode is " + responseMode);
        logger.debug("+ Search Qualifier is " + (searchQualifier != null ? Conversion.arrayToHex(searchQualifier) : "null"));
        logger.debug("+ SecLevel is " + this.secMode);

        if (fileType == null) {
            throw new IllegalArgumentException("fileType must be not null");
        }

        if (responseMode == null) {
            throw new IllegalArgumentException("responseMode must be not null");
        }

        // TODO: check searchQualifier size ?
        List<ResponseAPDU> responsesList = new LinkedList<ResponseAPDU>();

        byte[] getStatusCmd = null;
        byte dataSize = (byte) 0; // '0xD0' + Key Identifier + '0xD2' + Key Version Number + C-MAC
        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC

        if (searchQualifier == null) {
            searchQualifier = new byte[2];
            searchQualifier[0] = (byte) 0x4F;
            searchQualifier[1] = (byte) 0x00;
            logger.debug("* Search Qualifier equals " + Conversion.arrayToHex(searchQualifier));
        }

        if (this.secMode == SecLevel.NO_SECURITY_LEVEL) {
            dataSize = (byte) (searchQualifier.length); // searchQualifier
            getStatusCmd = new byte[headerSize + dataSize];
            getStatusCmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80;
        } else {
            dataSize = (byte) (searchQualifier.length + 8); // searchQualifier + C-MAC
            getStatusCmd = new byte[headerSize + dataSize];
            getStatusCmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x84;
        }

        getStatusCmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0xF2;
        getStatusCmd[ISO7816.OFFSET_P1.getValue()] = fileType.getValue();
        getStatusCmd[ISO7816.OFFSET_P2.getValue()] = responseMode.getValue();
        getStatusCmd[ISO7816.OFFSET_LC.getValue()] = dataSize;

        logger.debug("* Get Status command is " + Conversion.arrayToHex(getStatusCmd));

        System.arraycopy(searchQualifier, 0, getStatusCmd, 5, searchQualifier.length);

        if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
            byte[] dataCmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC

            logger.debug("* Data used to generate Mac value is " + Conversion.arrayToHex(dataCmac));

            System.arraycopy(getStatusCmd, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, getStatusCmd, dataCmac.length, cmac.length); // put C-MAC into getStatusCmd

            logger.debug("* Get Status command with CMac is " + Conversion.arrayToHex(getStatusCmd));
        }

        byte[] uncipheredgetStatusCmd = getStatusCmd.clone();

        if (this.secMode == SecLevel.C_ENC_AND_MAC) {
            getStatusCmd = this.encryptCommand(getStatusCmd);
            logger.debug("* Encrypt get Status command is " + Conversion.arrayToHex(getStatusCmd));
        }

        CommandAPDU cmdGetstatus = new CommandAPDU(getStatusCmd);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdGetstatus);

        logger.debug("GET STATUS command "
                + "(-> " + Conversion.arrayToHex(cmdGetstatus.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        responsesList.add(resp);

        while (resp.getSW() == ISO7816.SW_MORE_DATA_AVAILABLE.getValue()) {
            uncipheredgetStatusCmd[ISO7816.OFFSET_P2.getValue()] = (byte) (responseMode.getValue() + (byte) 0x01); // Get next occurrence(s)
            if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
                byte[] dataCmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
                System.arraycopy(uncipheredgetStatusCmd, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
                byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
                System.arraycopy(cmac, 0, uncipheredgetStatusCmd, dataCmac.length, cmac.length); // put C-MAC into getStatusCmd
            }
            getStatusCmd = uncipheredgetStatusCmd;
            if (this.secMode == SecLevel.C_ENC_AND_MAC) {
                getStatusCmd = this.encryptCommand(uncipheredgetStatusCmd);
            }
            cmdGetstatus = new CommandAPDU(getStatusCmd);
            resp = this.getCardChannel().transmit(cmdGetstatus);

            logger.debug("GET STATUS command "
                    + "(-> " + Conversion.arrayToHex(cmdGetstatus.getBytes()) + ") "
                    + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

            // TODO: no check at responses ?
            responsesList.add(resp);
        }

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            throw new CardException("Error in Get Status : " + Integer.toHexString(resp.getSW()));
        }

        ResponseAPDU[] r = new ResponseAPDU[responsesList.size()];

        logger.debug("=> Get Status end");

        return responsesList.toArray(r);
    }

    /**
     * Generate mac value according input data in a specific SCP version
     *
     * @param data data used to generate Mac value
     * @return Mac value calculated
     */
    protected byte[] generateMac(byte[] data) {

        logger.debug("==> Generate Mac");

        byte[] dataWithPadding = null;

        logger.debug("generateMac with data: " + Conversion.arrayToHex(data));

        if (data.length % 8 != 0) { // We need a PADDING

            logger.debug("- Data needs PADDING!");

            int nbBytes = 8 - (data.length % 8);
            dataWithPadding = new byte[data.length + nbBytes];
            System.arraycopy(data, 0, dataWithPadding, 0, data.length);
            System.arraycopy(GP2xCommands.PADDING, 0, dataWithPadding, data.length, nbBytes);
        } else {
            dataWithPadding = new byte[data.length + 8];
            System.arraycopy(data, 0, dataWithPadding, 0, data.length);
            System.arraycopy(GP2xCommands.PADDING, 0, dataWithPadding, data.length, GP2xCommands.PADDING.length);
        }

        logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));

        byte[] res = new byte[8];
        IvParameterSpec ivSpec = new IvParameterSpec(this.icv);
        try {
            logger.debug("SCP: " + this.scp);
            if ((this.scp == SCPMode.SCP_UNDEFINED)    // TODO: Undefined SCPMode Here ?
                    || (this.scp == SCPMode.SCP_01_05)
                    || (this.scp == SCPMode.SCP_01_15)) {

                logger.debug("* SCP 01 Protocol (" + this.scp + ") used");
                logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));

                Cipher myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessMac, "DESede"), ivSpec);
                byte[] cryptogram = myCipher.doFinal(dataWithPadding);
                System.arraycopy(cryptogram, cryptogram.length - 8, res, 0, 8);

                logger.debug("* Calculated cryptogram is " + Conversion.arrayToHex(res));

                switch (this.scp) {
                    case SCP_01_05:
                        this.icv = res; // update ICV with new C-MAC
                        break;
                    case SCP_01_15: // update ICV with new ENCRYPTED C-MAC
                        Cipher myCipher2 = Cipher.getInstance("DESede/ECB/NoPadding");
                        myCipher2.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessMac, "DESede"));
                        this.icv = myCipher2.doFinal(res);
                        break;
                }

                logger.debug("* New ICV is " + Conversion.arrayToHex(this.icv));

            } else if (this.scp == SCPMode.SCP_02_15
                    || this.scp == SCPMode.SCP_02_04
                    || this.scp == SCPMode.SCP_02_05
                    || this.scp == SCPMode.SCP_02_14
                    || this.scp == SCPMode.SCP_02_45
                    || this.scp == SCPMode.SCP_02_55) {

                logger.debug("* SCP 02 Protocol (" + this.scp + ") used");
                logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));

                SecretKeySpec desSingleKey = new SecretKeySpec(this.sessMac, 0, 8, "DES");
                Cipher singleDesCipher;
                singleDesCipher = Cipher.getInstance("DES/CBC/NoPadding", "SunJCE");

                // Calculate the first n - 1 block.
                int noOfBlocks = dataWithPadding.length / 8;
                byte ivForNextBlock[] = this.icv;
                int startIndex = 0;
                for (int i = 0; i < (noOfBlocks - 1); i++) {
                    singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
                    ivForNextBlock = singleDesCipher.doFinal(dataWithPadding, startIndex, 8);
                    startIndex += 8;
                    ivSpec = new IvParameterSpec(ivForNextBlock);
                    logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
                }


                SecretKeySpec desKey = new SecretKeySpec(this.sessMac, "DESede");
                Cipher myCipher;

                myCipher = Cipher.getInstance("DESede/CBC/NoPadding", "SunJCE");
                int offset = dataWithPadding.length - 8;

                // Generate C-MAC. Use 8-LSB
                // For the last block, you can use TripleDES EDE with ECB mode, now I select the CBC and
                // use the last block of the previous encryption result as ICV.
                //ivSpec = new IvParameterSpec(ivForLastBlock);
                myCipher.init(Cipher.ENCRYPT_MODE, desKey, ivSpec);
                res = myCipher.doFinal(dataWithPadding, offset, 8);
                if (this.scp == SCPMode.SCP_02_04
                        || this.scp == SCPMode.SCP_02_05
                        || this.scp == SCPMode.SCP_02_45) {
                    this.icv = res; // update ICV with new C-MAC //no ICV encryption
                } else if (this.scp == SCPMode.SCP_02_15
                        || this.scp == SCPMode.SCP_02_14
                        || this.scp == SCPMode.SCP_02_55) {
                    // update ICV with new ENCRYPTED C-MAC
                    ivSpec = new IvParameterSpec(new byte[8]);
                    singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
                    this.icv = singleDesCipher.doFinal(res);
                }

                logger.debug("* Calculated cryptogram is " + Conversion.arrayToHex(res));
                logger.debug("* New ICV is " + Conversion.arrayToHex(this.icv));

            } else if (this.scp == SCPMode.SCP_03_65
                    || this.scp == SCPMode.SCP_03_6D
                    || this.scp == SCPMode.SCP_03_05
                    || this.scp == SCPMode.SCP_03_0D
                    || this.scp == SCPMode.SCP_03_2D
                    || this.scp == SCPMode.SCP_03_25) {


                if (data.length % 16 != 0) { // We need a PADDING

                    logger.debug("- Data needs PADDING!");

                    int nbBytes = 16 - (data.length % 16);
                    dataWithPadding = new byte[data.length + nbBytes];
                    System.arraycopy(data, 0, dataWithPadding, 0, data.length);
                    System.arraycopy(GP2xCommands.SCP03_PADDING, 0, dataWithPadding, data.length, nbBytes);
                } else {
                    dataWithPadding = new byte[data.length + 16];
                    System.arraycopy(data, 0, dataWithPadding, 0, data.length);
                    System.arraycopy(GP2xCommands.SCP03_PADDING, 0, dataWithPadding, data.length, GP2xCommands.SCP03_PADDING.length);
                }

                logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));


                byte[] dataToCalulateMac = new byte[dataWithPadding.length + 16];
                System.arraycopy(this.icv, 0, dataToCalulateMac, 0, this.icv.length);
                System.arraycopy(dataWithPadding, 0, dataToCalulateMac, 16, dataWithPadding.length);

                logger.debug("data used to génerate mac : " + Conversion.arrayToHex(dataToCalulateMac));

                ivSpec = new IvParameterSpec(iv_zero_scp03);
                logger.debug("* SCP 03 Protocol (" + this.scp + ") used");
                logger.debug("* IV is " + Conversion.arrayToHex(this.icv));

                SecretKeySpec skeySpec = new SecretKeySpec(this.sessMac, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                byte[] encrypted = cipher.doFinal(dataToCalulateMac);

                logger.debug("encrypted command : " + Conversion.arrayToHex(encrypted));
                byte[] eightRightMostBit = new byte[8];
                System.arraycopy(encrypted, 0, res, 0, 8);
                System.arraycopy(encrypted, encrypted.length - 8, eightRightMostBit, 0, 8);

                //update ICV : 8 most significant bytes + 8 lest significant bytes
                System.arraycopy(res, 0, this.icv, 0, 8);
                System.arraycopy(eightRightMostBit, 0, this.icv, 8, 8);


            }
        } catch (NoSuchProviderException ex) {
            java.util.logging.Logger.getLogger(GP2xCommands.class.getName()).log(Level.SEVERE, null, ex);
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
     * Encrypt APDU command in a specific SCP version.
     * <p/>
     * Only SCP 01 protocol is yet implemented
     *
     * @param command command to encrypt
     * @return encrypted command
     */
    protected byte[] encryptCommand(byte[] command) {

        logger.debug("==> Encrypt Command Begin");
        logger.debug("* Command to encrypt is " + Conversion.arrayToHex(command));

        byte[] datas = null;
        byte[] encryptedCmd = null;


        try {
            if (scp == SCPMode.SCP_01_05 || scp == SCPMode.SCP_01_15) {

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
                    System.arraycopy(GP2xCommands.PADDING, 0, datas, dataLength, nbBytes); // add necessary PADDING

                    logger.debug("- New data to encrypt is " + Conversion.arrayToHex(datas));
                }
                IvParameterSpec ivSpec = new IvParameterSpec(Conversion.hexToArray("00 00 00 00 00 00 00 00"));

                logger.debug("* SCP 01 Protocol used");
                logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));
                logger.debug("* sessEnc key is " + Conversion.arrayToHex(this.sessEnc));

                Cipher myCipher = Cipher.getInstance("DESede/CBC/NoPadding");

                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), ivSpec);
                byte[] res = myCipher.doFinal(datas);
                encryptedCmd = new byte[5 + res.length + 8];
                System.arraycopy(command, 0, encryptedCmd, 0, 5);
                System.arraycopy(res, 0, encryptedCmd, 5, res.length);
                System.arraycopy(command, command.length - 8, encryptedCmd, res.length + 5, 8);
                encryptedCmd[4] = (byte) (encryptedCmd.length - 5);

                logger.debug("* Encrypted data is " + Conversion.arrayToHex(encryptedCmd));
            }

            if (scp == SCPMode.SCP_02_04
                    || scp == SCPMode.SCP_02_05
                    || scp == SCPMode.SCP_02_0A
                    || scp == SCPMode.SCP_02_0B
                    || scp == SCPMode.SCP_02_14
                    || scp == SCPMode.SCP_02_15
                    || scp == SCPMode.SCP_02_1A
                    || scp == SCPMode.SCP_02_1B
                    || scp == SCPMode.SCP_02_45
                    || scp == SCPMode.SCP_02_54
                    || scp == SCPMode.SCP_02_55) {

                int dataLength = command.length - 5 - 8; // command without (CLA, INS, P1, P2) AND C-MAC

                if (dataLength % 8 == 0) { // don't need a PADDING
                    datas = new byte[dataLength + 8];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(GP2xCommands.PADDING, 0, datas, dataLength, 8);
                    command[4] = (byte) (dataLength + 8);


                } else { // need a PADDING
                    int nbBytes = 8 - (dataLength % 8); // bytes needed for the PADDING
                    logger.debug("- We need a PADDING (" + nbBytes + " bytes) ");
                    datas = new byte[dataLength + nbBytes];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(GP2xCommands.PADDING, 0, datas, dataLength, nbBytes); // add necessary PADDING


                }


                logger.debug("- New data to encrypt is " + Conversion.arrayToHex(datas));
                logger.debug("* DATAFIELD from command is " + Conversion.arrayToHex(datas));


                IvParameterSpec ivSpec = new IvParameterSpec(iv_zero);

                logger.debug("* SCP 02 Protocol used");
                logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));
                logger.debug("* sessEnc key is " + Conversion.arrayToHex(this.sessEnc));

                Cipher myCipher = Cipher.getInstance("DESede/CBC/NoPadding", "SunJCE");
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), ivSpec);
                byte[] res = myCipher.doFinal(datas);
                encryptedCmd = new byte[5 + res.length + 8];
                System.arraycopy(command, 0, encryptedCmd, 0, 5);
                System.arraycopy(res, 0, encryptedCmd, 5, res.length);
                System.arraycopy(command, command.length - 8, encryptedCmd, res.length + 5, 8);
                encryptedCmd[4] = (byte) (datas.length + 8);


                logger.debug("* Encrypted data is " + Conversion.arrayToHex(encryptedCmd));
            }


            if (this.scp == SCPMode.SCP_03_65
                    || this.scp == SCPMode.SCP_03_05
                    || this.scp == SCPMode.SCP_03_25) {

                int dataLength = command.length - 5; // command without (CLA, INS, P1, P2) AND C-MAC

                if (dataLength % 16 == 0) { // don't need a PADDING
                    datas = new byte[dataLength + 16];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(GP2xCommands.SCP03_PADDING, 0, datas, dataLength, 16);
                    command[4] = (byte) (dataLength + 8);


                } else { // need a PADDING
                    int nbBytes = 16 - (dataLength % 16); // bytes needed for the PADDING
                    logger.debug("- We need a PADDING (" + nbBytes + " bytes) ");
                    datas = new byte[dataLength + nbBytes];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(GP2xCommands.SCP03_PADDING, 0, datas, dataLength, nbBytes); // add necessary PADDING

                }
                logger.debug("- New data to encrypt is " + Conversion.arrayToHex(datas));
                logger.debug("* DATAFIELD from command is " + Conversion.arrayToHex(datas));


                IvParameterSpec ivSpec = new IvParameterSpec(iv_zero_scp03);
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "AES"), ivSpec);
                byte[] res = cipher.doFinal(datas);
                encryptedCmd = new byte[5 + res.length];
                System.arraycopy(command, 0, encryptedCmd, 0, 5);
                System.arraycopy(res, 0, encryptedCmd, 5, res.length);
                encryptedCmd[4] = (byte) (datas.length + 8);


                logger.debug("* Encrypted data is " + Conversion.arrayToHex(encryptedCmd));


            }
            if (this.scp == SCPMode.SCP_03_6D
                    || this.scp == SCPMode.SCP_03_0D
                    || this.scp == SCPMode.SCP_03_2D) {
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
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "AES"), ivSpec);
                icvCEnc = cipher.doFinal(icvCEnc);
                logger.debug("* icv counter = " + Conversion.arrayToHex(icvCEnc));
                int dataLength = command.length - 5; // command without (CLA, INS, P1, P2) AND C-MAC

                if (dataLength % 16 == 0) { // don't need a PADDING
                    datas = new byte[dataLength + 16];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(GP2xCommands.SCP03_PADDING, 0, datas, dataLength, 16);
                    command[4] = (byte) (dataLength + 8);


                } else { // need a PADDING
                    int nbBytes = 16 - (dataLength % 16); // bytes needed for the PADDING
                    logger.debug("- We need a PADDING (" + nbBytes + " bytes) ");
                    datas = new byte[dataLength + nbBytes];
                    System.arraycopy(command, 5, datas, 0, dataLength); // copies LC + DATAFIELD from command
                    System.arraycopy(GP2xCommands.SCP03_PADDING, 0, datas, dataLength, nbBytes); // add necessary PADDING

                }
                logger.debug("- New data to encrypt is " + Conversion.arrayToHex(datas));
                logger.debug("* DATAFIELD from command is " + Conversion.arrayToHex(datas));


                ivSpec = new IvParameterSpec(icvCEnc);
                cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "AES"), ivSpec);
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
     * ICV Initialization. All values set to 0
     */
    protected void initIcv() {
        if (scp == SCPMode.SCP_01_15
                || scp == SCPMode.SCP_01_05
                || scp == SCPMode.SCP_UNDEFINED
                || scp == SCPMode.SCP_02_04
                || scp == SCPMode.SCP_02_05
                || scp == SCPMode.SCP_02_0A
                || scp == SCPMode.SCP_02_0B
                || scp == SCPMode.SCP_02_14
                || scp == SCPMode.SCP_02_15
                || scp == SCPMode.SCP_02_1A
                || scp == SCPMode.SCP_02_1B
                || scp == SCPMode.SCP_02_45
                || scp == SCPMode.SCP_02_54
                || scp == SCPMode.SCP_02_55) {

            logger.debug("==> Init ICV begin");
            this.icv = new byte[8];
            for (int i = 0; i < this.icv.length; i++) {
                this.icv[i] = (byte) 0x00;
            }
            logger.debug("* New ICV is " + Conversion.arrayToHex(this.icv));
            logger.debug("==> Init ICV end");
        }
        if (scp == SCPMode.SCP_03_05
                || scp == SCPMode.SCP_03_0D
                || scp == SCPMode.SCP_03_25
                || scp == SCPMode.SCP_03_2D
                || scp == SCPMode.SCP_03_65
                || scp == SCPMode.SCP_03_6D) {

            logger.debug("==> Init ICV begin");
            this.icv = new byte[16];
            for (int i = 0; i < this.icv.length; i++) {
                this.icv[i] = (byte) 0x00;
            }
            logger.debug("* New ICV is " + Conversion.arrayToHex(this.icv));
            logger.debug("==> Init ICV end");

        }

    }


    /**
     * Initialization ICV to mac ovec AID of selected application, this function is used in
     * SCP 02_1A and SCP 02_0A and SCP 02_1B which use the implicit initiation mode
     */
    protected void initIcvToMacOverAid(byte[] aid) {

        logger.info("==> init ICV to mac over AID");
        logger.info("* SCP 02 Protocol (" + this.scp + ") used");
        logger.info("* IV is " + Conversion.arrayToHex(iv_zero));

        IvParameterSpec ivSpec = new IvParameterSpec(iv_zero);
        byte[] dataWithPadding;

        byte[] res;

        if (aid.length % 8 != 0) { // We need a PADDING

            logger.debug("- Data needs PADDING!");

            int nbBytes = 8 - (aid.length % 8);
            dataWithPadding = new byte[aid.length + nbBytes];
            System.arraycopy(aid, 0, dataWithPadding, 0, aid.length);
            System.arraycopy(GP2xCommands.PADDING, 0, dataWithPadding, aid.length, nbBytes);
        } else {
            dataWithPadding = new byte[aid.length + 8];
            System.arraycopy(aid, 0, dataWithPadding, 0, aid.length);
            System.arraycopy(GP2xCommands.PADDING, 0, dataWithPadding, aid.length, GP2xCommands.PADDING.length);
        }

        logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));

        try {

            SecretKeySpec desSingleKey = new SecretKeySpec(this.sessMac, 0, 8, "DES");
            Cipher singleDesCipher;
            singleDesCipher = Cipher.getInstance("DES/CBC/NoPadding", "SunJCE");

            // Calculate the first n - 1 block.
            int noOfBlocks = dataWithPadding.length / 8;
            byte ivForNextBlock[] = this.icv;
            int startIndex = 0;
            for (int i = 0; i < (noOfBlocks - 1); i++) {
                singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
                ivForNextBlock = singleDesCipher.doFinal(dataWithPadding, startIndex, 8);
                startIndex += 8;
                ivSpec = new IvParameterSpec(ivForNextBlock);
                logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
            }


            SecretKeySpec desKey = new SecretKeySpec(this.sessMac, "DESede");
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
        } catch (NoSuchProviderException ex) {
            java.util.logging.Logger.getLogger(GP2xCommands.class.getName()).log(Level.SEVERE, null, ex);
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
    protected void calculateCryptograms() {

        logger.debug("==> Calculate Cryptograms");

        byte[] data = new byte[24];
        Cipher myCipher;
        try {

            myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
            IvParameterSpec ivSpec = new IvParameterSpec(this.icv);

            logger.debug("* IV is " + Conversion.arrayToHex(ivSpec.getIV()));

            if ((this.scp == SCPMode.SCP_UNDEFINED)
                    || (this.scp == SCPMode.SCP_01_05)
                    || (this.scp == SCPMode.SCP_01_15)) {

                logger.debug("* SCP 01 protocol used");

                /* Calculing Cryptogram */
                System.arraycopy(this.hostChallenge, 0, data, 0, 8);
                System.arraycopy(this.cardChallenge, 0, data, 8, 8);
                System.arraycopy(GP2xCommands.PADDING, 0, data, 16, 8);

                logger.debug("* Data to encrypt: " + Conversion.arrayToHex(data));

                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), ivSpec);
                byte[] cardcryptogram = myCipher.doFinal(data);
                this.cardCrypto = new byte[8];
                System.arraycopy(cardcryptogram, 16, this.cardCrypto, 0, 8);

                logger.debug("* Calculated Card Crypto: " + Conversion.arrayToHex(this.cardCrypto));

                System.arraycopy(this.cardChallenge, 0, data, 0, 8);
                System.arraycopy(this.hostChallenge, 0, data, 8, 8);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), ivSpec);
                byte[] hostcryptogram = myCipher.doFinal(data);
                this.hostCrypto = new byte[8];
                System.arraycopy(hostcryptogram, 16, this.hostCrypto, 0, 8);

                logger.debug("* Calculated Host Crypto: " + Conversion.arrayToHex(this.hostCrypto));

            } else if (this.scp == SCPMode.SCP_02_15
                    || this.scp == SCPMode.SCP_02_04
                    || this.scp == SCPMode.SCP_02_05
                    || this.scp == SCPMode.SCP_02_14
                    || this.scp == SCPMode.SCP_02_45
                    || this.scp == SCPMode.SCP_02_55) {

                logger.debug("* SCP 02 protocol used");

                /* Calculing Card Cryptogram */
                System.arraycopy(this.hostChallenge, 0, data, 0, 8);
                System.arraycopy(this.sequenceCounter, 0, data, 8, 2);
                System.arraycopy(this.cardChallenge, 0, data, 10, 6);
                System.arraycopy(GP2xCommands.PADDING, 0, data, 16, GP2xCommands.PADDING.length);

                logger.debug("* Data to encrypt: " + Conversion.arrayToHex(data));

                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), ivSpec);
                byte[] cardcryptogram = myCipher.doFinal(data);
                this.cardCrypto = new byte[8];
                System.arraycopy(cardcryptogram, 16, this.cardCrypto, 0, 8);

                logger.debug("* Calculated Card Crypto: " + Conversion.arrayToHex(this.cardCrypto));

                /* Calculing Host Cryptogram */
                System.arraycopy(this.sequenceCounter, 0, data, 0, 2);
                System.arraycopy(this.cardChallenge, 0, data, 2, 6);
                System.arraycopy(this.hostChallenge, 0, data, 8, 8);
                System.arraycopy(GP2xCommands.PADDING, 0, data, 16, GP2xCommands.PADDING.length);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), ivSpec);
                byte[] hostcryptogram = myCipher.doFinal(data);
                this.hostCrypto = new byte[8];
                System.arraycopy(hostcryptogram, 16, this.hostCrypto, 0, 8);

                logger.debug("* Calculated Host Crypto: " + Conversion.arrayToHex(this.hostCrypto));

            } else if (this.scp == SCPMode.SCP_03_65
                    || this.scp == SCPMode.SCP_03_6D
                    || this.scp == SCPMode.SCP_03_05
                    || this.scp == SCPMode.SCP_03_0D
                    || this.scp == SCPMode.SCP_03_2D
                    || this.scp == SCPMode.SCP_03_25) {

                logger.debug("* SCP 03 protocol used");

                /* Calculing Card Cryptogram */
                derivationData[11] = SCP03_DERIVATION4CardCryptogram;
                derivationData[13] = (byte) 0x00;
                derivationData[14] = (byte) 0x40;
                derivationData[15] = (byte) 0x01;

                logger.debug("* derivation data : " + Conversion.arrayToHex(derivationData));


                SecretKeySpec skeySpec = new SecretKeySpec(this.sessMac, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

                byte icvNextBloc[] = new byte[16];
                int noOfBlocks = derivationData.length / 16;

                int startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(this.derivationData);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                this.cardCrypto = new byte[8];
                System.arraycopy(icvNextBloc, 0, this.cardCrypto, 0, 8);
                logger.debug("Calculated Card Cryptogram = " + Conversion.arrayToHex(this.cardCrypto));

                ivSpec = new IvParameterSpec(this.icv);

                /* Calculing Card Cryptogram */
                derivationData[11] = SCP03_DERIVATION4HostCryptogram;
                derivationData[13] = (byte) 0x00;
                derivationData[14] = (byte) 0x40;
                derivationData[15] = (byte) 0x01;

                logger.debug("* derivation data : " + Conversion.arrayToHex(derivationData));


                skeySpec = new SecretKeySpec(this.sessMac, "AES");
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

                noOfBlocks = derivationData.length / 16;

                startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(this.derivationData, startIndex, 16);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                this.hostCrypto = new byte[8];
                System.arraycopy(icvNextBloc, 0, this.hostCrypto, 0, 8);
                logger.debug("Calculated Host Cryptogram = " + Conversion.arrayToHex(this.hostCrypto));


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
    protected void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek) {

        logger.debug("==> Generate Session Keys");

        try {

            byte[] session;

            Cipher myCipher = null;

            logger.debug("* staticKenc: " + Conversion.arrayToHex(staticKenc.getValue()));
            logger.debug("* staticKmac: " + Conversion.arrayToHex(staticKmac.getValue()));
            logger.debug("* staticKkek: " + Conversion.arrayToHex(staticKkek.getValue()));
            logger.debug("* SCP_Mode is " + this.scp);

            if ((this.scp == SCPMode.SCP_UNDEFINED)
                    || (this.scp == SCPMode.SCP_01_05)
                    || (this.scp == SCPMode.SCP_01_15)) {  // TODO: SCPMode.SCP_UNDEFINED Here ?

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

            } else if (this.scp == SCPMode.SCP_02_15
                    || this.scp == SCPMode.SCP_02_04
                    || this.scp == SCPMode.SCP_02_05
                    || this.scp == SCPMode.SCP_02_14
                    || this.scp == SCPMode.SCP_02_45
                    || this.scp == SCPMode.SCP_02_55) {

                this.sessEnc = new byte[24];
                this.sessMac = new byte[24];
                this.sessRMac = new byte[24];
                this.sessKek = new byte[24];

                myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
                IvParameterSpec ivSpec = new IvParameterSpec(this.icv);

                logger.debug("*** Initialize IV : " + Conversion.arrayToHex(this.sessEnc));

                // Calculing Encryption Session Keys
                System.arraycopy(GP2xCommands.SCP02_DERIVATION4ENCKEY, 0, this.derivationData, 0, 2);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKenc.getValue(), "DESede"), ivSpec);
                session = myCipher.doFinal(this.derivationData);
                System.arraycopy(session, 0, this.sessEnc, 0, 16);
                System.arraycopy(session, 0, this.sessEnc, 16, 8);

                logger.debug("* sessEnc = " + Conversion.arrayToHex(this.sessEnc));

                // Calculing C_Mac Session Keys
                System.arraycopy(GP2xCommands.SCP02_DERIVATION4CMAC, 0, this.derivationData, 0, 2);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKmac.getValue(), "DESede"), ivSpec);
                session = myCipher.doFinal(this.derivationData);
                System.arraycopy(session, 0, this.sessMac, 0, 16);
                System.arraycopy(session, 0, this.sessMac, 16, 8);

                logger.debug("* sessMac = " + Conversion.arrayToHex(this.sessMac));

                // Calculing R_Mac Session Keys
                System.arraycopy(GP2xCommands.SCP02_DERIVATION4RMAC, 0, this.derivationData, 0, 2);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKmac.getValue(), "DESede"), ivSpec);
                session = myCipher.doFinal(this.derivationData);
                System.arraycopy(session, 0, this.sessRMac, 0, 16);
                System.arraycopy(session, 0, this.sessRMac, 16, 8);

                logger.debug("* sessRMac = " + Conversion.arrayToHex(this.sessRMac));

                // Calculing Data Encryption Session Keys
                System.arraycopy(GP2xCommands.SCP02_DERIVATION4DATAENC, 0, this.derivationData, 0, 2);
                myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKkek.getValue(), "DESede"), ivSpec);
                session = myCipher.doFinal(this.derivationData);
                System.arraycopy(session, 0, this.sessKek, 0, 16);
                System.arraycopy(session, 0, this.sessKek, 16, 8);

                logger.debug("* sessKek = " + Conversion.arrayToHex(this.sessRMac));

            } else if ((scp == SCPMode.SCP_03_65)
                    || (scp == SCPMode.SCP_03_6D)
                    || (scp == SCPMode.SCP_03_05)
                    || (scp == SCPMode.SCP_03_0D)
                    || (scp == SCPMode.SCP_03_2D)
                    || (scp == SCPMode.SCP_03_25)) {

                logger.debug("derivation data : " + Conversion.arrayToHex(derivationData));

                this.sessEnc = new byte[16];
                this.sessMac = new byte[16];
                this.sessRMac = new byte[16];

                IvParameterSpec ivSpec = new IvParameterSpec(this.icv);
                logger.debug("*** Initialize IV : " + Conversion.arrayToHex(this.icv));


                SecretKeySpec skeySpec = new SecretKeySpec(staticKenc.getValue(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                logger.debug("key : " + Conversion.arrayToHex(skeySpec.getEncoded()));

                // Calculing Encryption Session Keys
                this.derivationData[11] = SCP03_DERIVATION4DATAENC;
                this.derivationData[13] = (byte) 0x00;
                this.derivationData[14] = (byte) 0xC0;
                this.derivationData[15] = (byte) 0x02;

                byte icvNextBloc[] = new byte[16];
                int noOfBlocks = derivationData.length / 16;

                int startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(this.derivationData, startIndex, 16);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
//                    logger.debug("derivation data : " + Conversion.arrayToHex(derivationData));
//                    logger.debug("Icv for block = " + Conversion.arrayToHex(icvNextBloc));
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                System.arraycopy(icvNextBloc, 0, this.sessEnc, 0, icvNextBloc.length);
                logger.debug("sessEnc = " + Conversion.arrayToHex(this.sessEnc));

                ivSpec = new IvParameterSpec(this.icv);


                // Calculing C_Mac Session Keys
                this.derivationData[11] = SCP03_DERIVATION4CMAC;
                this.derivationData[13] = (byte) 0x00;
                this.derivationData[14] = (byte) 0xC0;
                this.derivationData[15] = (byte) 0x02;


                noOfBlocks = derivationData.length / 16;

                startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(this.derivationData, startIndex, 16);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
//                    logger.debug("derivation data : " + Conversion.arrayToHex(derivationData));
//                    logger.debug("Icv for block = " + Conversion.arrayToHex(icvNextBloc));
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                System.arraycopy(icvNextBloc, 0, this.sessMac, 0, icvNextBloc.length);
                logger.debug("sessMac = " + Conversion.arrayToHex(this.sessMac));

                ivSpec = new IvParameterSpec(this.icv);

                // Calculing R_Mac Session Keys
                this.derivationData[11] = SCP03_DERIVATION4RMAC;
                this.derivationData[13] = (byte) 0x00;
                this.derivationData[14] = (byte) 0xC0;
                this.derivationData[15] = (byte) 0x02;

                noOfBlocks = derivationData.length / 16;

                startIndex = 0;
                for (int i = 0; i < (noOfBlocks); i++) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                    byte[] encrypted = cipher.doFinal(this.derivationData, startIndex, 16);
                    System.arraycopy(encrypted, 0, icvNextBloc, 0, 16);
                    startIndex += 16;
//                    logger.debug("derivation data : " + Conversion.arrayToHex(derivationData));
//                    logger.debug("Icv for block = " + Conversion.arrayToHex(icvNextBloc));
                    ivSpec = new IvParameterSpec(icvNextBloc);
                }
                System.arraycopy(icvNextBloc, 0, this.sessRMac, 0, icvNextBloc.length);
                logger.debug("sessRMac = " + Conversion.arrayToHex(this.sessRMac));


            }


        } catch (InvalidAlgorithmParameterException ex) {
            java.util.logging.Logger.getLogger(GP2xCommands.class.getName()).log(Level.SEVERE, null, ex);
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
     * Calculate Derivation data. This step depending to the @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} card response.
     */
    protected void calculateDerivationData() {

        logger.debug("==> Calculate Derivation Data");

        if ((this.scp == SCPMode.SCP_UNDEFINED)
                || (this.scp == SCPMode.SCP_01_05)
                || (this.scp == SCPMode.SCP_01_15)) { // SCP 01_*

            this.derivationData = new byte[16];

            System.arraycopy(this.hostChallenge, 0, this.derivationData, 4, 4);
            System.arraycopy(this.hostChallenge, 4, this.derivationData, 12, 4);
            System.arraycopy(this.cardChallenge, 0, this.derivationData, 8, 4);
            System.arraycopy(this.cardChallenge, 4, this.derivationData, 0, 4);

        } else if (this.scp == SCPMode.SCP_02_15
                || this.scp == SCPMode.SCP_02_04
                || this.scp == SCPMode.SCP_02_05
                || this.scp == SCPMode.SCP_02_14
                || this.scp == SCPMode.SCP_02_0A
                || this.scp == SCPMode.SCP_02_45
                || this.scp == SCPMode.SCP_02_55) { // SCP 02_*

            this.derivationData = new byte[16];
            System.arraycopy(this.sequenceCounter, 0, this.derivationData, 2, 2);

        } else if ((this.scp == SCPMode.SCP_03_65)
                || (scp == SCPMode.SCP_03_6D)
                || (scp == SCPMode.SCP_03_05)
                || (scp == SCPMode.SCP_03_0D)
                || (scp == SCPMode.SCP_03_2D)
                || (scp == SCPMode.SCP_03_25)) {


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


            this.derivationData = new byte[32];
            byte[] label = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            System.arraycopy(label, 0, derivationData, 0, label.length);
            System.arraycopy(this.hostChallenge, 0, derivationData, 16, this.hostChallenge.length);
            System.arraycopy(this.cardChallenge, 0, derivationData, 24, this.hostChallenge.length);
        }

        logger.debug("* Derivation Data is " + Conversion.arrayToHex(this.derivationData));

        logger.debug("==> Calculate Derivation Data End");

    }

    // SECURITY DOMAIN

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.Commands#deleteOnCardKey(byte[], boolean)
     */

    /**
     * Generate the pseudo Random Card Challenge to compare with the Challenge
     * of the card obtained with initializeUpdate Command
     *
     * @param aid AID of the current selected application
     */
    protected byte[] pseudoRandomGenerationCardChallenge(byte[] aid) {

        logger.info("==> pseudo Random Generation CardChallenge");
        logger.info("* SCP 02 Protocol (" + this.scp + ") used");
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
            System.arraycopy(GP2xCommands.PADDING, 0, dataWithPadding, aid.length, nbBytes);
        } else {
            dataWithPadding = new byte[aid.length + 8];
            System.arraycopy(aid, 0, dataWithPadding, 0, aid.length);
            System.arraycopy(GP2xCommands.PADDING, 0, dataWithPadding, aid.length, GP2xCommands.PADDING.length);
        }
        logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));
        try {

            SecretKeySpec desSingleKey = new SecretKeySpec(this.sessMac, 0, 8, "DES");
            Cipher singleDesCipher;
            singleDesCipher = Cipher.getInstance("DES/CBC/NoPadding", "SunJCE");

            // Calculate the first n - 1 block.
            int noOfBlocks = dataWithPadding.length / 8;
            byte ivForNextBlock[] = this.icv;
            int startIndex = 0;
            for (int i = 0; i < (noOfBlocks - 1); i++) {
                singleDesCipher.init(Cipher.ENCRYPT_MODE, desSingleKey, ivSpec);
                ivForNextBlock = singleDesCipher.doFinal(dataWithPadding, startIndex, 8);
                startIndex += 8;
                ivSpec = new IvParameterSpec(ivForNextBlock);
                logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
            }


            SecretKeySpec desKey = new SecretKeySpec(this.sessMac, "DESede");
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
            java.util.logging.Logger.getLogger(GP2xCommands.class.getName()).log(Level.SEVERE, null, ex);
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
    public ResponseAPDU deleteOnCardObj(byte[] aid, boolean cascade) throws CardException {

        logger.debug("=> Delete On Card Object begin");

        logger.debug("+ " + (aid != null ? "AID to delete is " + Conversion.arrayToHex(aid) : "There is not AID"));
        logger.debug("+ Cascade mode ? " + cascade);
        logger.debug("+ Security mode is " + this.secMode);

        if (aid == null) {
            throw new IllegalArgumentException("aid must be not null");
        }

        byte dataSize = (byte) (2 + aid.length); // params +  AID

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            dataSize = (byte) (dataSize + 8); // add space for the C-MAC
        }

        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC

        byte[] deleteComm = new byte[headerSize + dataSize];

        deleteComm[ISO7816.OFFSET_CLA.getValue()] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84); // (CLA) command class (GlobalPlatform command + secure messaging with GlobalPlatform format)
        deleteComm[ISO7816.OFFSET_INS.getValue()] = (byte) 0xE4; // (INS) DELETE command
        deleteComm[ISO7816.OFFSET_P1.getValue()] = (byte) 0x00; // (P1) the only and the last DELETE command
        deleteComm[ISO7816.OFFSET_P2.getValue()] = (cascade ? (byte) 0x80 : (byte) 0x00); // (P2) 0x00 : only delete the specified object
        //      0x80 : delete object and related objects
        deleteComm[ISO7816.OFFSET_LC.getValue()] = dataSize;   // (LC) data length

        deleteComm[ISO7816.OFFSET_CDATA.getValue()] = (byte) 0x4F; // the object being deleted is specified by its AID
        deleteComm[ISO7816.OFFSET_CDATA.getValue() + 1] = (byte) aid.length; // AID length
        System.arraycopy(aid, 0, deleteComm, ISO7816.OFFSET_CDATA.getValue() + 2, aid.length); // put the AID into deleteComm

        logger.debug("* Delete Command is " + Conversion.arrayToHex(deleteComm));


        if (this.getSecMode() == SecLevel.C_MAC) {
            byte[] dataCmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(deleteComm, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, deleteComm, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm

            logger.debug("* delete Command which CMac is " + Conversion.arrayToHex(deleteComm));
        }

        if (((this.getSecMode() == SecLevel.C_ENC_AND_MAC) || (this.getSecMode() == SecLevel.C_ENC_AND_C_MAC_AND_R_MAC) || (this.getSecMode() == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC))
                && (this.scp != SCPMode.SCP_03_65)
                && (this.scp != SCPMode.SCP_03_6D)
                && (this.scp != SCPMode.SCP_03_05)
                && (this.scp != SCPMode.SCP_03_0D)
                && (this.scp != SCPMode.SCP_03_2D)
                && (this.scp != SCPMode.SCP_03_25)) {
            byte[] dataCmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(deleteComm, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, deleteComm, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm

            logger.debug("* delete Command which CMac is " + Conversion.arrayToHex(deleteComm));
            deleteComm = this.encryptCommand(deleteComm);
            logger.debug("* Encrypted delete Command is " + Conversion.arrayToHex(deleteComm));
        }

        if ((this.getSecMode() == SecLevel.C_ENC_AND_MAC) || (this.getSecMode() == SecLevel.C_ENC_AND_C_MAC_AND_R_MAC) || (this.getSecMode() == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC)) {
            if ((this.scp == SCPMode.SCP_03_65)
                    || (this.scp == SCPMode.SCP_03_6D)
                    || (this.scp == SCPMode.SCP_03_05)
                    || (this.scp == SCPMode.SCP_03_0D)
                    || (this.scp == SCPMode.SCP_03_2D)
                    || (this.scp == SCPMode.SCP_03_25)) {
                byte[] dataToEnc = new byte[headerSize + dataSize - 8]; // data to encrypt without 8 bytes of CMAC
                System.arraycopy(deleteComm, 0, dataToEnc, 0, dataToEnc.length); // data used to generate C-MAC

                byte[] encDeleteComm = this.encryptCommand(dataToEnc);
                logger.debug("* Encrypted Delete Command is " + Conversion.arrayToHex(encDeleteComm));
                byte[] cmac = this.generateMac(encDeleteComm); // generate C-MAC
                logger.debug("* CMac " + Conversion.arrayToHex(cmac));
                deleteComm = new byte[encDeleteComm.length + 8];
                System.arraycopy(encDeleteComm, 0, deleteComm, 0, encDeleteComm.length);
                System.arraycopy(cmac, 0, deleteComm, encDeleteComm.length, cmac.length);
                logger.debug("Final install for load Command : " + Conversion.arrayToHex(deleteComm));
            }
        }

        CommandAPDU cmdDelete = new CommandAPDU(deleteComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdDelete);

        logger.debug("DELETE OBJECT command "
                + "(-> " + Conversion.arrayToHex(cmdDelete.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            throw new CardException("Error in DELETE OBJECT : " + Integer.toHexString(resp.getSW()));
        }

        this.compudeAndVerifyRMac(resp.getBytes());

        if ((this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) && (this.scp == SCPMode.SCP_03_65)) {
            this.decryptCardResponseData(resp.getBytes());
        }

        // increment the value of counter icv for CENC
        if (this.getSecMode() == secMode.C_ENC_AND_MAC
                || this.getSecMode() == secMode.C_ENC_AND_C_MAC_AND_R_MAC
                || this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
            CENC_Counter++;
        }


        logger.debug("=> Delete On Card Object End");

        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#deleteOnCardKey(byte, byte)
     */

    @Override
    public ResponseAPDU deleteOnCardKey(byte keySetVersion, byte keyId) throws CardException {

        logger.debug("=> Delete On Card Key begin");

        logger.debug("+ Key Set Version to delete is " + Integer.toHexString((int) (keySetVersion) & 0xFF));
        logger.debug("+ Key Id to delete is " + Integer.toHexString((int) (keyId) & 0xFF));
        logger.debug("+ SecLevel is " + this.secMode);

        byte dataSize = (byte) 4; // '0xD0' + Key Identifier + '0xD2' + Key Version Number

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            dataSize = (byte) (dataSize + 8); // add space for the C-MAC
        }

        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC

        byte[] deleteComm = new byte[headerSize + dataSize];

        deleteComm[0] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84); // (CLA) command class (GlobalPlatform command + secure messaging with GlobalPlatform format)
        deleteComm[1] = (byte) 0xE4; // (INS) DELETE command
        deleteComm[2] = (byte) 0x00; // (P1) the only and the last DELETE command
        deleteComm[3] = (byte) 0x00; // (P2) 0x00 : only delete the specified key
        deleteComm[4] = dataSize;   // (LC) data length

        deleteComm[5] = (byte) 0xD0; // Key Identifier
        deleteComm[6] = keyId;
        deleteComm[7] = (byte) 0xD2; // Key Version Number
        deleteComm[8] = keySetVersion;

        logger.debug("* Delete Command is " + Conversion.arrayToHex(deleteComm));

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            byte[] dataCmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(deleteComm, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, deleteComm, dataCmac.length, cmac.length); // put C-MAC into deleteComm

            logger.debug("* Delete Command whith CMAC is " + Conversion.arrayToHex(deleteComm));
        }

        if (this.getSecMode() == SecLevel.C_ENC_AND_MAC) {
            deleteComm = this.encryptCommand(deleteComm);

            logger.debug("* Encrypted Delete Command is " + Conversion.arrayToHex(deleteComm));
        }

        CommandAPDU cmdDelete = new CommandAPDU(deleteComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdDelete);

        logger.debug("DELETE KEY command "
                + "(-> " + Conversion.arrayToHex(cmdDelete.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != 0x9000) {
            throw new CardException("Error in DELETE KEY : " + Integer.toHexString(resp.getSW()));
        }

        logger.debug("=> Delete On Card Key End");

        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#installForLoad(byte[], byte[], byte[])
     */

    @Override
    public ResponseAPDU installForLoad(byte[] packageAid, byte[] securityDomainAid, byte[] params) throws CardException {

        logger.debug("=> Install for load begin");

        logger.debug("+ " + (packageAid != null ? "Package AID to install is " + Conversion.arrayToHex(packageAid) : "There is not Package AID"));
        logger.debug("+ " + (securityDomainAid != null ? "Security Domain AID is " + Conversion.arrayToHex(securityDomainAid) : "There is not Security Domain AID"));
        logger.debug("+ " + (params != null ? "Parameters is " + Conversion.arrayToHex(params) : "There is not parameter"));
        logger.debug("+ SecLevel is " + this.secMode);

        if (packageAid == null) {
            throw new IllegalArgumentException("packageAid must be not null");
        }
        if (securityDomainAid == null) {
            throw new IllegalArgumentException("securityDomainAid must be not null");
        }

        int paramLength = ((params != null) ? params.length : 0);
        byte[] paramLengthEncoded = null;
        if (paramLength < 128) {
            paramLengthEncoded = new byte[1];
            paramLengthEncoded[0] = (byte) paramLength;
        } else if (paramLength <= 255) {
            paramLengthEncoded = new byte[2];
            paramLengthEncoded[0] = (byte) 0x81;
            paramLengthEncoded[1] = (byte) paramLength;
        } else {
            throw new IllegalArgumentException("params must size must be <= 255");
        }

        logger.debug("* Parameters Length is " + paramLength + " (0x" + Integer.toHexString(paramLength) + ")");
        logger.debug("* Parameters Length Encoded is " + Conversion.arrayToHex(paramLengthEncoded));

        int secDomLength = securityDomainAid.length;

        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC
        byte dataSize = (byte) (1 + packageAid.length // Length of Load File AID +  Load File AID
                + 1 + secDomLength // + Length of Security Domain AID + Security Domain AID
                + 1 // + Length of Load File Data BlockHash (0x00)
                + paramLengthEncoded.length + paramLength // + Length of Load Parameters field + Load Parameters field
                + 1);                                          // + Length of Load Token (0x00)

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            dataSize = (byte) (dataSize + 8); // add space for the C-MAC
        }

        byte[] installForLoadComm = new byte[(headerSize + (short) (dataSize & 0xFF))];

        installForLoadComm[ISO7816.OFFSET_CLA.getValue()] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84); // (CLA) command class (GlobalPlatform command + secure messaging with GlobalPlatform format)
        installForLoadComm[ISO7816.OFFSET_INS.getValue()] = (byte) 0xE6; // (INS) INSTALL command
        installForLoadComm[ISO7816.OFFSET_P1.getValue()] = (byte) 0x02; // (P1) For load
        installForLoadComm[ISO7816.OFFSET_P2.getValue()] = (byte) 0x00; // (P2) no information provided
        installForLoadComm[ISO7816.OFFSET_LC.getValue()] = dataSize; // (LC) data length

        installForLoadComm[ISO7816.OFFSET_CDATA.getValue()] = (byte) packageAid.length; // AID length

        int i = 6; // next index of installForLoadComm to deal with

        System.arraycopy(packageAid, 0, installForLoadComm, i, packageAid.length); // put the AID into installForLoadComm
        i += packageAid.length;

        installForLoadComm[i] = (byte) secDomLength; // length of security domain
        i++;

        System.arraycopy(securityDomainAid, 0, installForLoadComm, i, secDomLength);
        i += secDomLength;

        installForLoadComm[i] = (byte) 0x00; // length of load file data block
        i++;

        System.arraycopy(paramLengthEncoded, 0, installForLoadComm, i, paramLengthEncoded.length);
        i += paramLengthEncoded.length;

        if (params != null) {
            System.arraycopy(params, 0, installForLoadComm, i, paramLength);
            i += paramLength;
        }

        installForLoadComm[i] = (byte) 0x00; // length of load token

        logger.debug("* Install For Load Command is " + Conversion.arrayToHex(installForLoadComm));

        if (this.getSecMode() == SecLevel.C_MAC) {
            byte[] dataCmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(installForLoadComm, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, installForLoadComm, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm

            logger.debug("* Install For Load Command whith CMAC is " + Conversion.arrayToHex(installForLoadComm));
        }

        if ((this.getSecMode() == SecLevel.C_ENC_AND_MAC)
                && (this.scp != SCPMode.SCP_03_65)
                && (this.scp != SCPMode.SCP_03_6D)
                && (this.scp != SCPMode.SCP_03_05)
                && (this.scp != SCPMode.SCP_03_0D)
                && (this.scp != SCPMode.SCP_03_2D)
                && (this.scp != SCPMode.SCP_03_25)) {
            byte[] dataCmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(installForLoadComm, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, installForLoadComm, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm

            logger.debug("* Install For Load Command whith CMAC is " + Conversion.arrayToHex(installForLoadComm));
            installForLoadComm = this.encryptCommand(installForLoadComm);
            logger.debug("* Encrypted Install For Load Command is " + Conversion.arrayToHex(installForLoadComm));
        }

        if (((this.getSecMode() == SecLevel.C_ENC_AND_MAC) || (this.getSecMode() == SecLevel.C_ENC_AND_C_MAC_AND_R_MAC) || (this.getSecMode() == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC))) {
            if (this.scp == SCPMode.SCP_03_65
                    || this.scp == SCPMode.SCP_03_6D
                    || this.scp == SCPMode.SCP_03_05
                    || this.scp == SCPMode.SCP_03_0D
                    || this.scp == SCPMode.SCP_03_2D
                    || this.scp == SCPMode.SCP_03_25) {
                byte[] dataToEnc = new byte[headerSize + dataSize - 8]; // data to encrypt without 8 bytes of CMAC
                System.arraycopy(installForLoadComm, 0, dataToEnc, 0, dataToEnc.length); // data used to generate C-MAC

                byte[] encInstallForLoadComm = this.encryptCommand(dataToEnc);
                logger.debug("* Encrypted Install For Load Command is " + Conversion.arrayToHex(encInstallForLoadComm));
                byte[] cmac = this.generateMac(encInstallForLoadComm); // generate C-MAC
                logger.debug("* CMac " + Conversion.arrayToHex(cmac));
                installForLoadComm = new byte[encInstallForLoadComm.length + 8];
                System.arraycopy(encInstallForLoadComm, 0, installForLoadComm, 0, encInstallForLoadComm.length);
                System.arraycopy(cmac, 0, installForLoadComm, encInstallForLoadComm.length, cmac.length);
                logger.debug("Final install for load Command : " + Conversion.arrayToHex(installForLoadComm));
            }

        }

        CommandAPDU cmdInstallforload = new CommandAPDU(installForLoadComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdInstallforload);

        logger.debug("INSTALL FOR LOAD command "
                + "(-> " + Conversion.arrayToHex(cmdInstallforload.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            throw new CardException("Error in INSTALL FOR LOAD : " + Integer.toHexString(resp.getSW()));
        }

        this.compudeAndVerifyRMac(resp.getBytes());

        if (this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
            byte[] plainCommand = this.decryptCardResponseData(resp.getBytes());
            if (plainCommand != null) {
                logger.debug("plain text response is " + Conversion.arrayToHex(plainCommand));
            }

        }

        // increment the value of counter icv for CENC
        if (this.getSecMode() == secMode.C_ENC_AND_MAC
                || this.getSecMode() == secMode.C_ENC_AND_C_MAC_AND_R_MAC
                || this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
            CENC_Counter++;
        }

        logger.debug("=> Install For Load Command End");

        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#load(java.io.File)
     */

    @Override
    public ResponseAPDU[] load(byte[] capFile) throws CardException {
        logger.debug("=> Load Command without maxDataLenght");
        return this.load(capFile, (byte) 0xF0);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#load(java.io.File, byte)
     */

    @Override
    public ResponseAPDU[] load(byte[] capFile, byte maxDataLength) throws CardException {

        logger.debug("=> Load Command Begin");

        logger.debug("+ Cap File size to load is " + capFile.length);
        logger.debug("+ Max Data Length is " + (short) (maxDataLength & 0xFF)
                + "(0x" + Integer.toHexString((int) (maxDataLength & 0xFF)) + ")");
        logger.debug("+ SecLevel is " + this.secMode);

        List<ResponseAPDU> responses = new LinkedList<ResponseAPDU>();
        int capFileRemainLen = capFile.length;
        ByteBuffer buffer = null;

        buffer = ByteBuffer.wrap(capFile);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int cMacLen = 0;                // Size of C-MAC (unused by default)
        int headerLen = 5;
        int dataBlockSize = maxDataLength & 0x0ff;            // Size of a block

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            cMacLen = 8;                // we need to update cMacLen...
            dataBlockSize -= cMacLen;
            logger.debug("* SecLevel != NO_SECURITY_LEVEL => new dataBlockSize is " + dataBlockSize);
        }

        if ((this.getSecMode() == SecLevel.C_ENC_AND_MAC) && (dataBlockSize >= 0xF0)) { // check valid data length...
            dataBlockSize -= 1;
            logger.debug("* SecLevel != C_ENC_AND_MAC => dataBlockSize >= 0xF0 so I decrease it!");
        }

        byte[] ber = null;

        logger.debug("* Cap File Remain Length is " + capFileRemainLen);

        if (capFileRemainLen < 128) {
            ber = new byte[2];
            ber[0] = (byte) 0xC4;
            ber[1] = (byte) capFileRemainLen;
        } else if (capFileRemainLen < 256) {
            ber = new byte[3];
            ber[0] = (byte) 0xC4;
            ber[1] = (byte) 0x81;
            ber[2] = (byte) capFileRemainLen;
        } else if (capFileRemainLen < 65536) {
            ber = new byte[4];
            ber[0] = (byte) 0xC4;
            ber[1] = (byte) 0x82;
            ber[2] = (byte) (capFileRemainLen / 256);
            ber[3] = (byte) (capFileRemainLen % 256);
        }  else {
            throw new IllegalStateException("capFileRemainLen is >= 65536");
        }

        logger.debug("* ber is " + Conversion.arrayToHex(ber));

        int dataSizeInFirstCommand = dataBlockSize - ber.length;

        // number of subsequent blocks to send
        int nbBlock = 0;

        if (capFileRemainLen <= dataSizeInFirstCommand) {
            nbBlock = 1;
        } else if ((capFileRemainLen - dataSizeInFirstCommand) % dataBlockSize == 0) {
            nbBlock = ((capFileRemainLen - dataSizeInFirstCommand) / dataBlockSize) + 1;
        } else {
            nbBlock = ((capFileRemainLen - dataSizeInFirstCommand) / dataBlockSize) + 2;
        }

        logger.debug("* number of block is " + nbBlock);

        byte[] cmd = null;

        for (int i = 0; i < nbBlock; i++) {

            if (i == nbBlock - 1) { // only or last block to be sent
                if (i == 0) { // only block to be sent
                    cmd = new byte[headerLen + capFileRemainLen + ber.length + cMacLen];
                    cmd[4] = (byte) (capFileRemainLen + ber.length + cMacLen);
                    System.arraycopy(ber, 0, cmd, 5, ber.length);
                    buffer.get(cmd, 5 + ber.length, capFileRemainLen);
                } else { //last block to be sent
                    cmd = new byte[headerLen + capFileRemainLen + cMacLen];
                    cmd[4] = (byte) (capFileRemainLen + cMacLen);
                    buffer.get(cmd, 5, capFileRemainLen);
                }
            } else { // 2 blocks at least
                cmd = new byte[headerLen + dataBlockSize + cMacLen];
                cmd[4] = (byte) (dataBlockSize + cMacLen);
                if (i == 0) { // first block
                    System.arraycopy(ber, 0, cmd, 5, ber.length);
                    buffer.get(cmd, 5 + ber.length, dataSizeInFirstCommand);
                    capFileRemainLen -= dataSizeInFirstCommand;
                } else { // block i
                    buffer.get(cmd, 5, dataBlockSize);
                    capFileRemainLen -= dataBlockSize;
                }
            }
            cmd[ISO7816.OFFSET_CLA.getValue()] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84);
            cmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0xE8;
            cmd[ISO7816.OFFSET_P1.getValue()] = (byte) ((i == nbBlock - 1) ? 0x80 : 0x00);
            cmd[ISO7816.OFFSET_P2.getValue()] = (byte) i;

            logger.debug("* Load Command is " + Conversion.arrayToHex(cmd));


            if (this.getSecMode() == SecLevel.C_MAC) {
                byte[] dataCmac = new byte[cmd.length - 8]; // data used to generate C-MAC
                System.arraycopy(cmd, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
                byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
                System.arraycopy(cmac, 0, cmd, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm

                logger.debug("* Load Command whith CMAC is " + Conversion.arrayToHex(cmd));
            }

            if ((this.getSecMode() == SecLevel.C_ENC_AND_MAC)
                    && (this.scp != SCPMode.SCP_03_65)
                    && (this.scp != SCPMode.SCP_03_6D)
                    && (this.scp != SCPMode.SCP_03_05)
                    && (this.scp != SCPMode.SCP_03_0D)
                    && (this.scp != SCPMode.SCP_03_2D)
                    && (this.scp != SCPMode.SCP_03_25)) {
                byte[] dataCmac = new byte[cmd.length - 8]; // data used to generate C-MAC
                System.arraycopy(cmd, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
                byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
                System.arraycopy(cmac, 0, cmd, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm

                logger.debug("* Load Command whith CMAC is " + Conversion.arrayToHex(cmd));
                cmd = this.encryptCommand(cmd);
                logger.debug("* Encrypted Load Command is " + Conversion.arrayToHex(cmd));
            }

            if (((this.getSecMode() == SecLevel.C_ENC_AND_MAC) || (this.getSecMode() == SecLevel.C_ENC_AND_C_MAC_AND_R_MAC) || (this.getSecMode() == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC))) {
                if (this.scp == SCPMode.SCP_03_65
                        || scp == SCPMode.SCP_03_6D
                        || scp == SCPMode.SCP_03_05
                        || scp == SCPMode.SCP_03_0D
                        || scp == SCPMode.SCP_03_2D
                        || scp == SCPMode.SCP_03_25) {
                    byte[] dataToEnc = new byte[cmd.length - 8]; // data to encrypt without 8 bytes of CMAC
                    System.arraycopy(cmd, 0, dataToEnc, 0, dataToEnc.length); // data used to generate C-MAC

                    byte[] encInstallForLoadComm = this.encryptCommand(dataToEnc);
                    logger.debug("* Encrypted Load Command is " + Conversion.arrayToHex(encInstallForLoadComm));
                    byte[] cmac = this.generateMac(encInstallForLoadComm); // generate C-MAC
                    logger.debug("* CMac " + Conversion.arrayToHex(cmac));
                    cmd = new byte[encInstallForLoadComm.length + 8];
                    System.arraycopy(encInstallForLoadComm, 0, cmd, 0, encInstallForLoadComm.length);
                    System.arraycopy(cmac, 0, cmd, encInstallForLoadComm.length, cmac.length);
                    logger.debug("Final load Command : " + Conversion.arrayToHex(cmd));
                }
            }

            CommandAPDU cmdLoad = new CommandAPDU(cmd);
            ResponseAPDU resp = null;
            resp = this.getCardChannel().transmit(cmdLoad);

            logger.debug("LOAD command "
                    + "(-> " + Conversion.arrayToHex(cmdLoad.getBytes()) + ") "
                    + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

            responses.add(resp);
            if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
                throw new CardException("Error in LOAD : " + Integer.toHexString(resp.getSW()));
            }
            this.compudeAndVerifyRMac(resp.getBytes());
            if ((this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) && (this.scp == SCPMode.SCP_03_65)) {
                this.decryptCardResponseData(resp.getBytes());
            }
            if (this.getSecMode() == secMode.C_ENC_AND_MAC
                    || this.getSecMode() == secMode.C_ENC_AND_C_MAC_AND_R_MAC
                    || this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
                CENC_Counter++;
            }

        }
        ResponseAPDU[] r = new ResponseAPDU[responses.size()];

        // increment the value of counter icv for CENC


        logger.debug("=> Load Command End");

        return responses.toArray(r);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#installForInstallAndMakeSelectable(byte[], byte[], byte[], byte[], byte[])
     */

    @Override
    public ResponseAPDU installForInstallAndMakeSelectable(byte[] loadFileAID,
                                                           byte[] moduleAID, byte[] applicationAID, byte[] privileges, byte[] parameters)
            throws CardException {

        logger.debug("=> Install For Install And Make Selectable Begin");

        logger.debug("+ " + (loadFileAID != null ? "Load File AID is " + Conversion.arrayToHex(loadFileAID) : "There is not Load File AID"));
        logger.debug("+ " + (moduleAID != null ? "Module AID is " + Conversion.arrayToHex(moduleAID) : "There is not Module AID"));
        logger.debug("+ " + (applicationAID != null ? "Application AID is " + Conversion.arrayToHex(applicationAID) : "There is not Application AID"));
        logger.debug("+ " + (privileges != null ? "Privileges AID is " + Conversion.arrayToHex(privileges) : "There is not privileges"));
        logger.debug("+ " + (parameters != null ? "Parameters is " + Conversion.arrayToHex(parameters) : "There is not parameters"));

        if (loadFileAID == null) {
            throw new IllegalArgumentException("loadFileAID must be not null");
        }

        if (moduleAID == null) {
            throw new IllegalArgumentException("moduleAID must be not null");
        }

        if (privileges == null) {
            throw new IllegalArgumentException("privileges must be not null");
        }

        byte[] params = parameters;

        if (params == null) {
            params = new byte[2];
            params[0] = (byte) 0xC9;
            params[1] = (byte) 0x00;

            logger.debug("* New parameters are " + Conversion.arrayToHex(params));
        }

        if (applicationAID == null) {
            applicationAID = moduleAID.clone();
            logger.debug("* New application AID is " + Conversion.arrayToHex(applicationAID));
        }

        int paramLength = params.length;

        logger.debug("* Parameters Length is " + paramLength);

        byte[] paramLengthEncoded = null;
        if (params.length < 128) {
            paramLengthEncoded = new byte[1];
            paramLengthEncoded[0] = (byte) paramLength;
        } else {
            paramLengthEncoded = new byte[2];
            paramLengthEncoded[0] = (byte) 0x81;
            paramLengthEncoded[1] = (byte) paramLength;
        }

        logger.debug("* Parameters Length Encoded is " + Conversion.arrayToHex(paramLengthEncoded));

        int cMacLen = this.getSecMode() != SecLevel.NO_SECURITY_LEVEL ? 8 : 0;
        byte headerSize = (byte) 5;     // CLA + INS + P1 + P2 + LC
        byte dataSize = (byte) (1 + loadFileAID.length // Length of Executable Load File AID + Executable Load File AID
                + 1 + moduleAID.length // Length of Executable Module AID + Executable Module AID
                + 1 + applicationAID.length // Length of Application AID + Application AID
                + 1 + privileges.length // Length of Privileges + Privileges
                + paramLengthEncoded.length + paramLength // Length of Install Parameters field + Install Parameters field
                + 1 // Length of Install Token (0)
                + cMacLen);                                         // C-MAC Length

        byte[] installForInstallComm = new byte[headerSize + dataSize];

        installForInstallComm[ISO7816.OFFSET_CLA.getValue()] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84); // (CLA) command class (GlobalPlatform command + secure messaging with GlobalPlatform format)
        installForInstallComm[ISO7816.OFFSET_INS.getValue()] = (byte) 0xE6;    // (INS) INSTALL command
        installForInstallComm[ISO7816.OFFSET_P1.getValue()] = (byte) 0x0C;     // (P1) For install
        installForInstallComm[ISO7816.OFFSET_P2.getValue()] = (byte) 0x00;    // (P2) no information provided
        installForInstallComm[ISO7816.OFFSET_LC.getValue()] = dataSize;    // (LC) data length

        /* ------------ BEGIN -- Install for install Data Field -------------- */

        int i = 5; // next index of installForLoadComm to deal with

        // Load file AID
        installForInstallComm[i] = (byte) loadFileAID.length; // AID length
        i++;
        System.arraycopy(loadFileAID, 0, installForInstallComm, i, loadFileAID.length); // put the AID into installForLoadComm
        i += loadFileAID.length;

        // Module AID
        installForInstallComm[i] = (byte) moduleAID.length;
        i++;
        System.arraycopy(moduleAID, 0, installForInstallComm, i, moduleAID.length);
        i += moduleAID.length;

        // Application AID
        installForInstallComm[i] = (byte) applicationAID.length;
        i++;
        System.arraycopy(applicationAID, 0, installForInstallComm, i, applicationAID.length);
        i += applicationAID.length;

        // Privileges
        installForInstallComm[i] = (byte) privileges.length;
        i++;
        System.arraycopy(privileges, 0, installForInstallComm, i, privileges.length);
        i += privileges.length;

        // Install Parameters
        System.arraycopy(paramLengthEncoded, 0, installForInstallComm, i, paramLengthEncoded.length);
        i += paramLengthEncoded.length;
        System.arraycopy(params, 0, installForInstallComm, i, paramLength);
        i += paramLength;

        // Install token length
        installForInstallComm[i] = (byte) 0x00;
        i++;

        /* ------------ END -- Install for install Data Field -------------- */

        logger.debug("* Install For Install Command is " + Conversion.arrayToHex(installForInstallComm));


        if (this.getSecMode() == SecLevel.C_MAC) {
            byte[] dataCmac = new byte[installForInstallComm.length - 8]; // data used to generate C-MAC
            System.arraycopy(installForInstallComm, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, installForInstallComm, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm

            logger.debug("* Install For Install Command whith mac is " + Conversion.arrayToHex(installForInstallComm));
        }

        if ((this.getSecMode() == SecLevel.C_ENC_AND_MAC)
                && (this.scp != SCPMode.SCP_03_65)
                && (this.scp != SCPMode.SCP_03_6D)
                && (this.scp != SCPMode.SCP_03_05)
                && (this.scp != SCPMode.SCP_03_0D)
                && (this.scp != SCPMode.SCP_03_2D)
                && (this.scp != SCPMode.SCP_03_25)) {
            byte[] dataCmac = new byte[installForInstallComm.length - 8]; // data used to generate C-MAC
            System.arraycopy(installForInstallComm, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, installForInstallComm, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm

            logger.debug("* Install For Install Command whith mac is " + Conversion.arrayToHex(installForInstallComm));
            installForInstallComm = this.encryptCommand(installForInstallComm);
            logger.debug("* Encrypted Install For Install Command is " + Conversion.arrayToHex(installForInstallComm));
        }

        if ((this.getSecMode() == SecLevel.C_ENC_AND_MAC)) {
            if (scp == SCPMode.SCP_03_65
                    || scp == SCPMode.SCP_03_6D
                    || scp == SCPMode.SCP_03_05
                    || scp == SCPMode.SCP_03_0D
                    || scp == SCPMode.SCP_03_2D
                    || scp == SCPMode.SCP_03_25) {
                byte[] dataToEnc = new byte[installForInstallComm.length - 8]; // data to encrypt without 8 bytes of CMAC
                System.arraycopy(installForInstallComm, 0, dataToEnc, 0, dataToEnc.length); // data used to generate C-MAC

                byte[] encInstallForLoadComm = this.encryptCommand(dataToEnc);
                byte[] cmac = this.generateMac(encInstallForLoadComm); // generate C-MAC
                installForInstallComm = new byte[encInstallForLoadComm.length + 8];
                System.arraycopy(encInstallForLoadComm, 0, installForInstallComm, 0, encInstallForLoadComm.length);
                System.arraycopy(cmac, 0, installForInstallComm, encInstallForLoadComm.length, cmac.length);
                logger.debug("Final install for Install Command : " + Conversion.arrayToHex(installForInstallComm));
            }
        }

        CommandAPDU cmdInstallForInstall = new CommandAPDU(installForInstallComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdInstallForInstall);
        logger.debug("INSTALL FOR INSTALL AND MAKE SELECTABLE "
                + "(-> " + Conversion.arrayToHex(cmdInstallForInstall.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            throw new CardException("Error in INSTALL FOR INSTALL AND MAKE SELECTABLE : " + Integer.toHexString(resp.getSW()));
        }
        this.compudeAndVerifyRMac(resp.getBytes());

        if ((this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) && (this.scp == SCPMode.SCP_03_65)) {
            this.decryptCardResponseData(resp.getBytes());
        }

        // increment the value of counter icv for CENC
        if (this.getSecMode() == secMode.C_ENC_AND_MAC
                || this.getSecMode() == secMode.C_ENC_AND_C_MAC_AND_R_MAC
                || this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
            CENC_Counter++;
        }

        logger.debug("=> Install For Install And Make Selectable End");

        return resp;
    }

    @Override
    public ResponseAPDU getData() throws CardException {
        byte headerSize = (byte) 5;
        byte[] getDataComm = new byte[headerSize];

        getDataComm[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x00; // (CLA) command class
        getDataComm[ISO7816.OFFSET_INS.getValue()] = (byte) 0xCA; // (INS) SELECT command
        getDataComm[ISO7816.OFFSET_P1.getValue()] = (byte) 0x00; // (P1) SELECT by name
        getDataComm[ISO7816.OFFSET_P2.getValue()] = (byte) 0xC1; // (P2) first or only occurrence
        getDataComm[4] = (byte) 0x00; // (LC) data length
        CommandAPDU cmdGetData = new CommandAPDU(getDataComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdGetData);
        logger.debug("Get Data Command "
                + "(-> " + Conversion.arrayToHex(cmdGetData.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");
        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            this.resetParams();
            throw new CardException("Invalid response SW after Get Data command (" + Integer.toHexString(resp.getSW()) + ")");
        }
        System.arraycopy(resp.getBytes(), 0, this.sequenceCounter, 0, 2);
        logger.debug("Sequence counter : " + Conversion.arrayToHex(this.sequenceCounter));
        return resp;
    }

    @Override
    public void InitParamForImplicitInitiationMode(byte[] aid, SCPMode desiredScp, byte keyId) throws CardException {


        byte keyVersNumRec = (byte) 0xFF;
        //if (desiredScp == SCPMode.SCP_02_0A || desiredScp == SCPMode.SCP_02_0B){
        resetParams();
        if (true) {
            this.sequenceCounter = new byte[2];
            this.scp = desiredScp;
            getData();
            calculateDerivationData();
            if (keyId == (byte) 0) {
                keyId = (byte) 1;
                logger.info("key id switchs from 0 to 1");
            }
            SCKey key = this.getKey(keyVersNumRec, keyId);
            if (key == null) {
                this.resetParams();
                throw new CardException("Selected key not found in local repository (keySetVersion: "
                        + (keyVersNumRec & 0xff) + ", keyId: " + keyId + ")");
            }

            SCGPKey kEnc = null;
            SCGPKey kMac = null;
            SCGPKey kKek = null;

            if (scp == SCPMode.SCP_02_15) {
                logger.debug("je suis la");
                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) this.getKey(keyVersNumRec, (byte) (++keyId));
                if (kMac == null) {
                    this.resetParams();
                    throw new CardException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) this.getKey(keyVersNumRec, (byte) (++keyId));
                if (kKek == null) {
                    this.resetParams();
                    throw new CardException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }

            } else if (scp == SCPMode.SCP_02_0A) {

                kEnc = (SCGPKey) key;
                kMac = (SCGPKey) this.getKey(keyVersNumRec, (byte) (keyId));
                if (kMac == null) {
                    this.resetParams();
                    throw new CardException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }
                kKek = (SCGPKey) this.getKey(keyVersNumRec, (byte) (keyId));
                if (kKek == null) {
                    this.resetParams();
                    throw new CardException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
                }

            }
            logger.debug("scp " + this.scp);
            this.generateSessionKeys(kEnc, kMac, kKek);
            initIcvToMacOverAid(aid);


            //initIcvToMacOverAid(aid);
        } else {
            this.resetParams();
            throw new CardException("this SELECT Command is used for card which implement SCPMode (SCP 02_0A or SCP_02_0B) ");
        }
    }


    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#beginRMacSession();
     */
    @Override
    public ResponseAPDU beginRMacSession() throws CardException {
        logger.debug("=> Begin R-Mac Session begin");
        if (this.secMode == null) {
            throw new IllegalArgumentException("secLevel must be not null");
        }

        logger.debug("* Sec Mode is" + this.secMode);

        if (this.sessState != SessionState.SESSION_AUTH) {
            this.resetParams();
            throw new CardException("Session has not been authentificate");
        }

        byte[] cmd = null;
        if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
            cmd = new byte[5 + 8];
        } else {
            cmd = new byte[5];
        }
        cmd[ISO7816.OFFSET_CLA.getValue()] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84);
        cmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0x7A;
        if ((this.secMode == SecLevel.C_ENC_AND_C_MAC_AND_R_MAC) || (this.secMode == SecLevel.C_MAC_AND_R_MAC)) {
            cmd[ISO7816.OFFSET_P1.getValue()] = (byte) 0x10;
        } else if ((this.secMode == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC)) {
            cmd[ISO7816.OFFSET_P1.getValue()] = (byte) 0x30;
        }
        cmd[ISO7816.OFFSET_P2.getValue()] = (byte) 0x01;

        if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
            cmd[ISO7816.OFFSET_LC.getValue()] = 0x08;
        } else {
            cmd[ISO7816.OFFSET_LC.getValue()] = 0x00;
        }
        if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
            byte[] dataCmac = new byte[cmd.length - 8]; // data used to generate C-MAC
            System.arraycopy(cmd, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, cmd, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm
            logger.debug("* Begin R-Mac Command whith CMAC is " + Conversion.arrayToHex(cmd));
        }

        CommandAPDU cmdBeginRMac = new CommandAPDU(cmd);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdBeginRMac);

        logger.debug("EXTERNAL AUTHENTICATE command "
                + "(-> " + Conversion.arrayToHex(cmdBeginRMac.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            this.resetParams();
            throw new CardException("Error in External Authenticate : " + Integer.toHexString(resp.getSW()));
        }


        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#endRMacSession();
     */
    @Override
    public ResponseAPDU endRMacSession() throws CardException {
        logger.debug("=> End R-Mac Session begin");
        if (this.secMode == null) {
            throw new IllegalArgumentException("secLevel must be not null");
        }

        logger.debug("* Sec Mode is" + this.secMode);

        if (this.sessState != SessionState.SESSION_AUTH) {
            this.resetParams();
            throw new CardException("Session has not been authentificate");
        }

        byte[] cmd = null;
        if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
            cmd = new byte[5 + 8];
        } else {
            cmd = new byte[5];
        }
        cmd[ISO7816.OFFSET_CLA.getValue()] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84);
        cmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0x78;

        cmd[ISO7816.OFFSET_P1.getValue()] = (byte) 0x00;
        cmd[ISO7816.OFFSET_P2.getValue()] = (byte) 0x03;

        if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
            cmd[ISO7816.OFFSET_LC.getValue()] = 0x08;
        } else {
            cmd[ISO7816.OFFSET_LC.getValue()] = 0x00;
        }
        if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
            byte[] dataCmac = new byte[cmd.length - 8]; // data used to generate C-MAC
            System.arraycopy(cmd, 0, dataCmac, 0, dataCmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(dataCmac); // generate C-MAC
            System.arraycopy(cmac, 0, cmd, dataCmac.length, cmac.length); // put C-MAC into installForLoadComm
            logger.debug("* Begin R-Mac Command whith CMAC is " + Conversion.arrayToHex(cmd));
        }

        CommandAPDU cmdBeginRMac = new CommandAPDU(cmd);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdBeginRMac);

        logger.debug("EXTERNAL AUTHENTICATE command "
                + "(-> " + Conversion.arrayToHex(cmdBeginRMac.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            this.resetParams();
            throw new CardException("Error in External Authenticate : " + Integer.toHexString(resp.getSW()));
        }


        return resp;
    }


    /**
     * Compute and verify the Rmac recieved.
     *
     * @response Response Message receved by the off card entity
     */
    protected void compudeAndVerifyRMac(byte[] response) throws CardException {
        if (this.scp == SCPMode.SCP_03_65
                || this.scp == SCPMode.SCP_03_6D
                || this.scp == SCPMode.SCP_03_25) {
            if ((this.getSecMode() == SecLevel.R_MAC) ||
                    (this.getSecMode() == SecLevel.C_MAC_AND_R_MAC) ||
                    (this.getSecMode() == SecLevel.C_ENC_AND_C_MAC_AND_R_MAC) ||
                    (this.getSecMode() == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC)) {
                byte[] recevedRmac = new byte[8];
                byte[] data;
                if (response.length > 10)// the response contain data
                {
                    data = new byte[16 + (response.length - 10) + 2];
                    System.arraycopy(this.icv, 0, data, 0, this.icv.length);
                    System.arraycopy(response, 0, data, 16, response.length - 10);
                    System.arraycopy(response, response.length - 2, data, 16 + response.length - 10, 2);

                } else    //the response does not contain data
                {
                    data = new byte[18];
                    System.arraycopy(this.icv, 0, data, 0, this.icv.length);
                    System.arraycopy(response, response.length - 2, data, 16, 2);
                }

                logger.debug("* data used to  calculate RMac: " + Conversion.arrayToHex(data));

                byte[] copyOfIcv = new byte[16];
                System.arraycopy(this.icv, 0, copyOfIcv, 0, this.icv.length);


                byte[] Rmac = this.generateMac(data);

                logger.debug("* Computed RMac is : " + Conversion.arrayToHex(Rmac));

                System.arraycopy(response, response.length - 10, recevedRmac, 0, recevedRmac.length);

                logger.debug("* Receved RMac is : " + Conversion.arrayToHex(recevedRmac));

                System.arraycopy(copyOfIcv, 0, this.icv, 0, copyOfIcv.length);

                boolean eq = Arrays.equals(Rmac, recevedRmac);
                if (!eq) {
                    throw new CardException("Response APDU error - RMAC Not verification error: ");
                }


            }
        }
    }


    protected byte[] decryptCardResponseData(byte[] response) throws CardException {

        try {
            byte[] encryptedData;
            if (response.length > 10)// the response contain data
            {
                byte[] res = null;
                if (scp == SCPMode.SCP_03_65) {
                    encryptedData = new byte[(response.length - 10)];
                    System.arraycopy(response, 0, encryptedData, 0, response.length - 10);
                    if ((encryptedData.length % 16) != 0) {
                        throw new CardException("The length of received encrypted data is invalid");
                    }
                    IvParameterSpec ivSpec = new IvParameterSpec(iv_zero_scp03);
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.sessEnc, "AES"), ivSpec);
                    res = cipher.doFinal(encryptedData);
                }
                if (scp == SCPMode.SCP_03_6D) {
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
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.sessEnc, "AES"), ivSpec);
                    res = cipher.doFinal(encryptedData);
                    RENC_counter++;
                }
                if (scp == SCPMode.SCP_03_05) {// this mode don't support RMAC then the response contain only Crypted data and Statuts words
                    encryptedData = new byte[(response.length - 2)];
                    System.arraycopy(response, 0, encryptedData, 0, response.length - 2);
                    if ((encryptedData.length % 16) != 0) {
                        throw new CardException("The length of received encrypted data is invalid");
                    }
                    IvParameterSpec ivSpec = new IvParameterSpec(iv_zero_scp03);
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.sessEnc, "AES"), ivSpec);
                    res = cipher.doFinal(encryptedData);
                }
                if (scp == SCPMode.SCP_03_0D) {
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
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.sessEnc, "AES"), ivSpec);
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

    @Override
    public ResponseAPDU sendCommand(byte[] APDUCommand) throws CardException {
        CommandAPDU cmd = new CommandAPDU(APDUCommand);
        ResponseAPDU resp = this.getCardChannel().transmit(cmd);

        return resp;
    }
}
