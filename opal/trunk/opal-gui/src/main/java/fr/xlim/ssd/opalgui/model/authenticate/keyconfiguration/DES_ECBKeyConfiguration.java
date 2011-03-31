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
public class DES_ECBKeyConfiguration extends KeyConfiguration {
    private String key1;
    private String key2;
    private String key3;
    private byte   keyID1;
    private byte   keyID2;
    private byte   keyID3;

    public DES_ECBKeyConfiguration( ) {
        this((byte)255, "", (byte)1, "", (byte)2, "", (byte)3);
    }

    public DES_ECBKeyConfiguration(byte keySet) {
        this(keySet, "", (byte)1, "", (byte)2, "", (byte)3);
    }

    public DES_ECBKeyConfiguration(byte keySet, String key1, byte keyID1, String key2, byte keyID2, String key3, byte keyID3) {
        super(KeyType.DES_ECB, keySet);
        this.key1   = key1;
        this.key2   = key2;
        this.key3   = key3;
        this.keyID1 = keyID1;
        this.keyID2 = keyID2;
        this.keyID3 = keyID3;
    }

    public String getKey1() {
        return key1;
    }

    public String getKey2() {
        return key2;
    }

    public String getKey3() {
        return key3;
    }

    public byte getKeyID1() {
        return keyID1;
    }

    public byte getKeyID2() {
        return keyID2;
    }

    public byte getKeyID3() {
        return keyID3;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public void setKey3(String key3) {
        this.key3 = key3;
    }

    public void setKeyID1(byte keyID1) {
        this.keyID1 = keyID1;
    }

    public void setKeyID2(byte keyID2) {
        this.keyID2 = keyID2;
    }

    public void setKeyID3(byte keyID3) {
        this.keyID3 = keyID3;
    }
}
