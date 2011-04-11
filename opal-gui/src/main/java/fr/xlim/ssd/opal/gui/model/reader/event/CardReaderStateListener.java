/******************************************************************************
 *       CAGEVE - CAPTCHA Generation and Verification in SmartCards           *
 ******************************************************************************
 * Authors : Laurent Cart-Lamy <laurent.cart@etu.unilim.fr>                   *
 *           David Pequegnot   <david.pequegnot@etu.unilim.fr>                *
 *           Aurelien Thomas   <aurelien.thomas@etu.unilim.fr>                *
 *           Thibault Tigeon   <thibault.tigeon@etu.unilim.fr>                *
 ******************************************************************************
 * This file is part of the CAGEVE project.                                   *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim) and Xlim laboratory, 2011       *
 *****************************************************************************/
package fr.xlim.ssd.opal.gui.model.reader.event;

import java.util.EventListener;

/**
 * Listener interface for terminal state event.
 * <p/>
 * Objects which listen terminal state event must implement this interface.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public interface CardReaderStateListener extends EventListener {
    /**
     * Method to implement to handle terminal state event.
     *
     * @param event the associated event
     */
    void terminalStateChanged(CardReaderStateChangedEvent event);
}
