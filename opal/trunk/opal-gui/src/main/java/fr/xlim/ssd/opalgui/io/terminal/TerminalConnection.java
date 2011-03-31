/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.io.terminal;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Pequegnot David, Rispal Julie
 */
public class TerminalConnection extends Thread {

    /**
     * Defines if a SmartCard Reader is plugged.
     */
    private static boolean terminalPlugged = false ;

    /**
     * Defines if a Card is connected to a SmartCard Reader.
     */
    private static boolean cardPlugged = false ;

    /**
     * Current CardTerminal
     */
    private static CardTerminal terminal = null ;

    /**
     * Current instance
     */
    private static TerminalConnection instance = null ;

    /**
     * Listeners
     */
    private EventListenerList listeners ;

    /**
     * TerminalConnection constructor.
     */
    private TerminalConnection() {
        super("TerminalSurvey");
        this.listeners       = new EventListenerList() ;
    }

    public static TerminalConnection getInstance() {
        if ( instance == null ) {
            instance = new TerminalConnection() ;
        }
        return instance ;
    }

    /**
     *
     * @return true if a terminal is plugged, false else.
     */
    synchronized public static boolean isTerminalPlugged() {
        return TerminalConnection.terminalPlugged ;
    }

    /**
     *
     * @return true if a card is plugged, false else.
     */
    synchronized public static boolean isCardPlugged() {
        return TerminalConnection.cardPlugged ;
    }

    synchronized public static CardTerminal getTerminal() {
        return terminal ;
    }

    /**
     * Add a listener to the class
     * @param listener
     */
    public void addTerminalConnectionListener( TerminalConnectionListener listener ) {
        this.listeners.add( TerminalConnectionListener.class, listener );
    }

    /**
     * Remove a lister from the class
     * @param listener
     */
    public void removeTerminalConnectionListener( TerminalConnectionListener listener ) {
        this.listeners.remove( TerminalConnectionListener.class, listener );
    }

    /**
     * Overriding run method from Thread class.
     */
    @Override
    public void run() {
        // Variable initialization
        TerminalFactory factory ;
        List<CardTerminal> terminalList ;

        // Infinite loop to verify if terminal and card are plugged
        while( true ) {
            try {
                factory = TerminalFactory.getDefault() ;
                // Getting the terminal list
                terminalList = factory.terminals().list() ;

                // We notify listeners only when there is a modification
                // in attribute terminalPlugged.
                if ( !TerminalConnection.terminalPlugged ) {
                    TerminalConnection.terminalPlugged = true ;
                    fireTerminalConnectionChanged();
                }

                // We get the terminal list
                terminal = (CardTerminal) terminalList.get(0);

                // We verify if a card is present in the terminal
                if (!terminal.isCardPresent()) {
                    // Notification
                    if ( TerminalConnection.cardPlugged ) {
                        TerminalConnection.cardPlugged = false ;
                        fireTerminalConnectionChanged();
                    }
                } else {
                    // Notification
                    if ( !TerminalConnection.cardPlugged ) {
                        TerminalConnection.cardPlugged = true ;
                        fireTerminalConnectionChanged();
                    }
                }
            }
            catch ( CardException ex ) {
                // This exception is thrown when terminal are not connected
                // on the Computer
                if ( TerminalConnection.terminalPlugged ) {
                    // We notify listeners only when there is a modification
                    // in attributes.
                    TerminalConnection.terminalPlugged = false ;
                    TerminalConnection.cardPlugged     = false ;
                    fireTerminalConnectionChanged();
                }
                // Log
                Logger.getLogger(TerminalConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
                // Sleep 1s
                Thread.sleep(1000);
            }
            catch ( InterruptedException ex ) {
                // Log
                Logger.getLogger(TerminalConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Notify an event.
     */
    public void fireTerminalConnectionChanged() {
        TerminalConnectionListener [] listenerList = (TerminalConnectionListener []) this.listeners.getListeners( TerminalConnectionListener.class ) ;

        for ( TerminalConnectionListener listener : listenerList ) {
            listener.terminalStatusChanged( new TerminalConnectionEvent(this) );
        }
    }

}
