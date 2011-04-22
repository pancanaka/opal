
package fr.xlim.ssd.opal.gui.model.securityDomain.event;

import java.util.EventListener;
import javax.smartcardio.ResponseAPDU;
/**
 *
 * @author Tiana Razafindralambo
 */
public interface SecurityDomainStateListener extends EventListener {

    void securityDomainStateChanged(SecurityDomainStateChangedEvent event);
}
