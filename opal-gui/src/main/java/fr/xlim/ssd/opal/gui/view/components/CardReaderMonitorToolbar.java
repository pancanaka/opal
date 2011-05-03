/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : David Pequegnot <david.pequegnot@etu.unilim.fr>                   *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

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
 *
 * Allows the user to select a card reader in a list (<code>JComboBox</code>).
 * All card readers connected to the computer are displayed in this list. The toolbar implements a listener on
 * the <code>CardReaderModel</code> instance.
 * The list of card readers is displayed dynamically.
 *
 * @author David Pequegnot
 */
public class CardReaderMonitorToolbar extends JToolBar implements CardReaderStateListener {

    private JLabel    cardReaderInformationLabel;
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
        add(Box.createRigidArea(new Dimension(5,5)));
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
