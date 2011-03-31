/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.model.mode;

import java.util.EventListener;

/**
 *
 * @author Pequegnot David
 */
public interface ActionModelListener extends EventListener {
    public void actionChanged(ActionModelChangedEvent evt) ;
}
