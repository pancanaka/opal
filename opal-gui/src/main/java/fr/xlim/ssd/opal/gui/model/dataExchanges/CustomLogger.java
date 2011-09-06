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
 * This class contains the logger that will be used in this project
 */
package fr.xlim.ssd.opal.gui.model.dataExchanges;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import java.util.Map;
import org.slf4j.Marker;

public class CustomLogger implements ILoggingEvent{

    private Level mylevel;
    private String mymessage;
    private MyAppender appender;


    //Constructor of the Logger
    public CustomLogger(){

       this.mylevel=Level.ERROR;
       this.mymessage="There is no appender linked to this Logger";
       this.appender = new MyAppender();


    }

    //Links a logger to an info Level
    public void info(String message){

        mylevel=Level.INFO;
        mymessage=message;
        this.appender.append(this);

    }

    //Links a logger to a warning Level
    public void warn(String message){

        mylevel = Level.WARN;
        mymessage =message;
        this.appender.append(this);

    }

    //Links a logger to a warning Level + display an exception if it exists
    public void warn(String message,Exception ie){

        mylevel = Level.WARN;
        mymessage =message;
        this.appender.append(this);
        System.out.println(ie.getMessage());

    }

    //Links a logger to an error Level
    public void error(String message){

        mylevel = Level.ERROR;
        mymessage =message;
        this.appender.append(this);

    }

    //Links a logger to an error Level
    public void error(String message,Exception ie){

        mylevel = Level.ERROR;
        mymessage =message;
        this.appender.append(this);
        System.out.println(ie.getMessage());

    }

    //Links a logger to a debug Level
    public void debug(String message){

        mylevel = Level.DEBUG;
        mymessage =message;
        this.appender.append(this);

    }

    //Links a logger to a debug Level
    public void debug(String message,Exception ie){

        mylevel = Level.DEBUG;
        mymessage =message;
        this.appender.append(this);
        System.out.println(ie.getMessage());

    }

    //displays a logger and link it to a Level added in the first parameter
    public void log(Level lvl,String message){

        this.mylevel=lvl;
        this.mymessage=message;
        this.appender.append(this);

    }

    //Gets only the level of a logger
    @Override
    public Level getLevel() {
        return this.mylevel;
    }

    //Setting the level of a logger
    public void setLevel(Level level){
        mylevel=level;
    }

    //gets only the message of a logger
    @Override
    public String getMessage() {
        return this.mymessage;
    }

    //Implemented methods that aren't used yet in this project

    @Override
    public String getThreadName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] getArgumentArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getFormattedMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getLoggerName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LoggerContextVO getLoggerContextVO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IThrowableProxy getThrowableProxy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StackTraceElement[] getCallerData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasCallerData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Marker getMarker() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> getMDCPropertyMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> getMdc() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTimeStamp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void prepareForDeferredProcessing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
