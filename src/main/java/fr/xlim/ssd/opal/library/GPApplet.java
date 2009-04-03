package fr.xlim.ssd.opal.library;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import fr.xlim.ssd.opal.library.commands.Commands;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;

/**
 * This class contains methods that could be sent to an Applet. <br/>
 * 
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class GPApplet {

    /**
     *
     */
    protected byte[] aid;
    /**
     *
     */
    protected Commands cmds;

	/**
	 * Creates the off-card "Applet"
	 * @param CmdImplementation the String representation of the chosen implementation (i.e. "fr.xlim.ssd.opal.commands.GP2xCommands") <br/>
	 * This designed implementation must override the class {@link fr.xlim.ssd.opal.commands.Commands}
	 * @param cc the initialized card channel on which data will be sent to the card
	 * @param aid the byte array containing the aid representation of the Applet
	 * @throws CommandsImplementationNotFound
	 * @throws ClassNotFoundException
	 */
    public GPApplet(String CmdImplementation, CardChannel cc, byte[] aid) throws CommandsImplementationNotFound, ClassNotFoundException {
        this.aid = aid.clone();
        Class.forName(CmdImplementation);
        this.cmds = CommandsProvider.getImplementation(CmdImplementation, cc);
    }

    /**
     * @return
     */
    public CardChannel getCc() {
        return this.cmds.getCc();
    }

    /**
     * @return
     */
    public byte[] getAid() {
        return aid.clone();
    }

    /**
     * @return
     */
    public SCPMode getScp() {
        return this.cmds.getScp();
    }

    /**
     * @return
     */
    public SessionState getSessState() {
        return this.cmds.getSessState();
    }

    /**
     * @return
     */
    public SecLevel getSecMode() {
        return this.cmds.getSecMode();
    }

    /**
     * @return
     */
    public SCKey[] getKeys() {
        return this.cmds.getKeys();
    }

    /**
     * @param keySetVersion
     * @param keyId
     * @return
     */
    public SCKey getKey(byte keySetVersion, byte keyId) {
        return this.cmds.getKey(keySetVersion, keyId);
    }

    /**
     * @param key
     * @return
     */
    public SCKey setOffCardKey(SCKey key) {
        return this.cmds.setOffCardKey(key);
    }

    /**
     * @param keys
     */
    public void setOffCardKeys(SCKey[] keys) {
        this.cmds.setOffCardKeys(keys);
    }

    /**
     * @param keySetVersion
     * @param keyId
     * @return
     */
    public SCKey deleteOffCardKey(int keySetVersion, int keyId) {
        return this.cmds.deleteOffCardKey(keySetVersion, keyId);
    }

    /**
     * @return
     * @throws CardException
     */
    public ResponseAPDU select() throws CardException {
        return this.cmds.select(this.aid);
    }

    /**
     *
     */
    public void resetParams() {
        this.cmds.resetParams();
    }

    /**
     * @param keySetVersion
     * @param keyId
     * @param desiredScp
     * @return
     * @throws CardException
     */
    public ResponseAPDU initializeUpdate(byte keySetVersion, byte keyId, SCPMode desiredScp) throws CardException {
        return this.cmds.initializeUpdate(keySetVersion, keyId, desiredScp);
    }

    /**
     * @param secLevel
     * @return
     * @throws CardException
     */
    public ResponseAPDU externalAuthenticate(SecLevel secLevel) throws CardException {
        return this.cmds.externalAuthenticate(secLevel);
    }

    /**
     * @param command
     * @return
     * @throws CardException
     */
    public ResponseAPDU send(CommandAPDU command) throws CardException {
        return this.cmds.send(command);
    }

    /**
     * @param ft
     * @param respMode
     * @param searchQualifier
     * @return
     * @throws CardException
     */
    public ResponseAPDU[] getStatus(FileType ft, GetStatusResponseMode respMode, byte[] searchQualifier) throws CardException {
        return this.cmds.getStatus(ft, respMode, searchQualifier);
    }
}
