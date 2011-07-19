package fr.xlim.ssd.opal.gui.view.components.custom.keys;

import fr.xlim.ssd.opal.gui.view.components.custom.HexadecimalJTextField;
import fr.xlim.ssd.opal.library.KeyType;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Pequegnot
 */
public class KeyComponent extends JPanel {
    private static String [] KEY_TYPES;
    static {
        KeyType [] keyTypes = KeyType.values();
        KeyComponent.KEY_TYPES = new String[keyTypes.length];
        for (int idx = 0; idx < keyTypes.length; idx++) {
            KeyComponent.KEY_TYPES[idx] = keyTypes[idx].name();
        }
    }

    private JLabel keyTypeLabel;
    private JComboBox keyTypeComboBox;

    private JLabel keyVersionNumberLabel;
    private JTextField keyVersionNumberTextField;

    private JLabel keyIdLabel;
    private JTextField keyIdTextField;

    private JLabel keyValueLabel;
    private HexadecimalJTextField keyValueTextField;

    private JButton addKeyButton;
    private JButton removeKeyButton;

    public KeyComponent() {
        super();

        this.drawComponents();
    }

    private void drawComponents() {
        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5,5,5,5);

        this.keyTypeLabel = new JLabel();
        this.keyTypeLabel.setName("keyTypeLabel");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        ((GridBagLayout) this.getLayout()).setConstraints(this.keyTypeLabel, constraints);
        this.add(this.keyTypeLabel);
        this.keyTypeComboBox = new JComboBox(KeyComponent.KEY_TYPES);
        this.keyTypeComboBox.setName("keyTypeComboBox");
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        ((GridBagLayout) this.getLayout()).setConstraints(this.keyTypeComboBox, constraints);
        this.add(this.keyTypeComboBox);

        this.keyVersionNumberLabel = new JLabel();
        this.keyVersionNumberLabel.setName("keyVersionNumberLabel");
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        ((GridBagLayout) this.getLayout()).setConstraints(this.keyVersionNumberLabel, constraints);
        this.add(this.keyVersionNumberLabel);
        this.keyVersionNumberTextField = new JTextField(3);
        this.keyVersionNumberTextField.setName("keyVersionNumberTextField");
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        ((GridBagLayout) this.getLayout()).setConstraints(this.keyVersionNumberTextField, constraints);
        this.add(this.keyVersionNumberTextField);

        this.keyIdLabel = new JLabel();
        this.keyIdLabel.setName("keyIdLabel");
        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        ((GridBagLayout) this.getLayout()).setConstraints(this.keyIdLabel, constraints);
        this.add(this.keyIdLabel);
        this.keyIdTextField = new JTextField(3);
        this.keyIdTextField.setName("keyIdTextField");
        constraints.gridx = 5;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        ((GridBagLayout) this.getLayout()).setConstraints(this.keyIdTextField, constraints);
        this.add(this.keyIdTextField);

        this.keyValueLabel = new JLabel();
        this.keyValueLabel.setName("keyValueLabel");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        ((GridBagLayout) this.getLayout()).setConstraints(this.keyValueLabel, constraints);
        this.add(this.keyValueLabel);
        this.keyValueTextField = new HexadecimalJTextField(HexadecimalJTextField.HEXADECIMAL_MODE);
        this.keyValueTextField.setName("keyValueTextField");
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        constraints.gridwidth = 5;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        ((GridBagLayout) this.getLayout()).setConstraints(this.keyValueTextField, constraints);
        this.add(this.keyValueTextField);

        this.refreshResources();
    }

    private void refreshResources() {
        ResourceMap resourceMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getResourceMap(KeyComponent.class);

        resourceMap.injectComponents(this);
    }
}
