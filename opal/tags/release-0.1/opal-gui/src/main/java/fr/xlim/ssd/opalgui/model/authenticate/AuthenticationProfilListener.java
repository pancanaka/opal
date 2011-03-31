/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.model.authenticate;

import java.util.EventListener;

/**
 *
 * @author Pequegnot David
 */
public interface AuthenticationProfilListener extends EventListener  {
    public void profilChanged( AuthenticationProfilChangedEvent evt ) ;
}
