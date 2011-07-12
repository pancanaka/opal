package fr.xlim.ssd.opal.gui.view.components.custom;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Document for hexadecimal entry.
 *
 * This class extends {@link LimitedSizePlainDocument} to manage hexadecimal entries in Swing components
 * (<code>JTextField</code>, <code>JComboBox</code>, <i>etc.</i>).
 *
 * Hexadecimal inputs are a subset of alphanumeric characters (only A to F characters and digits are allowed).
 * For instance:</br>
 * <code>48 65 6C 6C 6F 21</code>
 *
 * For convenience, the digits are grouped automatically by two.
 *
 * @author David Pequegnot
 */
public class HexadecimalPlainDocument extends LimitedSizePlainDocument {
    /**
     * The list of accepted characters (digits, characters from A to F and space).
     */
    public static final String ACCEPTED_CHARS = "0123456789ABCDEF ";

    /**
     * Default constructor.
     */
    public HexadecimalPlainDocument() {
        super();
    }

    /**
     * Constructor with a maximum length.
     *
     * The document will be limited to the number of characters given in parameter.
     *
     * @param maximumLength the document maximum length
     * @throws IllegalArgumentException if the <code>maximumLength</code> parameter is negative or equal to 0
     */
    public HexadecimalPlainDocument(int maximumLength) {
        super(maximumLength);
        if (maximumLength <= 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative or equal to 0.");
        }
        this.setMaximumLength(maximumLength);
    }

    /**
     * Inserts some content into the document.
     *
     * The content will be limited to the number of characters defined by the document maximum
     * length. Moreover, the input will be formatted to obtain a user friendly string where
     * each byte is represented by a group of two hexadecimal characters.
     * Then the parent insertString method will be called to perform the writing.
     *
     * Inserting content causes a write lock to be held while the actual changes are taking
     * place, followed by notification to the observers on the thread that grabbed the write lock.
     *
     * This method is thread safe, although most Swing methods are not. Please see Threads and Swing
     * for more information.
     *
     * @param offset the starting offset >= 0
     * @param str the string to insert; does nothing with null/empty strings
     * @param attr the attributes for the inserted content
     * @throws BadLocationException
     */
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }

        int availableCharacters = this.getMaximumLength() - (super.getLength() + str.length());

        if (availableCharacters < 0) {
            str = str.substring(0, (super.getLength() + str.length()) - this.getMaximumLength() - 1);
        }

        if (str.equals("")) {
            return;
        }

        for (int idx = 0; idx < str.length(); idx++) {
            if (ACCEPTED_CHARS.indexOf(Character.toUpperCase(str.charAt(idx))) == -1) {
                return;
            }
        }

        if ((offset + 1) % 3 == 0) {
            super.insertString(offset, " ", attr);
            offset += 1;
        }

        String result = "";
        int counter = offset + 1;
        for (int idx = 0; idx < str.length(); idx++) {
            if (str.charAt(idx) == ' ') {
                if (counter != offset) {
                    if (counter % 3 == 0) {
                        result += ' ';
                        counter += 1;
                    }
                }
            } else {
                if (counter != offset) {
                    if (counter % 3 == 0) {
                        result += ' ';
                        counter += 1;
                    }
                }
                result += Character.toUpperCase(Character.toUpperCase(str.charAt(idx)));
                counter += 1;
            }
        }

        super.insertString(offset, result, attr);
    }
}
