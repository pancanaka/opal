/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.controller.authentication;

import fr.xlim.ssd.opalgui.model.authenticate.AuthenticationModel;
import fr.xlim.ssd.opalgui.view.authenticate.AuthenticatePanelView;

/**
 *
 * @author Pequegnot David
 */
public class AuthenticationController {
    private AuthenticationModel authenticationModel ;
    private AuthenticatePanelView authenticationView ;

    public AuthenticationController( AuthenticationModel authenticationModel ) {
        this.authenticationModel = authenticationModel ;
        this.authenticationView = new AuthenticatePanelView( 
                this,
                this.authenticationModel ) ;
        this.authenticationModel.addProfilListener(
                this.authenticationView);
    }

    public AuthenticatePanelView getPanel() {
        return this.authenticationView ;
    }

    public void notifyProfileSelectedChanged( String cardName ) {
            this.authenticationModel.setSelectedCard(cardName);
    }
}
