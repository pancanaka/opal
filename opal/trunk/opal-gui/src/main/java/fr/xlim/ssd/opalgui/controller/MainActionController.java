/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.controller;

import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opalgui.io.terminal.TerminalConnection;
import fr.xlim.ssd.opalgui.io.proceed.Proceed;
import fr.xlim.ssd.opalgui.model.authenticate.AuthenticationModel;
import fr.xlim.ssd.opalgui.model.mode.ActionModel;
import fr.xlim.ssd.opalgui.model.mode.ModeModel;
import fr.xlim.ssd.opalgui.view.mainaction.MainActionView;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Pequegnot David
 */
public class MainActionController {
    private ActionModel actionModel   = null ;
    private ModeModel   modeModel     = null ;
    private MainActionView actionView = null ;
    private TerminalConnection terminalConnection = null ;

    public MainActionController ( ActionModel actionModel, ModeModel modeModel ) {
        this.actionModel = actionModel ;
        this.modeModel   = modeModel   ;
        this.terminalConnection = TerminalConnection.getInstance() ;
        this.actionView  = new MainActionView( this, actionModel, this.terminalConnection ) ;
        this.terminalConnection.addTerminalConnectionListener(actionView);
        terminalConnection.start();
        actionModel.addActionListener(actionView);
    }

    public MainActionView getPanel() {
        return this.actionView ;
    }

    public void proceed( ) {
        switch ( this.actionModel.getCurrentAction() ) {
            case AUTHENTIFICATE :
                try {
                    if ( TerminalConnection.isCardPlugged() &&
                            TerminalConnection.isTerminalPlugged() ) {
                        CardConfig cardConfig = AuthenticationModel.getInstance().getSelectedCard();
                        Card card = TerminalConnection.getTerminal().
                                connect( cardConfig.getTransmissionProtocol() );
                        CardChannel channel = card.getBasicChannel() ;

                        // reset the card
                        ATR atr = card.getATR() ;
                        Proceed.getInstance().goAuthenticate(channel, cardConfig,
                                SecLevel.C_ENC_AND_MAC);
                    } else {
                        System.out.println( "No terminal or card connected" ) ;
                    }
                } catch (CardException ex) {
                    Logger.getLogger(MainActionController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CardConfigNotFoundException ex) {
                    Logger.getLogger(MainActionController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(MainActionController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break ;
            default :
                System.out.println("Cette fonctionnalité n'est pas encore prise en charge.");
        }
    }
}
