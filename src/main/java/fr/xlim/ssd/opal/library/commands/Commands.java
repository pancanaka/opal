package fr.xlim.ssd.opal.library.commands;

import java.io.File;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.FileType;
import fr.xlim.ssd.opal.library.GetStatusResponseMode;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SessionState;

/**
 * @author dede
 *
 */
public abstract class Commands {

    /**
     *
     */
    protected CardChannel cc;

    /**
     *
     */
    protected Commands() {
        this.cc = null;
    }

    /**
     * @param cc
     */
    public final void setCc(CardChannel cc) {
        if (this.cc == null) {
            this.cc = cc;
        }
    }

    /**
     * @return
     */
    public final CardChannel getCc() {
        return this.cc;
    }

    /**
     * @return
     */
    public abstract SCPMode getScp();

    /**
     * @return
     */
    public abstract SessionState getSessState();

    /**
     * @return
     */
    public abstract SecLevel getSecMode();

    /**
     * @return
     */
    public abstract SCKey[] getKeys();

    /**
     * @param keySetVersion
     * @param keyId
     * @return
     */
    public abstract SCKey getKey(byte keySetVersion, byte keyId);

    /**
     * @param key
     * @return
     */
    public abstract SCKey setOffCardKey(SCKey key);

    /**
     * @param keys
     */
    public abstract void setOffCardKeys(SCKey[] keys);

    /**
     * @param keySetVersion
     * @param keyId
     * @return
     */
    public abstract SCKey deleteOffCardKey(int keySetVersion, int keyId);

    /**
     *
     */
    public abstract void resetParams();

    /**
     * @param aid
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU select(byte[] aid) throws CardException;

    /**
     * @param keySetVersion
     * @param keyId
     * @param desiredScp
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU initializeUpdate(byte keySetVersion, byte keyId,
            SCPMode desiredScp) throws CardException;

    /**
     * @param secLevel
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU externalAuthenticate(SecLevel secLevel) throws CardException;

    /**
     * @param command
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU send(CommandAPDU command) throws CardException;

    /**
     * @param ft
     * @param respMode
     * @param searchQualifier
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU[] getStatus(FileType ft,
            GetStatusResponseMode respMode, byte[] searchQualifier)
            throws CardException;

    /**
     * @param aid
     * @param cascade
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU deleteOnCardObj(byte[] aid, boolean cascade)
            throws CardException;

    /**
     * @param keySetVersion
     * @param keyId
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU deleteOnCardKey(byte keySetVersion, byte keyId)
            throws CardException;

    /**
     * @param packageAid
     * @param securityDomainAID
     * @param params
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU installForLoad(byte[] packageAid, byte[] securityDomainAID,
            byte[] params) throws CardException;

    /**
     * @param capFile
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU[] load(File capFile) throws CardException;

    /**
     * @param capFile
     * @param maxDataLength
     * @return
     * @throws CardException
     */
    public abstract ResponseAPDU[] load(File capFile, byte maxDataLength)
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
    public abstract ResponseAPDU installForInstallAndMakeSelectable(byte[] loadFileAID,
            byte[] moduleAID, byte[] applicationAID, byte[] privileges,
            byte[] params) throws CardException;
}
