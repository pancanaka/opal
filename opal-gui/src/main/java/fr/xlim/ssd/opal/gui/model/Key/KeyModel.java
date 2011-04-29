
package fr.xlim.ssd.opal.gui.model.Key;

/**
 *
 * @author Tiana Razafindralambo
 */
public class KeyModel {

    public String type, version, keyID, key;

    public KeyModel(String type, String version, String keyID, String key) {
        this.type = type;
        this.version = version;
        this.keyID = keyID;
        this.key = key;
    }
}
