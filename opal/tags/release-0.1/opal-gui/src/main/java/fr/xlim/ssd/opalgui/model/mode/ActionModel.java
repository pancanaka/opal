/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.model.mode;

import fr.xlim.ssd.opal.library.SecLevel;
import fr.xlim.ssd.opal.library.SecurityDomain;
import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opalgui.model.mode.enumeration.ActionEnum;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import javax.swing.event.EventListenerList;

/**
 * <b>Defines the action used in the GUI.</b>
 * It uses the design pattern 'observer' to notify modifications in the current
 * model.
 * @author Pequegnot David
 * @author Rispal    Julie
 * @version 0.1
 * @see MainMenuView
 */
public class ActionModel {
    /**
     *
     */
    private ActionEnum currentModule = null ;

    /**
     * The class constructor
     * <p>It initialize the action used in the GUI.</p>
     */
    public ActionModel() {
        this( ActionEnum.DELETE_STANDARD ) ;
    }

    /**
     * The class constructor
     * @param action The action to initialize.
     */
    public ActionModel( ActionEnum action ) {
        super();
        this.action = action;
        this.listeners = new EventListenerList();
    }

    /**
     * Getting the action normally used by the GUI.
     * @return the currection action used by the GUI.
     */
    public ActionEnum getCurrentAction() {
        return this.action ;
    }

    public void goAuthenticate(CardChannel channel, CardConfig currentCard,
            SecLevel secLevel) {
        
    }

    /**
     * Setting the action to an other defined by the enumeration ActionEnum.
     * @param action The action to set.
     */
    public void setAction(ActionEnum action) {
        this.action = action ;

        fireActionChanged();
    }

    /**
     * Adding a listener to the model cocnerning the action.
     * @param listener the object which is seeing modification in the
     * instanciated ModeModel class. It need to implement the ActionModelListener
     * interface.
     * @see ActionModelListener
     */
    public void addActionListener(ActionModelListener listener) {
        listeners.add(ActionModelListener.class, listener);
    }

    /**
     * Removing a listener to the model.
     * @param listener the object to remove the listener in the instanciated
     * ModeModel class.
     * @see ActionModelListener
     */
    public void removeActionListener(ActionModelListener listener) {
        listeners.remove(ActionModelListener.class, listener);
    }

    /**
     * Notify that the action changed through listeners.
     */
    private void fireActionChanged() {
        ActionModelListener [] listenerList = (ActionModelListener [])listeners.getListeners(ActionModelListener.class);

        for (ActionModelListener listener : listenerList) {
            listener.actionChanged(new ActionModelChangedEvent(this, getCurrentAction()));
        }
    }

    /**
     * The current action.
     */
    private ActionEnum action;

    /**
     * @see ModeModel#fireModeChanged()
     */
    private EventListenerList listeners;
}
