/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.io.proceed;

import java.util.EventObject;
import javax.smartcardio.ResponseAPDU;

/**
 *
 * @author Pequegnot David
 */
public class ProceedEvent extends EventObject {
    private ModulesEnum module ;

    private ResponseAPDU response ;

    public ProceedEvent( Object source, ModulesEnum module, ResponseAPDU response) {
        super(source) ;
        this.module = module ;
        this.response = response ;
    }

    public ModulesEnum getModule() {
        return this.module ;
    }

    public ResponseAPDU getResponse() {
        return this.response ;
    }
}
