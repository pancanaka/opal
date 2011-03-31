/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.io.proceed;

import java.util.EventListener;

/**
 *
 * @author Pequegnot David
 */
public interface ProceedListener extends EventListener {
    public void onProceed( ProceedEvent event ) ;
    public void onProceedError( ProceedErrorEvent event ) ;
}
