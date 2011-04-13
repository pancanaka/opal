package fr.xlim.ssd.opal.gui.model.reader;

import fr.xlim.ssd.opal.library.params.ATR;

/**
 * A simple card reader representation.
 *
 * The card reader representation contains:
 * <ul>
 *     <li>the card reader name;</li>
 *     <li>the card name;</li>
 *     <li>the card ATR.</li>
 * </ul>
 *
 * Please notice that a card reader may not contain a card. A good representation can be an empty <code>String</code>
 * for the card name and a <code>null ATR</code> instance for the card ATR.
 * 
 * @author David Pequegnot
 */
public class CardReaderItem {
    private String cardReaderName;
    private String cardName;
    private ATR    cardATR;

    /**
     * Default constructor.
     *
     * Card reader and card names will be set to an empty <code>String</code> and the card ATR to <code>null</code>.
     */
    public CardReaderItem() {
        this("", "", null);
    }

    /**
     * Constructor.
     *
     * Card reader name will be set with the <code>cardReaderName</code> value, card name will be set to an empty
     * <code>String</code> and the card ATR to <code>null</code>.
     *
     * @param cardReaderName the card reader name
     */
    public CardReaderItem(String cardReaderName) {
        this(cardReaderName, "", null);
    }

    /**
     * Constructor.
     *
     * Card reader name will be set with the <code>cardReaderName</code> value, card name will be set to an empty
     * <code>String</code> and the card ATR with the <code>cardATR</code> instance.
     *
     * @param cardReaderName the card reader name
     * @param cardATR        the card ATR in an <code>ATR</code> instance
     */
    public CardReaderItem(String cardReaderName, ATR cardATR) {
        this(cardReaderName, "", cardATR);
    }

    /**
     * Constructor.
     *
     * Card reader name will be set with the <code>cardReaderName</code> value, card name with the <code>cardName</code>
     * value and the card ATR to <code>null</code>.
     *
     * @param cardReaderName the card reader name
     * @param cardName       the card name
     */
    public CardReaderItem(String cardReaderName, String cardName) {
        this(cardReaderName, cardName, null);
    }

    /**
     * Constructor.
     *
     * Card reader name will be set with the <code>cardReaderName</code> value, card name with the <code>cardName</code>
     * value and the card ATR the <code>cardATR</code> instance.
     *
     * @param cardReaderName the card reader name
     * @param cardName       the card name
     * @param cardATR        the card ATR in an <code>ATR</code> instance
     */
    public CardReaderItem(String cardReaderName, String cardName, ATR cardATR) {
        this.cardReaderName = cardReaderName;
        this.cardName       = cardName;
        this.cardATR        = cardATR;
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
     * Gets the card ATR.
     *
     * @return the card ATR
     * @see fr.xlim.ssd.opal.library.params.ATR
     */
    public ATR getCardATR() {
        return this.cardATR;
    }

    /**
     * Sets the card ATR.
     *
     * @param cardATR the card ATR to set
     */
    public void setCardATR(ATR cardATR) {
        this.cardATR = cardATR;
    }

    /**
     * Equals method.
     *
     * @param aThat the object to compare
     * @return <code>true</code> if object are equal
     */
    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) {
            return true;
        }
        
        if (!(aThat instanceof CardReaderItem)) {
            return false;
        }

        CardReaderItem that = (CardReaderItem) aThat;

        return
            that.getCardName().equals(this.getCardName()) &&
            that.getCardReaderName().equals(this.getCardReaderName());
    }
}
