package fr.xlim.ssd.opal.gui.view.components.custom;

import org.jdesktop.application.*;
import org.jdesktop.application.Action;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.event.*;

/**
 * Custom <code>JTextField</code> for AID entries.
 *
 * This text field allow two entry modes: ASCII and hexadecimal.
 * The user can select the one or the other thanks to a popup menu enabled by a right click,
 * or a shortcut.
 *
 * Thus, the popup allows to switch from hexadecimal mode to ASCII mode and <i>vice-versa</i>.
 * The switch will also convert the value from hexadecimal to ASCII.
 * Shortcuts make the switch easier:
 * <ul>
 *     <li>ALT-A to use the ASCII mode,</li>
 *     <lI>ALT-H to use the hexadecimal mode.</lI>
 * </ul>
 * @author David Pequegnotd
 */
public class AIDJTextField extends JTextField {
    /**
     * ASCII mode.
     */
    public static final int ASCII_MODE = 1;
    /**
     * Hexadecimal mode.
     */
    public static final int HEXADECIMAL_MODE = 2;

    private int length = Short.MAX_VALUE;

    private final JPopupMenu conversionMenu = new JPopupMenu();
    private final JMenuItem hexadecimalMenuItem = new JMenuItem();
    private final JMenuItem asciiMenuItem = new JMenuItem();

    private Document hexadecimalDocument;
    private Document asciiDocument;

    private int currentEditionMode;

    /**
     * Default constructor.
     *
     * The text field length will be set to <code>Short.MAX_VALUE</code>
     * and the initial edition mode <code>HEXADECIMAL_MODE</code>.
     */
    public AIDJTextField() {
        this(AIDJTextField.HEXADECIMAL_MODE);
    }

    /**
     * Constructor with edition mode as parameter.
     *
     * The text field length will be set to <code>Short.MAX_VALUE</code>
     * and the initial edition mode must be given as parameter.
     *
     * The initial edition mode can be:
     * <ul>
     *     <li>ASCII_MODE: the text field uses the ASCII mode,</li>
     *     <li>HEXADECIMAL_MODE: the text field uses the hexadecimal mode.</li>
     * </ul>
     *
     * @param editionMode the edition mode (<code>ASCII_MODE</code> or
     *                    <code>HEXADECIMAL_MODE</code>).
     * @throws IllegalArgumentException if the edition mode is not supported (wrong integer)
     */
    public AIDJTextField(int editionMode) throws IllegalArgumentException {
        super();

        if (!checkEditionMode(editionMode)) {
            throw new IllegalArgumentException("Wrong edition mode value.");
        }

        this.currentEditionMode = editionMode;

        this.initComponent();
    }

    /**
     * Constructor with length and edition mode as parameter.
     *
     * The text field length and the initial edition mode must
     * be given as parameter.
     *
     * The initial edition mode can be:
     * <ul>
     *     <li>ASCII_MODE: the text field uses the ASCII mode,</li>
     *     <li>HEXADECIMAL_MODE: the text field uses the hexadecimal mode.</li>
     * </ul>
     *
     * @param length      the text field length (ASCII mode)
     * @param editionMode the edition mode (<code>ASCII_MODE</code> or
     *                    <code>HEXADECIMAL_MODE</code>).
     * @throws IllegalArgumentException if the edition mode is not supported (wrong integer)
     */
    public AIDJTextField(int length, int editionMode) throws IllegalArgumentException {
        super();

        if (!checkEditionMode(editionMode)) {
            throw new IllegalArgumentException("Wrong edition mode value.");
        }

        if (length <= 0) {
            throw new IllegalArgumentException("Length cannot be negative or equal to 0.");
        }

        this.currentEditionMode = editionMode;
        this.length = length;

        this.initComponent();
    }

    /**
     * Return the AID contained in the text field as an hexadecimal value.
     *
     * @return the AID as an hexadecimal value
     */
    public String getAID() {
        if (this.currentEditionMode == AIDJTextField.HEXADECIMAL_MODE)
            return this.getText().trim();
        else
            return this.convertAsciiToHexadecimal();
    }

    /**
     * Switch to hexadecimal edition action.
     *
     * The content will also be converted from ASCII to hexadecimal.
     */
    @Action
    public void switchToHexadecimalEdition() {
        String value = this.convertAsciiToHexadecimal();
        this.currentEditionMode = AIDJTextField.HEXADECIMAL_MODE;
        this.setDocument();
        this.setText(value);
    }

    /**
     * Switch to ASCII edition action.
     *
     * The content will also be converted from hexadecimal to ASCII.
     */
    @Action
    public void switchToAsciiEdition() {
        String value = this.convertHexadecimalToAscii();
        this.currentEditionMode = AIDJTextField.ASCII_MODE;
        this.setDocument();
        this.setText(value);
    }

    /**
     * Contain all instructions to draw component of the <code>AIDJTextField</code>.
     */
    private void initComponent() {
        this.hexadecimalMenuItem.setName("hexadecimalMenuItem");

        this.asciiDocument = (Document) new LimitedSizePlainDocument(this.length);
        this.hexadecimalDocument = (Document) new HexadecimalPlainDocument((this.length == 1)?2:this.length * 2 + this.length - 1);

        this.asciiMenuItem.setName("asciiMenuItem");

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getModifiers() == KeyEvent.ALT_MASK && Character.toLowerCase(e.getKeyChar()) == 'a' && currentEditionMode == AIDJTextField.HEXADECIMAL_MODE) {
                    switchToAsciiEdition();
                } else if (e.getModifiers() == KeyEvent.ALT_MASK && Character.toLowerCase(e.getKeyChar()) == 'h' && currentEditionMode == AIDJTextField.ASCII_MODE) {
                    switchToHexadecimalEdition();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            private void showPopupMenu(MouseEvent e) {
                conversionMenu.removeAll();

                if (currentEditionMode == AIDJTextField.HEXADECIMAL_MODE) {
                    conversionMenu.add(asciiMenuItem);
                } else if (currentEditionMode == AIDJTextField.ASCII_MODE) {
                    conversionMenu.add(hexadecimalMenuItem);
                } else {
                    throw new IllegalArgumentException("Wrong edition mode value.");
                }

                conversionMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        this.setDocument();

        this.refreshResources();
    }

    /**
     * Refresh text field resources.
     *
     * All properties from resource bundles can be refreshed using this method.
     * It is a convenient method for translate purposes.
     */
    private void refreshResources() {
        ActionMap actionMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getActionMap(AIDJTextField.class, this);
        this.hexadecimalMenuItem.setAction(actionMap.get("switchToHexadecimalEdition"));
        this.asciiMenuItem.setAction(actionMap.get("switchToAsciiEdition"));
    }

    /**
     * Check if the edition mode passed as an integer exists.
     *
     * @param editionMode the integer to test
     * @return <code>true</code> if the integer is an edition mode, else it returns <code>false</code>
     */
    private boolean checkEditionMode(int editionMode) {
        if (editionMode != AIDJTextField.ASCII_MODE && editionMode != AIDJTextField.HEXADECIMAL_MODE) {
            return false;
        }
        return true;
    }

    /**
     * Set the document to use in the text field.
     *
     * There are two documents types: one just limits the number of characters and
     * the second one is specific to hexadecimal mode.
     */
    private void setDocument() {
        if (this.currentEditionMode == AIDJTextField.ASCII_MODE) {
            this.setDocument(this.asciiDocument);
        } else if (this.currentEditionMode == AIDJTextField.HEXADECIMAL_MODE) {
            this.setDocument(this.hexadecimalDocument);
        }
    }

    /**
     * Convert the text field ASCII value to hexadecimal.
     *
     * @return the converted value
     */
    private String convertAsciiToHexadecimal() {
        String converted = "";
        String toConvert = this.getText().trim();

        for (int idx = 0; idx < toConvert.length(); idx++) {
            String item = Integer.toHexString((int) toConvert.charAt(idx));
            if (item.length() == 1) {
                item = '0' + item;
            }
            converted += item + " ";
        }

        return converted.trim();
    }

    /**
     * Convert the text field hexadecimal value to ASCII.
     *
     * @return the converted value
     */
    private String convertHexadecimalToAscii() {
        String converted = "";

        String item = "";
        int idx = 0;

        while (idx < this.getText().length()) {
            if (this.getText().charAt(idx) != ' ') {
                item += this.getText().charAt(idx);
            }
            if (item.length() == 2) {
                converted += (char) Integer.parseInt(item, 16);
                item = "";
            }
            idx++;
        }

        if (item.length() == 1) {
            item = '0' + item;
            converted += (char) Integer.parseInt(item, 16);
        }

        return converted.trim();
    }
}