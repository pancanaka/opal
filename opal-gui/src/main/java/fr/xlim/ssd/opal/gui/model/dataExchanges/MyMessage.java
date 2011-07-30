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
    public MyMessage(String level,String msg_content){

        this.level=level;
        this.msg_content=msg_content;

    }

    //Getters
    public String getLevel(){
        return  this.level;
    }

    public String getMessage(){
        return  this.msg_content;
    }

    //Overriding toString method to format the message
    @Override
    public String toString(){
        Date date=new Date();
        
        return (this.getCurrentTime()+"  ["+this.level+"]  "+msg_content);
        
    }

    //Function that gets the current time, it is used in the toString method
    public String getCurrentTime(){

        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        String strDate=sdf.format(date);
        return  strDate;

    }

}