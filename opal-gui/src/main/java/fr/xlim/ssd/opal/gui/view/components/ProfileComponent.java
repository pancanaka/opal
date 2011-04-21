package fr.xlim.ssd.opal.gui.view.components;

import java.util.ArrayList;

/**
 * Container for one profile
 *
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 */
public class ProfileComponent implements Comparable {
    private String name, description, AID, SCPmode, TP, implementation;
    private String[] ATR;
    private ArrayList<KeyComponent> Keylist = new ArrayList<KeyComponent>();

    public ProfileComponent(String name, String description, String AID, String SCPmode, String TP, String[] ATR, String implementation) {
        this.name = name;
        this.description = description;
        this.AID = AID;
        this.SCPmode = SCPmode;
        this.TP = TP;
        this.ATR = ATR;
        this.implementation = implementation;
    }

    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getAID() { return this.AID; }
    public String getSCPmode() { return this.SCPmode; }
    public String getTP() { return this.TP; }
    public String getImplementation() { return this.implementation; }
    public String[] getATR() { return this.ATR; }
    public ArrayList getKeylist() { return this.Keylist; }

    public void addKey(KeyComponent key) {
        Keylist.add(key);
    }


    @Override
    public int compareTo(Object o) {
        ProfileComponent p2 = (ProfileComponent) o;
        return this.name.compareToIgnoreCase(p2.getName());
    }

}
