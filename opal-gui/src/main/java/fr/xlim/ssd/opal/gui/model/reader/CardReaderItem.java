package fr.xlim.ssd.opal.gui.model.reader;

/**
 * A simple card reader representation.
 *
 * This representation contains only two strings:
 * <ul>
 *     <li>the card reader name;</li>
 *     <li>the card name.</li>
 * </ul>
 * 
 * @author David Pequegnot
 */
public class CardReaderItem {
    private String cardReaderName;
    private String cardName;

    /**
     * Constructor.
     *
     * @param cardReaderName the name of the card reader
     * @param cardName       the name of the card
     */
    public CardReaderItem(String cardReaderName, String cardName) {
        this.setCardReaderName(cardReaderName);
        this.setCardName(cardName);
    }

    /**
     * Gets the card reader name.
     *
     * @return the card reader name
     */
    public String getCardReaderName() {
        return cardReaderName;
    }

    /**
     * Sets the card reader name.
     *
     * @param cardReaderName the card reader name to set
     */
    public void setCardReaderName(String cardReaderName) {
        this.cardReaderName = cardReaderName;
    }

    /**
     * Gets the card name.
     *
     * @return the card name
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * Sets the card name.
     *
     * @param cardName the card name to set
     */
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    /**
     * Equals method.
     *
     * The equals method has been overridden to compare two <code>CardReaderItem</code> instances by 
     * @param aThat
     * @return
     */
    @Override
    public boolean equals(Object aThat) {
        if (!(aThat instanceof CardReaderItem)) {
            return false;
        }

        if (this == aThat) {
            return true;
        }

        CardReaderItem that = (CardReaderItem) aThat;

        return
            that.getCardName().equals(this.getCardName()) &&
            that.getCardReaderName().equals(this.getCardReaderName());
    }
}
