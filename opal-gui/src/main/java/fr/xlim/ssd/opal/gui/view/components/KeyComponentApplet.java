/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Estelle Blandinieres <estelle.blandinieres@etu.unilim.fr>         *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * @author Estelle Blandinieres
 */
public class KeyComponentApplet extends KeyComponent {
    private short lineHeight = 20;

    /**
     * Default constructor
     */
    public KeyComponentApplet() {
        super();
    }

    /**
     * Constructor with tyoe, version, Id and key
     *
     * @param type
     * @param keyVersion
     * @param keyId
     * @param key
     */
    public KeyComponentApplet(String type, String keyVersion, String keyId, String key) {
        super(type, keyVersion, keyId, key);
    }

    public Box createLineForm() {

        Box line1 = Box.createHorizontalBox();
        line1.setPreferredSize(new Dimension(500, lineHeight));
        line1.add(new JLabel("Type "));
        line1.add(cbImp);
        line1.add(new JLabel("Key version number "));
        line1.add(JkeyVersion);
        line1.add(new JLabel("Key id "));
        line1.add(JkeyId);

        Box line2 = Box.createHorizontalBox();
        line2.setPreferredSize(new Dimension(500, lineHeight));
        line2.add(new JLabel("Key "));
        line2.add(Jkey);

        Box v = Box.createVerticalBox();
        v.add(line1);
        v.add(Box.createRigidArea(new Dimension(500, 10)));
        v.add(line2);

        return v;
    }
}
