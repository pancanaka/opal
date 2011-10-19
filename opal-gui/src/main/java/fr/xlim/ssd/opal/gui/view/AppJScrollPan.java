/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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
