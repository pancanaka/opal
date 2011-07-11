package fr.xlim.ssd.opal.gui.view.about;

import fr.xlim.ssd.opal.gui.controller.Browser;
import fr.xlim.ssd.opal.gui.event.locale.LocaleChangedEvent;
import fr.xlim.ssd.opal.gui.event.locale.LocaleListener;
import org.jdesktop.application.*;
import org.jdesktop.application.Action;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * About dialog.
 * <p/>
 * This dialog is the traditional "about the product" dialog.
 * This interface does not have window decoration, the user must click on the dialog to dispose the view.
 * <p/>
 * Displayed information are:
 * <ul>
 * <li>product name and its logo,</li>
 * <li>a description,</li>
 * <li>the version,</li>
 * <li>websites (development and official),</li>
 * <li>license and copyright.</li>
 * </ul>
 * <p/>
 * The dialog contains two panels (north and south). The first one displays the product information with a
 * white background, and the second one the license and copyright information with a light grey background.
 *
 * @author David Pequegnot
 */
public class AboutDialog extends JDialog implements LocaleListener {
    private JPanel northPanel;

    private JLabel titleLabel;
    private JPanel titlePanel;

    private JEditorPane descriptionEditorPane;

    private JPanel productInformationPanel;
    private JLabel productTitleLabel;
    private JLabel productVersionLabel;
    private JLabel productVersionNumberLabel;
    private JLabel productOfficialWebSiteLabel;
    private JEditorPane productOfficialWebSiteEditorPane;
    private JLabel productDevelopmentWebSiteLabel;
    private JEditorPane productDevelopmentWebSiteEditorPane;

    private JLabel xlimInstituteLogoLabel;
    private JPanel poweredByPanel;
    private JLabel poweredByLabel;
    private JLabel poweredByLogoLabel;

    private JPanel southPanel;
    private JEditorPane cecillEditorPane;
    private JEditorPane copyrightEditorPane;

    private String bodyRule;

    /**
     * Default constructor.
     *
     * @param parent the parent <code>Frame</code>
     */
    public AboutDialog(Frame parent) {
        super(parent);

        this.drawComponents();
    }

    /**
     * Action allowing to close the dialog.
     */
    @Action
    public void closeAboutDialog() {
        this.dispose();
    }

    /**
     * Refresh the string translations.
     * <p/>
     * Override the LocaleListener interface.
     *
     * @param event the event corresponding to the new locale
     */
    @Override
    public void localeChanged(LocaleChangedEvent event) {

    }

    /**
     * Initialize about dialog components and properties.
     */
    private void drawComponents() {
        this.setName("aboutDialog");

        // Window behavior
        this.setModal(true);
        this.setResizable(false);
        this.clickToExit(this);

        // Window preferences
        this.setMinimumSize(new Dimension(500, 300));
        this.setPreferredSize(new Dimension(500, 300));

        // Force the dialog background color
        this.setBackground(Color.WHITE);
        this.getRootPane().setBackground(Color.WHITE);
        this.getLayeredPane().setBackground(Color.WHITE);
        this.getContentPane().setBackground(Color.WHITE);

        // Remove window decorations
        this.setUndecorated(true);

        // Add a border surrounding the dialog
        Border firstLevelInnerBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        Border grayLineBorder = BorderFactory.createLineBorder(Color.GRAY, 2);
        Border compound = BorderFactory.createCompoundBorder(firstLevelInnerBorder, grayLineBorder);
        this.getRootPane().setBorder(compound);

        // Get the system font for JEditorPanes using html rendering
        Font font = UIManager.getFont("Label.font");
        this.bodyRule = "body { font-family: " + font.getFamily() + "; "
                + "font-size: " + font.getSize() + "pt; "
                + "text-align: justify;}";

        // Layout and components management
        this.getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        this.drawNorthPanel();
        this.drawTitlePanel();
        this.drawDescriptionEditorPane();
        this.northPanel.add(Box.createVerticalStrut(10));
        this.drawProductInformationPanel();
        this.drawPoweredByPanel();
        this.add(northPanel);
        this.add(Box.createVerticalGlue());
        this.drawSouthPanel();
        this.add(southPanel);

        this.refreshResources();
    }

    /**
     * Refresh about dialog resources.
     * <p/>
     * All properties from resource bundles can be refreshed using this method.
     * It is a convenient method for translate purposes.
     */
    private void refreshResources() {
        ResourceMap resourceMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getResourceMap(AboutDialog.class);

        resourceMap.injectComponents(this);
    }

    /**
     * Add a mouse listener to a component to dispose the dialog.
     * <p/>
     * Add a new <code>MouseListener</code> to the component given in parameter. The only method overridden from the
     * <code>MouseListener</code> interface is the "click event".
     * <p/>
     * Then, when the user clicks on the component in which this listener was added, the dialog window will be disposed.
     *
     * @param component the component in which adding the listener
     */
    private void clickToExit(Component component) {
        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeAboutDialog();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Nothing to do
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Nothing to do
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Nothing to do
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Nothing to do
            }
        });
    }

    /**
     * Initialize the north panel containing product information.
     * <p/>
     * Create the <code>northPanel</code> instance, add an inner empty border of 5px
     * and set the layout to a vertical <code>BoxLayout</code>.
     * <p/>
     * This method must be called before the {@link #drawTitlePanel()}, {@link #drawDescriptionEditorPane()},
     * {@link #drawProductInformationPanel()}, and {@link #drawPoweredByPanel()} methods.
     */
    private void drawNorthPanel() {
        this.northPanel = new JPanel();
        this.northPanel.setLayout(new BoxLayout(this.northPanel, BoxLayout.PAGE_AXIS));
        this.northPanel.setOpaque(false);
        Border innerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        this.northPanel.setBorder(innerBorder);
    }

    /**
     * Initialize the title panel and its content.
     * <p/>
     * This method must be called after the {@link #drawNorthPanel()} panel.
     */
    private void drawTitlePanel() {
        this.titleLabel = new JLabel();
        this.titleLabel.setName("titleLabel");
        this.titlePanel = new JPanel();
        this.titlePanel.setLayout(new BoxLayout(this.titlePanel, BoxLayout.X_AXIS));
        this.titlePanel.add(this.titleLabel);
        this.northPanel.add(titlePanel);
    }

    /**
     * Initialize the product description in a <code>JEditorPane</code>.
     * <p/>
     * The <code>JEditorPane</code> displaying the product description will use html rendering, trigger clicks on url
     * to open the user's browser.<br/>
     * It also uses a stylesheet rule enabling to have the same font as the system one (html rendering uses a serif font
     * by default which is really different from the system one).
     * Finally, the component triggers clicks to dispose the dialog window and is added to the north panel.
     */
    private void drawDescriptionEditorPane() {
        this.descriptionEditorPane = new JEditorPane();
        this.descriptionEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        this.descriptionEditorPane.setName("descriptionEditorPane");
        this.descriptionEditorPane.setEditable(false);
        ((HTMLDocument) this.descriptionEditorPane.getDocument()).getStyleSheet().addRule(this.bodyRule);
        this.descriptionEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    Browser.openUrl(e.getURL().toString().trim());
                }
            }
        });
        this.clickToExit(this.descriptionEditorPane);
        this.northPanel.add(descriptionEditorPane);
    }

    /**
     * Initialize the product information panel.
     * <p/>
     * This panel contains:
     * <ul>
     * <li>the product name,</li>
     * <li>the version,</li>
     * <li>the development website url,</li>
     * <li>and the official website url.</li>
     * </ul>
     * <p/>
     * Websites urls are displayed in <code>JEditorPanes</code> to allow clickable URLs thanks to the HTML
     * rendering and the url listener. The fonts are also modified to be compliant with system ones.
     */
    private void drawProductInformationPanel() {
        this.productInformationPanel = new JPanel();
        this.productInformationPanel.setLayout(new GridBagLayout());
        this.productInformationPanel.setBackground(Color.WHITE);
        this.productInformationPanel.setOpaque(true);

        GridBagConstraints productInformationConstraints = new GridBagConstraints();
        productInformationConstraints.ipadx = 10;

        this.productTitleLabel = new JLabel();
        this.productTitleLabel.setName("productTitleLabel");
        this.productTitleLabel.setFont(
                this.productTitleLabel.getFont().deriveFont(
                        this.productTitleLabel.getFont().getStyle() | Font.BOLD,
                        this.productTitleLabel.getFont().getSize() + 2));
        productInformationConstraints.gridx = 0;
        productInformationConstraints.gridy = 0;
        productInformationConstraints.gridwidth = GridBagConstraints.REMAINDER;
        productInformationConstraints.anchor = GridBagConstraints.WEST;
        ((GridBagLayout) this.productInformationPanel.getLayout())
                .setConstraints(this.productTitleLabel, productInformationConstraints);
        this.productInformationPanel.add(this.productTitleLabel);

        this.productVersionLabel = new JLabel();
        this.productVersionLabel.setName("productVersionLabel");
        productInformationConstraints.gridx = 0;
        productInformationConstraints.gridy = 1;
        productInformationConstraints.gridwidth = 1;
        productInformationConstraints.anchor = GridBagConstraints.EAST;
        ((GridBagLayout) this.productInformationPanel.getLayout())
                .setConstraints(this.productVersionLabel, productInformationConstraints);
        this.productInformationPanel.add(this.productVersionLabel);

        this.productVersionNumberLabel = new JLabel();
        this.productVersionNumberLabel.setName("productVersionNumberLabel");
        productInformationConstraints.gridx = 1;
        productInformationConstraints.gridy = 1;
        productInformationConstraints.weightx = 1.0;
        productInformationConstraints.anchor = GridBagConstraints.WEST;
        productInformationConstraints.fill = GridBagConstraints.HORIZONTAL;
        ((GridBagLayout) this.productInformationPanel.getLayout())
                .setConstraints(this.productVersionNumberLabel, productInformationConstraints);
        this.productInformationPanel.add(this.productVersionNumberLabel);

        this.productDevelopmentWebSiteLabel = new JLabel();
        this.productDevelopmentWebSiteLabel.setName("productDevelopmentWebSiteLabel");
        productInformationConstraints.gridx = 0;
        productInformationConstraints.gridy = 2;
        productInformationConstraints.weightx = 0.0;
        productInformationConstraints.anchor = GridBagConstraints.EAST;
        productInformationConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.productInformationPanel.getLayout())
                .setConstraints(this.productDevelopmentWebSiteLabel, productInformationConstraints);
        this.productInformationPanel.add(this.productDevelopmentWebSiteLabel);

        this.productDevelopmentWebSiteEditorPane = new JEditorPane();
        this.productDevelopmentWebSiteEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        this.productDevelopmentWebSiteEditorPane.setName("productDevelopmentWebSiteEditorPane");
        this.productDevelopmentWebSiteEditorPane.setEditable(false);
        ((HTMLDocument) this.productDevelopmentWebSiteEditorPane.getDocument()).getStyleSheet().addRule(this.bodyRule);
        this.productDevelopmentWebSiteEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    Browser.openUrl(e.getURL().toString().trim());
                }
            }
        });
        this.clickToExit(this.productDevelopmentWebSiteEditorPane);
        productInformationConstraints.gridx = 1;
        productInformationConstraints.gridy = 2;
        productInformationConstraints.weightx = 1.0;
        productInformationConstraints.anchor = GridBagConstraints.WEST;
        productInformationConstraints.fill = GridBagConstraints.HORIZONTAL;
        ((GridBagLayout) this.productInformationPanel.getLayout())
                .setConstraints(this.productDevelopmentWebSiteEditorPane, productInformationConstraints);
        this.productInformationPanel.add(this.productDevelopmentWebSiteEditorPane);

        this.productOfficialWebSiteLabel = new JLabel();
        this.productOfficialWebSiteLabel.setName("productOfficialWebSiteLabel");
        productInformationConstraints.gridx = 0;
        productInformationConstraints.gridy = 3;
        productInformationConstraints.weightx = 0.0;
        productInformationConstraints.anchor = GridBagConstraints.EAST;
        productInformationConstraints.fill = GridBagConstraints.NONE;
        ((GridBagLayout) this.productInformationPanel.getLayout())
                .setConstraints(this.productOfficialWebSiteLabel, productInformationConstraints);
        this.productInformationPanel.add(this.productOfficialWebSiteLabel);

        this.productOfficialWebSiteEditorPane = new JEditorPane();
        this.productOfficialWebSiteEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        this.productOfficialWebSiteEditorPane.setName("productOfficialWebSiteEditorPane");
        this.productOfficialWebSiteEditorPane.setEditable(false);
        ((HTMLDocument) this.productOfficialWebSiteEditorPane.getDocument()).getStyleSheet().addRule(this.bodyRule);
        this.productOfficialWebSiteEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    Browser.openUrl(e.getURL().toString().trim());
                }
            }
        });
        this.clickToExit(this.productOfficialWebSiteEditorPane);
        productInformationConstraints.gridx = 1;
        productInformationConstraints.gridy = 3;
        productInformationConstraints.weightx = 1.0;
        productInformationConstraints.anchor = GridBagConstraints.WEST;
        productInformationConstraints.fill = GridBagConstraints.HORIZONTAL;
        ((GridBagLayout) this.productInformationPanel.getLayout())
                .setConstraints(this.productOfficialWebSiteEditorPane, productInformationConstraints);
        this.productInformationPanel.add(this.productOfficialWebSiteEditorPane);

        this.northPanel.add(this.productInformationPanel);
    }

    /**
     * Initialize the "powered by" panel.
     * <p/>
     * In fact, this panel has been created to introduce the XLIM Labs and OPAL-library logos.
     */
    private void drawPoweredByPanel() {
        this.poweredByPanel = new JPanel();
        this.poweredByPanel.setLayout(new BoxLayout(this.poweredByPanel, BoxLayout.LINE_AXIS));
        this.poweredByPanel.setOpaque(false);

        this.xlimInstituteLogoLabel = new JLabel();
        this.xlimInstituteLogoLabel.setName("xlimInstituteLogoLabel");

        this.poweredByLabel = new JLabel();
        this.poweredByLabel.setName("poweredByLabel");
        this.poweredByLogoLabel = new JLabel();
        this.poweredByLogoLabel.setName("poweredByLogoLabel");

        this.poweredByPanel.add(this.xlimInstituteLogoLabel);
        this.poweredByPanel.add(this.poweredByLabel);
        this.poweredByPanel.add(this.poweredByLogoLabel);

        this.northPanel.add(this.poweredByPanel);
    }

    /**
     * Initialize the south panel and its components.
     * <p/>
     * This panel only contains the license and copyright information. <code>JEditorPanes</code> are also used,
     * allowing clickable URLs thanks to the HTML rendering and the url listener. The fonts are also modified
     * to be compliant with system ones.
     */
    private void drawSouthPanel() {
        this.add(new JSeparator(JSeparator.HORIZONTAL));

        this.southPanel = new JPanel();
        this.southPanel.setLayout(new BoxLayout(this.southPanel, BoxLayout.PAGE_AXIS));

        this.cecillEditorPane = new JEditorPane();
        this.cecillEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        this.cecillEditorPane.setName("cecillEditorPane");
        this.cecillEditorPane.setEditable(false);
        this.cecillEditorPane.setOpaque(true);
        this.cecillEditorPane.setBackground(new Color(240, 240, 240));
        ((HTMLDocument) this.cecillEditorPane.getDocument()).getStyleSheet().addRule(this.bodyRule);
        this.cecillEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    Browser.openUrl(e.getURL().toString().trim());
                }
            }
        });
        this.clickToExit(this.cecillEditorPane);
        this.southPanel.add(this.cecillEditorPane);

        this.copyrightEditorPane = new JEditorPane();
        this.copyrightEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        this.copyrightEditorPane.setName("copyrightEditorPane");
        this.copyrightEditorPane.setEditable(false);
        this.copyrightEditorPane.setOpaque(true);
        this.copyrightEditorPane.setBackground(new Color(240, 240, 240));
        ((HTMLDocument) this.copyrightEditorPane.getDocument()).getStyleSheet().addRule(this.bodyRule);
        ((HTMLDocument) this.copyrightEditorPane.getDocument()).getStyleSheet().addRule("body { text-align: center;}");
        this.copyrightEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    Browser.openUrl(e.getURL().toString().trim());
                }
            }
        });
        this.clickToExit(this.copyrightEditorPane);
        this.southPanel.add(this.copyrightEditorPane);

        this.add(southPanel);
    }
}
