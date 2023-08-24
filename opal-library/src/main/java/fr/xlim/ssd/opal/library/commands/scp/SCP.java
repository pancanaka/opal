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

import fr.xlim.ssd.opal.library.commands.SecLevel;
import fr.xlim.ssd.opal.library.commands.SessionState;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCPMode;
import java.security.Key;
import javax.smartcardio.CardException;

/**
 * Secure Channel Protocol interface
 *
 * @author Guillaume Bouffard
 * @author Jean Dubreuil
 */
public interface SCP {
    //========================= Getters and Setters ==========================\\
    
    /**
     * Set a session key. (Usage is recommended for tests execution.)
     * @see SCP#generateSessionKeys(fr.xlim.ssd.opal.library.config.SCGPKey, fr.xlim.ssd.opal.library.config.SCGPKey, fr.xlim.ssd.opal.library.config.SCGPKey) 
     * 
     * @param keyName "sessEnc" for the encryption session key, "sessCMac" for the CMac session key, "sessRMac" for the RMac session key, "sessDek" for the data encryption session key.
     * @param key the key value.
     */
    public void setSessKey(String keyName, byte[] key);
    /**
     * Get a session key. (Usage is recommended for tests execution.)
     * 
     * @param keyName "sessEnc" for the encryption session key, "sessCMac" for the CMac session key, "sessRMac" for the RMac session key, "sessDek" for the data encryption session key.
     * @return the key value.
     */
    public byte[] getSessKey(String keyName);
    
    /**
     * Set the security mode.
     * 
     * @param secMode the new security mode.
     */
    public void setSecLevel(SecLevel secMode);
    /**
     * Get the current security mode.
     *
     * @return The security level.
     */
    public SecLevel getSecLevel();
    
    /**
     * Set the session state.
     * 
     * @param sessState the new session state.
     */
    public void setSessionState(SessionState sessState);
    /**
     * Get the current session state.
     *
     * @return the session state.
     */
    public SessionState getSessState();
    
    /**
     * Get SCP mode used
     *
     * @return SCP mode used
     */
    public SCPMode getSCPMode();
    
    /**
     * Set the card challenge.
     * 
     * @param cardChallenge the card challenge.
     */
    public void setCardChallenge(byte[] cardChallenge);
    /**
     * Get the card challenge.
     * 
     * @return the card challenge.
     */
    public byte[] getCardChallenge();
    
    /**
     * Set the host challenge.
     * 
     * @param hostChallenge the host challenge.
     */
    public void setHostChallenge(byte[] hostChallenge);
    /**
     * Get the host challenge.
     * 
     * @return the host challenge.
     */
    public byte[] getHostChallenge();
    
    
    //================================ Crypto ================================\\
    /**
     * Init the ICV.
     */
    public void initICV();
    /**
     * Add padding as written in the specification.
     * 
     * @param data the data used.
     * @return the data with padding.
     */
    public byte[] addPadding(byte[] data);
    /**
     * Calculate the card cryptogram.
     * 
     * @return the card cryptogram.
     */
    public byte[] calculateCardCryptogram();
    /**
     * Calculate the host cryptogram.
     * 
     * @return the host cryptogram.
     */
    public byte[] calculateHostCryptogram();
    /**
     * Generate session keys depending on SCP protocol used
     *
     * @param staticKenc Static Encrypt key
     * @param staticKmac Static Mac key
     * @param staticKkek Static data encryption key
     */
    public void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek);
    /**
     * From an array, create a session key with right parameters depending on the protocol version.
     * 
     * @param keyBytes the key value.
     * @return the key generated.
     */
    public Key newKey(byte[] keyBytes);
    /**
     * Special step after Generate Session Keys.
     */
    public void extraStep();
    /**
     * From a plain command, add C-Mac and encryption according to the current security level.
     *
     * @param command 
     * @return command calculated
     */
    public byte[] encapsulateCommand(byte[] command);
    /**
     * From a response APDU, deciper it if the security level is asking it. The R-Mac is also verified.
     * 
     * @param response the response to analyse
     * @return the plain text response
     * @throws javax.smartcardio.CardException if the R-Mac is wrong.
     */
    public byte[] desencapsulateResponse(byte[] response) throws CardException;
    /**
     * Encrypt an APDU command.
     *
     * @param command command to encrypt
     * @return encrypted command
     */
    public byte[] encryptCommand(byte[] command);

    /**
     * Decrypt an APDU response. This command may return the same input if encryption is not supported.
     *
     * @param response response to decrypt
     * @return plain response
     */
    public byte[] decryptCardResponseData(byte[] response);
    /**
     * Generate mac value according input command
     *
     * @param command command used to generate Mac value
     * @return Mac value calculated
     */
    public byte[] generateCMac(byte[] command);
    /**
     * Check the response Mac.
     * 
     * @param response the response APDU to analyse.
     * @return if the R-Mac is correct.
     */
    public boolean checkRMac(byte[] response);
    /**
     * Calculate Derivation data.
     *
     * @return the Derivation data.
     */
    public byte[] calculateDerivationData();
}