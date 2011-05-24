/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : El Khaldi Omar <omar.el-khaldi@etu.unilim.fr>                     *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

/*
 * This is a custom appender created to format the loggers by using MyMessage class and displaying it in a JDialog
 */

package fr.xlim.ssd.opal.gui.model.dataExchanges;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;
import javax.swing.SwingUtilities;


public class MyAppender extends AppenderBase<ILoggingEvent>{

    MyMessage current_msg;


    //These function are used to send the message to display to the DataExchangeModel class
    @Override
    public synchronized void doAppend(ILoggingEvent event) {

        this.append(event);

    }

    @Override
    public void append(ILoggingEvent event){

        //Format the logger by using mymessage class
        current_msg=new MyMessage(event.getLevel().toString(), event.getMessage());

        //There is a Thread used for each logging event
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {

                //Instanciating a singleton Object or call it if it is created
                DataExchangesModel dem=DataExchangesModel.getInstance();
                //Send the message to the DataExchangesModel class
                dem.displayLogSending(current_msg);
                dem.displayAllLogSendings(current_msg);
                dem.displayApduSending(current_msg);
                
            }
        });






    }

   

}
