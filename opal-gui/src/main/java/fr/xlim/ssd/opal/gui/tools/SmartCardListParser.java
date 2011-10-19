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
