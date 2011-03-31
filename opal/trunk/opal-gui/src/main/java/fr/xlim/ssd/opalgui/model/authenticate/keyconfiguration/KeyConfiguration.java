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
abstract public class KeyConfiguration {
    private byte    keySet ;
    private KeyType keyType = null;

    public KeyConfiguration( ) {
        this(KeyType.DES_ECB, (byte)255);
    }

    public KeyConfiguration(KeyType keyType) {
        this(keyType, (byte)255);
    }

    public KeyConfiguration(byte keySet) {
        this(KeyType.DES_ECB, keySet);
    }
    
    public KeyConfiguration(KeyType keyType, byte keySet) {
        this.keyType = keyType;
        this.keySet  = keySet;
    }


    public KeyType getKeyType() {
        return this.keyType;
    }

    public byte getKeySet() {
        return this.keySet;
    }

    protected void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public void setKeySet(byte keySet) {
        this.keySet = keySet;
    }
}
