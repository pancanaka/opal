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

import fr.xlim.ssd.opal.library.commands.*;
import fr.xlim.ssd.opal.library.config.SCKey;
import fr.xlim.ssd.opal.library.config.SCPMode;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import java.io.IOException;


/**
 * This class contains methods that could be sent to an Applet.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class GPApplet {

    /// File Control Information Defines in the SELECT Command response
    private FileControlInformation fileControlInformation;

    /// Global Platform AID
    protected byte[] aid;

    /// Commands to dialog to the GP Applet
    protected Commands cmds;

    /**
     * Creates the off-card "Applet"
     *
     * @param cmdImplementation the String representation of the chosen implementation (i.e. "fr.xlim.ssd.opal.commands.GP2xCommands") <br/>
     *                          This designed implementation must override the class {@link fr.xlim.ssd.opal.library.commands.Commands}
     * @param cc                the initialized card channel on which data will be sent to the card
     * @param aid               the byte array containing the aid representation of the Applet
     * @throws ClassNotFoundException         cmdImplementation value is a wrong class name
     */
    public GPApplet(Commands implementation, byte[] aid) throws ClassNotFoundException {
        this.aid = aid.clone();
        this.cmds = implementation;
        this.fileControlInformation = null;
    }

    /**
     * Get Card Channel
     *
     * @return Card Channel
     */
    public CardChannel getCc() {
        return this.cmds.getCardChannel();
    }

    /**
     * Get Global Platform AID
     *
     * @return Get Global Platform AID
     */
    public byte[] getAid() {
        return aid.clone();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getScp()
     */
    public SCPMode getScp() {
        return this.cmds.getScp();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getSessState()
     */
    public SessionState getSessState() {
        return this.cmds.getSessState();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getSecMode()
     */
    public SecLevel getSecMode() {
        return this.cmds.getSecMode();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getKeys()
     */
    public SCKey[] getKeys() {
        return this.cmds.getKeys();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getKey()
     */
    public SCKey getKey(byte keySetVersion, byte keyId) {
        return this.cmds.getKey(keySetVersion, keyId);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#setOffCardKey(fr.xlim.ssd.opal.library.SCKey)
     */
    public SCKey setOffCardKey(SCKey key) {
        return this.cmds.setOffCardKey(key);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#setOffCardKeys(fr.xlim.ssd.opal.library.SCKey[])
     */
    public void setOffCardKeys(SCKey[] keys) {
        this.cmds.setOffCardKeys(keys);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#deleteOffCardKey(byte, byte)
     */
    public SCKey deleteOffCardKey(byte keySetVersion, byte keyId) {
        return this.cmds.deleteOffCardKey(keySetVersion, keyId);
    }

    /**
     * Select GP Applet and check card response
     *
     * @return Card APDU response
     * @throws CardException Communication error
     * @throws IOException   Response Select APDU is ill-formed
     */
    public ResponseAPDU select() throws CardException, IOException {
        ResponseAPDU ret = this.cmds.select(this.aid);

        this.fileControlInformation = new FileControlInformation(ret.getData());

        return ret;
    }


    /**
     * Select GP Applet and check card response (Used for SCPMode that implement implecit
     * initiation mode
     * @return Card APDU response
     * @throws CardException Communication error
     * @throws IOException   Response Select APDU is ill-formed
     */
    /*
    public ResponseAPDU select(SCPMode desiredScp) throws CardException, IOException {
        ResponseAPDU ret = this.cmds.select(this.aid,desiredScp);

        this.fileControlInformation = new FileControlInformation(ret.getValue());

        return ret;
    }

     */


    /**
     *
     */
    public void resetParams() {
        this.cmds.resetParams();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#initializeUpdate(byte, byte, fr.xlim.ssd.opal.library.SCPMode)
     */
    public ResponseAPDU initializeUpdate(byte keySetVersion, byte keyId, SCPMode desiredScp) throws CardException {
        return this.cmds.initializeUpdate(keySetVersion, keyId, desiredScp);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#externalAuthenticate(fr.xlim.ssd.opal.library.SecLevel)
     */
    public ResponseAPDU externalAuthenticate(SecLevel secLevel) throws CardException {
        return this.cmds.externalAuthenticate(secLevel);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#transmit(javax.smartcardio.CommandAPDU)
     */
    public ResponseAPDU send(CommandAPDU command) throws CardException {
        return this.cmds.getCardChannel().transmit(command);
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getStatus(fr.xlim.ssd.opal.library.GetStatusFileType, fr.xlim.ssd.opal.library.GetStatusFileType, byte[])
     */
    public ResponseAPDU[] getStatus(GetStatusFileType ft, GetStatusResponseMode respMode, byte[] searchQualifier) throws CardException {
        return this.cmds.getStatus(ft, respMode, searchQualifier);
    }

    /**
     * Get the analysed result of the GP Applet select command.
     *
     * @return Analysed result of the GP Applet select command.
     */
    public FileControlInformation getCardInformation() {
        return this.fileControlInformation;
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#getValue( )
     */
    public ResponseAPDU getData() throws CardException {
        return this.cmds.getData();
    }

    /* (non-Javadoc)
     * @see fr.xlim.ssd.opal.commands.Commands#InitParamForImplicitInitiationMode(byte[])
     */
    public void InitParamForImplicitInitiationMode(SCPMode desiredScp, byte keyId) throws CardException {
        this.cmds.InitParamForImplicitInitiationMode(this.aid, desiredScp, keyId);
    }
}
