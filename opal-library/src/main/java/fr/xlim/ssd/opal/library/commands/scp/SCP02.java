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

import fr.xlim.ssd.opal.library.commands.ISO7816;
import fr.xlim.ssd.opal.library.commands.SecLevel;
import static fr.xlim.ssd.opal.library.commands.scp.AbstractSCP.CMAC_ON_UNMODIFIED_APDU;
import static fr.xlim.ssd.opal.library.commands.scp.AbstractSCP.ICV_ENCRYPTION_FOR_CMAC_SESSION;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCPMode;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jean Dubreuil
 */
public class SCP02 extends AbstractSCP {

    // Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(SCP02.class);
    
    // SCP 02 constant used to obtain, the C-Mac session key
    protected static final byte[] SCP02_DERIVATION4CMAC = {(byte) 0x01, (byte) 0x01};
    // SCP 02 constant used to obtain, the R-Mac session key
    protected static final byte[] SCP02_DERIVATION4RMAC = {(byte) 0x01, (byte) 0x02};
    // SCP 02 constant used to obtain, the encryption session key
    protected static final byte[] SCP02_DERIVATION4ENCKEY = {(byte) 0x01, (byte) 0x82};
    // SCP 02 constant used to obtain, the data encryption session key
    protected static final byte[] SCP02_DERIVATION4DATAENC = {(byte) 0x01, (byte) 0x81};
    
    public SCP02(SCPMode scpMode) {
        super(scpMode);
        if (scpMode.getProtocolNumber() != 2)
            throw new IllegalArgumentException("Incorrect SCPMode. Protocol number value:" + scpMode.getProtocolNumber() + " instead of 2.");
    }

    @Override
    public void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek) {
        byte[] session;
        byte[] derivationData = calculateDerivationData();
        
        logger.debug("==> Generate Session Keys");
        logger.debug("* staticKenc: " + Conversion.arrayToHex(staticKenc.getValue()));
        logger.debug("* staticKmac: " + Conversion.arrayToHex(staticKmac.getValue()));
        logger.debug("* staticKkek: " + Conversion.arrayToHex(staticKkek.getValue()));
        logger.debug("* SCP_Mode is SCP02");
        logger.debug("*** Initialize IV : " + Conversion.arrayToHex(icv));

        // Calculing Encryption Session Keys
        System.arraycopy(SCP02_DERIVATION4ENCKEY, 0, derivationData, 0, 2);
        session = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", new SecretKeySpec(staticKenc.getValue(), "DESede"), icv, derivationData, 0, derivationData.length);
        sessEnc = newKey(session);
        logger.debug("* sessEnc = " + Conversion.arrayToHex(sessEnc.getEncoded()));  

        // Calculing C_Mac Session Keys
        System.arraycopy(SCP02_DERIVATION4CMAC, 0, derivationData, 0, 2);
        session = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", new SecretKeySpec(staticKmac.getValue(), "DESede"), icv, derivationData, 0, derivationData.length);
        sessCMac = newKey(session);
        logger.debug("* sessMac = " + Conversion.arrayToHex(sessCMac.getEncoded()));

        // Calculing R_Mac Session Keys
        System.arraycopy(SCP02_DERIVATION4RMAC, 0, derivationData, 0, 2);
        session = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", new SecretKeySpec(staticKmac.getValue(), "DESede"), icv, derivationData, 0, derivationData.length);
        sessRMac = newKey(session);
        logger.debug("* sessRMac = " + Conversion.arrayToHex(sessRMac.getEncoded()));

        // Calculing Data Encryption Session Keys
        System.arraycopy(SCP02_DERIVATION4DATAENC, 0, derivationData, 0, 2);
        session = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", new SecretKeySpec(staticKkek.getValue(), "DESede"), icv, derivationData, 0, derivationData.length);
        sessDek = newKey(session);
        logger.debug("* sessDek = " + Conversion.arrayToHex(sessDek.getEncoded()));
    }
    @Override
    public byte[] encapsulateCommand(byte[] command) {
        if ((secLevel.getVal() & SecLevel.C_MAC.getVal()) != 0) {
            command = generateCMac(command);
        }
        if ((secLevel.getVal() & SecLevel.C_ENC_AND_MAC.getVal()) == SecLevel.C_ENC_AND_MAC.getVal()) {
            command = encryptCommand(command);
        }
        return command;
    }
    @Override
    public byte[] desencapsulateResponse(byte[] response) throws CardException {
        //TODO
        return response;
    }
    @Override
    public byte[] encryptCommand(byte[] command) {
        int dataLength = command.length - 5 - 8; // command without (CLA, INS, P1, P2, LC) AND C-MAC
        byte[] data;
        byte[] encryptedCmd;
        
        logger.debug("==> Encrypt Command Begin");
        logger.debug("* Command to encrypt is " + Conversion.arrayToHex(command));
        logger.debug("* IV is " + Conversion.arrayToHex(new byte[8]));
        
        data = new byte[dataLength];
        System.arraycopy(command, 5, data, 0, dataLength);
        data = addPadding(data);
        data = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", sessEnc, new byte[8], data, 0, data.length);//ICV is 0
        encryptedCmd = new byte[5 + data.length + 8];
        System.arraycopy(command, 0, encryptedCmd, 0, 5);
        System.arraycopy(data, 0, encryptedCmd, 5, data.length);
        System.arraycopy(command, command.length - 8, encryptedCmd, data.length + 5, 8);
        encryptedCmd[4] = (byte) (encryptedCmd.length - 5);
        logger.debug("* Encrypted data is " + Conversion.arrayToHex(encryptedCmd));
        return encryptedCmd;
    }
    @Override
    public byte[] decryptCardResponseData(byte[] response) {
        return response;//SCP02 no response encryption
    }
    @Override
    public byte[] generateCMac(byte[] command) {
        logger.debug("==> Generate Mac");
        byte[] cmd = command.clone();
        if ((scpMode.getIParameter() & CMAC_ON_UNMODIFIED_APDU) == 0) {
            cmd[ISO7816.OFFSET_CLA.getValue()] |= 0x4;
            cmd[ISO7816.OFFSET_LC.getValue()] += 8;
        }
        byte[] dataWithPadding = addPadding(cmd);
        SecretKeySpec desSingleKey = new SecretKeySpec(sessCMac.getEncoded(), 0, 8, "DES");
        int noOfBlocks = dataWithPadding.length / 8;
        byte ivForNextBlock[] = icv;
        int startIndex = 0;
        for (int i = 0; i < (noOfBlocks - 1); i++) {
            ivForNextBlock = doFinal(Cipher.ENCRYPT_MODE, "DES/CBC/NoPadding", desSingleKey, ivForNextBlock, dataWithPadding, startIndex, 8);
            startIndex += 8;
            logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
        }
        byte[] cMac = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", sessCMac, ivForNextBlock, dataWithPadding, startIndex, 8);
        byte[] newCommand = new byte[cmd.length + 8];
        System.arraycopy(cmd, 0, newCommand, 0, cmd.length);
        System.arraycopy(cMac, 0, newCommand, cmd.length, cMac.length);
        if ((scpMode.getIParameter() & CMAC_ON_UNMODIFIED_APDU) != 0) {
            cmd[ISO7816.OFFSET_CLA.getValue()] |= 0x4;
            cmd[ISO7816.OFFSET_LC.getValue()] += 8;
        }
        
        if ((scpMode.getIParameter() & ICV_ENCRYPTION_FOR_CMAC_SESSION) != 0)
            icv = doFinal(Cipher.ENCRYPT_MODE, "DES/CBC/NoPadding", desSingleKey, new byte[8], cMac, 0, cMac.length);
        else
            icv = cMac;
        
        logger.debug("* New ICV is " + Conversion.arrayToHex(icv));
        logger.debug("* New Command is " + Conversion.arrayToHex(newCommand));
        return newCommand;
    }
    @Override
    public boolean checkRMac(byte[] response) {
        //TODO
        return true;
    }
    @Override
    public byte[] calculateDerivationData() {
        byte[] derivationData = new byte[16];
        System.arraycopy(cardChallenge, 0, derivationData, 2, 2);//The sequence counter are the two first byte of the card challenge
        return derivationData;
    }
    
    public void initIcvToMacOverAid(byte[] aid) {
        initICV();
        logger.info("==> init ICV to mac over AID");
        logger.info("* SCP 02 Protocol (" + scpMode + ") used");
        logger.info("* IV is " + Conversion.arrayToHex(icv));

        byte[] dataWithPadding = addPadding(aid);

        byte[] res;
        logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));

        
        // Calculate the first n - 1 block.
        int noOfBlocks = dataWithPadding.length / 8;
        byte ivForNextBlock[] = icv;
        int startIndex = 0;
        for (int i = 0; i < (noOfBlocks - 1); i++) {
            ivForNextBlock = doFinal(Cipher.ENCRYPT_MODE, "DES/CBC/NoPadding", new SecretKeySpec(sessCMac.getEncoded(), 0, 8, "DES"), ivForNextBlock, dataWithPadding, startIndex, 8);
            startIndex += 8;
            logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
        }
        res = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", sessCMac, ivForNextBlock, dataWithPadding, startIndex, 8);
        logger.info("New ICV is " + Conversion.arrayToHex(res));
    }
    public byte[] pseudoRandomGenerationCardChallenge(byte[] aid) {
        initICV();
        logger.info("==> init ICV to mac over AID");
        logger.info("* SCP 02 Protocol (" + scpMode + ") used");
        logger.info("* IV is " + Conversion.arrayToHex(icv));

        byte[] dataWithPadding = addPadding(aid);

        byte[] res;
        logger.debug("* data with PADDING: " + Conversion.arrayToHex(dataWithPadding));

        
        // Calculate the first n - 1 block.
        int noOfBlocks = dataWithPadding.length / 8;
        byte ivForNextBlock[] = icv;
        int startIndex = 0;
        for (int i = 0; i < (noOfBlocks - 1); i++) {
            ivForNextBlock = doFinal(Cipher.ENCRYPT_MODE, "DES/CBC/NoPadding", new SecretKeySpec(sessCMac.getEncoded(), 0, 8, "DES"), ivForNextBlock, dataWithPadding, startIndex, 8);
            startIndex += 8;
            logger.debug("* Calculated cryptogram is for Bolck " + i + " " + Conversion.arrayToHex(ivForNextBlock));
        }
        res = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", sessCMac, ivForNextBlock, dataWithPadding, startIndex, 8);
        
        byte[] computedCardChallenge = new byte[8];
        System.arraycopy(cardChallenge, 0, computedCardChallenge, 0, 2);
        System.arraycopy(res, 0, computedCardChallenge, 2, 6);
        return computedCardChallenge;
    }
}