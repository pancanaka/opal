package fr.xlim.ssd.opal.library.commands.SCP;

import fr.xlim.ssd.opal.library.config.SCGPKey;

/**
 * Secure Channel Protocol interface
 *
 * @author Guillaume Bouffard
 */
public interface SCP {

    /**
     * Generate session keys depending with SCP protocol used
     *
     * @param staticKenc Static Encrypt key
     * @param staticKmac Static Mac key
     * @param staticKkek Static data encryption key
     */
    public void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek);

    /**
     * Generate mac value according input data
     *
     * @param data data used to generate Mac value
     * @return Mac value calculated
     */
    public byte[] generateMac(byte[] data);

    /**
     * Encrypt APDU command
     *
     * @param command command to encrypt
     * @return encrypted command
     */
    public byte[] encryptCommand(byte[] command);

    /**
     * Decrypt APDU response. This command may be unimplemented
     *
     * @param response response to decrypt
     * @return plain response
     */
    public byte[] decryptCardResponseData(byte[] response);

    /**
     * Calculate Derivation data.
     *
     * @param hostChallenge host challenge used to generate derivation data
     * @param cardChallenge card challenge used to generate derivation data
     */
    public void calculateDerivationData(byte[] hostChallenge, byte[] cardChallenge);

    /**
     * Calculate Cryptogramm
     *
     * @param challenge challenge to calculate
     */
    public void calculateCryptogram(byte[] challenge);


}
