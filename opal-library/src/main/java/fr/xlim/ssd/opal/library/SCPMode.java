package fr.xlim.ssd.opal.library;

/**
 * Define the SCP protocol used. Please see the description Secure Channel Protocols 01 and 02 in Section D and E,
 * global Platform Card Specification 2.1.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @author Guillaume Bouffard
 */

public enum SCPMode {

    /// used when SCP used by the card is not know (before exchanges with the card)
    SCP_UNDEFINED,

    /// Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, no ICV encryption, 3 Secure Channel Keys
    SCP_01_05,

    /// Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, ICV encryption, 3 Secure Channel Keys
    SCP_01_15,

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero, no ICV encryption, 1 Secure Channel base key
     *
     * TODO: implemented, Not tested
     */
    SCP_02_04,

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero, no ICV encryption, 3 Secure Channel keys
     *
     * TODO: implemented, Not tested
     */
    SCP_02_05,

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID, no ICV encryption, 1 Secure Channel base key
     *
     * TODO: implemented, Not tested
     */
    SCP_02_0A,

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID, no ICV encryption, 3 Secure Channel keys
     *
     * TODO: implemented, Not tested
     */
    SCP_02_0B,

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero, ICV encryption for C_MAC, 1 Secure Channel base key
     *
     * TODO: implemented, Not tested
     */
    SCP_02_14,

    /*
     * Explicit mode, C_MAC on modified APDU, ICV set to zero, ICV encryption for C_MAC, 3 Secure Channel keys
     *)
     */
    SCP_02_15,

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID, ICV encryption for C_MAC session, 1 Secure
     * Channel base key
     *
     * TODO: implemented, Not tested
     */
    SCP_02_1A,

    /*
     * Implicit mode, C_MAC on unmodified APDU, ICV set to MAC over AID, ICV encryption for C_MAC session, 3 Secure
     * Channel keys
     *
     * TODO: implemented, Not tested
     */
    SCP_02_1B,

    /*
     * Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, ICV encryption for C-MAC session, 3 Secure
     * Channel Keys, well-known pseudo-random algorithm (card challenge)
     *
     * TODO: implemented, Not tested
     */
    SCP_02_55,

    /*
     * Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, no ICV encryption, 3 Secure
     * Channel Keys, well-known pseudo-random algorithm (card challenge)
     *
     * TODO: implemented, Not tested
     */
    SCP_02_45,


    /*
     * Initiation mode explicit, C-MAC on modified APDU, ICV set to zero, no ICV encryption, 1 Secure
     * Channel Keys, well-known pseudo-random algorithm (card challenge)
     *
     * TODO: implemented, Not tested
     */
    SCP_02_54,

    /// TODO: Not implemented
    SCP_10,

    /*
     *
     * TODO: implemented, Not tested
     */
    SCP_03_65,

    /*
    *
    * TODO: implemented, Not tested
    */
    SCP_03_6D,

    /*
    *
    * TODO: implemented, Not tested
    */
    SCP_03_05,

    /*
    *
    * TODO: implemented, Not tested
    */
    SCP_03_0D,

    /*
    *
    * TODO: implemented, Not tested
    */
    SCP_03_2D,

    /*
    *
    * TODO: implemented, Not tested
    */
    SCP_03_25;
}
