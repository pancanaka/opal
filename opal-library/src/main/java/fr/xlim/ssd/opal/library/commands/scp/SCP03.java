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
import java.security.Key;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Secure Channel Protocol 03 implementation.
 *
 * @author Jean Dubreuil
 */
public class SCP03 extends AbstractSCP {
    // Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(SCP03.class);

    // SCP 03 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.scp.SCP03.generateSessionKeys}, the C-Mac session key
    private static final byte SCP03_DERIVATION4CMAC = (byte) 0x06;
    // SCP 03 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.scp.SCP03.generateSessionKeys}, the encryption session key
    private static final byte SCP03_DERIVATION4DATAENC = (byte) 0x04;
    // SCP 03 constant used to obtain, in @see{fr.xlim.ssd.opal.library.commands.scp.SCP03.generateSessionKeys}, the R-Mac session key
    private static final byte SCP03_DERIVATION4RMAC = (byte) 0x07;
    // SCP 03 constant used to obtain the card cryptogram
    private static final byte SCP03_DERIVATION4CardCryptogram = (byte) 0x00;
    // SCP 03 constant used to obtain the host cryptogram
    private static final byte SCP03_DERIVATION4HostCryptogram = (byte) 0x01;
    
    private int encCounter;
    private byte[] macChainingValue;
    
    public SCP03(SCPMode scpMode) {
        super(scpMode);
        if (scpMode.getProtocolNumber() != 3)
            throw new IllegalArgumentException("Incorrect SCPMode. Protocol number value:" + scpMode.getProtocolNumber() + " instead of 3.");
        encCounter = 1;
        macChainingValue = new byte[16];
    }
    
    @Override
    public void initICV() {
        logger.debug("==> Init ICV begin");
        icv = new byte[16];
        logger.debug("* New ICV is " + Conversion.arrayToHex(icv));
        logger.debug("==> Init ICV end");
    }
    @Override
    public byte[] calculateCardCryptogram() {
        logger.debug("==> Calculate Card Cryptogram");
        logger.debug("* ICV: " + Conversion.arrayToHex(icv));
        
        byte[] crypto = new byte[8];
        byte[] derivationData = calculateDerivationData();
        derivationData[11] = SCP03_DERIVATION4CardCryptogram;
        derivationData[14] = (byte) 0x40;//Card Crypto -> L is 0x40
        derivationData[15] = (byte) 0x01;//Because L = 0x40
        logger.debug("* Derivation Data: " + Conversion.arrayToHex(derivationData));
        
        derivationData = generateCryptogram(sessCMac, derivationData);
        System.arraycopy(derivationData, 0, crypto, 0, 8);
        
        logger.debug("* Calculated Card Crypto: " + Conversion.arrayToHex(crypto));
        logger.debug("==> Calculate Card Cryptogram End");
        return crypto;
    }
    @Override
    public byte[] calculateHostCryptogram() {
        logger.debug("==> Calculate Host Cryptogram");
        logger.debug("* ICV: " + Conversion.arrayToHex(icv));
        
        byte[] crypto = new byte[8];
        byte[] derivationData = calculateDerivationData();
        derivationData[11] = SCP03_DERIVATION4HostCryptogram;
        derivationData[14] = (byte) 0x40;//Host Crypto -> L is 0x40
        derivationData[15] = (byte) 0x01;//Because L = 0x40
        logger.debug("* Derivation Data: " + Conversion.arrayToHex(derivationData));
        
        derivationData = generateCryptogram(sessCMac, derivationData);
        System.arraycopy(derivationData, 0, crypto, 0, 8);
        
        logger.debug("* Calculated Host Crypto: " + Conversion.arrayToHex(crypto));
        logger.debug("==> Calculate Host Cryptogram End");
        return crypto;
    }
    @Override
    public void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek) {
        byte[] derivationData = calculateDerivationData();
        logger.debug("==> Generate Session Keys");
        logger.debug("* ICV: " + Conversion.arrayToHex(icv));
        
        SecretKeySpec skeySpec = new SecretKeySpec(staticKenc.getValue(), "AES");
        logger.debug("* Key: " + Conversion.arrayToHex(skeySpec.getEncoded()));
        
        derivationData[14] = (byte) 0x80;//AES-128 -> L is 0x80
        derivationData[15] = (byte) 0x01;//Because L = 0x80
        
        derivationData[11] = SCP03_DERIVATION4DATAENC;
        sessEnc = new SecretKeySpec(generateCryptogram(skeySpec, derivationData), "AES");
        logger.debug("* sessEnc = " + Conversion.arrayToHex(sessEnc.getEncoded()));
        
        derivationData[11] = SCP03_DERIVATION4CMAC;
        sessCMac = new SecretKeySpec(generateCryptogram(skeySpec, derivationData), "AES");
        logger.debug("* sessMac = " + Conversion.arrayToHex(sessCMac.getEncoded()));
        
        derivationData[11] = SCP03_DERIVATION4RMAC;
        sessRMac = new SecretKeySpec(generateCryptogram(skeySpec, derivationData), "AES");
        logger.debug("* sessRMac = " + Conversion.arrayToHex(sessRMac.getEncoded()));
        logger.debug("==> Generate Session Keys End");
    }
    @Override
    public Key newKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }
    @Override
    public byte[] encapsulateCommand(byte[] command) {
        if ((secLevel.getVal() & SecLevel.C_ENC_AND_MAC.getVal()) == SecLevel.C_ENC_AND_MAC.getVal()) {
            command = encryptCommand(command);
        }
        if ((secLevel.getVal() & SecLevel.C_MAC.getVal()) == SecLevel.C_MAC.getVal()) {
            command = generateCMac(command);
        }
        return command;
    }
    @Override
    public byte[] desencapsulateResponse(byte[] response) throws CardException {
        if ((secLevel.getVal() & SecLevel.R_MAC.getVal()) == SecLevel.R_MAC.getVal() && !checkRMac(response)) {
            throw new CardException("Invalid R-Mac.");
        }
        if (secLevel == SecLevel.C_ENC_AND_R_ENC_AND_C_MAC_AND_R_MAC) {
            response = decryptCardResponseData(response);
        }
        return response;
    }
    @Override
    public byte[] encryptCommand(byte[] command) {
        logger.debug("==> Encrypt Command");
        logger.debug("* Command to encrypt: " + Conversion.arrayToHex(command));
        byte[] data = new byte[command.length - 5];
        System.arraycopy(command, 5, data, 0, data.length);
        data = addPadding(data);
        
        byte[] icvEnc = new byte[16];
        for (int i = 0; i < 4; i++) {
            icvEnc[15 - i] = (byte) ((encCounter >> (i * 8)) & 0xFF);
        }
        encCounter++;
        icvEnc = doFinal(Cipher.ENCRYPT_MODE, "AES/CBC/NoPadding", sessEnc, new byte[16], icvEnc, 0, icvEnc.length);
        logger.debug("* ICV: " + Conversion.arrayToHex(icvEnc));
        
        data = doFinal(Cipher.ENCRYPT_MODE, "AES/CBC/NoPadding", sessEnc, icvEnc, data, 0, data.length);
        
        byte[] encrypted = new byte[5 + data.length];
        System.arraycopy(command, 0, encrypted, 0, 5);
        System.arraycopy(data, 0, encrypted, 5, data.length);
        encrypted[4] = (byte) (data.length);
        
        logger.debug("* Encrypted command: " + Conversion.arrayToHex(encrypted));
        logger.debug("==> Encrypt Command End");
        return encrypted;
    }
    @Override
    public byte[] decryptCardResponseData(byte[] response) {
        logger.debug("Response to decrypt: " + Conversion.arrayToHex(response));
        byte[] icvEnc = new byte[16];
        icvEnc[0] = (byte) 0x80;
        encCounter--;
        for (int i = 0; i < 4; i++) {
            icvEnc[15 - i] = (byte) ((encCounter >> (i * 8)) & 0xFF);
        }
        encCounter++;
        icvEnc = doFinal(Cipher.ENCRYPT_MODE, "AES/CBC/NoPadding", sessEnc, new byte[16], icvEnc, 0, icvEnc.length);
        logger.debug("* ICV: " + Conversion.arrayToHex(icvEnc));
        
        int dataLength;
        byte[] data = doFinal(Cipher.DECRYPT_MODE, "AES/CBC/NoPadding", sessEnc, icvEnc, response, 0, response.length - 10);
        logger.debug("* Plain data: " + Conversion.arrayToHex(data));
        for (dataLength = data.length - 1; dataLength >= 0 && data[dataLength] != (byte) 0x80; dataLength--);
        byte[] newResponse = new byte[dataLength + 2];
        System.arraycopy(data, 0, newResponse, 0, dataLength);
        System.arraycopy(response, response.length - 2, newResponse, dataLength, 2);
        
        logger.debug("Decrypted response: " + Conversion.arrayToHex(newResponse));
        return newResponse;
    }
    @Override
    public byte[] generateCMac(byte[] command) {
        logger.debug("==> Generate C-Mac");
        logger.debug("* No ICV");
        logger.debug("* Mac Chaining Value : " + Conversion.arrayToHex(macChainingValue));
        
        byte[] data = new byte[macChainingValue.length + command.length];
        System.arraycopy(macChainingValue, 0, data, 0, macChainingValue.length);
        System.arraycopy(command, 0, data, macChainingValue.length, command.length);
        data[macChainingValue.length + ISO7816.OFFSET_CLA.getValue()] |= 0x4;//CLA modification
        data[macChainingValue.length + ISO7816.OFFSET_LC.getValue()] += 8;//Lc + 8
        data = addPadding(data);
        
        data = doFinal(Cipher.ENCRYPT_MODE, "AES/CBC/NoPadding", sessCMac, new byte[16], data, 0, data.length);
        logger.debug("* Encrypted data : " + Conversion.arrayToHex(data));
        
        //Update Mac Chaining Value
        System.arraycopy(data, 0, macChainingValue, 0, 8);
        System.arraycopy(data, data.length - 8, macChainingValue, 8, 8);
        logger.debug("* New Mac Chaining Value : " + Conversion.arrayToHex(macChainingValue));
        
        byte[] cmdMac = new byte[command.length + 8];
        System.arraycopy(command, 0, cmdMac, 0, command.length);
        System.arraycopy(data, 0, cmdMac, command.length, 8);
        cmdMac[ISO7816.OFFSET_CLA.getValue()] |= 0x4;//CLA modification
        cmdMac[ISO7816.OFFSET_LC.getValue()] += 8;//Lc + 8
        logger.debug("* Command with C-Mac: " + Conversion.arrayToHex(cmdMac));
        logger.debug("==> Generate C-Mac End");
        return cmdMac;
    }
    @Override
    public boolean checkRMac(byte[] response) {
        logger.debug("==> Check R-Mac");
        logger.debug("* No ICV");
        logger.debug("* Mac Chaining Value : " + Conversion.arrayToHex(macChainingValue));
        
        byte[] data = new byte[macChainingValue.length + response.length - 8];//R-Mac value is left
        System.arraycopy(macChainingValue, 0, data, 0, macChainingValue.length);
        System.arraycopy(response, 0, data, macChainingValue.length, response.length - 10);//data response
        System.arraycopy(response, response.length - 2, data, data.length - 2, 2);//status word
        data = addPadding(data);
        
        data = doFinal(Cipher.ENCRYPT_MODE, "AES/CBC/NoPadding", sessRMac, new byte[16], data, 0, data.length);
        logger.debug("* Encrypted data : " + Conversion.arrayToHex(data));
        
        byte[] calculatedRMac = new byte[8];
        byte[] respRMac = new byte[8];
        System.arraycopy(data, 0, calculatedRMac, 0, 8);
        System.arraycopy(response, response.length - 10, respRMac, 0, 8);
        logger.debug("* R-Mac: " + Conversion.arrayToHex(respRMac));
        logger.debug("* Calculated R-Mac: " + Conversion.arrayToHex(calculatedRMac));
        logger.debug("==> Check R-Mac End");
        return Arrays.equals(calculatedRMac, respRMac);
    }
    @Override
    public byte[] addPadding(byte[] data) {
        byte[] dataWithPadding = new byte[data.length + (16 - (data.length % 16))];
        
        System.arraycopy(data, 0, dataWithPadding, 0, data.length);
        dataWithPadding[data.length] = (byte) 0x80;
        //The remaining bytes are already set to 0 with the new byte[] call
        
        logger.debug("* Data before PADDING: " + Conversion.arrayToHex(data));
        logger.debug("* Data with PADDING: " + Conversion.arrayToHex(dataWithPadding));
        
        return dataWithPadding;
    }
    @Override
    public byte[] calculateDerivationData() {
        /*
         * Derivation data in SCP 03 mode
         *
         * -0-----------------------10--11---12--13--14--15--16-----------23-24-------------31-
         * | label (11 byte of 00)    | dc | 00 |  L   | i  | Host Challenge | Card Challenge |
         * ------------------------------------------------------------------------------------
         *
         * Definition of the derivation constant (dc):
         * - 00 : derivation data to calculate card cryptogram
         * - 01 : derivation data to calculate host cryptogram
         * - 04 : derivation of S-ENC
         * - 06 : derivation of S-MAC
         * - 07 : derivation of S-RMAC
         */
       byte[] derivationData = new byte[32];
       //[0-10] : label -> already set to zero
       //dc, L, and i are added later
       System.arraycopy(hostChallenge, 0, derivationData, 16, hostChallenge.length);
       System.arraycopy(cardChallenge, 0, derivationData, 24, hostChallenge.length);
       
       logger.debug("* Derivation Data: " + Conversion.arrayToHex(derivationData));
       
       return derivationData;
    }
    /**
     * Calculate Cryptogramm.
     * 
     * @param key the key used to generate the cryptogram.
     * @param derivationData data used to generate the cryptogram.
     * @return the cryptogram
     */
    private byte[] generateCryptogram(Key key, byte[] derivationData) {
        byte[] icvNextBloc = icv.clone();
        int noOfBlocks = derivationData.length / 16;
        int startIndex = 0;
        for (int i = 0; i < (noOfBlocks); i++) {
            icvNextBloc = doFinal(Cipher.ENCRYPT_MODE, "AES/CBC/NoPadding", key, icvNextBloc, derivationData, startIndex, 16);
            startIndex += 16;
        }
        logger.debug("* Generated cryptogram: " + Conversion.arrayToHex(icvNextBloc));
        return icvNextBloc;
    }
}