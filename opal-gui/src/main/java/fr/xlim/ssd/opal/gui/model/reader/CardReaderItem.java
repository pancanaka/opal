/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.gui.model.reader;

import fr.xlim.ssd.opal.library.params.ATR;

import javax.smartcardio.CardChannel;

/**
 * A simple card reader representation.
 * <p/>
 * The card reader representation contains:
 * import org.jdesktop.application.Application;d the model which contains cardd the model which contains card
 * import org.jdesktop.application.Application;d the model which contains cardd the model which contains card
 * <ul>
 * <li>the card reader name;</li>
 * <li>the card name;</li>
 * <li>the card ATR.</li>
 * </ul>
 * <p/>
 * Please notice that a card reader may not contain a card. A good representation can be an empty <code>String</code>
 * for the card name and a <code>null ATR</code> instance for the card ATR.
 *
 * @author David Pequegnot
 * @author Tiana Razafindralambo
 */
public class CardReaderItem {
    private String cardReaderName;
    private String cardName;
    private ATR cardATR;
    private CardChannel channel;

    /**
     * Default constructor.
     * <p/>
     * Card reader and card names will be set to an empty <code>String</code> and the card ATR to <code>null</code>.
     */
    public CardReaderItem() {
        this("", "", null);
    }

    /**
     * Constructor.
     * <p/>
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
     * <p/>
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
     * <p/>
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
     * <p/>
     * Card reader name will be set with the <code>cardReaderName</code> value, card name with the <code>cardName</code>
     * value and the card ATR the <code>cardATR</code> instance.
     *
     * @param cardReaderName the card reader name
     * @param cardName       the card name
     * @param cardATR        the card ATR in an <code>ATR</code> instance
     */
    public CardReaderItem(String cardReaderName, String cardName, ATR cardATR) {
        this.cardReaderName = cardReaderName;
        this.cardName = cardName;
        this.cardATR = cardATR;
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
     * @return
     */
    public CardChannel getCardChannel() {
        return this.channel;
    }

    /**
     * Sets the card channel
     *
     * @param channel the card channel to set
     */
    public void setCardChannel(CardChannel channel) {
        this.channel = channel;
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
