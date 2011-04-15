package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.view.components.KeyComponent;
import java.util.ArrayList;

/**
 * Container for one profile
 *
 * @author Thibault Desmoulins
 */
public class ProfileComponent implements Comparable {
    private String name, description, AID, SCPmode, TP;
    private String[] ATR;
    private ArrayList<KeyComponent> Keylist = new ArrayList<KeyComponent>();

    public ProfileComponent(String name, String description, String AID, String SCPmode, String TP, String[] ATR) {
        this.name = name;
        this.description = description;
        this.AID = AID;
        this.SCPmode = SCPmode;
        this.TP = TP;
        this.ATR = ATR;
    }

    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getAID() { return this.AID; }
    public String getSCPmode() { return this.SCPmode; }
    public String getTP() { return this.TP; }
    public String[] getATR() { return this.ATR; }
    public ArrayList getKeylist() { return this.Keylist; }

    public void addKey(KeyComponent key) {
        Keylist.add(key);
    }


    @Override
    public int compareTo(Object o) {
        ProfileComponent p2 = (ProfileComponent) o;
        return this.name.compareTo(p2.getName());
    }

}
