package fr.xlim.ssd.opal.gui.communication.task;

import fr.xlim.ssd.opal.gui.App;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderItem;
import fr.xlim.ssd.opal.gui.model.reader.CardReaderModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Listens to card reader connected to the computer.
 *
 * This <code>Task</code> listens for card readers connected to the computer. When the list of readers changes,
 * an event will be published, and the model which contains card terminal list will be updated.
 *
 * @author David Pequegnot
 */
public class CardReaderTask extends Task<Void, List<CardReaderItem>>{
    private static final Logger logger = LoggerFactory.getLogger(CardReaderTask.class);

    private static final int DEFAULT_REFRESH_INTERVAL  = 2000;

    private int refreshInterval;
    private CardReaderModel model;

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
    }

    /**
     * Parse some options in the corresponding <code>properties</code> file.
     */
    private void getResources() {
        ResourceMap resourceMap = Application.getInstance(App.class).getContext().getResourceMap(CardReaderTask.class);

        Integer refreshInterval = resourceMap.getInteger("CardReaderManagement.refreshInterval");
        this.refreshInterval = (refreshInterval == null) ? DEFAULT_REFRESH_INTERVAL : refreshInterval;
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
        }

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
}
