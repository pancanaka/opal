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
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCPMode;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Secure Channel Protocol 01 implementation.
 *
 * @author Guillaume Bouffard
 * @author Jean Dubreuil
 */
public class SCP01 extends AbstractSCP {
    // Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(SCP01.class);

    public SCP01(SCPMode scpMode) {
        super(scpMode);
        if (scpMode.getProtocolNumber() != 1)
            throw new IllegalArgumentException("Incorrect SCPMode. Protocol number value:" + scpMode.getProtocolNumber() + " instead of 1.");
    }
    
    @Override
    public void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek) {
        byte[] session;
        byte[] derivationData = calculateDerivationData();
        
        logger.debug("==> Generate Session Keys");
        logger.debug("* staticKenc: " + Conversion.arrayToHex(staticKenc.getValue()));
        logger.debug("* staticKmac: " + Conversion.arrayToHex(staticKmac.getValue()));
        logger.debug("* staticKkek: " + Conversion.arrayToHex(staticKkek.getValue()));
        logger.debug("* SCP_Mode is SCP01");
        
        /* Calculating session encryption key */
        session = doFinal(Cipher.ENCRYPT_MODE, "DESede/ECB/NoPadding", new SecretKeySpec(staticKenc.getValue(), "DESede"), null, derivationData, 0, derivationData.length);
        sessEnc = newKey(session);
        logger.debug("* sessEnc = " + Conversion.arrayToHex(sessEnc.getEncoded()));

        /* Calculating session mac key */
        session = doFinal(Cipher.ENCRYPT_MODE, "DESede/ECB/NoPadding", new SecretKeySpec(staticKmac.getValue(), "DESede"), null, derivationData, 0, derivationData.length);
        sessCMac = newKey(session);
        logger.debug("* sessMac = " + Conversion.arrayToHex(sessCMac.getEncoded()));

        /* Calculating session data encryption key */
        session = doFinal(Cipher.ENCRYPT_MODE, "DESede/ECB/NoPadding", new SecretKeySpec(staticKkek.getValue(), "DESede"), null, derivationData, 0, derivationData.length);
        sessDek = newKey(session);
        logger.debug("* sessDek = " + Conversion.arrayToHex(sessDek.getEncoded()));
        
        logger.debug("==> Generate Session Keys Data End");
    }
    @Override
    public byte[] encapsulateCommand(byte[] command) {
        if ((secLevel.getVal() & SecLevel.C_MAC.getVal()) != 0) {
            command = generateCMac(command);
        }
        if (secLevel == SecLevel.C_ENC_AND_MAC) {
            command = encryptCommand(command);
        }
        return command;
    }
    @Override
    public byte[] desencapsulateResponse(byte[] response) throws CardException {
        return response;//SCP01 no encryption, no R-Mac
    }
    @Override
    public byte[] encryptCommand(byte[] command) {
        int dataLength = command.length - 4 - 8; // command without (CLA, INS, P1, P2) AND C-MAC
        byte[] data;
        byte[] encryptedCmd;
        
        logger.debug("==> Encrypt Command Begin");
        logger.debug("* Command to encrypt is " + Conversion.arrayToHex(command));
        logger.debug("* IV is " + Conversion.arrayToHex(new byte[8]));
        
        data = new byte[dataLength];
        System.arraycopy(command, 4, data, 0, dataLength);
        data[0] = (byte) (data.length - 1);//Update length
        if (data.length % 8 != 0)//If multiple of 8, no further padding is required
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
        return response;//SCP01 no response encryption
    }
    @Override
    public byte[] generateCMac(byte[] command) {
        logger.debug("==> Generate Mac");
        byte[] cmd = command.clone();
        cmd[ISO7816.OFFSET_CLA.getValue()] |= 0x4;
        cmd[ISO7816.OFFSET_LC.getValue()] += 8;
        byte[] dataWithPadding = addPadding(cmd);
        byte[] encrypt = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", sessCMac, icv, dataWithPadding, 0, dataWithPadding.length);
        byte[] newCommand = new byte[cmd.length + 8];
        byte[] cMac = new byte[8];
        System.arraycopy(encrypt, encrypt.length - 8, cMac, 0, cMac.length);
        System.arraycopy(cmd, 0, newCommand, 0, cmd.length);
        System.arraycopy(cMac, 0, newCommand, cmd.length, cMac.length);
        
        if ((scpMode.getIParameter() & ICV_ENCRYPTION_FOR_CMAC_SESSION) != 0)
            icv = doFinal(Cipher.ENCRYPT_MODE, "DESede/ECB/NoPadding", sessCMac, null, cMac, 0, cMac.length);
        else
            icv = cMac;
        logger.debug("* New ICV is " + Conversion.arrayToHex(icv));
        logger.debug("* New Command is " + Conversion.arrayToHex(newCommand));
        return newCommand;
    }
    @Override
    public boolean checkRMac(byte[] response) {
        return true;//SCP01 no R-Mac
    }
    @Override
    public byte[] calculateDerivationData() {
        byte[] derivationData = new byte[16];
        logger.debug("==> Calculate Derivation Data");
        /*
         * Derivation data in SCP 01 mode
         *
         * -0-------------------3-4------------------7--8----------------11-12-----------------15--
         * |   Card Challenge    |   Host challenge   |   Card Challenge   |   Host challenge    |
         * | (4 byte right half) | (4 byte left half) | (4 byte left half) | (4 byte right half) |
         * ----------------------------------------------------------------------------------------
         */
        System.arraycopy(hostChallenge, 0, derivationData, 4, 4);
        System.arraycopy(hostChallenge, 4, derivationData, 12, 4);
        System.arraycopy(cardChallenge, 0, derivationData, 8, 4);
        System.arraycopy(cardChallenge, 4, derivationData, 0, 4);

        logger.debug("* Derivation Data is " + Conversion.arrayToHex(derivationData));
        logger.debug("==> Calculate Derivation Data End");
        return derivationData;
    }
}