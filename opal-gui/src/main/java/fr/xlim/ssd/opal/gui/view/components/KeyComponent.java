package fr.xlim.ssd.opal.gui.view.components;

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
    //private JTextField type = new JTextField(), keyId = new JTextField(), key = new JTextField();
    String[] tab = {"DES_ECB", "DES_CBC", "SCGemVisa,", "SCGemVisa2", "AES"};
    JComboBox cbImp = new JComboBox(tab);

    private short lineHeight  = 25;
    private short lineSpacing = 10;

    private String keyVersion = "", keyId = "", key = "", type = "";

    public KeyComponent() {}

    public KeyComponent(String type, String keyVersion, String keyId, String key) {
        this.keyVersion = keyVersion;
        this.keyId      = keyId;
        this.key        = key;
        this.type       = type;

        int index = getIndexComboBox(type);
        cbImp.setSelectedIndex(index);
    }

    public int getIndexComboBox(String type) {
        int n = tab.length;
        for(int i=1 ; i<n ; i++) {
            if(tab[i].equalsIgnoreCase(type)) {
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
        line1.add(new JTextField(keyVersion));
        line1.add(new JLabel("Key id : "));
        line1.add(new JTextField(keyId));


        Box line2  = Box.createHorizontalBox();
        line2.setPreferredSize(new Dimension(500, lineHeight));
        line2.add(new JLabel("Key : "));
        line2.add(new JTextField(key));

        Box v = Box.createVerticalBox();
        v.add(line1);
        v.add(line2);

        return v;
    }

    public String getType() {
        return this.type;
    }

    public String getKeyVersion() {
        return this.keyVersion;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public String getKey() {
        return this.key;
    }
}
