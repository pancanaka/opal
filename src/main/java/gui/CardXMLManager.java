 
package gui;

import fr.xlim.ssd.opal.library.SCPMode;
import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author razaina
 */
public class CardXMLManager{
    private CardConfig cardDesired = null;
    private String ISDAID = null;
    private String ScpMode = null;
    private String TransmissionProtocol = null;
    private String implementation = null;
    
    public CardXMLManager(String cardName)
    { 
        try{
             cardDesired = CardConfigFactory.getCardConfig(cardName);
             ISDAID = Conversion.arrayToHex(cardDesired.getIssuerSecurityDomainAID());
             ScpMode = cardDesired.getScpMode().toString();
             TransmissionProtocol = cardDesired.getTransmissionProtocol();
             implementation = cardDesired.getImplementation().substring(34);
        }catch(CardConfigNotFoundException e)
        {
            Logger.getLogger("CardXMLManager").log(Level.SEVERE, null, e);
        } 
    }
    public String getISDAID()
    {
        return ISDAID;
    }
    public String getScpMode()
    {
        return ScpMode;
    }
    public String getTransmissionProtocol()
    {
        return TransmissionProtocol;
    }
    public String getImplementation()
    {
        return implementation;
    }
}
