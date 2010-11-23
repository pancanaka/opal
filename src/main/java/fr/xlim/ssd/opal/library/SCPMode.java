package fr.xlim.ssd.opal.library;

/**
 * Define the SCP protocol used.
 *
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 * @author Guillaume Bouffard
 */

public enum SCPMode {

    SCP_UNDEFINED,
    SCP_01_05,
    SCP_01_15,

    /**
     * TODO: Not implemented
     */
    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero,
     * no ICV encryption, 1 Secure Channel base key
     */
    SCP_02_04,
    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero,
     * no ICV encryption, 3 Secure Channel keys
     */
    SCP_02_05,

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID,
     * no ICV encryption, 1 Secure Channel base key
     */
    SCP_02_0A,
    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID,
     * no ICV encryption, 3 Secure Channel keys
     */
    SCP_02_0B,

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero,
     * ICV encryption for C_MAC, 1 Secure Channel base key
     */
    SCP_02_14,
    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero,
     * ICV encryption for C_MAC, 3 Secure Channel keys
     */
    SCP_02_15,

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID,
     * ICV encryption for C_MAC session, 1 Secure Channel base key
     */
    SCP_02_1A,
    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID,
     * ICV encryption for C_MAC session, 3 Secure Channel keys
     */
    SCP_02_1B,

    /**
     * TODO: Not implemented
     */
    SCP_10;
}
