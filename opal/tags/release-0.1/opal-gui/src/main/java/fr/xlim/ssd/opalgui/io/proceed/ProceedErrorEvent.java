/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.io.proceed;

import java.util.EventObject;

/**
 *
 * @author Pequegnot David, Rispal Julie
 */
public class ProceedErrorEvent extends EventObject {
    private ModulesEnum module ;
    private String message ;

    public ProceedErrorEvent(Object source, ModulesEnum module, String message) {
        super( source ) ;
        this.module = module ;
        this.message = message ;
    }

    public ModulesEnum getModule ( ) {
        return this.module ;
    }

    public String getMessage ( ) {
        return this.message ;
    }
}
