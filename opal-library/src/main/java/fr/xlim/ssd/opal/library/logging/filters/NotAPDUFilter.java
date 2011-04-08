package fr.xlim.ssd.opal.library.logging.filters;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import fr.xlim.ssd.opal.library.logging.LogType;

/**
 * Filters not APDU logs.
 */
public class NotAPDUFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMarker() == null) {
            return FilterReply.ACCEPT;
        }

        if (event.getMarker().contains(LogType.APDU.getMarker())) {
            return FilterReply.DENY;
        } else {
            return FilterReply.ACCEPT;
        }
    }
}
