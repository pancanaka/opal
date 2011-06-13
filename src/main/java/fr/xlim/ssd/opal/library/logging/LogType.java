package fr.xlim.ssd.opal.library.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Log types
 * <p/>
 * A log type is a <i>marker</i> which indicates the type of log. It can be used to differentiate logs
 * and allow filtering.
 *
 * @author Guillaume Bouffard
 * @author David Pequegnot
 */
public enum LogType {
    // Command title
    COMMAND_TYPE,

    // Global APDU command log.
    APDU,

    // APDU log concerning the application level.
    APDU_APPLICATION,

    // APDU log concerning the application level. It indicates an incoming APDU.
    APDU_APPLICATION_IN,

    // APDU log concerning the application level. It indicates an outgoing APDU.
    APDU_APPLICATION_OUT,

    // APDU log concerning the Global Platform level.
    APDU_GP,

    // APDU log concerning the Global Platform level. It indicates an incoming APDU.
    APDU_GP_IN,

    // APDU log concerning the Global Platform level. It indicates an outgoing APDU.
    APDU_GP_OUT;

    // Establish relationships between <code>Marker</code> instances.
    static {
        APDU_APPLICATION.getMarker().add(APDU.getMarker());
        APDU_APPLICATION_IN.getMarker().add(APDU_APPLICATION.getMarker());
        APDU_APPLICATION_OUT.getMarker().add(APDU_APPLICATION.getMarker());
        APDU_GP.getMarker().add(APDU.getMarker());
        APDU_GP_IN.getMarker().add(APDU_GP.getMarker());
        APDU_GP_OUT.getMarker().add(APDU_GP.getMarker());
    }

    // The <code>Marker</code> instance corresponding to the log type.
    private Marker marker;

    /**
     * Link a <code>Marker</code> name to an enumerate value
     * <p/>
     * The <code>Marker</code> will be the <code>java.lang.String</code> representation of the enumerate value.
     */
    private LogType() {
        this.marker = MarkerFactory.getIMarkerFactory().getMarker(this.name());
    }

    /**
     * Get the <code>Marker</code> instance corresponding to the enumerate value.
     *
     * @return the <code>Marker</code> instance corresponding to the enumerate value
     */
    public Marker getMarker() {
        return this.marker;
    }
}
