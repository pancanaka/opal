package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.commands.Commands;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * This class contains methods that could be sent to an Applet.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class GPApplet {

    // TODO: Propagate this class use
    public class FileControlInformation {

        /* 6F                */ // File Control Information (FCI Template)
        protected byte[] allInformations;
        /* |-> 84            */ // Application / File AID
        protected byte[] applicationAID;
        /* |-> A5            */ // Proprietary data
        /* |----> 73         */ // Security Domain Management Data
        /* |------> 66       */ // Tag for 'Card Data'
        /* |--------> 73     */ // Tag for 'Card Data Recognition Data'
        /* |----------> 06   */ // Identifies Global Platform as the Tag Allocation Authority
        protected byte[] GPTagAllocationAuthority;
        /* |----------> 60   */ // Card Management Type and Version
        /* |------------> 06 */
        protected byte[] cardManagementTypeAndVersion;
        /* |----------> 63   */ // Card Identification Scheme
        /* |------------> 06 */
        protected byte[] cardIdentificationScheme;
        /* |----------> 64   */ // Secure Channel Protocol of the selected Security Domain ans its implementation options
        /* |------------> 06 */
        /* --0---------------6-- */
        protected byte[] SCPInformation; /*  | SCP Information |  */
        /* --------------------- */

        /* -------7---------     */
        protected byte SCPVersion;       /*  | SCP Version |      */
        /* -----------------     */

        /* -------8-----          */
        protected byte SCPMode;         /*  | SCP Mode |          */
        /* -------------          */
        /* |----------> 65   */ // Card Configuration details
        protected byte[] cardConfiguration;
        /* |----------> 66   */ // Card / Chip details
        protected byte[] cardDetails;
        /* |----> 9F 6E      */ // Application production lice cycle data
        protected byte[] applicationProductionLifeCycleData;
        /* |----> 9F 65      */ // Maximum length of data field in command message
        protected byte[] maximumLengthOfDataFieldInCommandMessage;

        public static final byte SIZE_TL = (byte) 0x02;

        public void setSCPInformation(byte[] info) {
            this.SCPInformation = new byte[info.length - 2];
            System.arraycopy(info, 0, getSCPInformation(), 0, info.length - 2);
            this.SCPVersion = info[info.length - 2];
            this.SCPMode = info[info.length - 1];
        }

        public byte[] getAllInformations() {
            return allInformations;
        }

        /**
         * @return the applicationAID
         */
        public byte[] getApplicationAID() {
            return applicationAID;
        }

        /**
         * @return the GPTagAllocationAuthority
         */
        public byte[] getGPTagAllocationAuthority() {
            return GPTagAllocationAuthority;
        }

        /**
         * @return the cardManagementTypeAndVersion
         */
        public byte[] getCardManagementTypeAndVersion() {
            return cardManagementTypeAndVersion;
        }

        /**
         * @return the cardIdentificationScheme
         */
        public byte[] getCardIdentificationScheme() {
            return cardIdentificationScheme;
        }

        /**
         * @return the SCPInformation
         */
        public byte[] getSCPInformation() {
            return SCPInformation;
        }

        /**
         * @return the SCPVersion
         */
        public byte getSCPVersion() {
            return SCPVersion;
        }

        /**
         * @return the SCPMode
         */
        public byte getSCPMode() {
            return SCPMode;
        }

        /**
         * @return the cardConfiguration
         */
        public byte[] getCardConfiguration() {
            return cardConfiguration;
        }

        /**
         * @return the cardDetails
         */
        public byte[] getCardDetails() {
            return cardDetails;
        }

        /**
         * @return the applicationProductionLifeCycleData
         */
        public byte[] getApplicationProductionLifeCycleData() {
            return applicationProductionLifeCycleData;
        }

        /**
         * @return the maximumLengthOfDataFieldInCommandMessage
         */
        public byte[] getMaximumLengthOfDataFieldInCommandMessage() {
            return maximumLengthOfDataFieldInCommandMessage;
        }

    }

    FileControlInformation fileControlInformation;

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
     *
     * @param CmdImplementation the String representation of the chosen implementation (i.e. "fr.xlim.ssd.opal.commands.GP2xCommands") <br/>
     *                          This designed implementation must override the class {@link fr.xlim.ssd.opal.library.commands.Commands}
     * @param cc                the initialized card channel on which data will be sent to the card
     * @param aid               the byte array containing the aid representation of the Applet
     * @throws CommandsImplementationNotFound
     * @throws ClassNotFoundException
     */
    public GPApplet(String CmdImplementation, CardChannel cc, byte[] aid) throws CommandsImplementationNotFound, ClassNotFoundException {
        this.aid = aid.clone();
        Class.forName(CmdImplementation);
        this.cmds = CommandsProvider.getImplementation(CmdImplementation, cc);
        this.fileControlInformation = null;
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
    public SCKey deleteOffCardKey(byte keySetVersion, byte keyId) {
        return this.cmds.deleteOffCardKey(keySetVersion, keyId);
    }

    /**
     * @return
     * @throws CardException
     */
    public ResponseAPDU select() throws CardException {
        ResponseAPDU ret = this.cmds.select(this.aid);
        this.checkSelectReturn(ret.getData());
        return ret;
    }

    protected void checkSelectReturn(byte[] data) throws CardException {
        if (data[0] != (byte) 0x6F) {
            return;
        }

        this.fileControlInformation = new FileControlInformation();

        for (int pos = 2; pos < data.length; pos += FileControlInformation.SIZE_TL) {
            if (data[pos] == (byte) 0x84) { // Application / File AID
                this.fileControlInformation.applicationAID = this.readTLV(data, pos);
                pos += this.fileControlInformation.getApplicationAID().length;

            } else if (data[pos] == (byte) 0xA5) { // Proprietary data

                byte[] proprietaryData = this.readTLV(data, pos);
                for (int j = 0; j < proprietaryData.length; j += FileControlInformation.SIZE_TL) {
                    if (proprietaryData[j] == (byte) 0x73) { // Security Domain Management Data
                        byte[] securityDomain = readTLV(proprietaryData, j);
                        for (int i = j; i < securityDomain.length; i += FileControlInformation.SIZE_TL) {

                            switch (securityDomain[i]) {
                                case (byte) 0x06: // Tag Allocation Authority
                                    this.fileControlInformation.GPTagAllocationAuthority = this.readTLV(securityDomain, i);
                                    i += this.fileControlInformation.getGPTagAllocationAuthority().length;
                                    break;

                                case (byte) 0x60: // Card Manager Type and Version
                                    if (securityDomain[i + FileControlInformation.SIZE_TL] == (byte) 0x06) {
                                        this.fileControlInformation.cardManagementTypeAndVersion = this.readTLV(securityDomain, i + FileControlInformation.SIZE_TL);
                                        i += this.fileControlInformation.getCardManagementTypeAndVersion().length + FileControlInformation.SIZE_TL;
                                    } else {
                                        this.resetParams();
                                        throw new CardException("Unknow Select Return Tag value");
                                    }
                                    break;

                                case (byte) 0x63: // Card Identification Scheme
                                    if (securityDomain[i + FileControlInformation.SIZE_TL] == (byte) 0x06) {
                                        this.fileControlInformation.cardIdentificationScheme = this.readTLV(securityDomain, i + FileControlInformation.SIZE_TL);
                                        i += this.fileControlInformation.getCardIdentificationScheme().length + FileControlInformation.SIZE_TL;
                                    } else {
                                        this.resetParams();
                                        throw new CardException("Unknow tag value");
                                    }
                                    break;

                                case (byte) 0x64: // Security Channel Domain and its implementation options
                                    if (securityDomain[i + FileControlInformation.SIZE_TL] == (byte) 0x06) {
                                        byte[] info = this.readTLV(securityDomain, i + FileControlInformation.SIZE_TL);
                                        i += info.length + FileControlInformation.SIZE_TL;
                                        this.fileControlInformation.setSCPInformation(info);
                                    } else {
                                        throw new CardException("Unknow tag value");
                                    }
                                    break;

                                case (byte) 0x65: // Card Configuration Details
                                    this.fileControlInformation.cardConfiguration = this.readTLV(securityDomain, i);
                                    i += this.fileControlInformation.getCardConfiguration().length;
                                    break;

                                case (byte) 0x66: // Card / Chips details
                                    this.fileControlInformation.cardDetails = this.readTLV(securityDomain, i);
                                    i += this.fileControlInformation.getCardDetails().length;
                                    break;
                            }
                        }

                        j += securityDomain.length + FileControlInformation.SIZE_TL;


                    } else if ((proprietaryData[j] == (byte) 0x9F)
                            && (proprietaryData[j + 1] == (byte) 0x6E)) { // Application production lice cycle data

                        this.fileControlInformation.applicationProductionLifeCycleData = this.readTLV(proprietaryData, j + 1);
                        j += this.fileControlInformation.getApplicationProductionLifeCycleData().length + 1;

                    } else if ((proprietaryData[j] == (byte) 0x9F)
                            && (proprietaryData[j + 1] == (byte) 0x65)) { // Maximum Length Of Data Field In Command Message

                        this.fileControlInformation.maximumLengthOfDataFieldInCommandMessage = this.readTLV(proprietaryData, j + 1);
                        j += this.fileControlInformation.getMaximumLengthOfDataFieldInCommandMessage().length + 1;

                    }
                }

                pos += proprietaryData.length + FileControlInformation.SIZE_TL;

            }
        }
    }

    protected byte[] readTLV(byte[] data, int begin) {

        /*
         * ------------------------
         * | Tag | Length | Value |
         * ------------------------
         */

        byte[] value = null;
        byte length;

        if (begin >= data.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        length = data[begin + 1];
        value = new byte[length];

        System.arraycopy(data, begin + 2, value, 0, length);

        return value;
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
        return this.cmds.getCc().transmit(command);
    }

    /**
     * @param ft
     * @param respMode
     * @param searchQualifier
     * @return
     * @throws CardException
     */
    public ResponseAPDU[] getStatus(GetStatusFileType ft, GetStatusResponseMode respMode, byte[] searchQualifier) throws CardException {
        return this.cmds.getStatus(ft, respMode, searchQualifier);
    }

    public FileControlInformation getCardInformation() {
        return this.fileControlInformation;
    }
}
