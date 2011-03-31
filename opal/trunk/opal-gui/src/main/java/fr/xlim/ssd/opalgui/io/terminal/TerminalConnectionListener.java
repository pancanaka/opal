/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.io.terminal;

import java.util.EventListener;

/**
 *
 * @author Pequegnot David
 */
public interface TerminalConnectionListener extends EventListener {
    public void terminalStatusChanged( TerminalConnectionEvent evt ) ;
}
