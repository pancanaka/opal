package fr.xlim.ssd.opal.gui.model.authentication;

import fr.xlim.ssd.opal.gui.tools.HexadecimalTools;
import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.SecLevel;
import java.util.ArrayList;

/**
 * The authentication model.
 *
 * @author David Pequegnot
 */
public class AuthenticationModel {
    private String isdAID = "";
    private SCPMode scpMode = SCPMode.SCP_UNDEFINED;
    private SecLevel secLevel = SecLevel.NO_SECURITY_LEVEL;
    private String transmissionProtocol = "*";
    private ArrayList<KeyComponent> keyComponents = new ArrayList();
    private String command = "GP2xCommands";

    /**
     * Default constructor.
     */
    public AuthenticationModel() {
    }

    /**
     * Get the ISD AID.
     *
     * @return the ISD AID
     */
    public String getIsdAID() {
        return this.isdAID;
    }

    /**
     * Set the ISD AID.
     *
     * @param isdAID the ISD AID to set
     * @throws IllegalArgumentException if the ISD AID is not an hexadecimal value
     */
    public void setIsdAID(String isdAID) throws IllegalArgumentException {
        if (!HexadecimalTools.isHexadecimalValue(isdAID)) {
            throw new IllegalArgumentException("ISD AID parameter is a malformed hexadecimal value.");
        }
        this.isdAID = isdAID;
    }

    /**
     * Get the SCP mode.
     *
     * @return the SCP mode
     */
    public SCPMode getScpMode() {
        return scpMode;
    }

    /**
     * Set the SCP mode.
     *
     * @param scpMode the SCP mode to set
     */
    public void setScpMode(SCPMode scpMode) {
        this.scpMode = scpMode;
    }

    /**
     * Get the security level.
     *
     * @return the security level
     */
    public SecLevel getSecLevel() {
        return secLevel;
    }

    /**
     * Set the security level.
     *
     * @param secLevel the security level to set
     */
    public void setSecLevel(SecLevel secLevel) {
        this.secLevel = secLevel;
    }

    /**
     * Get the transmission protocol.
     *
     * @return the transmission protocol
     */
    public String getTransmissionProtocol() {
        return transmissionProtocol;
    }

    /**
     * Set the transmission protocol.
     *
     * Only *, T=0 and T=1 values are allowed.
     *
     * @param transmissionProtocol the transmission protocol to set
     * @throws IllegalArgumentException if the value is not allowed (ie. is different from *, T=0 or T=1)
     */
    public void setTransmissionProtocol(String transmissionProtocol) throws IllegalArgumentException {
        if (!"*".equals(transmissionProtocol)
                && !"T=0".equals(transmissionProtocol)
                && !"T=1".equals(transmissionProtocol)) {
            throw new IllegalArgumentException("Only \'*\', \'T=0\' and \'T=1\' transmission protocols are allowed.");
        }
        this.transmissionProtocol = transmissionProtocol;
    }

    /**
     * Get all key components.
     *
     * @return all key components
     */
    public ArrayList<KeyComponent> getAllKeyComponents() {
        return keyComponents;
    }

    /**
     * Set key components.
     *
     * @param keyComponents the key components to set
     */
    public void setKeyComponents(ArrayList<KeyComponent> keyComponents) {
        this.keyComponents = keyComponents;
    }
}
