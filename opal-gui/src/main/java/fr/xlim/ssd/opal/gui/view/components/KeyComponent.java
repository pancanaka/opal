package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.model.Key.KeyModel;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 * With this class a profile can use different keys
 *
 * @author Thibault Desmoulins
 */
public class KeyComponent {

    private short lineHeight  = 25;
    private short lineSpacing = 10;

    public JTextField JkeyVersion = new JTextField(), JkeyId = new JTextField(), Jkey = new JTextField();
    public String type, keyVersion, keyId, key;
    KeyType[] tabType = KeyType.values();
    JComboBox cbImp = new JComboBox(tabType);


    public KeyComponent() {}

    public KeyModel convert2KeyModel() {
        return new KeyModel(this.type, this.keyVersion, this.keyId, this.key);
    }
    
    public KeyComponent(String type, String keyVersion, String keyId, String key) {
        JkeyVersion.setText(keyVersion);
        JkeyId.setText(keyId);
        Jkey.setText(key); 
       
        this.keyVersion = keyVersion;
        this.keyId = keyId;
        this.key = key;

        int index = getIndexComboBox(type);
        cbImp.setSelectedIndex(index);
        
        this.type = this.getType();
    }
 
    public int getIndexComboBox(String type) {
        int n = tabType.length;
        for(int i=1 ; i<n ; i++) {
            String value = Integer.toHexString(tabType[i].getValue() & 0xFF).toUpperCase();
            
            if(value.compareTo(type) == 0) {
                return i;
            }
        }
        return 0;
    }


    public Box createLineForm() {
        Box line1  = Box.createHorizontalBox();
        line1.setPreferredSize(new Dimension(500, lineHeight));
        line1.add(new JLabel("Type : "));
        line1.add(cbImp);
        line1.add(new JLabel("Key version number : "));
        line1.add(JkeyVersion);
        line1.add(new JLabel("Key id : "));
        line1.add(JkeyId);


        Box line2  = Box.createHorizontalBox();
        line2.setPreferredSize(new Dimension(500, lineHeight));
        line2.add(new JLabel("Key : "));
        line2.add(Jkey);

        Box v = Box.createVerticalBox();
        v.add(line1);
        v.add(line2);

        return v;
    }

    // Getters
    public String getType() {
        String value = String.valueOf(tabType[this.cbImp.getSelectedIndex()]);
        
        if(value.compareTo("DES_ECB") == 0) {
            return "83";
        }
        else if(value.compareTo("DES_CBC") == 0) {
            return "84";
        }
        else if(value.compareTo("AES_CBC") == 0) {
            return "88";
        }
        else if(value.compareTo("SCGemVisa") == 0) {
            return "0";
        }
        else if(value.compareTo("SCGemVisa2") == 0) {
            return "1";
        }
        else {
            return value;
        }
    }

    public String getKeyVersion() {
        return this.JkeyVersion.getText();
    }

    public String getKeyId() {
        return this.JkeyId.getText();
    }

    public String getKey() {
        return this.Jkey.getText();
    }
}
