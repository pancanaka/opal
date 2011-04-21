/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.controller;

/**
 *
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
