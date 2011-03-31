/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opalgui.model.authenticate.keyconfiguration;

import fr.xlim.ssd.opal.library.KeyType;


/**
 *
 * @author Fox
 */
public class SCGEMVISAKeyConfiguration extends KeyConfiguration {
    private String keyData;

    public SCGEMVISAKeyConfiguration() {
        this((byte)255, "");
    }
    
    public SCGEMVISAKeyConfiguration(byte keySet) {
        this(keySet, "");
    }

    public SCGEMVISAKeyConfiguration(String keyData) {
        this((byte)255, keyData);
    }

    public SCGEMVISAKeyConfiguration(byte keySet, String keyData) {
        super(KeyType.MOTHER_KEY, keySet);
        this.keyData = keyData;
    }

    public String getKeyData() {
        return this.keyData;
    }

    public void setKeyData(String keyData) {
        this.keyData = keyData;
    }
}
