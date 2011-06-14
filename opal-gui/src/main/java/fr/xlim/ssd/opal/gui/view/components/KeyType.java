/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : Yorick Lesecque <yorick.lesecque@etu.unilim.fr>                   *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components;

/**
 * Define the key type used.
 *
 * @author Yorick Lesecque
 */
public enum KeyType {

    /// DES in ECB mode
    DES_ECB((byte) 0x83),

    /// DES in CBC mode
    DES_CBC((byte) 0x84),

    /// AES in CBC mode
    AES_CBC((byte) 0x88),

    SCGemVisa2((byte) 0x01),

    SCGemVisa((byte) 0x00);

    private byte value;

    private KeyType(byte val) {
        this.value = val;
    }

    public byte getValue() {
        return this.value;
    }
}
