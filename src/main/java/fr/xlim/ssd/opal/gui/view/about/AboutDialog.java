package fr.xlim.ssd.opal.gui.view.about;

import fr.xlim.ssd.opal.gui.controller.Browser;
import org.codehaus.janino.Java;
import org.jdesktop.application.*;
import org.jdesktop.application.Action;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;

/**
 * About dialog.
 *
 * This dialog displays some information concerning the application.
 *
 * @author David Pequegnot
 */
public class AboutDialog extends JDialog {
    private JButton closeButton;

    private JLabel titleLabel;

    private JLabel contributorsLabel;

    private JLabel copyrightLabel;
    private JLabel licenseLabel;

    private JEditorPane descriptionEditorPane;

    public AboutDialog(Frame parent) {
        super(parent);

        this.drawComponents();

        this.getRootPane().setDefaultButton(this.closeButton);
        this.setModal(true);
        this.setResizable(false);
    }

    @Action
    public void closeAboutDialog() {
        this.dispose();
    }

    private void drawComponents() {
        this.setName("aboutDialog");

        this.titleLabel = new JLabel();
        this.titleLabel.setName("titleLabel");
        this.titleLabel.setFont(this.titleLabel.getFont().deriveFont(this.titleLabel.getFont().getStyle() | Font.BOLD, this.titleLabel.getFont().getSize() + 4));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
        titlePanel.add(this.titleLabel);
        titlePanel.add(Box.createHorizontalGlue());

        this.contributorsLabel = new JLabel();
        this.contributorsLabel.setName("contributorsLabel");
        this.copyrightLabel = new JLabel();
        this.copyrightLabel.setName("copyrightLabel");
        this.licenseLabel = new JLabel();
        this.licenseLabel.setName("licenseLabel");

        this.descriptionEditorPane = new JEditorPane();
        this.descriptionEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        this.descriptionEditorPane.setName("descriptionEditorPane");
        this.descriptionEditorPane.setEditable(false);
        this.descriptionEditorPane.setOpaque(false);
        this.descriptionEditorPane.setVisible(true);
        this.descriptionEditorPane.setEnabled(true);
        this.descriptionEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    Browser.openUrl(e.getURL().toString().trim());
                }
            }
        });

        this.closeButton = new JButton();
        this.closeButton.setName("closeButton");

        this.getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        this.add(titlePanel);
        this.add(this.descriptionEditorPane);
        this.add(Box.createHorizontalGlue());
        this.add(this.closeButton);

        //this.setSize(new Dimension(400,500));
        this.setMinimumSize(new Dimension(400,500));
        this.setPreferredSize(new Dimension(400, 500));
        this.refreshResources();

    }

    private void refreshResources() {
        ResourceMap resourceMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getResourceMap(AboutDialog.class);

        resourceMap.injectComponents(this);

        ActionMap actionMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getActionMap(AboutDialog.class, this);
        this.closeButton.setAction(actionMap.get("closeAboutDialog"));
    }
}
