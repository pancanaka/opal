package fr.xlim.ssd.opal.library.logging.filters;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import fr.xlim.ssd.opal.library.logging.LogType;

/**
 * Filters incoming APDU.
 *
 * @author David Pequegnot
 */
public class APDUInFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMarker() == null) {
            return FilterReply.DENY;
        }
        if (event.getMarker().getName().equals(LogType.APDU_GP_IN.name())
                || event.getMarker().getName().equals(LogType.APDU_APPLICATION_IN.name())) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}
