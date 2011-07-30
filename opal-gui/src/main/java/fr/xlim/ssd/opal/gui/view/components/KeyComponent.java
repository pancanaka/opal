/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>           *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components;

import fr.xlim.ssd.opal.gui.model.Key.KeyModel;

import javax.swing.*;
import java.awt.*;


/**
 * With this class a profile can use different keys (useful for the "Add field" button in AddUpdateProfileView)
 *
 * @author Thibault Desmoulins
 * @see fr.xlim.ssd.opal.gui.view.profiles.AddUpdateProfileView
 */
public class KeyComponent {
    private short lineHeight = 25;

    public JTextField JkeyVersion = new JTextField(), JkeyId = new JTextField(), Jkey = new JTextField();

    public String type, keyVersion, keyId, key;

    KeyType[] tabType = KeyType.values();

    JComboBox cbImp = new JComboBox(tabType);


    /**
     * Constructor
     */
    public KeyComponent() {
    }


    /**
     * This constructor initializes fields with values ​​given in parameters
     *
     * @param type       the type of the key
     * @param keyVersion the version of the key
     * @param keyId      the ID of the key
     * @param key        the value of the key
     */
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

    /**
     * Converts all fields of this class into a KeyModel object
     *
     * @return an instance of <code>KeyModel</code>
     * @see fr.xlim.ssd.opal.gui.model.Key.KeyModel
     */
    public KeyModel convert2KeyModel() {
        return new KeyModel(this.type, this.keyVersion, this.keyId, this.key);
    }


    /**
     * Returns the index of the "type" combobox corresponding to the string given in parameter
     *
     * @param type the type of the key
     * @return the index of the combobox
     */
    public int getIndexComboBox(String type) {
        int n = tabType.length;
        for (int i = 1; i < n; i++) {
            String value = Integer.toHexString(tabType[i].getValue() & 0xFF).toUpperCase();

            if (value.compareTo(type) == 0) {
                return i;
            }
        }
        return 0;
    }


    /**
     * Creates the form in order to create or to update a key
     *
     * @return a <code>Box</code> object containing the form to show
     */
    public Box createLineForm() {
        Box line1 = Box.createHorizontalBox();
        line1.setPreferredSize(new Dimension(500, lineHeight));
        line1.add(new JLabel("Type : "));
        line1.add(cbImp);
        line1.add(new JLabel("Key version number : "));
        line1.add(JkeyVersion);
        line1.add(new JLabel("Key id : "));
        line1.add(JkeyId);


        Box line2 = Box.createHorizontalBox();
        line2.setPreferredSize(new Dimension(500, lineHeight));
        line2.add(new JLabel("Key : "));
        line2.add(Jkey);

        Box v = Box.createVerticalBox();
        v.add(line1);
        v.add(line2);

        return v;
    }


    /**
     * @return the type of the key
     */
    public String getType() {
        String value = String.valueOf(tabType[this.cbImp.getSelectedIndex()]);

        if (value.compareTo("DES_ECB") == 0) {
            return "83";
        } else if (value.compareTo("DES_CBC") == 0) {
            return "84";
        } else if (value.compareTo("AES_CBC") == 0) {
            return "88";
        } else if (value.compareTo("SCGemVisa") == 0) {
            return "0";
        } else if (value.compareTo("SCGemVisa2") == 0) {
            return "1";
        } else {
            return value;
        }
    }

    /**
     * @return the key version
     */
    public String getKeyVersion() {
        return this.JkeyVersion.getText();
    }

    /**
     * @return the key ID
     */
    public String getKeyId() {
        return this.JkeyId.getText();
    }

    /**
     * @return the key value
     */
    public String getKey() {
        return this.Jkey.getText();
    }
}
