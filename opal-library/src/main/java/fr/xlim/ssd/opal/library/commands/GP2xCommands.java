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

import fr.xlim.ssd.opal.library.commands.scp.AbstractSCP;
import fr.xlim.ssd.opal.library.commands.scp.SCP;
import fr.xlim.ssd.opal.library.commands.scp.SCP01;
import fr.xlim.ssd.opal.library.commands.scp.SCP02;
import fr.xlim.ssd.opal.library.commands.scp.SCP03;
import fr.xlim.ssd.opal.library.config.SCDerivableKey;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCKey;
import fr.xlim.ssd.opal.library.config.SCPMode;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.library.utilities.RandomGenerator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of Global Platform specification
 *
 * @author Damien Arcuset, Eric Linke
 * @author Guillaume Bouffard
 * @author Jean Dubreuil
 */
public class GP2xCommands extends AbstractCommands implements Commands {

    /// Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(GP2xCommands.class);

    // SCP 01 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP01 = (byte) 0x01;

    // SCP 02 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP02 = (byte) 0x02;

    // SCP 03 constant used in @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} Response command
    protected static final byte SCP03 = (byte) 0x03;
    /// Static Keys
    protected List<SCKey> keys = new LinkedList<SCKey>();

    protected byte[] aid;
    
    protected SCP secureProtocol;

    // used to calculate Encrypted counter ICV for C-ENC
    protected int CENC_Counter = 01;

    // used to calculate Encrypted counter ICV for R-ENC
    protected int RENC_counter = 01;

    //Counter ICV padding for R-ENC computing
    protected static final byte[] SCP03_R_ENC_COUNTER_ICV_PADDING = Conversion.hexToArray("80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    //Counter ICV padding for R-ENC computing
    protected static final byte[] SCP03_C_ENC_COUNTER_ICV_PADDING = Conversion.hexToArray("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    protected byte[] sequenceCounter;
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
        if (secureProtocol == null)
            return SCPMode.SCP_UNDEFINED;
        return secureProtocol.getSCPMode();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#getSessState()
     */
    @Override
    public SessionState getSessState() {
        if (secureProtocol == null)
            return SessionState.NO_SESSION;
        return secureProtocol.getSessState();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#getSecMode()
     */
    @Override
    public SecLevel getSecMode() {
        if (secureProtocol == null)
            return SecLevel.NO_SECURITY_LEVEL;
        return secureProtocol.getSecLevel();
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
        //this.scp = desiredScp;???
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

        // get the value of Secure Channel Sequence Counter,Calculate derivation data
        getData();
        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#resetParams()
     */
    @Override
    public void resetParams() {
        this.secureProtocol = null;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#initializeUpdate(byte, byte, fr.xlim.ssd.opal.library.SCP_Mode)
     */
    @Override
    public ResponseAPDU initializeUpdate(byte keySetVersion, byte keyId, SCPMode desiredScp) throws CardException {

        logger.debug("=> Initialize Update");
        this.resetParams();
        byte[] hostChallenge = RandomGenerator.generateRandom(8);

        byte[] initUpdCmd = new byte[13];
        initUpdCmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80;
        initUpdCmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0x50;
        initUpdCmd[ISO7816.OFFSET_P1.getValue()] = keySetVersion;
        initUpdCmd[ISO7816.OFFSET_P2.getValue()] = keyId;
        initUpdCmd[ISO7816.OFFSET_LC.getValue()] = (byte) hostChallenge.length;

        System.arraycopy(hostChallenge, 0, initUpdCmd, 5, hostChallenge.length);

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
        
        byte[] cardChallenge = new byte[8];
        byte[] cardCryptoResp = new byte[8];
        byte[] keyDivData = new byte[10];

        byte keyVersNumRec = resp.getData()[10];
        byte scpRec = resp.getData()[11];
        
        
        switch(scpRec) {
            case SCP01:
                if (desiredScp == SCPMode.SCP_UNDEFINED) {
                    secureProtocol = new SCP01(SCPMode.SCP_01_05);
                }
                else if (desiredScp.getProtocolNumber() == SCP01) {
                    secureProtocol = new SCP01(desiredScp);
                }
                else {
                    this.resetParams();
                    throw new CardException("Desired SCP does not match with card SCP value (" + scpRec + ")");
                }
                logger.debug("SCPMode is " + secureProtocol.getSCPMode());
                
                /*
                 * INITIALIZE UPDATE response in SCP 01 mode
                 * -0-----------------------09-10------11-12------------19-20-------------27-
                 * | Key Diversification Data | Key Info | Card Challenge | Card Cryptogram |
                 * --------------------------------------------------------------------------
                 */

                System.arraycopy(resp.getData(), 0, keyDivData, 0, 10);
                System.arraycopy(resp.getData(), 12, cardChallenge, 0, 8);
                System.arraycopy(resp.getData(), 20, cardCryptoResp, 0, 8);
                break;
            case SCP02:
                if (desiredScp == SCPMode.SCP_UNDEFINED) {
                    secureProtocol = new SCP02(SCPMode.SCP_02_15);
                }
                else if (desiredScp.getProtocolNumber() == SCP02) {
                    secureProtocol = new SCP02(desiredScp);
                }
                else {
                    this.resetParams();
                    throw new CardException("Desired SCP does not match with card SCP value (" + scpRec + ")");
                }
                logger.debug("SCPMode is " + secureProtocol.getSCPMode());
                
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
                System.arraycopy(resp.getData(), 12, cardChallenge, 0, 8);
                System.arraycopy(resp.getData(), 20, cardCryptoResp, 0, 8);
                break;
            case SCP03:
                if (desiredScp == SCPMode.SCP_UNDEFINED) {
                    secureProtocol = new SCP03(SCPMode.SCP_03_05);
                }
                else if (desiredScp.getProtocolNumber() == SCP03) {
                    secureProtocol = new SCP03(desiredScp);
                }
                else {
                    this.resetParams();
                    throw new CardException("Desired SCP does not match with card SCP value (" + scpRec + ")");
                }
                logger.debug("SCPMode is " + secureProtocol.getSCPMode());
                
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
                System.arraycopy(resp.getData(), 13, cardChallenge, 0, 8);
                System.arraycopy(resp.getData(), 21, cardCryptoResp, 0, 8);
                if (resp.getData().length == 32) {
                    byte[] sequenceCounter = new byte[3];
                    System.arraycopy(resp.getData(), 29, sequenceCounter, 0, 3);
                    logger.debug("* Sequence Counter is " + Conversion.arrayToHex(sequenceCounter));
                }
                break;
            default:
                this.resetParams();
                throw new CardException("SCP version not available (" + scpRec + ")");
        }
        
        secureProtocol.setCardChallenge(cardChallenge);
        secureProtocol.setHostChallenge(hostChallenge);
        logger.debug("* Key Diversification Data is " + Conversion.arrayToHex(keyDivData));
        logger.debug("* Host Challenge is " + Conversion.arrayToHex(secureProtocol.getHostChallenge()));
        logger.debug("* Card Challenge is " + Conversion.arrayToHex(secureProtocol.getCardChallenge()));
        logger.debug("* Card Crypto Resp is " + Conversion.arrayToHex(cardCryptoResp));


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


        if (secureProtocol.getSCPMode().getProtocolNumber() == SCP01) {
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


        if (secureProtocol.getSCPMode().getProtocolNumber() == SCP02 
                && (secureProtocol.getSCPMode().getIParameter() & AbstractSCP.SECURE_CHANNEL_KEYS_3) != 0) {
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

        } else if (secureProtocol.getSCPMode().getProtocolNumber() == SCP02 
                && (secureProtocol.getSCPMode().getIParameter() & AbstractSCP.SECURE_CHANNEL_KEYS_3) == 0) {
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

        if (secureProtocol.getSCPMode().getProtocolNumber() == SCP03) {
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

        secureProtocol.initICV();
        generateSessionKeys(kEnc, kMac, kKek);


        if (secureProtocol.getSCPMode() == SCPMode.SCP_02_45 || secureProtocol.getSCPMode() == SCPMode.SCP_02_55) {
            byte[] computedCardChallenge = ((SCP02) secureProtocol).pseudoRandomGenerationCardChallenge(this.aid);
            if (!Arrays.equals(cardChallenge, computedCardChallenge)) {
                logger.debug("Card challege is " + Conversion.arrayToHex(cardChallenge) + "   " + cardChallenge.length);
                throw new CardException("Error verifying Card Challenge");
            }
        }

        byte[] tmp = secureProtocol.getCardCryptogram();
        if (!Arrays.equals(cardCryptoResp, tmp)) {
            this.resetParams();
            throw new CardException("Error verifying Card Cryptogram : " + Conversion.arrayToHex(cardCryptoResp) + " -- " + Conversion.arrayToHex(tmp));
        }

        secureProtocol.setSessionState(SessionState.SESSION_INIT);

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

        if (secureProtocol == null || secureProtocol.getSessState() != SessionState.SESSION_INIT) {
            this.resetParams();
            throw new CardException("Session is not initialized");
        }

        secureProtocol.setSecLevel(secLevel);

        logger.debug("* Sec Mode is" + secureProtocol.getSecLevel());

        byte[] extAuthCmd = new byte[5 + 8];
        extAuthCmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x84;
        extAuthCmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0x82;
        extAuthCmd[ISO7816.OFFSET_P1.getValue()] = secureProtocol.getSecLevel().getVal();
        extAuthCmd[ISO7816.OFFSET_P2.getValue()] = (byte) 0x00;
        extAuthCmd[ISO7816.OFFSET_LC.getValue()] = (byte) 0x8;
        System.arraycopy(secureProtocol.getHostCryptogram(), 0, extAuthCmd, 5, 8);
        
        logger.debug("* Data uses to calculate mac value is" + Conversion.arrayToHex(extAuthCmd));

        extAuthCmd = secureProtocol.generateCMac(extAuthCmd);

        CommandAPDU cmdExtauth = new CommandAPDU(extAuthCmd);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdExtauth);

        logger.debug("EXTERNAL AUTHENTICATE command "
                + "(-> " + Conversion.arrayToHex(cmdExtauth.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            this.resetParams();
            throw new CardException("Error in External Authenticate : " + Integer.toHexString(resp.getSW()));
        }
        secureProtocol.setSessionState(SessionState.SESSION_AUTH);

        //Protocol SCP03 - intitialisation of counters for computing of counter ICV for C/R-Enc.
        CENC_Counter = 1;
        RENC_counter = 1;

        logger.debug("=> External Authenticate end");

        return resp;
    }
    protected void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek) {
        secureProtocol.generateSessionKeys(staticKenc, staticKmac, staticKkek);
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
        logger.debug("+ SecLevel is " + getSecMode());

        if (fileType == null) {
            throw new IllegalArgumentException("fileType must be not null");
        }

        if (responseMode == null) {
            throw new IllegalArgumentException("responseMode must be not null");
        }

        // TODO: check searchQualifier size ?
        List<ResponseAPDU> responsesList = new LinkedList<ResponseAPDU>();

        byte[] getStatusCmd = null;
        //byte dataSize = (byte) 0; // '0xD0' + Key Identifier + '0xD2' + Key Version Number + C-MAC
        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC

        if (searchQualifier == null) {
            searchQualifier = new byte[2];
            searchQualifier[0] = (byte) 0x4F;
            searchQualifier[1] = (byte) 0x00;
            logger.debug("* Search Qualifier equals " + Conversion.arrayToHex(searchQualifier));
        }

        getStatusCmd = new byte[headerSize + searchQualifier.length];
        getStatusCmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80;
        getStatusCmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0xF2;
        getStatusCmd[ISO7816.OFFSET_P1.getValue()] = fileType.getValue();
        getStatusCmd[ISO7816.OFFSET_P2.getValue()] = responseMode.getValue();
        getStatusCmd[ISO7816.OFFSET_LC.getValue()] = (byte) searchQualifier.length;
        System.arraycopy(searchQualifier, 0, getStatusCmd, 5, searchQualifier.length);
        logger.debug("* Get Status command is " + Conversion.arrayToHex(getStatusCmd));

        byte[] uncipheredgetStatusCmd = getStatusCmd.clone();
        if (secureProtocol != null)
            getStatusCmd = secureProtocol.encapsulateCommand(uncipheredgetStatusCmd);

        CommandAPDU cmdGetstatus = new CommandAPDU(getStatusCmd);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdGetstatus);
        
        logger.debug("GET STATUS command "
                + "(-> " + Conversion.arrayToHex(cmdGetstatus.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        responsesList.add(resp);

        while (resp.getSW() == ISO7816.SW_MORE_DATA_AVAILABLE.getValue()) {
            uncipheredgetStatusCmd[ISO7816.OFFSET_P2.getValue()] = (byte) (responseMode.getValue() + (byte) 0x01); // Get next occurrence(s)
            if (secureProtocol != null)
                getStatusCmd = secureProtocol.encapsulateCommand(uncipheredgetStatusCmd);
            else
                getStatusCmd = uncipheredgetStatusCmd;
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
    // SECURITY DOMAIN

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.Commands#deleteOnCardKey(byte[], boolean)
     */
    @Override
    public ResponseAPDU deleteOnCardObj(byte[] aid, boolean cascade) throws CardException {

        logger.debug("=> Delete On Card Object begin");

        logger.debug("+ " + (aid != null ? "AID to delete is " + Conversion.arrayToHex(aid) : "There is not AID"));
        logger.debug("+ Cascade mode ? " + cascade);
        logger.debug("+ Security mode is " + getSecMode());

        if (aid == null) {
            throw new IllegalArgumentException("aid must be not null");
        }

        byte dataSize = (byte) (2 + aid.length); // params +  AID
        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC

        byte[] deleteComm = new byte[headerSize + dataSize];

        deleteComm[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80; // (CLA) command class 
        deleteComm[ISO7816.OFFSET_INS.getValue()] = (byte) 0xE4; // (INS) DELETE command
        deleteComm[ISO7816.OFFSET_P1.getValue()] = (byte) 0x00; // (P1) the only and the last DELETE command
        deleteComm[ISO7816.OFFSET_P2.getValue()] = (cascade ? (byte) 0x80 : (byte) 0x00); // (P2) 0x00 : only delete the specified object
        //      0x80 : delete object and related objects
        deleteComm[ISO7816.OFFSET_LC.getValue()] = dataSize;   // (LC) data length

        deleteComm[ISO7816.OFFSET_CDATA.getValue()] = (byte) 0x4F; // the object being deleted is specified by its AID
        deleteComm[ISO7816.OFFSET_CDATA.getValue() + 1] = (byte) aid.length; // AID length
        System.arraycopy(aid, 0, deleteComm, ISO7816.OFFSET_CDATA.getValue() + 2, aid.length); // put the AID into deleteComm

        logger.debug("* Delete Command is " + Conversion.arrayToHex(deleteComm));

        if (secureProtocol != null)
            deleteComm = secureProtocol.encapsulateCommand(deleteComm);
        
        CommandAPDU cmdDelete = new CommandAPDU(deleteComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdDelete);

        logger.debug("DELETE OBJECT command "
                + "(-> " + Conversion.arrayToHex(cmdDelete.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            throw new CardException("Error in DELETE OBJECT : " + Integer.toHexString(resp.getSW()));
        }

        if (secureProtocol != null)
            logger.debug("plain text response is " + Conversion.arrayToHex(secureProtocol.desencapsulateResponse(resp.getBytes())));

        // increment the value of counter icv for CENC
        /*if (this.getSecMode() == secMode.C_ENC_AND_MAC
                || this.getSecMode() == secMode.C_ENC_AND_C_MAC_AND_R_MAC
                || this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
            CENC_Counter++;
        }*/


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
        logger.debug("+ SecLevel is " + getSecMode());

        byte dataSize = (byte) 4; // '0xD0' + Key Identifier + '0xD2' + Key Version Number
        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC
        byte[] deleteComm = new byte[headerSize + dataSize];

        deleteComm[0] = (byte) 0x80; // (CLA) command class
        deleteComm[1] = (byte) 0xE4; // (INS) DELETE command
        deleteComm[2] = (byte) 0x00; // (P1) the only and the last DELETE command
        deleteComm[3] = (byte) 0x00; // (P2) 0x00 : only delete the specified key
        deleteComm[4] = dataSize;   // (LC) data length

        deleteComm[5] = (byte) 0xD0; // Key Identifier
        deleteComm[6] = keyId;
        deleteComm[7] = (byte) 0xD2; // Key Version Number
        deleteComm[8] = keySetVersion;

        logger.debug("* Delete Command is " + Conversion.arrayToHex(deleteComm));

        if (secureProtocol != null)
            deleteComm = secureProtocol.encapsulateCommand(deleteComm);

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
        logger.debug("+ SecLevel is " + getSecMode());

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

        byte[] installForLoadComm = new byte[(headerSize + (short) (dataSize & 0xFF))];

        installForLoadComm[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80; // (CLA) command class
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

        if (secureProtocol != null)
            installForLoadComm = secureProtocol.encapsulateCommand(installForLoadComm);

        CommandAPDU cmdInstallforload = new CommandAPDU(installForLoadComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdInstallforload);

        logger.debug("INSTALL FOR LOAD command "
                + "(-> " + Conversion.arrayToHex(cmdInstallforload.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            throw new CardException("Error in INSTALL FOR LOAD : " + Integer.toHexString(resp.getSW()));
        }

        if (secureProtocol != null)
            logger.debug("plain text response is " + Conversion.arrayToHex(secureProtocol.desencapsulateResponse(resp.getBytes())));

        // increment the value of counter icv for CENC
        /*if (this.getSecMode() == secMode.C_ENC_AND_MAC
                || this.getSecMode() == secMode.C_ENC_AND_C_MAC_AND_R_MAC
                || this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
            CENC_Counter++;
        }*/

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
        logger.debug("+ SecLevel is " + getSecMode());

        List<ResponseAPDU> responses = new LinkedList<ResponseAPDU>();
        int capFileRemainLen = capFile.length;
        ByteBuffer buffer = null;

        buffer = ByteBuffer.wrap(capFile);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int headerLen = 5;
        int dataBlockSize = maxDataLength & 0x0ff;            // Size of a block

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            dataBlockSize -= 8;
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
                    cmd = new byte[headerLen + capFileRemainLen + ber.length];
                    cmd[4] = (byte) (capFileRemainLen + ber.length);
                    System.arraycopy(ber, 0, cmd, 5, ber.length);
                    buffer.get(cmd, 5 + ber.length, capFileRemainLen);
                } else { //last block to be sent
                    cmd = new byte[headerLen + capFileRemainLen];
                    cmd[4] = (byte) (capFileRemainLen);
                    buffer.get(cmd, 5, capFileRemainLen);
                }
            } else { // 2 blocks at least
                cmd = new byte[headerLen + dataBlockSize];
                cmd[4] = (byte) (dataBlockSize);
                if (i == 0) { // first block
                    System.arraycopy(ber, 0, cmd, 5, ber.length);
                    buffer.get(cmd, 5 + ber.length, dataSizeInFirstCommand);
                    capFileRemainLen -= dataSizeInFirstCommand;
                } else { // block i
                    buffer.get(cmd, 5, dataBlockSize);
                    capFileRemainLen -= dataBlockSize;
                }
            }
            cmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80;
            cmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0xE8;
            cmd[ISO7816.OFFSET_P1.getValue()] = (byte) ((i == nbBlock - 1) ? 0x80 : 0x00);
            cmd[ISO7816.OFFSET_P2.getValue()] = (byte) i;

            logger.debug("* Load Command is " + Conversion.arrayToHex(cmd));

            if (secureProtocol != null)
                cmd = secureProtocol.encapsulateCommand(cmd);
            
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
            if (secureProtocol != null)
                logger.debug("plain text response is " + Conversion.arrayToHex(secureProtocol.desencapsulateResponse(resp.getBytes())));
            /*if (this.getSecMode() == secMode.C_ENC_AND_MAC
                    || this.getSecMode() == secMode.C_ENC_AND_C_MAC_AND_R_MAC
                    || this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
                CENC_Counter++;
            }*/

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

        byte headerSize = (byte) 5;     // CLA + INS + P1 + P2 + LC
        byte dataSize = (byte) (1 + loadFileAID.length // Length of Executable Load File AID + Executable Load File AID
                + 1 + moduleAID.length // Length of Executable Module AID + Executable Module AID
                + 1 + applicationAID.length // Length of Application AID + Application AID
                + 1 + privileges.length // Length of Privileges + Privileges
                + paramLengthEncoded.length + paramLength // Length of Install Parameters field + Install Parameters field
                + 1); // Length of Install Token (0)

        byte[] installForInstallComm = new byte[headerSize + dataSize];

        installForInstallComm[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80; // (CLA) command class
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

        if (secureProtocol != null)
            installForInstallComm = secureProtocol.encapsulateCommand(installForInstallComm);

        CommandAPDU cmdInstallForInstall = new CommandAPDU(installForInstallComm);
        ResponseAPDU resp = this.getCardChannel().transmit(cmdInstallForInstall);
        logger.debug("INSTALL FOR INSTALL AND MAKE SELECTABLE "
                + "(-> " + Conversion.arrayToHex(cmdInstallForInstall.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != ISO7816.SW_NO_ERROR.getValue()) {
            throw new CardException("Error in INSTALL FOR INSTALL AND MAKE SELECTABLE : " + Integer.toHexString(resp.getSW()));
        }
        if (secureProtocol != null)
            logger.debug("plain text response is " + Conversion.arrayToHex(secureProtocol.desencapsulateResponse(resp.getBytes())));

        // increment the value of counter icv for CENC
        /*if (this.getSecMode() == secMode.C_ENC_AND_MAC
                || this.getSecMode() == secMode.C_ENC_AND_C_MAC_AND_R_MAC
                || this.getSecMode() == secMode.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
            CENC_Counter++;
        }*/

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
        sequenceCounter = new byte[2];
        System.arraycopy(resp.getBytes(), 0, sequenceCounter, 0, 2);
        logger.debug("Sequence counter : " + Conversion.arrayToHex(sequenceCounter));
        return resp;
    }

    @Override
    public void InitParamForImplicitInitiationMode(byte[] aid, SCPMode desiredScp, byte keyId) throws CardException {
        byte keyVersNumRec = (byte) 0xFF;
        resetParams();
        //this.scp = desiredScp;
        getData();
        secureProtocol = new SCP02(desiredScp);
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

        if (secureProtocol.getSCPMode() == SCPMode.SCP_02_15) {
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

        } else if (secureProtocol.getSCPMode() == SCPMode.SCP_02_0A) {

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
        logger.debug("scp " + secureProtocol.getSCPMode());
        generateSessionKeys(kEnc, kMac, kKek);
        ((SCP02) secureProtocol).initIcvToMacOverAid(aid);
    }


    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands#beginRMacSession();
     */
    @Override
    public ResponseAPDU beginRMacSession() throws CardException {
        logger.debug("=> Begin R-Mac Session begin");
        if (secureProtocol.getSecLevel() == null) {
            throw new IllegalArgumentException("secLevel must be not null");
        }

        logger.debug("* Sec Mode is" + getSecMode());

        if (secureProtocol.getSessState() != SessionState.SESSION_AUTH) {
            this.resetParams();
            throw new CardException("Session has not been authentificate");
        }

        byte[] cmd = new byte[5];
        cmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80;
        cmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0x7A;
        if ((secureProtocol.getSecLevel() == SecLevel.C_ENC_AND_C_MAC_AND_R_MAC) || (secureProtocol.getSecLevel() == SecLevel.C_MAC_AND_R_MAC)) {
            cmd[ISO7816.OFFSET_P1.getValue()] = (byte) 0x10;
        } else if ((secureProtocol.getSecLevel() == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC)) {
            cmd[ISO7816.OFFSET_P1.getValue()] = (byte) 0x30;
        }
        cmd[ISO7816.OFFSET_P2.getValue()] = (byte) 0x01;
        cmd[ISO7816.OFFSET_LC.getValue()] = 0x00;
        
        cmd = secureProtocol.encapsulateCommand(cmd);

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
        if (secureProtocol.getSecLevel() == null) {
            throw new IllegalArgumentException("secLevel must be not null");
        }

        logger.debug("* Sec Mode is" + getSecMode());

        if (secureProtocol.getSessState() != SessionState.SESSION_AUTH) {
            this.resetParams();
            throw new CardException("Session has not been authentificate");
        }

        byte[] cmd = new byte[5];
        cmd[ISO7816.OFFSET_CLA.getValue()] = (byte) 0x80;
        cmd[ISO7816.OFFSET_INS.getValue()] = (byte) 0x78;
        cmd[ISO7816.OFFSET_P1.getValue()] = (byte) 0x00;
        cmd[ISO7816.OFFSET_P2.getValue()] = (byte) 0x03;
        cmd[ISO7816.OFFSET_LC.getValue()] = 0x00;
        
        cmd = secureProtocol.encapsulateCommand(cmd);

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

    @Override
    public ResponseAPDU sendCommand(byte[] APDUCommand) throws CardException {
        CommandAPDU cmd = new CommandAPDU(APDUCommand);
        ResponseAPDU resp = this.getCardChannel().transmit(cmd);

        return resp;
    }
}
