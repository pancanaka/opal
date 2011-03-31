package fr.xlim.ssd.opalgui.model.mode;

import fr.xlim.ssd.opalgui.model.mode.enumeration.ModeEnum;
import javax.swing.event.EventListenerList;


 /**
 * <b>Defines the mode used in the GUI.</b>
 * <p>
 * Modes can be:
 * <ul>
 *     <li>ModeEnum.STANDARD: standard mode;</li>
 *     <li>ModeEnum.ADVANCED: advanced mode;</li>
 * </ul>
 * </p>
 * <p>
 * It uses the design pattern 'observer' to notify modifications in the current
 * model.
 * </p>
 * @author Pequegnot David
 * @author Rispal    Julie
 * @version 0.1
 * @see MainMenuView
 */
public class ModeModel {
    /**
     * The class constructor
     * <p>It initialize the mode used in the GUI.</p>
     * @todo Initializing mode from a config file
     */
    public ModeModel() {
        super();
        this.mode = ModeEnum.STANDARD;
        this.listeners = new EventListenerList();
    }

    /**
     * Getting the mode normally used by the GUI.
     * @return the current mode used by the GUI.
     */
    public ModeEnum getCurrentMode() {
        return this.mode;
    }

    /**
     * Setting the mode to an other. Currently there is only two modes, but
     * it can be modifiable in the future.
     * @param newMode the new mode to use.
     */
    public void setMode(ModeEnum newMode) {
        mode = newMode;
        
        fireModeChanged();
    }

    /**
     * Adding a listener to the model concerning the mode.
     * @param listener the object which is seeing modifications in the
     * instanciated ModeModel class. It needs to implement the ModeModelListener
     * interface.
     * @see ModeModelListener
     */
    public void addModeListener(ModeModelListener listener) {
        listeners.add(ModeModelListener.class, listener);
    }

    /**
     * Removing a listener to the model.
     * @param listener the object to remove the listener in the instanciated
     * ModeModel class.
     * @see ModeModelListener
     */
    public void removeModeListener(ModeModelListener listener) {
        listeners.remove(ModeModelListener.class, listener);
    }

    /**
     * Notify that the mode changed through listeners.
     */
    private void fireModeChanged() {
        ModeModelListener [] listenerList = (ModeModelListener [])listeners.getListeners(ModeModelListener.class);

        for (ModeModelListener listener : listenerList) {
            listener.modeChanged(new ModeModelChangedEvent(this, getCurrentMode()));
        }
    }

    /**
     * The enum type ModeEnum defines the type of options used in the GUI.
     * @see ModeEnum
     */
    private ModeEnum mode;

    /**
     * @see ModeModel#fireModeChanged() 
     */
    private EventListenerList listeners;
}
