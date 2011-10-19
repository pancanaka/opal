/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a Java 6 library that implements Global Platform 2.x card
 * specification. OPAL is able to upload and manage Java Card applet lifecycle
 * on Java Card while dealing of the authentication of the user and encryption
 * of the communication between the user and the card. OPAL is also able
 * to manage different implementations of the specification via a pluggable
 * interface.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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
