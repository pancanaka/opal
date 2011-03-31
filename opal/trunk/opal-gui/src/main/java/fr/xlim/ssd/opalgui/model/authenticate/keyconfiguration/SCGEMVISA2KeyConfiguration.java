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
public class SCGEMVISA2KeyConfiguration extends KeyConfiguration {
    private String keyData;

    public SCGEMVISA2KeyConfiguration() {
        this((byte)255, "");
    }

    public SCGEMVISA2KeyConfiguration(byte keySet) {
        this(keySet, "");
    }

    public SCGEMVISA2KeyConfiguration(String keyData) {
        this((byte)255, keyData);
    }

    public SCGEMVISA2KeyConfiguration(byte keySet, String keyData) {
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
