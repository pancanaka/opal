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
 * This is a custom appender created to format the loggers by using MyMessage class and displaying it in a JDialog
 */

package fr.xlim.ssd.opal.gui.model.dataExchanges;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import javax.swing.*;


public class MyAppender extends AppenderBase<ILoggingEvent> {

    MyMessage current_msg;


    //These function are used to send the message to display to the DataExchangeModel class
    @Override
    public synchronized void doAppend(ILoggingEvent event) {

        this.append(event);

    }

    @Override
    public void append(ILoggingEvent event) {

        //Format the logger by using mymessage class
        current_msg = new MyMessage(event.getLevel().toString(), event.getMessage());

        //There is a Thread used for each logging event
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                //Instanciating a singleton Object or call it if it is created
                DataExchangesModel dem = DataExchangesModel.getInstance();
                //Send the message to the DataExchangesModel class
                dem.displayLogSending(current_msg);
                dem.displayAllLogSendings(current_msg);
                dem.displayApduSending(current_msg);

            }
        });


    }


}
