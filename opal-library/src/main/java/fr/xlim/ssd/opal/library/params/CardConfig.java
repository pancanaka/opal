package fr.xlim.ssd.opal.library.params;

import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;

/**
 * Contains card configuration information (instance obtained from CardConfigFactory)
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @see CardConfigFactory
 */
public class CardConfig {

    /// The configuration name
    private String name;

    /// The configuration description
    private String description;

    /// The ATR list linked to this card configuration
    private ATR[] atrs;

    /// The Issuer Security Domain (ISD) AID
    private byte[] isd;

    /// The Secure Channel Protocol (SCP) Mode
    private SCPMode scp;

    /// The transmission protocol
    private String tp;

    /// The credentials keys
    private SCKey[] keys;

    /// The implementation class name
    private String impl;

    /**
     * Constructor for CardConfig object
     *
     * @param name        the card configuration name
     * @param description the card configuration description
     * @param atrs        the card ATR linked to this configuration
     * @param isd         the issuer security domain AID
     * @param scp         the SCP mode
     * @param tp          the transmission protocol
     * @param keys        the credential keys
     * @param impl        the class name of the implementation
     */
    public CardConfig(String name,
                      String description,
                      ATR[] atrs,
                      byte[] isd,
                      SCPMode scp,
                      String tp,
                      SCKey[] keys,
                      String impl) {

        if (name == null) {
            throw new IllegalArgumentException("name must be not null");
        }

        // description may be null
        // ATR may be null

        if (isd == null) {
            throw new IllegalArgumentException("isd must be not null");
        }

        if (scp == null) {
            throw new IllegalArgumentException("scp must be not null");
        }

        if (tp == null) {
            throw new IllegalArgumentException("tp must be not null");
        }

        if (keys == null) {
            throw new IllegalArgumentException("keys must be not null");
        }

        if (impl == null) {
            throw new IllegalArgumentException("impl must be not null");
        }

        this.name = name;
        this.description = description;
        this.atrs = atrs;
        this.isd = isd;
        this.scp = scp;
        this.tp = tp;
        this.keys = keys;
        this.impl = impl;
    }

    /**
     * Get the name used in the configuration file
     *
     * @return A string with the configuration name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description used in the configuration file
     *
     * @return A string with the configuration description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the card ATRs linked to this configuration
     *
     * @return An ATR array which contains linked card ATR
     */
    public ATR[] getAtrs() {
        return atrs;
    }

    /**
     * Get the name of the default implementation class used by this card configuration
     *
     * @return A string with the class name
     */
    public String getImplementation() {
        return this.impl;
    }

    /**
     * Get the issuer security domain AID used by this card configuration
     *
     * @return An byte array with the ISD AID
     */
    public byte[] getIssuerSecurityDomainAID() {
        return this.isd;
    }

    /**
     * Get the secure channel protocol mode used by this card configuration
     *
     * @return the SCP mode
     */
    public SCPMode getScpMode() {
        return this.scp;
    }

    /**
     * Get the transmission protocol used by this card configuration
     *
     * @return the transmission protocol used
     */
    public String getTransmissionProtocol() {
        return this.tp;
    }

    /**
     * Get the Secure Channel Keys used by this card configuration
     *
     * @return the default SC keys
     */
    public SCKey[] getSCKeys() {
        return this.keys;
    }

    /**
     * Get the value P1 used by an extra-step (typically from proprietary class like Gemalto)
     *
     * @return the P1 param to send to initUpdtate Command according to the KeySetVersion found in configured keys
     */
    public byte getDefaultInitUpdateP1() {
        if (this.keys[0].getSetVersion() == (byte) 255) {
            return 0;
        } else {
            return this.keys[0].getSetVersion();
        }
    }

    /**
     * Get the value P2 used by an extra-step (typically from proprietary class like Gemalto)
     *
     * @return the P2 param to send to initUpdtate Command according to the first KeyId found in configured keys
     */
    public byte getDefaultInitUpdateP2() {
        if (this.keys[0].getId() == 1) {
            return 0;
        } else {
            return this.keys[0].getId();
        }

    }

    /**
     * Set the name used in the configuration file
     *
     * @param name A string with the new configuration name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the description used in the configuration file
     *
     * @param description A string with the new configuration description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the card ATRs linked to this configuration
     *
     * @param atrs An ATR array which contains linked card ATR
     */
    public void setAtrs(ATR[] atrs) {
        this.atrs = atrs;
    }

    /**
     * Set the issuer security domain AID used by this card configuration
     *
     * @param isd An byte array with the ISD AID
     */
    public void setIsd(byte[] isd) {
        this.isd = isd;
    }

    /**
     * Set the secure channel protocol mode used by this card configuration
     *
     * @param scp the SCP mode
     */
    public void setScp(SCPMode scp) {
        this.scp = scp;
    }

    /**
     * Set the transmission protocol used by this card configuration
     *
     * @param tp the transmission protocol used
     */
    public void setTp(String tp) {
        this.tp = tp;
    }

    /**
     * Set the Secure Channel Keys used by this card configuration
     *
     * @param keys the new default SC keys
     */
    public void setKeys(SCKey[] keys) {
        this.keys = keys;
    }

    /**
     * Set the name of the default implementation class used by this card configuration
     *
     * @param impl A string with the class name
     */
    public void setImpl(String impl) {
        this.impl = impl;
    }

}
