package fr.xlim.ssd.opal.library.logging.filters;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters by marker
 */
public class MarkerFilter extends Filter<ILoggingEvent> {

    private ArrayList<String> markers = new ArrayList<String>();

    private FilterReply onMismatch = FilterReply.NEUTRAL;
    private FilterReply onMatch    = FilterReply.NEUTRAL;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMarker() == null) {
            return this.onMismatch;
        }

        for (String marker: markers) {
            if (event.getMarker().contains(marker)) {
                return this.onMatch;
            }
        }
        return this.onMismatch;
    }

    public void addMarker(String marker) {
        this.markers.add(marker);
    }

    public void setOnMismatch(FilterReply onMismatch) {
        this.onMismatch = onMismatch;
    }

    public void setOnMatch(FilterReply onMatch) {
        this.onMatch = onMatch;
    }

    public List<String> getMarker() {
        return this.markers;
    }

    public FilterReply getOnMismatch() {
        return this.onMismatch;
    }

    public FilterReply getOnMatch() {
        return this.onMatch;
    }
}
