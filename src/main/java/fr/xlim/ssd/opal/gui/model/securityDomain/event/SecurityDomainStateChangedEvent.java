/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.model.securityDomain.event;

import java.util.EventObject;

/**
 *
 * @author Tiana Razafindralambo
 */
public class SecurityDomainStateChangedEvent extends EventObject{

    public SecurityDomainStateChangedEvent(Object source) {
        super(source);
    }
}
