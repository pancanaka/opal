
package fr.xlim.ssd.opal.gui.model.securityDomain;

import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.securityDomain.event.SecurityDomainStateListener;
import fr.xlim.ssd.opal.library.SecLevel;
import javax.swing.event.EventListenerList;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;
import java.util.logging.Level;
import javax.smartcardio.CardChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Tiana Razafindralambo
 */
public class SecurityDomainModel {

    private static final Logger logger = LoggerFactory.getLogger(SecurityDomainModel.class);
    private EventListenerList listeners = new EventListenerList();
    private SecurityDomain domain = null;
    private SecLevel secLevel = null;
    private boolean authenticated = false;
    public SecurityDomainModel(){}

    public SecurityDomain getDomain(){ return domain;}
    public boolean hasDomain(){ return (domain != null);}
    public void isAuthenticated(boolean bool){ this.authenticated = bool;}
    public boolean isAuthenticated(){ return this.authenticated;};
    public void setSecurityDomain(CardConfig cardConfig, CardChannel channel)
    {
        logger.info("Setting security domain..."); 
        try
        {
            logger.info("-> IMPL : "+cardConfig.getImplementation().toString());
            logger.info("-> channel : "+channel.toString()); 
            this.domain = new SecurityDomain(   cardConfig.getImplementation(),
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
        }
        catch(CommandsImplementationNotFound ex) { logger.error("Commands Implementation not found"); }
        catch(ClassNotFoundException ex) { logger.error("Class not found exception"); } 
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
