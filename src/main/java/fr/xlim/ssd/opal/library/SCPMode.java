package fr.xlim.ssd.opal.library;

/**
 * Define the SCP protocol used.
 *
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */

public enum SCPMode {

    SCP_UNDEFINED,
    SCP_01_05,
    SCP_01_15,

    /**
     * TODO: Not implemented
     */
    SCP_02,

    /**
     * TODO: Not implemented
     */
    SCP_10;
}
