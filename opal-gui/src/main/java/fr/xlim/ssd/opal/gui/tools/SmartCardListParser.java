/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : David Pequegnot <david.pequegnot@etu.unilim.fr>                   *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.tools;

import fr.xlim.ssd.opal.library.params.ATR;
import fr.xlim.ssd.opal.library.utilities.Conversion;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author David Pequegnot
 */
public class SmartCardListParser {
    public static String getCardNameByAtr(ATR atr) {
        return SmartCardListParser.getCardNameByAtr(atr, true);
    }

    public static String getCardNameByAtr(ATR atr, boolean firstLine) {
        InputStream input = SmartCardListParser.class.getResourceAsStream("/smartcard_list.txt");

        Scanner scanner = new Scanner(input);


        boolean readDescription = false;
        String cardDescription = "";

        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                int commentIndex = line.indexOf('#');
                if (commentIndex >= 0) {
                    line = line.substring(0, commentIndex);
                }

                String trimedLine = line.trim();
                if (line.isEmpty() || trimedLine.isEmpty()) {
                    if (readDescription) { // An empty line means that there is no more description to read
                        break;
                    }
                    continue;
                }

                if (line.startsWith("\t")) { // Comment line
                    if (readDescription) {
                        cardDescription += line.trim();
                    }
                    continue;
                }

                // The line is an ATR
                line = line.trim();
                String toMatch = line.replace(" ", "\\s");
                boolean matches = Pattern.matches(toMatch, Conversion.arrayToHex(atr.getValue()).trim());

                if (matches) {
                    readDescription = true;
                }
            }
        } finally {
            scanner.close();
        }

        return cardDescription;
    }
}
