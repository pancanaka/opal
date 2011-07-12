package fr.xlim.ssd.opal.gui.view.components.custom;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Document extending <code>PlainDocument</code> with a maximum length.
 *
 * This document is limited in length. Then any attempt to write a new character will fail silently
 * (no character will be displayed).
 *
 * By default, the document is limited to <code>Integer.MAX_VALUE</code> characters.
 *
 * @author David Pequegnot
 */
public class LimitedSizePlainDocument extends PlainDocument {
    private int maximumLength = Integer.MAX_VALUE;

    /**
     * Default constructor.
     *
     * The document will be limited to <code>Integer.MAX_VALUE</code> characters.
     */
    public LimitedSizePlainDocument() {
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
    public LimitedSizePlainDocument(int maximumLength) throws IllegalArgumentException {
        super();
        if (maximumLength <= 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative or equal to 0.");
        }
        this.maximumLength = maximumLength;
    }

    /**
     * Set the document maximum length.
     *
     * The maximum length parameter cannot be negative or equal to 0, else an <code>IllegalArgumentException</code>
     * exception will be thrown.
     *
     * @param maximumLength the document maximum length
     * @throws IllegalArgumentException if the <code>maximumLength</code> parameter is negative or equal to 0
     */
    public void setMaximumLength(int maximumLength) throws IllegalArgumentException {
        if (maximumLength <= 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative or equal to 0.");
        }
        this.maximumLength = maximumLength;
    }

    /**
     * Get the document maximum length.
     *
     * @return the document maximum length.
     */
    public int getMaximumLength() {
        return this.maximumLength;
    }

    /**
     * Inserts some content into the document.
     *
     * The content will be limited to the number of characters defined by the document maximum
     * length. Then the parent insertString method will be called to perform the writing.
     *
     * Inserting content causes a write lock to be held while the actual changes are taking
     * place, followed by notification to the observers on the thread that grabbed the write lock.
     *
     * This method is thread safe, although most Swing methods are not. Please see Threads and Swing
     * for more information.

     * @param offset the starting offset >= 0
     * @param str the string to insert; does nothing with null/empty strings
     * @param attr the attributes for the inserted content
     * @throws BadLocationException  the given insert position is not a valid position within the document
     */
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        int availableCharacters = this.getMaximumLength() - (super.getLength() + str.length());

        if (availableCharacters < 0) {
            str = str.substring(0, (super.getLength() + str.length()) - this.getMaximumLength() - 1);
        }

        if (str.equals("")) {
            return;
        }

        super.insertString(offset, str, attr);
    }
}
