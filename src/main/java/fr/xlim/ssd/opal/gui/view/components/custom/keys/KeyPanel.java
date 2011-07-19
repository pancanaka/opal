package fr.xlim.ssd.opal.gui.view.components.custom.keys;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author David Pequegnot
 */
public class KeyPanel extends JPanel {
    private final static String PANEL = "panel";
    private final static String KEY_COMPONENT = "keyComponent";
    private final static String REMOVE_KEY_COMPONENT_BUTTON = "removeKeyComponentButton";
    private int componentCounter = 1;

    private JButton addKeyButton;

    public KeyPanel() {
        super();

        this.drawComponents();
    }

    @Action
    public void addKeyComponent() {
        JPanel panel = new JPanel();
        panel.setName(KeyPanel.PANEL + Integer.toString(this.componentCounter + 1));
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(new KeyComponent());
        this.addRemoveButton(panel);

        this.add(panel, this.componentCounter);
        this.componentCounter += 1;

        // Specific case of first panel which displays the remove item only if there is more than one key component
        Component [] keyPanelComponents = this.getComponents();
        for (Component keyPanelComponent : keyPanelComponents) {
            if (keyPanelComponent.getName().equals(KeyPanel.PANEL + "1")) {
                this.addRemoveButton((JPanel) keyPanelComponent);
                break;
            }
        }

        this.revalidate();
    }

    @Action
    public void removeKeyComponent(ActionEvent event) {
        JButton button = (JButton) event.getSource();
        String counter = button.getName()
                .substring(KeyPanel.REMOVE_KEY_COMPONENT_BUTTON.length(), button.getName().length());

        // Remove the corresponding panel and rename the others
        Component [] components = this.getComponents();
        for (Component component : components) {
            if (component.getName() != null &&
                    component.getName().equals(KeyPanel.PANEL + counter)) {
                this.remove(component);
            } else if (component.getName() != null &&
                    component.getName().contains(KeyPanel.PANEL)) {
                String panelCounter = component.getName()
                        .substring(KeyPanel.PANEL.length(), component.getName().length());
                if (Integer.valueOf(panelCounter) > Integer.valueOf(counter)) {
                    component.setName(KeyPanel.PANEL + Integer.toString(Integer.valueOf(panelCounter) - 1));
                    for (Component panelComponent : ((JPanel) component).getComponents()) {
                        if (panelComponent.getName() != null &&
                                panelComponent.getName().contains(KeyPanel.KEY_COMPONENT)) {
                            panelComponent.setName(KeyPanel.KEY_COMPONENT + Integer.toString(Integer.valueOf(panelCounter) - 1));
                        } else if (panelComponent.getName() != null &&
                                panelComponent.getName().contains(KeyPanel.REMOVE_KEY_COMPONENT_BUTTON)) {
                            panelComponent.setName(KeyPanel.REMOVE_KEY_COMPONENT_BUTTON + Integer.toString(Integer.valueOf(panelCounter) - 1));
                        }
                    }
                }
            }
        }

        this.componentCounter -= 1;

        if (this.componentCounter == 1) {
            components = this.getComponents();
            for (Component component : components) {
                if (component.getName() != null &&
                        component.getName().equals(KeyPanel.PANEL + "1")) {
                    for (Component panelComponent : ((JPanel) component).getComponents()) {
                        if (panelComponent.getName() != null &&
                                panelComponent.getName().contains(KeyPanel.REMOVE_KEY_COMPONENT_BUTTON)) {
                            ((JPanel) component).remove(panelComponent);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        this.revalidate();
    }

    private void drawComponents() {
        ResourceMap resourceMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getResourceMap(KeyPanel.class);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createTitledBorder(resourceMap.getString("border.text")));

        JPanel panel = new JPanel();
        panel.setName(KeyPanel.PANEL + Integer.toString(this.componentCounter));
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        KeyComponent keyComponent = new KeyComponent();
        keyComponent.setName(KeyPanel.KEY_COMPONENT + Integer.toString(1));
        panel.add(new KeyComponent());

        this.add(panel);

        JPanel addKeyPanel = new JPanel();
        addKeyPanel.setName("addKeyComponentPanel");
        addKeyPanel.setLayout(new BoxLayout(addKeyPanel, BoxLayout.LINE_AXIS));
        this.addKeyButton = new JButton();
        this.addKeyButton.setName("addKeyComponentButton");
        this.addKeyButton.setMargin(new Insets(0, 0, 0, 0));
        this.addKeyButton.setPreferredSize(new Dimension(20,20));
        addKeyPanel.add(Box.createHorizontalGlue());
        addKeyPanel.add(this.addKeyButton);

        this.add(addKeyPanel);

        this.refreshResources();
    }

    private void refreshResources() {
        ResourceMap resourceMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getResourceMap(KeyPanel.class);

        resourceMap.injectComponents(this);

        ActionMap actionMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getActionMap(KeyPanel.class, this);
        this.addKeyButton.setAction(actionMap.get("addKeyComponent"));
    }

    private void addRemoveButton(JPanel panel) {
        // Find the current
        String counter = panel.getName().substring(KeyPanel.PANEL.length(), panel.getName().length());

        Component[] components = panel.getComponents();

        boolean componentFound = false;
        for (Component component : components) {
            if (component.getName() != null &&
                    component.getName().contains(KeyPanel.REMOVE_KEY_COMPONENT_BUTTON)) {
                componentFound = true;
                break;
            }
        }

        if (!componentFound) {
            JButton removeButton = new JButton();
            removeButton.setName(KeyPanel.REMOVE_KEY_COMPONENT_BUTTON + counter);
            removeButton.setPreferredSize(new Dimension(20, 20));
            removeButton.setMargin(new Insets(0, 0, 0, 0));
            removeButton.setText("-");

            ActionMap actionMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getActionMap(KeyPanel.class, this);
            removeButton.setAction(actionMap.get("removeKeyComponent"));

            panel.add(removeButton);
        }
    }
}
