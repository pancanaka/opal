package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.MainController;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Pequegnot
 */
public class AuthenticationScrollPane extends AbstractScrollPane {
    private MainController mainController;

    private JButton loadConfigurationButton;

    private JLabel isdAisLabel;
    private JTextField isdAisTextField;

    private JLabel scpModeLabel;
    private JComboBox scpModeComboBox;

    private JLabel securityLevelLabel;
    private JComboBox securityLevelComboBox;

    private JLabel transmissionProtocolLabel;
    private JComboBox transmissionProtocolComboBox;

    private JPanel keyPanel;

    private JLabel implementationLabel;
    private JComboBox implementationComboBox;

    private JButton authenticationButton;

    public AuthenticationScrollPane(MainController mainController) {
        super();
        this.mainController = mainController;

        this.drawComponents();
    }

    private void drawComponents() {
        this.setLayout(new GridBagLayout());

        GridBagConstraints mainConstraints = new GridBagConstraints();


    }
}
