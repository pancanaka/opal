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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import java.util.Map;
import org.slf4j.Marker;

public class CustomLogger implements ILoggingEvent{

      Level mylevel;
      String mymessage;
      MyAppender appender;

    public CustomLogger(){

       this.mylevel=Level.ERROR;
       this.mymessage="There is no appender linked to this Logger";
       this.appender = new MyAppender();


    }

    public void info(String message){

        mylevel=Level.INFO;
        mymessage=message;
        this.appender.append(this);

    }

    public void warn(String message){

        mylevel = Level.WARN;
        mymessage =message;
        this.appender.append(this);

    }

    public void warn(String message,Exception ie){

        mylevel = Level.WARN;
        mymessage =message;
        this.appender.append(this);
        System.out.println(ie.getMessage());

    }

    public void error(String message){

        mylevel = Level.ERROR;
        mymessage =message;
        this.appender.append(this);

    }

    public void error(String message,Exception ie){

        mylevel = Level.ERROR;
        mymessage =message;
        this.appender.append(this);
        System.out.println(ie.getMessage());

    }

    public void debug(String message){

        mylevel = Level.DEBUG;
        mymessage =message;
        this.appender.append(this);

    }

    public void debug(String message,Exception ie){

        mylevel = Level.DEBUG;
        mymessage =message;
        this.appender.append(this);
        System.out.println(ie.getMessage());

    }

    public void log(Level lvl,String message){

        this.mylevel=lvl;
        this.mymessage=message;
        this.appender.append(this);

    }

    public String getThreadName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Level getLevel() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return this.mylevel;
    }

    public String getMessage() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return this.mymessage;
    }

    public Object[] getArgumentArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getFormattedMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getLoggerName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LoggerContextVO getLoggerContextVO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IThrowableProxy getThrowableProxy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public StackTraceElement[] getCallerData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasCallerData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Marker getMarker() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, String> getMDCPropertyMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, String> getMdc() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getTimeStamp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void prepareForDeferredProcessing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLevel(Level level){
        mylevel=level;
    }

}
