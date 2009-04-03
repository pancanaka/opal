/**
 * Contains card configuration informations (instance obtained from
 * CardConfigFactory)
 * 
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
package fr.xlim.ssd.opal.library.params;

import fr.xlim.ssd.opal.library.SCKey;
import fr.xlim.ssd.opal.library.SCPMode;

public class CardConfig {

    /**
     * The Issuer Security Domain (ISD) AID
     */
    private byte[] isd;

    /**
     * The Secure Channel Protocol (SCP) Mode
     */
    private SCPMode scp;

    /**
     * The transmission protocol
     */
    private String tp;

    /**
     * The credentials keys
     */
    private SCKey[] keys;

    /**
     * The implementation class name
     */
    private String impl;

    /**
     * Constructor for CardConfig object
     * @param isd       the issuer security domain AID
     * @param scp       the SCP mode
     * @param tp        the transmission protocol
     * @param keys      the credential keys
     * @param impl      the class name of the implementation
     */
    public CardConfig(byte[] isd, SCPMode scp, String tp, SCKey[] keys, String impl) {
        this.isd = isd;
        this.scp = scp;
        this.tp = tp;
        this.keys = keys;
        this.impl = impl;
    }

    /**
     * Get the name of the default implementation class used by this card
     * configuration
     * @return A string with the class name
     */
    public String getImplementation() {
        return this.impl;
    }

    /**
     * Get the issuer security domain AID used by this card configuration
     * @return An byte array of size 6 with the ISDAID
     */
    public byte[] getIssuerSecurityDomainAID() {
        return this.isd;
    }

    /**
     * Get the secure channel protocol mode used by this card configuration
     * @return  the SCP mode
     */
    public SCPMode getScpMode() {
        return this.scp;
    }

    /**
     * Get the transmission prpotocol used by this card configuration
     * @return  the transmission protocol used
     */
    public String getTransmissionProtocol() {
        return this.tp;
    }

    /**
     * Get the Secure Channel Keys used by this card configuration
     * @return  the default SC keys
     */
    public SCKey[] getSCKeys() {
        return this.keys;
    }

    /**
     * Get the value P1 used by an extra-step (typically from proprietary class
     * like Gemalto)
     *
     * @return  the P1 param to send to initUpdtate Command according to the
     *          KeySetVersion found in configured keys
     */
    public byte getDefaultInitUpdateP1() {
        if (this.keys[0].getSetVersion() == (byte) 255) {
            return 0;
        } else {
            return this.keys[0].getSetVersion();
        }
    }

    /**
     * Get the value P2 used by an extra-step (typically from proprietary class
     * like Gemalto)
     *
     * @return  the P2 param to send to initUpdtate Command according to the
     *          first KeyId found in configured keys
     */
    public byte getDefaultInitUpdateP2() {
        if(this.keys[0].getKeyId() == 1)
            return 0;
        else
            return this.keys[0].getKeyId();
    }
}