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
import fr.xlim.ssd.opal.library.commands.SessionState;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract SCP defines getters and setters and contains common methods between SCP01 and SCP02 like initICV, generation cryptogram. (These methods are overrided in other protocols).
 *
 * @author Jean Dubreuil
 */
public abstract class AbstractSCP implements SCP {
    // Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(AbstractSCP.class);
    
    public static final byte SECURE_CHANNEL_KEYS_3 = (byte) 0x1;
    public static final byte CMAC_ON_UNMODIFIED_APDU = (byte) 0x2;
    public static final byte INITIATION_MODE_EXPLICIT = (byte) 0x4;
    public static final byte ICV_SET_TO_MAC_OVER_AID = (byte) 0x8;
    public static final byte ICV_ENCRYPTION_FOR_CMAC_SESSION = (byte) 0x10;
    public static final byte RMAC_SUPPORT = (byte) 0x20;
    public static final byte WELL_KNOWN_PSEUDO_RANDOM_ALGORITHM = (byte) 0x40;
    
    /// Initialized Cypher Vector used to initialized encryption steps
    protected byte[] icv;
    //Encryption session key
    protected Key sessEnc;
    //C-MAC session key
    protected Key sessCMac;
    //R-MAC session key
    protected Key sessRMac;
    //Data Encryption session key
    protected Key sessDek;
    /// Host challenge used to authenticate host in smartcard
    protected byte[] hostChallenge;
    /// Card challenge used to authenticate smartcard in host
    protected byte[] cardChallenge;
    // Secure Channel Protocol used
    protected SCPMode scpMode;
    // Secure Level used to communicate
    protected SecLevel secLevel;
    // State of smart card session (NO_SESSION, SESSION_INIT or SESSION_AUTH)
    protected SessionState sessState;
    
    protected AbstractSCP(SCPMode scpMode) {
        this.scpMode = scpMode;
        secLevel = SecLevel.NO_SECURITY_LEVEL;
        sessState = SessionState.NO_SESSION;
    }
    
    //========================= Getters and Setters ==========================\\
    @Override
    public void setSessKey(String keyName, byte[] key) {
        if (keyName.equals("sessEnc"))
            sessEnc = newKey(key);
        else if (keyName.equals("sessCMac"))
            sessCMac = newKey(key);
        else if (keyName.equals("sessRMac"))
            sessRMac = newKey(key);
        else if (keyName.equals("sessDek"))
            sessDek = newKey(key);
    }
    @Override
    public byte[] getSessKey(String keyName) {
        if (keyName.equals("sessEnc"))
            return sessEnc.getEncoded();
        else if (keyName.equals("sessCMac"))
            return sessCMac.getEncoded();
        else if (keyName.equals("sessRMac"))
            return sessRMac.getEncoded();
        else if (keyName.equals("sessDek"))
            return sessDek.getEncoded();
        return null;
    }
    @Override
    public void setSecLevel(SecLevel secLevel) {
        this.secLevel = secLevel;
    }
    @Override
    public SecLevel getSecLevel() {
        return secLevel;
    }
    @Override
    public void setSessionState(SessionState sessState) {
        this.sessState = sessState;
    }
    @Override
    public SessionState getSessState() {
        return sessState;
    }
    @Override
    public SCPMode getSCPMode() {
        return scpMode;
    }
    @Override
    public void setCardChallenge(byte[] cardChallenge) {
        this.cardChallenge = cardChallenge;
    }
    @Override
    public byte[] getCardChallenge() {
        return cardChallenge;
    }
    @Override
    public void setHostChallenge(byte[] hostChallenge) {
        this.hostChallenge = hostChallenge;
    }
    @Override
    public byte[] getHostChallenge() {
        return hostChallenge;
    }
    
    //================================ Crypto ================================\\
    @Override
    public void initICV() {//Default implementation for SCP01 and SCP02
        logger.debug("==> Init ICV begin");
        icv = new byte[8];
        logger.debug("* New ICV is " + Conversion.arrayToHex(icv));
        logger.debug("==> Init ICV end");
    }
    @Override
    public byte[] addPadding(byte[] data) {//Default implementation for SCP01 and SCP02
        byte[] dataWithPadding = new byte[data.length + (8 - (data.length % 8))];
        
        System.arraycopy(data, 0, dataWithPadding, 0, data.length);
        dataWithPadding[data.length] = (byte) 0x80;
        //The remaining bytes are already set to 0 with the new byte[] call
        
        logger.debug("Data before PADDING: " + Conversion.arrayToHex(data));
        logger.debug("Data with PADDING: " + Conversion.arrayToHex(dataWithPadding));
        
        return dataWithPadding;
    }
    @Override
    public byte[] calculateCardCryptogram() {//Default implementation for SCP01 and SCP02
        byte[] crypto = new byte[8];
        byte[] derivationData = new byte[16];
        logger.debug("ICV : " + Conversion.arrayToHex(icv));
        System.arraycopy(hostChallenge, 0, derivationData, 0, 8);
        System.arraycopy(cardChallenge, 0, derivationData, 8, 8);
        derivationData = addPadding(derivationData);
        derivationData = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", sessEnc, icv, derivationData, 0, derivationData.length);
        System.arraycopy(derivationData, 16, crypto, 0, 8);
        logger.debug("Calculated Card Crypto: " + Conversion.arrayToHex(crypto));
        return crypto;
    }
    @Override
    public byte[] calculateHostCryptogram() {//Default implementation for SCP01 and SCP02
        byte[] crypto = new byte[8];
        byte[] derivationData = new byte[16];
        logger.debug("ICV : " + Conversion.arrayToHex(icv));
        System.arraycopy(cardChallenge, 0, derivationData, 0, 8);
        System.arraycopy(hostChallenge, 0, derivationData, 8, 8);
        derivationData = addPadding(derivationData);
        derivationData = doFinal(Cipher.ENCRYPT_MODE, "DESede/CBC/NoPadding", sessEnc, icv, derivationData, 0, derivationData.length);
        System.arraycopy(derivationData, 16, crypto, 0, 8);
        logger.debug("Calculated Host Crypto: " + Conversion.arrayToHex(crypto));
        return crypto;
    }
    @Override
    public Key newKey(byte[] keyBytes) {//Default implementation for SCP01 and SCP02
        return new SecretKeySpec(get3DESKey(keyBytes), "DESede");
    }
    @Override
    public final void extraStep() {//Final implementation for SCP01, SCP02 and SCP03
        logger.debug("Extra step begin");
        sessEnc = extraStep(sessEnc);
        sessCMac = extraStep(sessCMac);
        sessDek = extraStep(sessDek);
        logger.debug("* sessEnc = " + Conversion.arrayToHex(sessEnc.getEncoded()));
        logger.debug("* sessCMac = " + Conversion.arrayToHex(sessCMac.getEncoded()));
        logger.debug("* sessDek = " + Conversion.arrayToHex(sessDek.getEncoded()));
        logger.debug("Extra step end");
    }
    protected final byte[] get3DESKey(byte[] key) {//Method used by SCP01 and SCP02
        if (key.length == 24)
            return key;
        if (key.length == 16) {
            byte[] newKey = new byte[24];
            System.arraycopy(key, 0, newKey, 0, 16);
            System.arraycopy(key, 0, newKey, 16, 8);
            return newKey;
        }
        throw new IllegalArgumentException("Invalid key length.");
    }
    protected final byte[] doFinal(int opmode, String transformation, Key key, byte[] icv, byte[] data, int startOffset, int length) {//Final implementation for SCP01, SCP02 and SCP03
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            if (icv != null)
                cipher.init(opmode, key, new IvParameterSpec(icv));
            else
                cipher.init(opmode, key);
            return cipher.doFinal(data, startOffset, length);
        } catch (InvalidAlgorithmParameterException e) {
            throw new UnsupportedOperationException("Invalid algorithm parameter problem", e);
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
    }
    private Key extraStep(Key k) {//Extra step for GemXpresso211
        byte[] keyBytes = k.getEncoded();
        
        for (int i = 0; i < keyBytes.length; i++) {
            if (keyBytes[i] % 2 == (byte) 0x00) {
                keyBytes[i] = (byte) 0xCA;
            } else {
                keyBytes[i] = (byte) 0x2D;
            }
        }
        return newKey(keyBytes);
    }
}
