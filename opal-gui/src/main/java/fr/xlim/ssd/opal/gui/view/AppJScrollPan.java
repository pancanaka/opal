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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 *
 * @author Thibault Desmoulins
 */
public class AppJScrollPan extends JScrollPane implements MouseWheelListener {
    
    public AppJScrollPan(final JPanel myTable) {
        setViewportView(myTable);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        myTable.addMouseWheelListener(this);
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        final JScrollBar scrollBar = getVerticalScrollBar();
        final int rotation = mwe.getWheelRotation();
        if (scrollBar!=null) {
            scrollBar.setValue(scrollBar.getValue() + (scrollBar.getBlockIncrement(rotation)*rotation));
        }
    }

}
