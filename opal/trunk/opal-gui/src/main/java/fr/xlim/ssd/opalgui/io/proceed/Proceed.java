/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.io.proceed;

import fr.xlim.ssd.opal.library.FileType;
import fr.xlim.ssd.opal.library.GetStatusResponseMode;
import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Pequegnot David
 */
public class Proceed {
    /**
     * Contains the instance of Proceed for the design pattern Singleton.
     */
    private static Proceed instance = null ;

    /**
     * Contains the Security Domain for a connection.
     */
    private SecurityDomain securityDomain = null ;

    /**
     * Listeners on the model
     */
    private EventListenerList listeners ;

    /**
     * Disbaling the defalt constructor.
     */
    private Proceed() {
        this.listeners = new EventListenerList();
    }

    public static Proceed getInstance() {
        if ( instance == null ) {
            instance = new Proceed() ;
        }
        return instance ;
    }

    public void addProceedListener( ProceedListener listener ) {
        this.listeners.add( ProceedListener.class, listener );
    }

    public void removeProceedListener( ProceedListener listener ) {
        this.listeners.remove( ProceedListener.class, listener );
    }

    private void fireProceedResultEvent( ModulesEnum module, ResponseAPDU response ) {
        ProceedListener [] listenerList = (ProceedListener []) this.listeners.getListeners( ProceedListener.class ) ;

        for ( ProceedListener listener : listenerList ) {
            listener.onProceed( new ProceedEvent(this, module, response));
        }
    }

    private void fireProceedErrorEvent( ModulesEnum module, String message ) {
        ProceedListener [] listenerList = (ProceedListener []) this.listeners.getListeners( ProceedListener.class ) ;

        for ( ProceedListener listener : listenerList ) {
            listener.onProceedError( new ProceedErrorEvent(this, module, message));
        }
    }

    public void goAuthenticate(CardChannel channel, CardConfig currentCard,
            SecLevel secLevel) {
        try {
            ResponseAPDU response ;
            ResponseAPDU [] responseA ;
            this.securityDomain = new SecurityDomain(currentCard.getImplementation(), channel, currentCard.getIssuerSecurityDomainAID());
            this.securityDomain.setOffCardKeys(currentCard.getSCKeys()) ;

            // Select the ISD AID
            try {
                response = this.securityDomain.select() ;
                fireProceedResultEvent( ModulesEnum.SELECT, response);
            }
            catch (CardException ex) {
                fireProceedErrorEvent( ModulesEnum.SELECT, ex.getMessage() );
                throw ex ;
            }

            // Initialize Update
            try {
                response = this.securityDomain.initializeUpdate(
                        currentCard.getDefaultInitUpdateP1(),
                        currentCard.getDefaultInitUpdateP2(),
                        currentCard.getScpMode()) ;
                fireProceedResultEvent( ModulesEnum.INITIALIZE_UPDATE, response);
            }
            catch (CardException ex) {
                fireProceedErrorEvent( ModulesEnum.INITIALIZE_UPDATE, ex.getMessage());
                throw ex ;
            }

            // External Authenticate
            try {
                response = this.securityDomain.externalAuthenticate( secLevel ) ;
                fireProceedResultEvent( ModulesEnum.EXTERNAL_AUTHENTICATE, response);
            }
            catch( CardException ex) {
                fireProceedErrorEvent( ModulesEnum.EXTERNAL_AUTHENTICATE, ex.getMessage());
                throw ex ;
            }

            responseA = this.securityDomain.getStatus(FileType.LOAD_FILES, GetStatusResponseMode.OLD_TYPE, null);
            for ( int ind = 0 ; ind < responseA.length ; ++ind ) {
                System.out.println(Conversion.arrayToHex(responseA[ind].getBytes()));
            }
        } catch (CardException ex) {
            Logger.getLogger(Proceed.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CommandsImplementationNotFound ex) {
            Logger.getLogger(Proceed.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Proceed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
