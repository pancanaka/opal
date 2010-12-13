package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;

/**
 * This class contains methods that could be sent to a Security Domain.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class SecurityDomain extends GPApplet {

    /**
     * Creates the off-card "Security Domain"
     *
     * @param CmdImplementation the String representation of the chosen implementation
     *                          (i.e. "fr.xlim.ssd.opal.commands.GP2xCommands"). This designed implementation must override the class
     *                          {@link fr.xlim.ssd.opal.library.commands.Commands}
     * @param cc                the initialized card channel on which data will be sent to the card
     * @param aid               the byte array containing the aid representation of the Security Domain
     * @throws CommandsImplementationNotFound
     * @throws ClassNotFoundException
     */
    public SecurityDomain(String CmdImplementation, CardChannel cc, byte[] aid) throws CommandsImplementationNotFound, ClassNotFoundException {
        super(CmdImplementation, cc, aid);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands.deleteOnCardObj
     */
    public ResponseAPDU deleteOnCardObj(byte[] aid, boolean cascade) throws CardException {
        return this.cmds.deleteOnCardObj(aid, cascade);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands.installForLoad
     */
    public ResponseAPDU installForLoad(byte[] packageAid, byte[] securityDomainAID, byte[] params) throws CardException {
        if (securityDomainAID == null) {
            securityDomainAID = this.aid.clone();
        }
        return this.cmds.installForLoad(packageAid, securityDomainAID, params);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands.load
     */
    public ResponseAPDU[] load(byte[] capFile) throws CardException {
        return this.cmds.load(capFile, (byte) 0xF0);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands.load
     */
    public ResponseAPDU[] load(byte[] capFile, byte maxDataLength) throws CardException {
        return this.cmds.load(capFile, maxDataLength);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.library.commands.Commands.installForInstallAndMakeSelectable
     */
    public ResponseAPDU installForInstallAndMakeSelectable(byte[] loadFileAID, byte[] moduleAID, byte[] applicationAID, byte[] privileges, byte[] params) throws CardException {
        return this.cmds.installForInstallAndMakeSelectable(loadFileAID, moduleAID, applicationAID, privileges, params);
    }
}
