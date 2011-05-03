/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Author : El Khaldi Omar <omar.el-khaldi@etu.unilim.fr>                     *
 *          Chanaa Anas <anas.chanaa@etu.unilim.fr>                           *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.model.dataExchanges;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;
import javax.swing.SwingUtilities;


public class MyAppender extends AppenderBase<ILoggingEvent>{

    MyMessage current_msg;

    @Override
    public synchronized void doAppend(ILoggingEvent event) {

        this.append(event);

    }

    @Override
    public void append(ILoggingEvent event){

        current_msg=new MyMessage(event.getLevel().toString(), event.getMessage());

//            SwingUtilities.invokeLater(new Runnable(){
  //          @Override
    //        public void run() {
       
                DataExchangesModel dem=DataExchangesModel.getInstance();
                dem.displayAllAPDUSendings(current_msg);
      //      }
        //});






    }

   

}
