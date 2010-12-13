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
     * @return
     */
    CardChannel getCc();

    /**
     * Get SCP mode used
     *
     * @return
     */
    SCPMode getScp();

    /**
     * @return
     */
    SessionState getSessState();

    /**
     * @return
     */
    SecLevel getSecMode();

    /**
     * @return
     */
    SCKey[] getKeys();

    /**
     * @param keySetVersion
     * @param keyId
     * @return
     */
    SCKey getKey(byte keySetVersion, byte keyId);

    /**
     * @param key
     * @return
     */
    SCKey setOffCardKey(SCKey key);

    /**
     * @param keys
     */
    void setOffCardKeys(SCKey[] keys);

    /**
     * @param keySetVersion
     * @param keyId
     * @return
     */
    SCKey deleteOffCardKey(byte keySetVersion, byte keyId);

    /**
     *
     */
    void resetParams();

    /**
     * @param aid
     * @return
     * @throws CardException
     */
    ResponseAPDU select(byte[] aid) throws CardException;

    /**
     * @param keySetVersion
     * @param keyId
     * @param desiredScp
     * @return
     * @throws CardException
     */
    ResponseAPDU initializeUpdate(byte keySetVersion, byte keyId,
                                  SCPMode desiredScp) throws CardException;

    /**
     * @param secLevel
     * @return
     * @throws CardException
     */
    ResponseAPDU externalAuthenticate(SecLevel secLevel) throws CardException;

    /**
     * @param ft
     * @param respMode
     * @param searchQualifier
     * @return
     * @throws CardException
     */
    ResponseAPDU[] getStatus(GetStatusFileType ft,
                             GetStatusResponseMode respMode, byte[] searchQualifier)
            throws CardException;

    /**
     * @param aid
     * @param cascade
     * @return
     * @throws CardException
     */
    ResponseAPDU deleteOnCardObj(byte[] aid, boolean cascade)
            throws CardException;

    /**
     * @param keySetVersion
     * @param keyId
     * @return
     * @throws CardException
     */
    ResponseAPDU deleteOnCardKey(byte keySetVersion, byte keyId)
            throws CardException;

    /**
     * @param packageAid
     * @param securityDomainAID
     * @param params
     * @return
     * @throws CardException
     */
    ResponseAPDU installForLoad(byte[] packageAid, byte[] securityDomainAID,
                                byte[] params) throws CardException;

    /**
     * @param capFile
     * @return
     * @throws CardException
     */
    ResponseAPDU[] load(byte[] capFile) throws CardException;

    /**
     * @param capFile
     * @param maxDataLength
     * @return
     * @throws CardException
     */
    ResponseAPDU[] load(byte[] capFile, byte maxDataLength)
            throws CardException;

    /**
     * @param loadFileAID
     * @param moduleAID
     * @param applicationAID
     * @param privileges
     * @param params
     * @return
     * @throws CardException
     */
    ResponseAPDU installForInstallAndMakeSelectable(byte[] loadFileAID,
                                                    byte[] moduleAID, byte[] applicationAID, byte[] privileges,
                                                    byte[] params) throws CardException;
}