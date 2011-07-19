package fr.xlim.ssd.opal.gui.view.components.tab.authentication;

import fr.xlim.ssd.opal.gui.controller.MainController;
import fr.xlim.ssd.opal.gui.tasks.MyTask;
import fr.xlim.ssd.opal.gui.view.components.custom.HexadecimalJTextField;
import fr.xlim.ssd.opal.gui.view.components.custom.keys.KeyPanel;
import fr.xlim.ssd.opal.gui.view.components.tab.AbstractScrollPane;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.SecLevel;
import org.jdesktop.application.*;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Authentication view in a scrollable panel.
 *
 * This is an authentication view which contains all necessary inputs to perform
 * an authentication with the OPAL library.
 *
 * @author David Pequegnot
 */
public class AuthenticationScrollPane extends AbstractScrollPane {
    /**
     * SCP modes as <code>Strings</code>.
     */
    private static String[] SCP_MODES;
    /**
     * Security levels as <code>Strings</code>.
     */
    private static String[] SECURITY_LEVELS;
    /**
     * Transmission protocols as <code>Strings</code>.
     */
    private static String[] TRANSMISSION_PROTOCOLS = {"T=0", "T=1", "*"};
    /**
     * Command implementations as <code>Strings</code>.
     */
    private static String[] IMPLEMENTATIONS = {"GP2xCommands", "GemXpresso211Commands"};
    static {
        SCPMode [] scpModes = SCPMode.values();
        AuthenticationScrollPane.SCP_MODES = new String[scpModes.length];
        for(int idx = 0; idx < scpModes.length; idx++) {
            AuthenticationScrollPane.SCP_MODES[idx] = scpModes[idx].name();
        }

        SecLevel [] secLevels = SecLevel.values();
        AuthenticationScrollPane.SECURITY_LEVELS = new String[secLevels.length];
        for (int idx = 0; idx < secLevels.length; idx++) {
            AuthenticationScrollPane.SECURITY_LEVELS[idx] = secLevels[idx].name();
        }



    }

    private MainController mainController;

    private JPanel mainPanel;
    private JPanel layoutPanel;

    private JButton loadConfigurationButton;

    private JLabel isdAidLabel;
    private HexadecimalJTextField isdAidTextField;

    private JLabel scpModeLabel;
    private JComboBox scpModeComboBox;

    private JLabel securityLevelLabel;
    private JComboBox securityLevelComboBox;

    private JLabel transmissionProtocolLabel;
    private JComboBox transmissionProtocolComboBox;

    private KeyPanel keyPanel;

    private JLabel implementationLabel;
    private JComboBox implementationComboBox;

    private JButton authenticationButton;

    /**
     * Default constructor.
     *
     * @param mainController application main controller
     */
    public AuthenticationScrollPane(MainController mainController) {
        super();
        this.mainController = mainController;

        this.drawComponents();
    }

    /**
     * Launch the authentication process.
     *
     * The controller will be called to verify and validate inputs before launching
     * the <code>Task</code> corresponding to the authentication process.
     */
    @Action
    public void doAuthentication() {
    }

    /**
     * Initialize <code>AuthenticationScrollPane</code> components and properties.
     */
    private void drawComponents() {
        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints mainConstraints = new GridBagConstraints();
        mainConstraints.insets = new Insets(5, 5, 5, 5);

        this.loadConfigurationButton = new JButton();
        this.loadConfigurationButton.setName("loadConfigurationButton");
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 0;
        mainConstraints.anchor = GridBagConstraints.EAST;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.loadConfigurationButton, mainConstraints);
        this.mainPanel.add(this.loadConfigurationButton);

        this.isdAidLabel = new JLabel();
        this.isdAidLabel.setName("isdAidLabel");
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 1;
        mainConstraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.isdAidLabel, mainConstraints);
        this.mainPanel.add(this.isdAidLabel);
        this.isdAidTextField = new HexadecimalJTextField(10, HexadecimalJTextField.HEXADECIMAL_MODE);
        this.isdAidTextField.setName("isdAidTextField");
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 1;
        mainConstraints.weightx = 1.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
        mainConstraints.fill = GridBagConstraints.HORIZONTAL;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.isdAidTextField, mainConstraints);
        this.mainPanel.add(this.isdAidTextField);

        this.scpModeLabel = new JLabel();
        this.scpModeLabel.setName("scpModeLabel");
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 2;
        mainConstraints.weightx = 0.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        mainConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.scpModeLabel, mainConstraints);
        this.mainPanel.add(this.scpModeLabel);
        this.scpModeComboBox = new JComboBox(AuthenticationScrollPane.SCP_MODES);
        this.scpModeComboBox.setName("scpModeComboBox");
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 2;
        mainConstraints.weightx = 1.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
        mainConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.scpModeComboBox, mainConstraints);
        this.mainPanel.add(this.scpModeComboBox);

        this.securityLevelLabel = new JLabel();
        this.securityLevelLabel.setName("securityLevelLabel");
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 3;
        mainConstraints.weightx = 0.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        mainConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.securityLevelLabel, mainConstraints);
        this.mainPanel.add(this.securityLevelLabel);
        this.securityLevelComboBox = new JComboBox(AuthenticationScrollPane.SECURITY_LEVELS);
        this.securityLevelComboBox.setName("securityLevelComboBox");
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 3;
        mainConstraints.weightx = 1.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
        mainConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.securityLevelComboBox, mainConstraints);
        this.mainPanel.add(this.securityLevelComboBox);

        this.transmissionProtocolLabel = new JLabel();
        this.transmissionProtocolLabel.setName("transmissionProtocolLabel");
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 4;
        mainConstraints.weightx = 0.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        mainConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.transmissionProtocolLabel, mainConstraints);
        this.mainPanel.add(this.transmissionProtocolLabel);
        this.transmissionProtocolComboBox = new JComboBox(AuthenticationScrollPane.TRANSMISSION_PROTOCOLS);
        this.transmissionProtocolComboBox.setName("transmissionProtocolComboBox");
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 4;
        mainConstraints.weightx = 1.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
        mainConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.transmissionProtocolComboBox, mainConstraints);
        this.mainPanel.add(this.transmissionProtocolComboBox);

        this.keyPanel = new KeyPanel();
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 5;
        mainConstraints.weightx = 1.0;
        mainConstraints.gridwidth = 2;
        mainConstraints.anchor = GridBagConstraints.BASELINE;
        mainConstraints.fill = GridBagConstraints.HORIZONTAL;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.keyPanel, mainConstraints);
        this.mainPanel.add(this.keyPanel);

        this.implementationLabel = new JLabel();
        this.implementationLabel.setName("implementationLabel");
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 6;
        mainConstraints.weightx = 0.0;
        mainConstraints.gridwidth = 1;
        mainConstraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        mainConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.implementationLabel, mainConstraints);
        this.mainPanel.add(this.implementationLabel);
        this.implementationComboBox = new JComboBox(AuthenticationScrollPane.IMPLEMENTATIONS);
        this.implementationComboBox.setName("implementationComboBox");
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 6;
        mainConstraints.weightx = 1.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
        mainConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.implementationComboBox, mainConstraints);
        this.mainPanel.add(this.implementationComboBox);

        this.authenticationButton = new JButton();
        this.authenticationButton.setName("authenticationButton");
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 7;
        mainConstraints.weightx = 0.0;
        mainConstraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        ((GridBagLayout) this.mainPanel.getLayout()).setConstraints(this.authenticationButton, mainConstraints);
        this.mainPanel.add(this.authenticationButton);

        this.layoutPanel = new JPanel();
        this.layoutPanel.setLayout(new BorderLayout());
        this.layoutPanel.add(this.mainPanel, BorderLayout.NORTH);

        this.setViewportView(this.layoutPanel);
        this.refreshResources();
    }

    /**
     * Refresh <code>AuthenticationScrollPane</code> resources.
     *
     * All properties from resource bundles can be refreshed using this method.
     * It is a convenient method for translate purposes.
     */
    private void refreshResources() {
        ResourceMap resourceMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getResourceMap(AuthenticationScrollPane.class);

        resourceMap.injectComponents(this);

        ActionMap actionMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getActionMap(AuthenticationScrollPane.class, this);
        this.authenticationButton.setAction(actionMap.get("doAuthentication"));
    }
}
