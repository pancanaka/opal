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

import java.util.EventObject;

/**
 * Event notified when terminal state changed.
 * <p/>
 * A terminal state event will be notified to listeners when :
 * <ul>
 * <li>the terminal list changed;</li>
 * <li>the selected terminal changed.</li>
 * </ul>
 * There is not internal methods: we think that listeners must use the
 * {@link fr.xlim.ssd.javacard.captcha.terminal.swingterminal.model.terminal.TerminalModel} model to check new values.
 *
 * @author David Pequegnot <david.pequegnot@etu.unilim.fr>
 */
public class CardReaderStateChangedEvent extends EventObject {

    /**
     * Default constructor.
     *
     * @param source the source object
     */
    public CardReaderStateChangedEvent(Object source) {
        super(source);
    }
}
