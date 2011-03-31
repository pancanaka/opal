package fr.xlim.ssd.opalgui.model.mode;

import fr.xlim.ssd.opalgui.model.mode.enumeration.ModeEnum;
import java.util.EventObject;

/**
 * <b>Event when the mode changed</b>
 * @author Pequegnot David
 * @author Rispal    Julie
 * @version 0.1
 * @see ModeListener
 * @see ModeEnum
 * @see ModeModel
 */
public class ModeModelChangedEvent extends EventObject {
    public ModeModelChangedEvent(Object source, ModeEnum newMode) {
        super(source);
        this.newMode = newMode;
    }

    public ModeEnum getNewMode() {
        return newMode;
    }
    
    private ModeEnum newMode;
}
