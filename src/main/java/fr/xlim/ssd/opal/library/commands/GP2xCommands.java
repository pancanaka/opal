package fr.xlim.ssd.opal.library.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import fr.xlim.ssd.opal.library.CommandsProvider;
import fr.xlim.ssd.opal.library.SCDerivableKey;
import fr.xlim.ssd.opal.library.SCGPKey;
import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.FileType;
import fr.xlim.ssd.opal.library.GetStatusResponseMode;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SessionState;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import fr.xlim.ssd.opal.library.utilities.RandomGenerator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dede
 *
 */
public class GP2xCommands extends AbstractCommands implements Commands {

    private static final Logger logger = LoggerFactory.getLogger(GP2xCommands.class);

    static {
        CommandsProvider.register(new GP2xCommands());
    }
    /**
     *
     */
    protected static final byte[] padding = Conversion.hexToArray("80 00 00 00 00 00 00 00");
    /**
     *
     */
    protected List<SCKey> keys = new LinkedList<SCKey>();
    /**
     *
     */
    protected SCPMode scp;
    /**
     *
     */
    protected SecLevel secMode;
    /**
     *
     */
    protected SessionState sessState;
    /**
     *
     */
    protected byte[] sessEnc;
    /**
     *
     */
    protected byte[] sessMac;
    /**
     *
     */
    protected byte[] sessKek;
    /**
     *
     */
    protected byte[] hostChallenge;
    /**
     *
     */
    protected byte[] cardChallenge;
    /**
     *
     */
    protected byte[] cardCrypto;
    /**
     *
     */
    protected byte[] derivationData;
    /**
     *
     */
    protected byte[] hostCrypto;
    /**
     *
     */
    protected byte[] icv;

    /**
     *
     */
    protected GP2xCommands() {
        resetParams();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getScp()
     */
    @Override
    public SCPMode getScp() {
        return this.scp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getSessState()
     */
    @Override
    public SessionState getSessState() {
        return this.sessState;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getSecMode()
     */
    @Override
    public SecLevel getSecMode() {
        return this.secMode;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getKeys()
     */
    @Override
    public SCKey[] getKeys() {
        return this.keys.toArray(new SCKey[0]);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getKey(byte, byte)
     */
    @Override
    public SCKey getKey(byte keySetVersion, byte keyId) {
        for (SCKey currKey : this.keys) {
            if (currKey.getSetVersion() == keySetVersion && currKey.getId() == keyId) {
                return currKey;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#setOffCardKey(fr.xlim.ssd.opal.SCKey)
     */
    @Override
    public SCKey setOffCardKey(SCKey key) {
        for (SCKey currKey : this.keys) {
            if (currKey.getSetVersion() == key.getSetVersion() && currKey.getId() == key.getId()) {
                this.keys.remove(currKey);
                this.keys.add(key);
                return currKey;
            }
        }
        this.keys.add(key);
        return key;
    }

    @Override
    public void setOffCardKeys(SCKey[] keys) {
        for (SCKey key : keys) {
            this.setOffCardKey(key);
        }
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#deleteOffCardKey(int, int)
     */
    // TODO: why int insted of byte in parameter ?
    @Override
    public SCKey deleteOffCardKey(byte keySetVersion, byte keyId) {
        for (SCKey currKey : this.keys) {
            if (currKey.getSetVersion() == keySetVersion && currKey.getId() == keyId) {
                this.keys.remove(currKey);
                return currKey;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#select(byte[])
     */
    @Override
    public ResponseAPDU select(byte[] aid) throws CardException {
        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC

        byte[] selectComm = new byte[headerSize + aid.length];

        selectComm[0] = (byte) 0x00; // (CLA) command class
        selectComm[1] = (byte) 0xA4; // (INS) SELECT command
        selectComm[2] = (byte) 0x04; // (P1) SELECT by name
        selectComm[3] = (byte) 0x00; // (P2) first or only occurrence
        selectComm[4] = (byte) aid.length; // (LC) data length

        System.arraycopy(aid, 0, selectComm, 5, aid.length); // put the AID into selectComm

        CommandAPDU cmdSelect = new CommandAPDU(selectComm);
        ResponseAPDU resp = this.cc.transmit(cmdSelect);
        logger.debug("SELECT Command "
                + "(-> " + Conversion.arrayToHex(cmdSelect.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");
        if (resp.getSW() != 0x9000) {
            this.resetParams();
            throw new CardException("Invalid response SW after SELECT command (" + Integer.toHexString(resp.getSW()) + ")");
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#resetParams()
     */
    @Override
    public void resetParams() {
        this.initIcv();
        this.scp = SCPMode.SCP_UNDEFINED;
        this.secMode = SecLevel.NO_SECURITY_LEVEL;
        this.sessState = SessionState.NO_SESSION;
        this.sessEnc = null;
        this.sessMac = null;
        this.sessKek = null;
        this.derivationData = null;
        this.hostCrypto = null;
        this.cardCrypto = null;
        this.cardChallenge = null;
        this.hostChallenge = null;

    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#initializeUpdate(byte, byte, fr.xlim.ssd.opal.Constant.SCPMode)
     */
    @Override
    public ResponseAPDU initializeUpdate(byte keySetVersion, byte keyId, SCPMode desiredScp) throws CardException {

        this.resetParams();
        this.hostChallenge = RandomGenerator.generateRandom(8);

        byte[] initUpdCmd = new byte[13];
        initUpdCmd[0] = (byte) 0x80;
        initUpdCmd[1] = (byte) 0x50;
        initUpdCmd[2] = keySetVersion;
        initUpdCmd[3] = keyId;
        initUpdCmd[4] = (byte) this.hostChallenge.length;

        System.arraycopy(this.hostChallenge, 0, initUpdCmd, 5, this.hostChallenge.length);

        CommandAPDU cmdInitUpd = new CommandAPDU(initUpdCmd);

        ResponseAPDU resp = this.cc.transmit(cmdInitUpd);

        logger.debug("INIT UPDATE command "
                + "(-> " + Conversion.arrayToHex(cmdInitUpd.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != 0x9000) {
            this.resetParams();
            throw new CardException("Invalid response SW after first INIT UPDATE command (" + resp.getSW() + ")");
        }

        if (resp.getData().length != 28) {
            this.resetParams();
            throw new CardException("Invalid response size after first INIT UPDATE command ("
                    + resp.getData().length + ")");
        }

        this.cardChallenge = new byte[8];
        byte[] cardCryptoResp = new byte[8];
        byte[] keyDivData = new byte[10];

        System.arraycopy(resp.getData(), 12, this.cardChallenge, 0, 8);
        System.arraycopy(resp.getData(), 20, cardCryptoResp, 0, 8);
        System.arraycopy(resp.getData(), 0, keyDivData, 0, 10);
        byte keyVersNumRec = resp.getData()[10];
        byte scpRec = resp.getData()[11];

        if (scpRec == (byte) 1) {
            if (desiredScp == SCPMode.SCP_UNDEFINED) {
                this.scp = SCPMode.SCP_01_05;
            } else if (desiredScp == SCPMode.SCP_01_05 || desiredScp == SCPMode.SCP_01_15) {
                this.scp = desiredScp;
            } else {
                this.resetParams();
                throw new CardException("Desired SCP does not match with card SCP value (" + scpRec + ")");
            }
        } else {
            this.resetParams();
            throw new CardException("SCP version not available (" + scpRec + ")");
        }

        logger.debug("Selected SCP is " + this.scp);

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

        SCGPKey k_enc = null;
        SCGPKey k_mac = null;
        SCGPKey k_kek = null;

        if (key instanceof SCDerivableKey) {
            SCGPKey[] keysFromDerivableKey = ((SCDerivableKey) key).deriveKey(keyDivData);
            k_enc = keysFromDerivableKey[0];
            k_mac = keysFromDerivableKey[1];
            k_kek = keysFromDerivableKey[2];
        } else {
            k_enc = (SCGPKey) key;
            k_mac = (SCGPKey) this.getKey(keyVersNumRec, (byte) (++keyId));
            if (k_mac == null) {
                this.resetParams();
                throw new CardException("Selected MAC Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
            }
            k_kek = (SCGPKey) this.getKey(keyVersNumRec, (byte) (++keyId));
            if (k_kek == null) {
                this.resetParams();
                throw new CardException("Selected KEK Key not found in Local Repository : keySetVersion : " + (keyVersNumRec & 0xff) + ", keyId : " + (keyId));
            }
        }

        this.calculateDerivationData();
        this.generateSessionKeys(k_enc, k_mac, k_kek);
        this.calculateCryptograms();

        System.out.println(Conversion.arrayToHex(cardCrypto));

        if (!Arrays.equals(cardCryptoResp, this.cardCrypto)) {
            this.resetParams();
            throw new CardException("Error verifying Card Cryptogram");
        }

        this.sessState = SessionState.SESSION_INIT;
        return resp;
    }

    @Override
    public ResponseAPDU externalAuthenticate(SecLevel secLevel) throws CardException {

        if (secLevel == null) {
            throw new IllegalArgumentException("secLevel must be not null");
        }

        if (this.sessState != SessionState.SESSION_INIT) {
            this.resetParams();
            throw new CardException("Session is not initialized");
        }

        this.secMode = secLevel;
        byte[] extAuthCmd = new byte[21];
        extAuthCmd[0] = (byte) 0x84;
        extAuthCmd[1] = (byte) 0x82;
        extAuthCmd[2] = this.secMode.getVal();
        extAuthCmd[3] = (byte) 0x00;
        extAuthCmd[4] = (byte) 0x10;

        System.arraycopy(this.hostCrypto, 0, extAuthCmd, 5, this.hostCrypto.length);
        byte[] data = new byte[5 + this.hostCrypto.length];
        System.arraycopy(extAuthCmd, 0, data, 0, data.length);
        byte[] mac = this.generateMac(data);
        System.arraycopy(mac, 0, extAuthCmd, extAuthCmd.length - mac.length, mac.length);
        CommandAPDU cmd_extauth = new CommandAPDU(extAuthCmd);
        ResponseAPDU resp = this.cc.transmit(cmd_extauth);

        logger.debug("INIT UPDATE command "
                + "(-> " + Conversion.arrayToHex(cmd_extauth.getBytes()) + ") "
                + "(<- " + Conversion.arrayToHex(resp.getBytes()) + ")");

        if (resp.getSW() != 0x9000) {
            this.resetParams();
            throw new CardException("Error in External Authenticate : " + Integer.toHexString(resp.getSW()));
        }
        this.sessState = SessionState.SESSION_AUTH;
        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getStatus(fr.xlim.ssd.opal.Constant.FileType, fr.xlim.ssd.opal.Constant.GetStatusResponseMode, byte[])
     */
    @Override
    public ResponseAPDU[] getStatus(FileType ft, GetStatusResponseMode respMode, byte[] searchQualifier) throws CardException {
        Set<ResponseAPDU> res = new HashSet<ResponseAPDU>();
        byte[] getStatusCmd = null;
        byte dataSize = (byte) 0; // '0xD0' + Key Identifier + '0xD2' + Key Version Number + C-MAC
        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC

        if (searchQualifier == null) {
            searchQualifier = new byte[2];
            searchQualifier[0] = (byte) 0x4F;
            searchQualifier[1] = (byte) 0x00;
        }

        if (this.secMode == SecLevel.NO_SECURITY_LEVEL) {
            dataSize = (byte) (searchQualifier.length); // searchQualifier
            getStatusCmd = new byte[headerSize + dataSize];
            getStatusCmd[0] = (byte) 0x80;
        } else {
            dataSize = (byte) (searchQualifier.length + 8); // searchQualifier + C-MAC
            getStatusCmd = new byte[headerSize + dataSize];
            getStatusCmd[0] = (byte) 0x84;
        }
        getStatusCmd[1] = (byte) 0xF2;
        getStatusCmd[2] = ft.getVal();
        getStatusCmd[3] = respMode.getVal();
        getStatusCmd[4] = dataSize;

        System.arraycopy(searchQualifier, 0, getStatusCmd, 5, searchQualifier.length);

        if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
            byte[] data_cmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(getStatusCmd, 0, data_cmac, 0, data_cmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(data_cmac); // generate C-MAC
            System.arraycopy(cmac, 0, getStatusCmd, data_cmac.length, cmac.length); // put C-MAC into getStatusCmd
        }
        byte[] UncipheredgetStatusCmd = getStatusCmd.clone();

        if (this.secMode == SecLevel.C_ENC_AND_MAC) {
            getStatusCmd = this.encryptCommand(getStatusCmd);
        }

        CommandAPDU cmd_getStatus = new CommandAPDU(getStatusCmd);
        ResponseAPDU resp = this.getCc().transmit(cmd_getStatus);
        System.out.println("GET STATUS");
        System.out.println("-> " + Conversion.arrayToHex(cmd_getStatus.getBytes()));
        System.out.println("<- " + Conversion.arrayToHex(resp.getBytes()));
        System.out.println();
        res.add(resp);
        while (resp.getSW() == 0x6310) {
            UncipheredgetStatusCmd[3] = (byte) (respMode.getVal() + (byte) 0x01); // Get next occurrence(s)
            if (this.secMode != SecLevel.NO_SECURITY_LEVEL) {
                byte[] data_cmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
                System.arraycopy(UncipheredgetStatusCmd, 0, data_cmac, 0, data_cmac.length); // data used to generate C-MAC
                byte[] cmac = this.generateMac(data_cmac); // generate C-MAC
                System.arraycopy(cmac, 0, UncipheredgetStatusCmd, data_cmac.length, cmac.length); // put C-MAC into getStatusCmd
            }
            getStatusCmd = UncipheredgetStatusCmd;
            if (this.secMode == SecLevel.C_ENC_AND_MAC) {
                getStatusCmd = this.encryptCommand(UncipheredgetStatusCmd);
            }
            cmd_getStatus = new CommandAPDU(getStatusCmd);
            resp = this.getCc().transmit(cmd_getStatus);
            System.out.println("GET STATUS");
            System.out.println("-> " + Conversion.arrayToHex(cmd_getStatus.getBytes()));
            System.out.println("<- " + Conversion.arrayToHex(resp.getBytes()));
            System.out.println();
            res.add(resp);
        }
        if (resp.getSW() != 0x9000) {
            throw new CardException("Error in Get Status : " + Integer.toHexString(resp.getSW()));
        }
        ResponseAPDU[] r = new ResponseAPDU[res.size()];
        return res.toArray(r);
    }

    /**
     * @param data
     * @return
     */
    protected byte[] generateMac(byte[] data) {
        byte[] dataWithPadding = null;
        if (data.length % 8 != 0) { // We need a padding
            int nbBytes = 8 - (data.length % 8);
            dataWithPadding = new byte[data.length + nbBytes];
            System.arraycopy(data, 0, dataWithPadding, 0, data.length);
            for (int i = 0; i < nbBytes; i++) {
                dataWithPadding[data.length + i] = GP2xCommands.padding[i];
            }
        } else {
            dataWithPadding = new byte[data.length + 8];
            System.arraycopy(data, 0, dataWithPadding, 0, data.length);
            System.arraycopy(GP2xCommands.padding, 0, dataWithPadding, data.length, GP2xCommands.padding.length);
        }
        byte[] res = new byte[8];
        IvParameterSpec ivSpec = new IvParameterSpec(this.icv);
        try {
            Cipher myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessMac, "DESede"), ivSpec);
            byte[] cryptogram = myCipher.doFinal(dataWithPadding);
            System.arraycopy(cryptogram, cryptogram.length - 8, res, 0, 8);
            if (this.scp == SCPMode.SCP_01_05) {
                this.icv = res; // update ICV with new C-MAC
            } else if (this.scp == SCPMode.SCP_01_15) { // update ICV with new ENCRYPTED C-MAC
                Cipher myCipher2 = Cipher.getInstance("DESede/ECB/NoPadding");
                myCipher2.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessMac, "DESede"));
                this.icv = myCipher2.doFinal(res);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Cannot find algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("No such padding problem", e);
        } catch (InvalidKeyException e) {
            throw new UnsupportedOperationException("Key problem", e);
        } catch (IllegalBlockSizeException e) {
            throw new UnsupportedOperationException("Block size problem", e);
        } catch (BadPaddingException e) {
            throw new UnsupportedOperationException("Bad padding problem", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new UnsupportedOperationException("Invalid Algorithm parameter", e);
        }

        return res.clone();
    }

    /**
     * @param command
     * @return
     */
    protected byte[] encryptCommand(byte[] command) {
        byte[] encryptedCmd = null;
        int dataLength = command.length - 4 - 8; // command without (CLA, INS, P1, P2) AND C-MAC
        byte[] datas = null;
        if (dataLength % 8 == 0) { // don't need a padding
            datas = new byte[dataLength];
            System.arraycopy(command, 4, datas, 0, dataLength); // copies LC + DATAFIELD from command
            datas[0] = (byte) (datas.length - 1); // update the "pseudo" LC with the length of the original clear text
        } else { // need a padding
            int nbBytes = 8 - (dataLength % 8); // bytes needed for the padding
            datas = new byte[dataLength + nbBytes];
            System.arraycopy(command, 4, datas, 0, dataLength); // copies LC + DATAFIELD from command
            datas[0] = (byte) (datas.length - 1 - nbBytes); // update the "pseudo" LC with the length of the original clear text
            for (int i = 0; i < nbBytes; i++) { // add necessary padding
                datas[dataLength + i] = GP2xCommands.padding[i];
            }
        }
        Cipher myCipher;
        try {
            myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), new IvParameterSpec(Conversion.hexToArray("00 00 00 00 00 00 00 00")));
            byte[] res = myCipher.doFinal(datas);
            encryptedCmd = new byte[5 + res.length + 8];
            System.arraycopy(command, 0, encryptedCmd, 0, 5);
            System.arraycopy(res, 0, encryptedCmd, 5, res.length);
            System.arraycopy(command, command.length - 8, encryptedCmd, res.length + 5, 8);
            encryptedCmd[4] = (byte) (encryptedCmd.length - 5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return encryptedCmd;
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

    /**
     *
     */
    protected void calculateCryptograms() {
        byte[] data = new byte[24];
        System.arraycopy(this.hostChallenge, 0, data, 0, 8);
        System.arraycopy(this.cardChallenge, 0, data, 8, 8);
        System.arraycopy(GP2xCommands.padding, 0, data, 16, 8);
        Cipher myCipher;
        try {
            myCipher = Cipher.getInstance("DESede/CBC/NoPadding");
            IvParameterSpec ivSpec = new IvParameterSpec(this.icv);
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), ivSpec);
            byte[] cardcryptogram = myCipher.doFinal(data);
            this.cardCrypto = new byte[8];
            System.arraycopy(cardcryptogram, 16, this.cardCrypto, 0, 8);

            System.arraycopy(this.cardChallenge, 0, data, 0, 8);
            System.arraycopy(this.hostChallenge, 0, data, 8, 8);
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.sessEnc, "DESede"), ivSpec);
            byte[] hostcryptogram = myCipher.doFinal(data);
            this.hostCrypto = new byte[8];
            System.arraycopy(hostcryptogram, 16, this.hostCrypto, 0, 8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @param staticKenc
     * @param staticKmac
     * @param staticKkek
     */
    protected void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek) {

        try {

            this.sessEnc = new byte[24];
            this.sessMac = new byte[24];
            this.sessKek = new byte[24];
            byte[] session;

            Cipher myCipher = Cipher.getInstance("DESede/ECB/NoPadding");

            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKenc.getData(), "DESede"));
            session = myCipher.doFinal(this.derivationData);
            System.arraycopy(session, 0, this.sessEnc, 0, 16);
            System.arraycopy(session, 0, this.sessEnc, 16, 8);

            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKmac.getData(), "DESede"));
            session = myCipher.doFinal(this.derivationData);
            System.arraycopy(session, 0, this.sessMac, 0, 16);
            System.arraycopy(session, 0, this.sessMac, 16, 8);

            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(staticKkek.getData(), "DESede"));
            session = myCipher.doFinal(this.derivationData);
            System.arraycopy(session, 0, this.sessKek, 0, 16);
            System.arraycopy(session, 0, this.sessKek, 16, 8);

        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Cannot find algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("No such padding problem", e);
        } catch (InvalidKeyException e) {
            throw new UnsupportedOperationException("Key problem", e);
        } catch (IllegalBlockSizeException e) {
            throw new UnsupportedOperationException("Block size problem", e);
        } catch (BadPaddingException e) {
            throw new UnsupportedOperationException("Bad padding problem", e);
        }
    }

    protected void calculateDerivationData() {

        this.derivationData = new byte[16];

        System.arraycopy(this.hostChallenge, 0, this.derivationData, 4, 4);
        System.arraycopy(this.hostChallenge, 4, this.derivationData, 12, 4);
        System.arraycopy(this.cardChallenge, 0, this.derivationData, 8, 4);
        System.arraycopy(this.cardChallenge, 4, this.derivationData, 0, 4);

    }

    // SECURITY DOMAIN
    @Override
    public ResponseAPDU deleteOnCardObj(byte[] aid, boolean cascade) throws CardException {
        byte dataSize = (byte) (2 + aid.length); // params +  AID

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            dataSize = (byte) (dataSize + 8); // add space for the C-MAC
        }

        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC

        byte[] deleteComm = new byte[headerSize + dataSize];

        deleteComm[0] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84); // (CLA) command class (GlobalPlatform command + secure messaging with GlobalPlatform format)
        deleteComm[1] = (byte) 0xE4; // (INS) DELETE command
        deleteComm[2] = (byte) 0x00; // (P1) the only and the last DELETE command
        deleteComm[3] = (cascade ? (byte) 0x80 : (byte) 0x00); // (P2) 0x00 : only delete the specified object
        //      0x80 : delete object and related objects
        deleteComm[4] = dataSize;   // (LC) data length

        deleteComm[5] = (byte) 0x4F; // the object being deleted is specified by its AID
        deleteComm[6] = (byte) aid.length; // AID length
        System.arraycopy(aid, 0, deleteComm, 7, aid.length); // put the AID into deleteComm

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            byte[] data_cmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(deleteComm, 0, data_cmac, 0, data_cmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(data_cmac); // generate C-MAC
            System.arraycopy(cmac, 0, deleteComm, data_cmac.length, cmac.length); // put C-MAC into deleteComm
        }

        if (this.getSecMode() == SecLevel.C_ENC_AND_MAC) {
            deleteComm = this.encryptCommand(deleteComm);
        }

        CommandAPDU cmd_delete = new CommandAPDU(deleteComm);
        ResponseAPDU resp = this.getCc().transmit(cmd_delete);
        System.out.println("DELETE OBJECT");
        System.out.println("-> " + Conversion.arrayToHex(cmd_delete.getBytes()));
        System.out.println("<- " + Conversion.arrayToHex(resp.getBytes()));
        System.out.println();
        if (resp.getSW() != 0x9000) {
            throw new CardException("Error in Delete : " + Integer.toHexString(resp.getSW()));
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#deleteOnCardKey(byte, byte)
     */
    @Override
    public ResponseAPDU deleteOnCardKey(byte keySetVersion, byte keyId) throws CardException {
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

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            byte[] data_cmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(deleteComm, 0, data_cmac, 0, data_cmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(data_cmac); // generate C-MAC
            System.arraycopy(cmac, 0, deleteComm, data_cmac.length, cmac.length); // put C-MAC into deleteComm
        }

        if (this.getSecMode() == SecLevel.C_ENC_AND_MAC) {
            deleteComm = this.encryptCommand(deleteComm);
        }

        CommandAPDU cmd_delete = new CommandAPDU(deleteComm);
        ResponseAPDU resp = this.getCc().transmit(cmd_delete);
        System.out.println("DELETE KEY");
        System.out.println("-> " + Conversion.arrayToHex(cmd_delete.getBytes()));
        System.out.println("<- " + Conversion.arrayToHex(resp.getBytes()));
        System.out.println();
        if (resp.getSW() != 0x9000) {
            throw new CardException("Error in Delete : " + Integer.toHexString(resp.getSW()));
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#installForLoad(byte[], byte[], byte[])
     */
    @Override
    public ResponseAPDU installForLoad(byte[] packageAid, byte[] securityDomainAID, byte[] params) throws CardException {
        // Check if mandatories values are provided
        if (packageAid == null) {
            throw new CardException("Error in Install For Load : bad parameters (null) ");
        }
        int paramLength = ((params != null) ? params.length : 0);
        byte[] paramLengthEncoded = null;
        if (params != null) {
            if (params.length < 128) {
                paramLengthEncoded = new byte[1];
                paramLengthEncoded[0] = (byte) params.length;
            } else {
                paramLengthEncoded = new byte[2];
                paramLengthEncoded[0] = (byte) 0x81;
                paramLengthEncoded[0] = (byte) params.length;
            }
        } else {
            paramLengthEncoded = new byte[1];
            paramLengthEncoded[0] = (byte) 0x00;
        }

        int secDomLength = securityDomainAID.length;

        byte headerSize = (byte) 5; // CLA + INS + P1 + P2 + LC
        byte dataSize = (byte) (1 + packageAid.length // Length of Load File AID +  Load File AID
                + 1 + secDomLength // + Length of Security Domain AID + Security Domain AID
                + 1 // + Length of Load File Data BlockHash (0x00)
                + paramLengthEncoded.length + paramLength // + Length of Load Parameters field + Load Parameters field
                + 1);                         				 // + Length of Load Token (0x00)

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            dataSize = (byte) (dataSize + 8); // add space for the C-MAC
        }

        byte[] installForLoadComm = new byte[headerSize + dataSize];

        installForLoadComm[0] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84); // (CLA) command class (GlobalPlatform command + secure messaging with GlobalPlatform format)
        installForLoadComm[1] = (byte) 0xE6; // (INS) INSTALL command
        installForLoadComm[2] = (byte) 0x02; // (P1) For load
        installForLoadComm[3] = (byte) 0x00; // (P2) no information provided
        installForLoadComm[4] = dataSize; // (LC) data length

        installForLoadComm[5] = (byte) packageAid.length; // AID length

        int i = 6; // next index of installForLoadComm to deal with

        System.arraycopy(packageAid, 0, installForLoadComm, i, packageAid.length); // put the AID into installForLoadComm
        i += packageAid.length;

        installForLoadComm[i] = (byte) secDomLength; // length of security domain
        i++;

        System.arraycopy(securityDomainAID, 0, installForLoadComm, i, secDomLength);
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

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            byte[] data_cmac = new byte[headerSize + dataSize - 8]; // data used to generate C-MAC
            System.arraycopy(installForLoadComm, 0, data_cmac, 0, data_cmac.length); // data used to generate C-MAC
            byte[] cmac = this.generateMac(data_cmac); // generate C-MAC
            System.arraycopy(cmac, 0, installForLoadComm, data_cmac.length, cmac.length); // put C-MAC into installForLoadComm
        }

        if (this.getSecMode() == SecLevel.C_ENC_AND_MAC) {
            installForLoadComm = this.encryptCommand(installForLoadComm);
        }

        CommandAPDU cmd_installForLoad = new CommandAPDU(installForLoadComm);
        ResponseAPDU resp = this.getCc().transmit(cmd_installForLoad);
        System.out.println("INSTALL FOR LOAD");
        System.out.println("-> " + Conversion.arrayToHex(cmd_installForLoad.getBytes()));
        System.out.println("<- " + Conversion.arrayToHex(resp.getBytes()));
        System.out.println();
        if (resp.getSW() != 0x9000) {
            throw new CardException("Error in Install For Load : " + Integer.toHexString(resp.getSW()));
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#load(java.io.File)
     */
    @Override
    public ResponseAPDU[] load(File capFile) throws CardException {
        return this.load(capFile, (byte) 0xF0);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#load(java.io.File, byte)
     */
    @Override
    public ResponseAPDU[] load(File capFile, byte maxDataLength) throws CardException {
        Set<ResponseAPDU> res = new HashSet<ResponseAPDU>();
        int capFileRemainLen = (int) capFile.length();
        ByteBuffer buffer = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(capFile);
            FileChannel fc = fis.getChannel();
            int sz = (int) fc.size();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        int cMacLen = 0;                // Size of C-MAC (unused by default)
        int headerLen = 5;
        int dataBlockSize = maxDataLength & 0x0ff;            // Size of a block

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            cMacLen = 8;				// we need to update cMacLen...
            dataBlockSize -= cMacLen;
        }

        if (this.getSecMode() == SecLevel.C_ENC_AND_MAC) {
            cMacLen = 8;
            if (dataBlockSize >= 0xF0) { // check valid data length...
                dataBlockSize -= 1;
            }
        }

        byte[] ber = null;

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
        }

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
            cmd[0] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84);
            cmd[1] = (byte) 0xE8;
            cmd[2] = (byte) ((i == nbBlock - 1) ? 0x80 : 0x00);
            cmd[3] = (byte) i;

            if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
                byte[] data_cmac = new byte[cmd.length - 8];                // data used to generate C-MAC
                System.arraycopy(cmd, 0, data_cmac, 0, data_cmac.length);   // data used to generate C-MAC
                byte[] cmac = this.generateMac(data_cmac);                  // generate C-MAC
                System.arraycopy(cmac, 0, cmd, data_cmac.length, cmac.length); // put C-MAC into installForLoadComm
            }

            if (this.getSecMode() == SecLevel.C_ENC_AND_MAC) {
                cmd = this.encryptCommand(cmd);
            }

            CommandAPDU cmd_load = new CommandAPDU(cmd);
            ResponseAPDU resp = null;
            resp = this.getCc().transmit(cmd_load);
            System.out.println("LOAD");
            System.out.println("-> " + Conversion.arrayToHex(cmd_load.getBytes()));
            System.out.println("<- " + Conversion.arrayToHex(resp.getBytes()));
            System.out.println();
            res.add(resp);
            if (resp.getSW() != 0x9000) {
                throw new CardException("Error in Load : " + Integer.toHexString(resp.getSW()));
            }
        }
        ResponseAPDU[] r = new ResponseAPDU[res.size()];
        return res.toArray(r);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#installForInstallAndMakeSelectable(byte[], byte[], byte[], byte[], byte[])
     */
    @Override
    public ResponseAPDU installForInstallAndMakeSelectable(byte[] loadFileAID, byte[] moduleAID, byte[] applicationAID, byte[] privileges, byte[] params) throws CardException {
        if (params == null) {
            params = new byte[2];
            params[0] = (byte) 0xC9;
            params[1] = (byte) 0x00;
        }
        // Check if mandatories values are provided
        if (loadFileAID == null || moduleAID == null || privileges == null) {
            throw new CardException("Error in Install For Install And Make Selectable : bad parameters (null) ");
        }

        if (applicationAID == null) {
            applicationAID = moduleAID.clone();
        }

        int paramLength = params.length;
        byte[] paramLengthEncoded = null;
        if (params != null) {
            if (params.length < 128) {
                paramLengthEncoded = new byte[1];
                paramLengthEncoded[0] = (byte) params.length;
            } else {
                paramLengthEncoded = new byte[2];
                paramLengthEncoded[0] = (byte) 0x81;
                paramLengthEncoded[0] = (byte) params.length;
            }
        } else {
            paramLengthEncoded = new byte[1];
            paramLengthEncoded[0] = (byte) 0x00;
        }

        int cMacLen = this.getSecMode() != SecLevel.NO_SECURITY_LEVEL ? 8 : 0;
        byte headerSize = (byte) 5; 	// CLA + INS + P1 + P2 + LC
        byte dataSize = (byte) (1 + loadFileAID.length // Length of Executable Load File AID + Executable Load File AID
                + 1 + moduleAID.length // Length of Executable Module AID + Executable Module AID
                + 1 + applicationAID.length // Length of Application AID + Application AID
                + 1 + privileges.length // Length of Privileges + Privileges
                + paramLengthEncoded.length + paramLength // Length of Install Parameters field + Install Parameters field
                + 1 // Length of Install Token (0)
                + cMacLen); 										// C-MAC Length

        byte[] installForInstallComm = new byte[headerSize + dataSize];

        installForInstallComm[0] = (byte) ((this.getSecMode() == SecLevel.NO_SECURITY_LEVEL) ? 0x80 : 0x84); // (CLA) command class (GlobalPlatform command + secure messaging with GlobalPlatform format)
        installForInstallComm[1] = (byte) 0xE6;	// (INS) INSTALL command
        installForInstallComm[2] = (byte) 0x0C; 	// (P1) For install
        installForInstallComm[3] = (byte) 0x00;	// (P2) no information provided
        installForInstallComm[4] = dataSize;	// (LC) data length

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

        if (this.getSecMode() != SecLevel.NO_SECURITY_LEVEL) {
            byte[] data_cmac = new byte[installForInstallComm.length - 8];                // data used to generate C-MAC
            System.arraycopy(installForInstallComm, 0, data_cmac, 0, data_cmac.length);   // data used to generate C-MAC
            byte[] cmac = this.generateMac(data_cmac);                  // generate C-MAC
            System.arraycopy(cmac, 0, installForInstallComm, data_cmac.length, cmac.length); // put C-MAC into installForLoadComm
        }
        if (this.getSecMode() == SecLevel.C_ENC_AND_MAC) {
            installForInstallComm = this.encryptCommand(installForInstallComm);
        }

        CommandAPDU cmd_installForInstall = new CommandAPDU(installForInstallComm);
        ResponseAPDU resp = null;
        try {
            resp = this.getCc().transmit(cmd_installForInstall);
            System.out.println("INSTALL FOR INSTALL AND MAKE SELECTABLE");
            System.out.println("-> " + Conversion.arrayToHex(cmd_installForInstall.getBytes()));
            System.out.println("<- " + Conversion.arrayToHex(resp.getBytes()));
            System.out.println();
        } catch (CardException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (resp.getSW() != 0x9000) {
            throw new CardException("Error in Install For Install And Make Selectable : " + Integer.toHexString(resp.getSW()));
        }
        return resp;
    }
}
