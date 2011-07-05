package fr.xlim.ssd.opal.library.commands;

import fr.xlim.ssd.opal.library.*;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;

/**
 * @author Julien Iguchi-Cartigny
 */
public interface Commands {

    void setCc(CardChannel cc);

    /**
     * Get Card Channel used to trasmit APDU
     *
     * @return Card Channel
     */
    CardChannel getCc();

    /**
     * Get SCP mode used
     *
     * @return SCP mode used
     */
    SCPMode getScp();

    /**
     * Get Session State used to indicate the Authenticate state
     *
     * @return Authenticate state
     */
    SessionState getSessState();

    /**
     * Get the security mode chosen with the @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.externalAuthenticate}
     *
     * @return The security level
     */
    SecLevel getSecMode();

    /**
     * Get static keys used to generate session keys
     *
     * @return static keys
     */
    SCKey[] getKeys();

    /**
     * Get a specific key, matching with your keySetVersion and keyID.
     *
     * @param keySetVersion key version of the wished key
     * @param keyId         Key id of the wished jey
     * @return wished key
     */
    SCKey getKey(byte keySetVersion, byte keyId);

    /**
     * Set the off card static keys with a same input key. Every key does the same.
     *
     * @param key new key used to set up static keys
     * @return the input key
     */
    SCKey setOffCardKey(SCKey key);

    /**
     * Set up off card static keys like:
     * <ul>
     * <li> keys[0] is the static encrypt key
     * <li> keys[1] is the static mac key
     * <li> keys[2] is the static dek key
     * </ul>
     *
     * @param keys new static keys
     */
    void setOffCardKeys(SCKey[] keys);

    /**
     * Delete a off card key matching with its Key set version and its key ID
     *
     * @param keySetVersion Key set version of the wished key
     * @param keyId         key ID of the wished key
     * @return The deleted key or null if this key had not found
     */
    SCKey deleteOffCardKey(byte keySetVersion, byte keyId);

    /**
     * Reset all values
     */
    void resetParams();

    /**
     * Select the Applet matching with AID parameter
     *
     * @param aid wished Applet AID
     * @return Select APDU response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU select(byte[] aid) throws CardException;


    /**
     * Select the Applet matching with And security canal protocol mode parameter
     *
     * @param aid wished Applet AID
     * @param SCPMode wished the security canal protocol Mode
     * @return Select APDU response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU select(byte[] aid,SCPMode desiredScp) throws CardException;

    /**
     *
     * @return
     * @throws CardException
     */

    ResponseAPDU getData () throws CardException;



    /**
     * initilizate parameters (Sequence counter,Session key, Mac Key,Encription key,ICV)
     * for Implicit Initiation Mode (SCP_02_0A,SCP_02_0B)
     * @param aid wished Applet AID
     * @param SCPMode wished the security canal protocol Mode
     * @param keyId Key ID to use to do the authenticate step
     * @throws CardException
     */

    void InitParamForImplicitInitiationMode (byte[] aid,SCPMode desiredScp,byte keyId) throws CardException;

    /**
     * Initialize Update command
     *
     * @param keySetVersion Key Set Version to use to do the authenticate step
     * @param keyId         Key ID to use to do the authenticate step
     * @param desiredScp    SCP mode to use to do the authenticate step
     * @return APDU response
     * @throws CardException <ul>
     *                       <il> Card return an error status word
     *                       <il> The calculated cryptograms are not the same
     *                       </ul>
     */
    ResponseAPDU initializeUpdate(byte keySetVersion, byte keyId,
                                  SCPMode desiredScp) throws CardException;

    /**
     * External Authenticate uses for validate the @see{fr.xlim.ssd.opal.library.commands.Commands.initializeUpdate}
     *
     * @param secLevel Security Level will use to communicate
     * @return APDU response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU externalAuthenticate(SecLevel secLevel) throws CardException;

    /**
     * Get status is used to retrieve ISD, Executable Load File, Executable Module, Application or Security Domain Life
     * Cycle status information according to a given match/search criteria
     *
     * @param ft              File Type wished
     * @param respMode        @see{fr.xlim.ssd.opal.library.GetStatusResponseMode}
     * @param searchQualifier Shall used to indicate the Application Identifier (AID). If is null, you search all occurrences that match the
     *                        selection criteria according to the reference by ft.
     * @return APDU Response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU[] getStatus(GetStatusFileType ft,
                             GetStatusResponseMode respMode, byte[] searchQualifier)
            throws CardException;

    /**
     * Delete On Card Object
     *
     * @param aid     Object AID to delete
     * @param cascade Delete in cascade mode
     * @return Delete APDU Response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU deleteOnCardObj(byte[] aid, boolean cascade)
            throws CardException;

    /**
     * Delete on card key defined with its Key Set Version and its Key ID
     *
     * @param keySetVersion Key Set Version to delete
     * @param keyId         Key ID to delete
     * @return APDU response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU deleteOnCardKey(byte keySetVersion, byte keyId)
            throws CardException;

    /**
     * Install for Load Command. This command prepares smart card to receive applet via @see{fr.xlim.ssd.opal.library.commands.Commands.load} command
     *
     * @param packageAid        Package AID of the install Applet
     * @param securityDomainAID Smart Card ISD
     * @param params            Install parameters
     * @return APDU response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU installForLoad(byte[] packageAid, byte[] securityDomainAID,
                                byte[] params) throws CardException;

    /**
     * Load CAP File to the smart card device
     *
     * @param capFile cap data to send
     * @return APDU response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU[] load(byte[] capFile) throws CardException;

    /**
     * Load CapFile with a specific data length send
     *
     * @param capFile       cap data to send
     * @param maxDataLength Size of each CAP File part sent to the smart card
     * @return APDU Response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU[] load(byte[] capFile, byte maxDataLength)
            throws CardException;

    /**
     * Install the previously loaded CAP File and select it.
     *
     * @param loadFileAID    Load File AID
     * @param moduleAID      Module AID
     * @param applicationAID Application AID
     * @param privileges     Applet privileges
     * @param params         Install parameters
     * @return APDU Response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU installForInstallAndMakeSelectable(byte[] loadFileAID,
                                                    byte[] moduleAID, byte[] applicationAID, byte[] privileges,
                                                    byte[] params) throws CardException;

    /**
     * The BEGIN R-MAC SESSION command is used to initiate additional response security. The BEGIN R-MAC SESSION command may only be issued to the card within a secure channel. It may only be used to increase the security of the responses
     *
     * @return APDU Response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU beginRMacSession() throws CardException;

    /**
     * The END R-MAC SESSION command is used to terminate the additional response security that was initiated by the preceding BEGIN R-MAC SESSION.
     * The Secure Channel session returns to its original security settings. The END R-MAC SESSION command may be issued to the card at any time during an R-MAC session.
     * 
     * @return APDU Response
     * @throws CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU endRMacSession() throws CardException;
    
    
    /**
     * Send APDU command to to the smart card device and retrive response
     * @param APDUCommand
     * @return APDU Response
     * @throws CardException CardException APDU status word response is not equals to @see{fr.xlim.ssd.opal.library.ISO7816.SW_NO_ERROR}
     */
    ResponseAPDU sendCommand(byte[] APDUCommand) throws CardException;
   
}
