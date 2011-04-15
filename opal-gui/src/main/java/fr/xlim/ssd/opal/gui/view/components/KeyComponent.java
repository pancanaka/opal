package fr.xlim.ssd.opal.gui.view.components;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * With this class a profile can use différent keys
 *
 * @author Thibault Desmoulins
 */
public class KeyComponent {
    private JTextField type = new JTextField(), keyVersion = new JTextField(), keyId = new JTextField(), key = new JTextField();
    JComboBox cbImp;

    private short lineHeight  = 25;
    private short lineSpacing = 10;


    public KeyComponent() {
        String[] tab = {"DES_ECB", "DES_CBC", "SCGemVisa,", "SCGemVisa2", "AES."};
        cbImp = new JComboBox(tab);
    }


    public Box createLineForm() {
        Box line1  = Box.createHorizontalBox();
        line1.setPreferredSize(new Dimension(500, lineHeight));
        line1.add(new JLabel("Type : "));
        line1.add(cbImp);
        line1.add(new JLabel("Key version number : "));
        line1.add(keyVersion);
        line1.add(new JLabel("Key id : "));
        line1.add(keyId);


        Box line2  = Box.createHorizontalBox();
        line2.setPreferredSize(new Dimension(500, lineHeight));
        line2.add(new JLabel("Key : "));
        line2.add(key);

        Box v = Box.createVerticalBox();
        v.add(line1);
        v.add(line2);

        return v;
    }
}
