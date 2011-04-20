/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.gui.controller;

import fr.xlim.ssd.opal.gui.model.Authentication.AuthenticationModel;
import fr.xlim.ssd.opal.gui.model.Communication.CommunicationModel;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;

/**
 *
 * @author razaina
 */
public class AuthenticationController {

    private AuthenticationModel authModel;
    private CardReaderModel cardReaderModel;
    private CommunicationModel communication;
    public AuthenticationController(CardReaderModel cardReaderModel, CommunicationModel communication)
    {
        this.cardReaderModel = cardReaderModel;
        this.communication = communication;
        this.authModel = new AuthenticationModel(this.cardReaderModel, this.communication);
        this.authModel.setConfiguration();
    }
}
