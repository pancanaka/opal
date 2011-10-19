/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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
