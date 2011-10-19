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
package fr.xlim.ssd.opal.gui.communication.task;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.model.dataExchanges.CustomLogger;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderItem;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import fr.xlim.ssd.opal.gui.tools.SmartCardListParser;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import javax.smartcardio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Listens to card reader connected to the computer.
 * <p/>
 * This <code>Task</code> listens for card readers connected to the computer. When the list of readers changes,
 * an event will be published, and the model which contains card terminal list will be updated.
 *
 * @author David Pequegnot
 * @author Tiana Razafindralambo
 */
public class CardReaderTask extends Task<Void, List<CardReaderItem>> {
    private static final CustomLogger logger = new CustomLogger();

    private static final int DEFAULT_REFRESH_INTERVAL = 2000;

    private static final int DEFAULT_INITIAL_READER_NUMBER = 10;

    private static final int DEFAULT_TIMEOUT_CARD_PRESENT = 1000;

    private int refreshInterval;
    private int timeoutCardPresent;

    private CardReaderModel model;

    private List<CardReaderItem> cardReaderItemList;
    private List<CardReaderItem> cardReaderItemListTmp;

    private HashMap<String, String> atrCache;

    /**
     * Default constructor.
     *
     * @param application the application life cycle
     * @param model       the card reader model which contains the list of card readers connected to the
     *                    computer.
     */
    public CardReaderTask(Application application, CardReaderModel model) {
        super(application);

        this.model = model;

        this.atrCache = new HashMap<String, String>();

        this.getResources();
    }

    /**
     * Parse some options in the corresponding <code>properties</code> file.
     */
    private void getResources() {
        ResourceMap resourceMap = Application.getInstance(App.class).getContext().getResourceMap(CardReaderTask.class);

        Integer refreshInterval = resourceMap.getInteger("CardReaderManagement.refreshInterval");
        this.refreshInterval = (refreshInterval == null) ? DEFAULT_REFRESH_INTERVAL : refreshInterval;

        Integer initialReaderNumber = resourceMap.getInteger("CardReaderManagement.initialCardReaderNumber");
        this.cardReaderItemList = new ArrayList<CardReaderItem>(
                (initialReaderNumber == null) ? DEFAULT_INITIAL_READER_NUMBER : initialReaderNumber);
        this.cardReaderItemListTmp = new ArrayList<CardReaderItem>(
                (initialReaderNumber == null) ? DEFAULT_INITIAL_READER_NUMBER : initialReaderNumber);

        Integer timeoutCardPresent = resourceMap.getInteger("CardReaderManagement.timeoutCardPresent");
        this.timeoutCardPresent = (timeoutCardPresent == null) ? DEFAULT_TIMEOUT_CARD_PRESENT : timeoutCardPresent;
    }

    /**
     * The <code>Task</code> operations.
     *
     * @return Nothing
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Void doInBackground() {
        message("startMessage");

        while (!isCancelled()) {
            this.populateCardReaderItemList();

            if (this.compareCardReaderItemsLists()) {
                logger.debug("Terminal list changed!");
                this.updateCardReaderItemList();
            }

            try {
                Thread.sleep(this.refreshInterval);
            } catch (InterruptedException ie) {
                if (!isCancelled()) {
                    logger.warn("Error while sleeping in terminal monitor", ie);
                }
            }
        }

/*
        // SPACE
        ArrayList<CardReaderItem> cardReaderItemsListTmp = new ArrayList<CardReaderItem>();

        while (!isCancelled()) {
            boolean changed = false;

            List<CardTerminal> cardReaderList;

            TerminalFactory factory = TerminalFactory.getDefault();
            try {
                cardReaderList = factory.terminals().list();

                if (cardReaderItemsListTmp.size() == cardReaderList.size()) {
                    for (int idx = 0; idx < cardReaderList.size(); idx++) {
                        CardTerminal card = cardReaderList.get(idx);
                        if (!cardReaderItemsListTmp.get(idx).getCardReaderName().equals(card.getName())) {
                            changed = true;
                            break;
                        }
                    }
                } else {
                    changed = true;
                }
            } catch (CardException ce) {
                cardReaderList = new ArrayList<CardTerminal>();

                if (!cardReaderItemsListTmp.isEmpty()) {
                    changed = true;
                }

                logger.error("Error while reading the terminal list", ce);
            }

            if (changed) {
                logger.debug("Terminal list has changed");
                if (!cardReaderItemsListTmp.isEmpty()) {
                    cardReaderItemsListTmp = new ArrayList<CardReaderItem>();
                }

                for (CardTerminal aTerminalList : cardReaderList) {
                    System.out.println(aTerminalList.getName());
                    cardReaderItemsListTmp.add(new CardReaderItem(aTerminalList.getName(), "Card name"));
                }
                this.publish(cardReaderItemsListTmp);
            }

            try {
                Thread.sleep(this.refreshInterval);
            } catch (InterruptedException ie) {
                if (!isCancelled()) {
                    logger.warn("Error while sleeping in terminal monitor", ie);
                }
            }
        }*/

        message("finishedMessage");
        return null;
    }

    /**
     * Publish events to model.
     *
     * @param chunks new card readers list
     */
    @Override
    protected void process(List<List<CardReaderItem>> chunks) {
        for (List<CardReaderItem> chunk : chunks) {
            logger.debug("Sending updated terminal list to model");
            this.model.setCardReaderItems(chunk);
        }
    }

    private void populateCardReaderItemList() {
        List<CardTerminal> cardReaderList;

        this.cardReaderItemListTmp.clear();

        TerminalFactory factory = TerminalFactory.getDefault();

        try {
            cardReaderList = factory.terminals().list();
        } catch (CardException ce) {
            logger.info("No terminal found");
            //logger.debug("No terminal found", ce);
            return;
        }

        if (!cardReaderList.isEmpty()) {
            for (CardTerminal cardReader : cardReaderList) {
                boolean cardFound = false;

                CardReaderItem item = new CardReaderItem();
                item.setCardReaderName(cardReader.getName());

                try {
                    cardFound = cardReader.waitForCardPresent(this.timeoutCardPresent);
                } catch (CardException ce) {
                    this.cardReaderItemListTmp.add(item);
                    logger.info("Unable to state card");
                    // logger.debug("Unable to state card", ce);
                    continue;
                }

                Card card;

                try {
                    card = cardReader.connect("*");
                } catch (CardException ce) {
                    this.cardReaderItemListTmp.add(item);
                    logger.info("Error while connecting to the card");
                    // logger.debug("Error while connecting to the card", ce);
                    continue;
                }

                CardChannel channel = card.getBasicChannel();

                item.setCardChannel(channel);

                ATR atr = card.getATR();
                String sAtr = Conversion.arrayToHex(atr.getBytes()).trim();

                item.setCardATR(new fr.xlim.ssd.opal.library.params.ATR(atr.getBytes()));

                String cardName = this.atrCache.get(sAtr);
                if (cardName == null) {
                    cardName = SmartCardListParser.getCardNameByAtr(item.getCardATR());
                }

                this.atrCache.put(sAtr, cardName);

                item.setCardName(cardName);
                this.cardReaderItemListTmp.add(item);
            }
        }
    }

    private boolean compareCardReaderItemsLists() {
        boolean changed = false;

        if (this.cardReaderItemList.size() == this.cardReaderItemListTmp.size()) {
            for (int idx = 0; idx < this.cardReaderItemList.size(); idx++) {
                CardReaderItem item = this.cardReaderItemList.get(idx);
                if (!item.equals(this.cardReaderItemListTmp.get(idx))) {
                    changed = true;
                    break;
                }
            }
        } else {
            changed = true;
        }
        return changed;
    }

    private void updateCardReaderItemList() {
        if (!this.cardReaderItemList.isEmpty()) {
            this.cardReaderItemList.clear();
        }

        for (CardReaderItem item : this.cardReaderItemListTmp) {
            this.cardReaderItemList.add(item);
        }

        this.publish(cardReaderItemList);
    }
}
