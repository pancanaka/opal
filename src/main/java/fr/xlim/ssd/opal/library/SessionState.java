package fr.xlim.ssd.opal.library;

/**
 * Define the session state of Smart Card identification :
 * <ul>
 * <li> NO_SESSION
 * </ul>
 *
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public enum SessionState {
    NO_SESSION,
    SESSION_INIT,
    SESSION_AUTH;
}

