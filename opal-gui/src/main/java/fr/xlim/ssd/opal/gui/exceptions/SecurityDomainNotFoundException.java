/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>        *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.exceptions;

/**
 * @author Tiana Razafindralambo
 */
public class SecurityDomainNotFoundException extends Exception {

    public SecurityDomainNotFoundException() {
    }

    ;

    @Override
    public String getMessage() {
        return "Security domain isn't yet set.";
    }
} 