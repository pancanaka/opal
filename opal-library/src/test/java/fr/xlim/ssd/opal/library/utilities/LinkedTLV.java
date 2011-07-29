package fr.xlim.ssd.opal.library.utilities;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Guillaume Bouffard
 */
public class LinkedTLV {

    private Map tlvMap;

    /**
     * Create an empty Linked TLV
     */
    public LinkedTLV() {
        tlvMap = new TreeMap<TLV, TLV>();
    }

    /**
     * Save and link a TLV with your "father"
     *
     * @param father  a TLV which contained sibling TLV
     * @param sibling a TLV contains sibling TLV
     * @return TLV linked
     */
    public TLV addTLV(TLV father, TLV sibling) {
        return (TLV) tlvMap.put(father, sibling);
    }

    /**
     * Get TLV Map container
     *
     * @return TLV Map container
     */
    public Map getTlvMap() {
        return this.tlvMap;
    }

    /**
     * Set TLV Map
     *
     * @param tlvMap new TLV Map
     */
    public void setTlvMap(Map tlvMap) {
        this.tlvMap = tlvMap;
    }

}
