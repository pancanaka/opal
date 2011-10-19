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
package fr.xlim.ssd.opal.library.applet;

import fr.xlim.ssd.opal.library.commands.Commands;

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
     * @param cmdImplementation the String representation of the chosen implementation
     *                          (i.e. "fr.xlim.ssd.opal.commands.GP2xCommands"). This designed implementation must override the class
     *                          {@link fr.xlim.ssd.opal.library.commands.Commands}
     * @param cc                the initialized card channel on which data will be sent to the card
     * @param aid               the byte array containing the aid representation of the Security Domain
     * @throws ClassNotFoundException
     */
    public SecurityDomain(Commands implementation, byte[] aid) throws ClassNotFoundException {
        super(implementation, aid);
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

        byte[] isdAid = securityDomainAID;

        if (isdAid == null) {
            isdAid = this.aid.clone();
        }
        return this.cmds.installForLoad(packageAid, isdAid, params);
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

    /* (non-Javadoc)
    * @see fr.xlim.ssd.opal.library.commands.Commands.sendCommand
    */
    public ResponseAPDU sendCommand(byte[] APDUCommand) throws CardException {
        return this.cmds.sendCommand(APDUCommand);
    }
}
