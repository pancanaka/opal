package fr.xlim.ssd.opal.gui.view.components.tab;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Modified <code>JScrollPane</code>.
 *
 * This abstract class extends <code>JScrollPane</code>. It was designed to tip the wheel problem when using a
 * mouse (too slow).
 * Class which need a <code>JScrollPane</code> must extend this abstract class.
 *
 * @author David Pequegnot
 * @author Thibault Desmoulins
 */
public abstract class AbstractScrollPane extends JScrollPane implements MouseWheelListener {
    /**
     * Default constructor.
     */
    public AbstractScrollPane() {
        super();
        this.addMouseWheelListener(this);
    }

    /**
     * Override the <code>mouseWheelMoved</code> method to make the scrolling movement faster.
     *
     * @param event the mouse wheel moved event
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        final JScrollBar scrollBar = getVerticalScrollBar();
        final int rotation = event.getWheelRotation();
        if (scrollBar != null) {
            scrollBar.setValue(scrollBar.getValue() + (scrollBar.getBlockIncrement(rotation)*rotation));
        }
    }
}
