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

import java.text.SimpleDateFormat;
import java.util.Date;


public class MyMessage {

    private String level;
    private String msg_content;

    public MyMessage(String level,String msg_content){

        this.level=level;
        this.msg_content=msg_content;

    }

    public String getLevel(){
        return  this.level;
    }

    public String getMessage(){
        return  this.msg_content;
    }

    @Override
    public String toString(){
        Date date=new Date();
        
        
        return (this.getCurrentTime()+"  ["+this.level+"]  "+msg_content);
        
    }

    public String getCurrentTime(){

        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        String strDate=sdf.format(date);
        return  strDate;

    }

}