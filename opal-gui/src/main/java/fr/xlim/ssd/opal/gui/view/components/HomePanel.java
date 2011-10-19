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
package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.view.HomeView;
import fr.xlim.ssd.opal.gui.view.components.tab.*;

import javax.swing.*;
import java.awt.*;

/**
 * The first panel displayed on the application. It shows these different panels :
 * <ul>
 * <li>Authentication;</li>
 * <li>Applet;</li>
 * <li>Delete;</li>
 * <li>Select;</li>
 * <li>Send APDU;</li>
 * </ul>
 *
 * @author Thibault Desmoulins
 * @author Tiana Razafindralambo
 */
public class HomePanel extends JPanel {
    private AuthenticationPanel authentication;
    private AppletPanel applet;
    private DeletePanel delete;
    private SelectPanel select;
    private SendAPDUPanel send;


    /**
     * Constructor
     * Display the <code>JTabbedPane</code> which contains all the panels mentioned in the class description
     *
     * @param controller the main controller of the application
     * @param f          the main view of the application
     * @see fr.xlim.ssd.opal.gui.view.HomeView
     */
    public HomePanel(MainController controller, HomeView f) {
        authentication = new AuthenticationPanel(controller, f, this);
        applet = new AppletPanel();
        delete = new DeletePanel();
        select = new SelectPanel();
        send = new SendAPDUPanel(controller.sendApduController);
        JTabbedPane myPanel = new JTabbedPane();

        myPanel.addTab(authentication.title, authentication);
        myPanel.addTab(applet.title, applet);
        myPanel.addTab(delete.title, delete);
        myPanel.addTab(select.title, select);
        myPanel.addTab(send.title, send);

        //scrollPane = new JScrollPane(myPanel);
        //myPanel.setSize(800,800);

        Box frame = Box.createVerticalBox();
        frame.add(myPanel, BorderLayout.SOUTH);
        this.add(frame);
    }


    /**
     * @return the authentication panel, instance of the <code>AuthenticationPanel</code> class
     */
    public AuthenticationPanel getAuthenticationPanel() {
        return authentication;
    }


    /**
     * @return the applet panel, instance of the <code>AppletPanel</code> class
     */
    public AppletPanel getAppletPanel() {
        return applet;
    }


    /**
     * @return the delete panel, instance of the <code>DeletePanel</code> class
     */
    public DeletePanel getDeletePanel() {
        return delete;
    }


    /**
     * @return the select panel, instance of the <code>SelectPanel</code> class
     */
    public SelectPanel getSelectPanel() {
        return select;
    }


    /**
     * @return the send APDU panel, instance of the <code>SendAPDUPanel</code> class
     */
    public SendAPDUPanel getSendApduPanel() {
        return send;
    }

}
