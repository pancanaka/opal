/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.model.mode;

import fr.xlim.ssd.opalgui.model.mode.enumeration.ActionEnum;
import java.util.EventObject;

/**
 *
 * @author Pequegnot David
 */
public class ActionModelChangedEvent extends EventObject {
    public ActionModelChangedEvent( Object source, ActionEnum action) {
        super(source) ;
        this.action = action ;
    }

    public ActionEnum getAction() {
        return this.action ;
    }

    private ActionEnum action ;
}
