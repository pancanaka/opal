package fr.xlim.ssd.opal.library;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;

import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;

/**
 * This class contains methods that could be sent to a Security Domain. <br/>
 * 
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class SecurityDomain extends GPApplet {

	/**
	 * Creates the off-card "Security Domain"
	 * @param CmdImplementation the String representation of the chosen implementation (i.e. "fr.xlim.ssd.opal.commands.GP2xCommands") <br/>
	 * This designed implementation must override the class {@link fr.xlim.ssd.opal.commands.Commands}
	 * @param cc the initialized card channel on which data will be sent to the card
	 * @param aid the byte array containing the aid representation of the Security Domain
	 * @throws CommandsImplementationNotFound
	 * @throws ClassNotFoundException
	 */
    public SecurityDomain(String CmdImplementation, CardChannel cc, byte[] aid) throws CommandsImplementationNotFound, ClassNotFoundException {
        super(CmdImplementation, cc, aid);
    }

    /**
     * @param aid
     * @param cascade
     * @return
     * @throws CardException
     */
    public ResponseAPDU deleteOnCardObj(byte[] aid, boolean cascade) throws CardException {
        return this.cmds.deleteOnCardObj(aid, cascade);
    }

    /**
     * @param packageAid
     * @param securityDomainAID
     * @param params
     * @return
     * @throws CardException
     */
    public ResponseAPDU installForLoad(byte[] packageAid, byte[] securityDomainAID, byte[] params) throws CardException {
        if (securityDomainAID == null) {
            securityDomainAID = this.aid.clone();
        }
        return this.cmds.installForLoad(packageAid, securityDomainAID, params);
    }

    /**
     * @param capFile
     * @return
     * @throws CardException
     */
    public ResponseAPDU[] load(byte[] capFile) throws CardException {
        return this.cmds.load(capFile, (byte) 0xF0);
    }

    /**
     * @param capFile
     * @param maxDataLength
     * @return
     * @throws CardException
     */
    public ResponseAPDU[] load(byte[] capFile, byte maxDataLength) throws CardException {
        return this.cmds.load(capFile, maxDataLength);
    }

    /**
     * @param loadFileAID
     * @param moduleAID
     * @param applicationAID
     * @param privileges
     * @param params
     * @return
     * @throws CardException
     */
    public ResponseAPDU installForInstallAndMakeSelectable(byte[] loadFileAID, byte[] moduleAID, byte[] applicationAID, byte[] privileges, byte[] params) throws CardException {
        return this.cmds.installForInstallAndMakeSelectable(loadFileAID, moduleAID, applicationAID, privileges, params);
    }
}