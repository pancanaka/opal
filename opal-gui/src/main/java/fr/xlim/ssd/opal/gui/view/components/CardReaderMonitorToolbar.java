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

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderItem;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateChangedEvent;
import fr.xlim.ssd.opal.gui.model.reader.event.CardReaderStateListener;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;

/**
 * Toolbar which monitors card readers.
 * <p/>
 * Allows the user to select a card reader in a list (<code>JComboBox</code>).
 * All card readers connected to the computer are displayed in this list. The toolbar implements a listener on
 * the <code>CardReaderModel</code> instance.
 * The list of card readers is displayed dynamically.
 *
 * @author David Pequegnot
 */
public class CardReaderMonitorToolbar extends JToolBar implements CardReaderStateListener {

    private JLabel cardReaderInformationLabel;
    private JComboBox cardReaderComboBox;
    private MainController mainController;
    private CardReaderModel cardReaderModel;

    /**
     * Default constructor.
     *
     * @param mainController the application main controller
     */
    public CardReaderMonitorToolbar(MainController mainController) {
        super();

        this.mainController = mainController;

        this.cardReaderModel = this.mainController.getCardReaderModel();
        this.cardReaderModel.addCardReaderStateListener(this);

        this.setEnabled(false);

        this.drawComponents();
    }

    /**
     * Draw toolbar components.
     */
    private void drawComponents() {
        setName("cardReaderToolbar");

        setBorderPainted(true);

        this.cardReaderInformationLabel = new JLabel("Terminal :");
        this.cardReaderInformationLabel.setName("cardReaderInformationLabel");

        this.cardReaderComboBox = new JComboBox();
        this.cardReaderComboBox.setEditable(false);

        add(this.cardReaderInformationLabel);
        add(Box.createRigidArea(new Dimension(5, 5)));
        add(this.cardReaderComboBox);

        add(Box.createHorizontalGlue());

        this.refreshResources();
    }

    /**
     * Refresh resources (i18n compliant).
     */
    private void refreshResources() {
        /* Get the resource map for the TerminalToolBar component */
        ResourceMap resourceMap = Application
                .getInstance(App.class)
                .getContext()
                .getResourceMap(CardReaderMonitorToolbar.class);

        this.cardReaderStateChanged(null);

        resourceMap.injectComponents(this);
    }

    /**
     * Update the list of card readers connected to the computer.
     *
     * @param event the new card reader list status
     */
    @Override
    public void cardReaderStateChanged(CardReaderStateChangedEvent event) {
        /* Get the resource map for the TerminalToolBar component */
        ResourceMap resourceMap = Application
                .getInstance(App.class)
                .getContext()
                .getResourceMap(CardReaderMonitorToolbar.class);

        this.cardReaderComboBox.removeAllItems();

        if (this.cardReaderModel.getCardReaderItems().isEmpty()) {
            this.cardReaderComboBox.addItem(resourceMap.getString("emptyCardReaderItemsList.text"));
        } else {
            for (CardReaderItem item : cardReaderModel.getCardReaderItems()) {
                this.cardReaderComboBox.addItem(item.getCardReaderName() +
                        " : " + item.getCardName() +
                        ((item.getCardATR() == null) ? "" :
                                " [" + Conversion.arrayToHex(item.getCardATR().getValue()) + ']'));
            }
        }

        if (cardReaderModel.getSelectedCardReaderName().equals("")) {
            this.cardReaderComboBox.setSelectedIndex(0);
        } else {
            this.cardReaderComboBox.setSelectedItem(this.cardReaderModel.getSelectedCardReaderName() +
                    " : " + this.cardReaderModel.getSelectedCardName() +
                    ((this.cardReaderModel.getSelectedCardATR() == null) ? "" :
                            " [" + Conversion.arrayToHex(this.cardReaderModel.getSelectedCardATR().getValue()) + ']'));
        }
    }
}
