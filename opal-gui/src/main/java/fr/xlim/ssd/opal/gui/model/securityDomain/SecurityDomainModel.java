/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
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
package fr.xlim.ssd.opal.gui.model.securityDomain;

import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateListener;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;

import javax.smartcardio.CardChannel;
import javax.swing.event.EventListenerList;
import java.util.logging.Level;

/**
 * Security domain Model
 *
 * @author Tiana Razafindralambo
 */
public class SecurityDomainModel {

    private static final CustomLogger logger = new CustomLogger();
    private EventListenerList listeners = new EventListenerList();
    private SecurityDomain domain = null;
    private boolean authenticated = false;

    public SecurityDomainModel() {
    }

    public SecurityDomain getDomain() {
        return domain;
    }

    public boolean hasDomain() {
        return (domain != null);
    }

    public void isAuthenticated(boolean bool) {
        this.authenticated = bool;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    ;

    /**
     * Security domain setter
     * update the security domain model
     *
     * @param cardConfig
     * @param channel
     */
    public void setSecurityDomain(CardConfig cardConfig, CardChannel channel) {
        logger.info("Setting security domain...");
        try {
            logger.info("-> IMPL : " + cardConfig.getImplementation().toString());
            logger.info("-> channel : " + channel.toString());
            this.domain = new SecurityDomain(cardConfig.getImplementation(),
                    channel,
                    cardConfig.getIssuerSecurityDomainAID()
            );

            this.domain.setOffCardKeys(cardConfig.getSCKeys());

            try {
                logger.info("APDU Response to selection : " + domain.select().toString());
                this.fireSecurityDomainStateChanged();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(SecurityDomainModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (CommandsImplementationNotFound ex) {
            logger.error("Commands Implementation not found");
        } catch (ClassNotFoundException ex) {
            logger.error("Class not found exception");
        }
    }

    /**
     * Add a security domain state listener.
     *
     * @param listener the listener to add
     */
    public void addSecurityDomainStateListener(SecurityDomainStateListener listener) {
        logger.info("Adding listeners to SecModel");
        this.listeners.add(SecurityDomainStateListener.class, listener);
    }

    /**
     * Remove a security domain state listener.
     *
     * @param listener the listener to remove
     */
    public void removeSecurityDomainStateListener(SecurityDomainStateListener listener) {
        logger.info("Removing listeners to SecModel");
        this.listeners.remove(SecurityDomainStateListener.class, listener);
    }

    /**
     * Notify listeners when security domain state changed.
     */
    private void fireSecurityDomainStateChanged() {
        logger.info("Seucurity domain state changed ! Fire ! Fire !");
        SecurityDomainStateListener[] listenerList = (SecurityDomainStateListener[]) this.listeners.getListeners(SecurityDomainStateListener.class);

        for (SecurityDomainStateListener listener : listenerList) {
            listener.securityDomainStateChanged(new SecurityDomainStateChangedEvent(this));
        }
    }
}
