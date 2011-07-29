package fr.xlim.ssd.opal.library.logging.filters;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter based on markers name.
 * <p/>
 * Unlike the default <code>MarkerFilter</code> supplied by the <i>logback</i> logging framework,
 * a <code>null</code> marker is considered as a mismatch.
 *
 * @author David Pequegnot
 * @see org.slf4j.Marker
 */
public class MarkerFilter extends Filter<ILoggingEvent> {

    private ArrayList<String> markers = new ArrayList<String>();

    private FilterReply onMismatch = FilterReply.NEUTRAL;
    private FilterReply onMatch = FilterReply.NEUTRAL;

    /**
     * Filters the event.
     * <p/>
     * A <code>null</code> marker implies a mismatch. If the marker name belongs to the marker list, it matches.
     *
     * @param event the logging event
     * @return the reply corresponding to the event and the filter action
     */
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMarker() == null) {
            return this.onMismatch;
        }

        for (String marker : markers) {
            if (event.getMarker().contains(marker)) {
                return this.onMatch;
            }
        }
        return this.onMismatch;
    }

    /**
     * Add a marker in the list.
     *
     * @param marker the marker
     */
    public void addMarker(String marker) {
        this.markers.add(marker);
    }

    /**
     * Defines the "on mismatch" reply (<code>FilterReply.NEUTRAL</code> by default).
     *
     * @param onMismatch the "on mismatch" reply
     */
    public void setOnMismatch(FilterReply onMismatch) {
        this.onMismatch = onMismatch;
    }

    /**
     * Defines the "on match" reply (<code>FilterReply.NEUTRAL</code> by default).
     *
     * @param onMatch the "on match" reply
     */
    public void setOnMatch(FilterReply onMatch) {
        this.onMatch = onMatch;
    }

    /**
     * Gets the list of markers.
     *
     * @return the list of markers
     */
    public List<String> getMarker() {
        return this.markers;
    }

    /**
     * Gets the "on mismatch" reply.
     *
     * @return the "on mismatch reply"
     */
    public FilterReply getOnMismatch() {
        return this.onMismatch;
    }

    /**
     * Gets the "on match" reply.
     *
     * @return the "on match" reply
     */
    public FilterReply getOnMatch() {
        return this.onMatch;
    }
}
