package fr.xlim.ssd.opal.library;

/**
 * Define the session state of Smart Card identification. It used internally to identify state during authentication
 * exchanges.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public enum SessionState {
    /// no session
    NO_SESSION,

    /// session initialized
    SESSION_INIT,

    /// session authenticated
    SESSION_AUTH;
}

