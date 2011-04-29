/*
 * This is a customised observer interface used to update the DataExchangesVue class
 */

package fr.xlim.ssd.opal.gui.model.dataExchanges;

/**
 * @author CHANAA Anas
 * @author EL KHALDI Omar
 */
public interface Observer {

	public void updateALL(String change_text);
    public void updateAPDU(String change_text);
    public void updateLog(String change_text);
    public void clearALL(String change_text);
    public void clearAPDU(String change_text);
    public void clearLog(String change_text);

}