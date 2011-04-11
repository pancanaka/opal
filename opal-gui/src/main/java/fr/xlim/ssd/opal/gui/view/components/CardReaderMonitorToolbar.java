package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.controller.MainController;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Pequegnot
 */
public class CardReaderMonitorToolbar extends JToolBar {

    public CardReaderMonitorToolbar(MainController mainController) {
        super();

        this.mainController = mainController;

        this.drawComponents();
    }

    private void drawComponents() {
        setName("cardReaderToolbar");

        setBorderPainted(true);

        this.cardReaderInformationLabel = new JLabel();
        this.cardReaderInformationLabel.setName("cardReaderInformationLabel");

        this.cardReaderComboBox = new JComboBox();
        this.cardReaderComboBox.setEditable(false);

        add(this.cardReaderInformationLabel);
        add(Box.createRigidArea(new Dimension(5,5)));
        add(this.cardReaderComboBox);

        add(Box.createHorizontalGlue());

        this.refreshResources();
    }

    private void refreshResources() {
        /* Get the resource map for the TerminalToolBar component */
        ResourceMap resourceMap = Application
                .getInstance(App.class)
                .getContext()
                .getResourceMap(CardReaderMonitorToolbar.class);

        //this.terminalStateChanged(null);

        resourceMap.injectComponents(this);
    }

    private JLabel    cardReaderInformationLabel;
    private JComboBox cardReaderComboBox;

    private MainController mainController;
}
