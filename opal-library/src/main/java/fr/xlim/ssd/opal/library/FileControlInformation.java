package fr.xlim.ssd.opal.library;

import fr.xlim.ssd.opal.library.utilities.TLV;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Deep analysing of the SELECT Command Respond. This class try respect the Global Platform specification defined by the
 * sections 11.9.3 and H.3 of Global Platform 2.2
 * <p/>
 * TODO: Propagate this class use
 *
 * @author Guillaume Bouffard
 */
public class FileControlInformation {

    /**
     * ******************************************************************************************************************
     */
    /* 6F                */
    /// File Control Information (FCI Template)
    private TLV allInformation;
    /* |-> 84            */
    /// Application / File AID
    private TLV applicationAID;
    /* |-> A5            */
    /// Proprietary data
    private TLV proprietaryData;
    /* |----> 73         */
    /// Security Domain Management Data
    private TLV securityDomainManagementData;
    /* |------> 06   */
    /// Identifies Global Platform as the Tag Allocation Authority
    private TLV gpTagAllocationAuthority;
    /* |------> 60   */ // Card Management Type and Version
    /* |--------> 06 */
    private TLV cardManagementTypeAndVersion;
    /* |------> 63   */
    /* |--------> 06 */
    /// Card Identification Scheme
    private TLV cardIdentificationScheme;
    /* |------> 64   */
    /* |--------> 06 */
    /// Secure Channel Protocol of the selected Security Domain ans its implementation options
    private TLV scpConfiguration;
    /* --0---------------6-- */
    private byte[] scpImplementation; /*  | SCP Information |  */
    /* --------------------- */
    /* -------7---------     */
    private byte scpVersion;          /*  | SCP Version |      */
    /* -----------------     */
    /* -------8-----         */
    private byte scpMode;             /*  | SCP Mode |         */
    /* -------------         */
    /* |------> 65   */
    /// Card Configuration details
    private TLV cardConfiguration;
    /* |------> 66   */
    /// Card / Chip details
    private TLV cardDetails;
    /* |------> 67   */
    /// Security Domain's Trust Point Certificate Information
    private TLV securityDomainsTrustPointCertificateInformation;
    /* |------> 68   */
    /// Security Domain Certificate Information
    private TLV securityDomainCertificateInformation;
    /* |----> 9F 6E      */
    /// Application production lice cycle data
    private TLV applicationProductionLifeCycleData;
    /* |----> 9F 65      */
    /// Maximum length of data field in command message
    private TLV maximumLengthOfDataFieldInCommandMessage;
/**********************************************************************************************************************/

    /**
     * Class Constructor. We analyse and store a Global Platform Applet SELECT Command Response.
     *
     * @param data Global Platform Applet SELECT Command Response
     * @throws IOException Incorrect Global Platform Applet SELECT Command Response
     */
    public FileControlInformation(byte[] data) throws IOException {
        this.setAllInformation(data);
    }

    /**
     * Get All information, in TLV form, of the Select APDU Response
     *
     * @return All information, in TLV form
     */
    public byte[] getAllInformation() {
        return allInformation.getValue();
    }

    /**
     * Get Application AID
     *
     * @return the Application AID
     */
    public byte[] getApplicationAID() {
        return applicationAID.getValue();
    }

    /**
     * Get Global Platform Allocation Authority
     *
     * @return the Global Platform Allocation Authority
     */
    public byte[] getGpTagAllocationAuthority() {
        return gpTagAllocationAuthority.getValue();
    }

    /**
     * Get Card Management Type And Version
     *
     * @return the Card Management Type And Version
     */
    public byte[] getCardManagementTypeAndVersion() {
        return cardManagementTypeAndVersion.getValue();
    }

    /**
     * Get Card Identification Scheme
     *
     * @return the Card Identification Scheme
     */
    public byte[] getCardIdentificationScheme() {
        return cardIdentificationScheme.getValue();
    }

    /**
     * Get Secure Channel Protocol Information
     *
     * @return the SCP Information
     */
    public byte[] getScpImplementation() {
        return scpImplementation;
    }

    /**
     * Get Secure Channel Protocol Version
     *
     * @return the SCP Version
     */
    public byte getScpVersion() {
        return scpVersion;
    }

    /**
     * Get Secure Channel Protocol Mode
     *
     * @return the SCP Mode
     */
    public byte getScpMode() {
        return scpMode;
    }

    /**
     * Get Card Configuration
     *
     * @return the Card Configuration
     */
    public byte[] getCardConfiguration() {
        return cardConfiguration.getValue();
    }

    /**
     * Get Card Details
     *
     * @return the Card Details
     */
    public byte[] getCardDetails() {
        return cardDetails.getValue();
    }

    /**
     * Get Application Production Life Cycle Data
     *
     * @return the applicationProductionLifeCycleData
     */
    public byte[] getApplicationProductionLifeCycleData() {
        return applicationProductionLifeCycleData.getValue();
    }

    /**
     * Get Maximum Length Of Data Field In Command Message
     *
     * @return the maximumLengthOfDataFieldInCommandMessage
     */
    public byte[] getMaximumLengthOfDataFieldInCommandMessage() {
        return maximumLengthOfDataFieldInCommandMessage.getValue();
    }

    /**
     * Set All Value of File Control Information
     *
     * @param allInformation All Value of File Control Information
     * @throws IOException Incorrect All Value of File Control Information
     */
    public void setAllInformation(byte[] allInformation) throws IOException {
        this.allInformation = new TLV(allInformation);

        if (this.allInformation.getTag() != (byte) 0x6F) {
            throw new IOException("Invalid Select Response Tag (0x" + Integer.toHexString(this.allInformation.getTag()));
        }

        ByteBuffer data = ByteBuffer.wrap(this.allInformation.getValue()).slice().asReadOnlyBuffer();

        while (data.hasRemaining()) {

            int tag = data.get();
            byte length = data.get();
            byte[] value = new byte[length];

            data.get(value, 0, length);

            switch ((byte) (tag & 0x00FF)) {
                case (byte) 0x84:
                    this.setApplicationAID(value);
                    break;
                case (byte) 0xA5:
                    this.setProprietaryData(value);
                    break;
                default:
                    throw new IOException("Tag value is incorrect (0x" + Integer.toHexString(tag & 0xFF) + ")");
            }

            //logger.debug("Remained Data " + data.toString());
        }
    }

    /**
     * Get Proprietary Data
     *
     * @return Proprietary Data value
     */
    public TLV getProprietaryData() {
        return proprietaryData;
    }

    /**
     * Get Security Domain's Trust Point Certificate Information
     *
     * @return Security Domain's Trust Point Certificate Information value
     */
    public byte[] getSecurityDomainsTrustPointCertificateInformation() {
        return securityDomainsTrustPointCertificateInformation.getValue();
    }

    /**
     * Set Security Domain's Trust Point Certificate Information
     *
     * @param securityDomainsTrustPointCertificateInformation
     *         New Security Domain's Trust Point Certificate Information
     * @throws IOException Incorrect Security Domain's Trust Point Certificate Information
     */
    public void setSecurityDomainsTrustPointCertificateInformation(byte[] securityDomainsTrustPointCertificateInformation) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.securityDomainsTrustPointCertificateInformation = new TLV((byte) 0x67, securityDomainsTrustPointCertificateInformation);
    }

    /**
     * Get Security Domain Certificate Information
     *
     * @return Security Domain Certificate Information value
     */
    public byte[] getSecurityDomainCertificateInformation() {
        return securityDomainCertificateInformation.getValue();
    }

    /**
     * Set Security Domain Certificate Information
     *
     * @param securityDomainCertificateInformation
     *         New Security Domain Certificate Information value
     * @throws IOException Incorrect Security Domain Certificate Information value
     */
    public void setSecurityDomainCertificateInformation(byte[] securityDomainCertificateInformation) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.securityDomainCertificateInformation = new TLV((byte) 0x68, securityDomainCertificateInformation);
    }

    /**
     * Set Proprietary Data
     *
     * @param proprietaryData New Proprietary Data
     * @throws IOException Incorrect Proprietary Data
     */
    public void setProprietaryData(byte[] proprietaryData) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.proprietaryData = new TLV((byte) 0xA5, proprietaryData);

        ByteBuffer data = ByteBuffer.wrap(proprietaryData).slice().asReadOnlyBuffer();

        while (data.hasRemaining()) {

            int tag = data.get() & 0xFF;
            byte length;
            byte[] value;

            switch (tag) {
                case 0x0073:
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);
                    this.setSecurityDomainManagementData(value);
                    break;
                case 0x009F:
                    tag = data.get() & 0xFF;
                    if (tag == 0x006E) {
                        length = data.get();
                        value = new byte[length];
                        data.get(value, 0, length);
                        this.setApplicationProductionLifeCycleData(value);
                    } else if (tag == 0x0065) {
                        length = data.get();
                        value = new byte[length];
                        data.get(value, 0, length);
                        this.setMaximumLengthOfDataFieldInCommandMessage(value);
                    } else {
                        throw new IOException("Invalid tag after 0x9F (0x" + Integer.toHexString(tag) + ")");
                    }
                    break;
                default:
                    throw new IOException("Invalid tag (0x" + Integer.toHexString(tag) + ")");
            }
        }

    }

    /**
     * Get The Security Domain Management Data value
     *
     * @return Security Domain Management Data value
     */
    public byte[] getSecurityDomainManagementData() {
        return securityDomainManagementData.getValue();
    }

    /**
     * Set Security Domain Management Data
     *
     * @param securityDomainManagementData New Security Domain Management Data
     * @throws IOException Incorrect Security Domain Management Data value
     */
    public void setSecurityDomainManagementData(byte[] securityDomainManagementData) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.securityDomainManagementData = new TLV((byte) 0x73, securityDomainManagementData);

        ByteBuffer data = ByteBuffer.wrap(securityDomainManagementData).slice().asReadOnlyBuffer();

        while (data.hasRemaining()) {

            int tag = data.get() & 0xFF;
            byte length;
            byte[] value;

            switch (tag) {

                case 0x0006:
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);

                    this.setGpTagAllocationAuthority(value);
                    break;

                case 0x0060:
                    data.position(data.position() + 1); // jump 0x60 length
                    tag = data.get() & 0xFF;
                    if (tag != 0x06) {
                        throw new IOException("Invalid tag after 0x60 (0x" + Integer.toHexString(tag) + ")");
                    }
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);
                    this.setCardManagementTypeAndVersion(value);
                    break;

                case 0x0063:
                    data.position(data.position() + 1); // jump 0x63 length
                    tag = data.get() & 0xFF;
                    if (tag != 0x06) {
                        throw new IOException("Invalid tag after 0x63 (0x" + Integer.toHexString(tag) + ")");
                    }
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);
                    this.setCardIdentificationScheme(value);
                    break;

                case 0x0064:
                    data.position(data.position() + 1); // jump 0x64 length
                    tag = data.get() & 0xFF;
                    if (tag != 0x06) {
                        throw new IOException("Invalid tag after 0x64 (0x" + Integer.toHexString(tag) + ")");
                    }
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);
                    this.setScpConfiguration(value);
                    break;

                case 0x0065:
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);

                    this.setCardConfiguration(value);
                    break;

                case 0x0066:
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);

                    this.setCardDetails(value);
                    break;

                case 0x0067:
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);

                    this.setSecurityDomainsTrustPointCertificateInformation(value);
                    break;

                case 0x0068:
                    length = data.get();
                    value = new byte[length];
                    data.get(value, 0, length);

                    this.setSecurityDomainCertificateInformation(value);
                    break;

                default:
                    throw new IOException("Invalid Tag value in the proprietary data (0x" + Integer.toHexString(tag) + ")");
            }
        }
    }

    /**
     * Get SCP Implementation value
     *
     * @return SCP Implementation value
     */
    public byte[] getScpConfiguration() {
        return scpConfiguration.getValue();
    }

    /**
     * Set SCP Implementation value
     *
     * @param scpConfiguration Set SCP Implementation value
     * @throws IOException Incorrect SCP Implementation value
     */
    public void setScpConfiguration(byte[] scpConfiguration) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.scpConfiguration = new TLV((byte) 0x64, scpConfiguration);

        ByteBuffer buffer = ByteBuffer.wrap(scpConfiguration);

        this.scpImplementation = new byte[this.scpConfiguration.getLength() - 2];
        buffer.get(this.scpImplementation, 0, this.scpImplementation.length);
        this.setScpVersion(buffer.get());
        this.setScpMode(buffer.get());
    }

    /**
     * Set Application AID
     *
     * @param applicationAID New Application AID value
     * @throws IOException Incorrect Data
     */
    public void setApplicationAID(byte[] applicationAID) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.applicationAID = new TLV((byte) 0x84, applicationAID);
    }

    /**
     * Set Global Platform Allocation Authority value
     *
     * @param gpTagAllocationAuthority new Global Platform Allocation Authority value
     * @throws IOException Incorrect Global Platform Allocation Authority value
     */
    public void setGpTagAllocationAuthority(byte[] gpTagAllocationAuthority) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.gpTagAllocationAuthority = new TLV((byte) 0x06, gpTagAllocationAuthority);
    }

    /**
     * Set Card Management Type And Version value
     *
     * @param cardManagementTypeAndVersion New Card Management Type And Version value
     * @throws IOException Incorrect Card Management Type And Version value
     */
    public void setCardManagementTypeAndVersion(byte[] cardManagementTypeAndVersion) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.cardManagementTypeAndVersion = new TLV((byte) 0x60, cardManagementTypeAndVersion);
    }

    public void setCardIdentificationScheme(byte[] cardIdentificationScheme) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.cardIdentificationScheme = new TLV((byte) 0x63, cardIdentificationScheme);
    }

    /**
     * Set SCP Version used for communication with the Smart card
     *
     * @param scpVersion new SCP Version used for communication with the Smart card
     */
    public void setScpVersion(byte scpVersion) {
        this.scpVersion = scpVersion;
    }

    /**
     * Set SCP Mode used for communication with the Smart card
     *
     * @param scpMode New SCP mode used for communication with the Smart card
     */
    public void setScpMode(byte scpMode) {
        this.scpMode = scpMode;
    }

    /**
     * Set Card Configuration value
     *
     * @param cardConfiguration new Card Configuration value
     * @throws IOException Incorrect Card Configuration value
     */
    public void setCardConfiguration(byte[] cardConfiguration) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.cardConfiguration = new TLV((byte) 0x65, cardConfiguration);
    }

    /**
     * Set Card Details
     *
     * @param cardDetails New Card Details value
     * @throws IOException Incorrect Card Details
     */
    public void setCardDetails(byte[] cardDetails) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.cardDetails = new TLV((byte) 0x66, cardDetails);
    }

    /**
     * Set Application Production Life Cycle Data value
     *
     * @param applicationProductionLifeCycleData
     *         New Application Production Life Cycle Data value
     * @throws IOException Incorrect application Production Life Cycle Data value
     */
    public void setApplicationProductionLifeCycleData(byte[] applicationProductionLifeCycleData) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.applicationProductionLifeCycleData = new TLV((byte) 0x6E, applicationProductionLifeCycleData);
    }

    /**
     * Set Maximum Length Of Data Field In Command Message value
     *
     * @param maximumLengthOfDataFieldInCommandMessage
     *         New Maximum Length Of Data Field In Command Message value
     * @throws IOException Incorrect Maximum Length Of Data Field In Command Message value
     */
    public void setMaximumLengthOfDataFieldInCommandMessage(byte[] maximumLengthOfDataFieldInCommandMessage) throws IOException {
        // TODO: if this function is called by a other method than FileControlInformation constructor, you should modify each other TLV values
        this.maximumLengthOfDataFieldInCommandMessage = new TLV((byte) 0x65, maximumLengthOfDataFieldInCommandMessage);
    }
}
