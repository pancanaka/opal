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
/*
 * This class is used only to format the loggers by adding the current Date and hour, it adds also the Level to it
 */

package fr.xlim.ssd.opal.gui.model.dataExchanges;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MyMessage {

    //Attributes
    private String level;
    private String msg_content;

    //Constructor
    public MyMessage(String level, String msg_content) {

        this.level = level;
        this.msg_content = msg_content;

    }

    //Getters
    public String getLevel() {
        return this.level;
    }

    public String getMessage() {
        return this.msg_content;
    }

    //Overriding toString method to format the message
    @Override
    public String toString() {
        Date date = new Date();

        return (this.getCurrentTime() + "  [" + this.level + "]  " + msg_content);

    }

    //Function that gets the current time, it is used in the toString method
    public String getCurrentTime() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(date);
        return strDate;

    }

}