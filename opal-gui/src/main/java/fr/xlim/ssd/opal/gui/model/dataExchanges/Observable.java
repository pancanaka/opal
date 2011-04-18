/*
 * This interface is used by the DataExchangesModel class
 */

package fr.xlim.ssd.opal.gui.model.dataExchanges;

/**
 * @author Chanaa Anas
 * @author EL KHALDI Omar
 */
public interface Observable {

    public void addObserver(Observer obver);
    public void displayAllAPDUSendings();
    public void displayAllLogSendings();
    public void displayApduSending();
    public void displayLogSending();
    public void clearAll();
    public void clearAPDU();
    public void clearLogging();

}