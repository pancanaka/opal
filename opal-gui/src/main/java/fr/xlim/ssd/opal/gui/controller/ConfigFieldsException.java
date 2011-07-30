/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Yorick Lesecque <yorick.lesecque@etu.unilim.fr>                   *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.controller;

/**
 * @author Yorick Lesecque <yorick.lesecque@gmail.com>
 */
public class ConfigFieldsException extends Exception {
    private String message = " ";

    ConfigFieldsException(String message) {
        this.message += message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
