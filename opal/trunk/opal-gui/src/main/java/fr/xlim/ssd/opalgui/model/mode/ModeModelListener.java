package fr.xlim.ssd.opalgui.model.mode;

import java.util.EventListener;

/**
 * <b>The mode listener interface.</b>
 * @author Pequegnot David
 * @author Rispal    Julie
 * @version 0.1
 * @see ModeEnum
 * @see ModeModelChangedEvent
 * @see ModeModel
 */
public interface ModeModelListener extends EventListener {
    public void modeChanged(ModeModelChangedEvent event);
}
