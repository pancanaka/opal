/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>           *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * The main panel of the gui, useful to bring up a scroll when
 * some items do not appear in the window.
 *
 * @author Thibault Desmoulins
 */
public class AppJScrollPan extends JScrollPane implements MouseWheelListener {

    /**
     * Constructor.
     *
     * @param myTable the <code>JPanel</code> which has to be shown
     */
    public AppJScrollPan(final JPanel myTable) {
        setViewportView(myTable);
        myTable.addMouseWheelListener(this);
    }


    /**
     * This function override the function <code>mouseWheelMoved</code> which is in
     * the <code>MouseWheelListener</code> interface in order to accelerate the scroll
     *
     * @param mwe the mouse wheel event
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        final JScrollBar scrollBar = getVerticalScrollBar();
        final int rotation = mwe.getWheelRotation();
        if (scrollBar != null) {
            scrollBar.setValue(scrollBar.getValue() + (scrollBar.getBlockIncrement(rotation) * rotation));
        }
    }

}
