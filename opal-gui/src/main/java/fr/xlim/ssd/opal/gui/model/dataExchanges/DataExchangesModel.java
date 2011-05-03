/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Chanaa Anas <anas.chanaa@etu.unilim.fr>                          *
 *           El Khaldi Omar <omar.el-khaldi@etu.unilim.fr>                    *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.model.dataExchanges;

import java.util.ArrayList;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *@author CHANAA Anas
 * @author EL KHALDI Omar
 */
public class DataExchangesModel implements Observable{

    ArrayList<Observer> lst_obver=new ArrayList<Observer>();

    String txt_apdu;
    String txt_log;
    String txt_all;


    @Override
    public void addObserver(Observer obver){

        lst_obver.add(obver);

    }

    @Override
    public void displayAllAPDUSendings(){

    	for(Observer obver: lst_obver){

            txt_all="APDU-> Sending APDU Test\n";
            obver.updateALL(txt_all);

        }


    }

    public void displayAllLogSendings(){

    	for(Observer obver: lst_obver){


            txt_all="Logging-> Logging Test\n";
            obver.updateALL(txt_all);

        }

    }

    public void displayApduSending(){

        for(Observer obver: lst_obver){


            txt_apdu="APDU-> Sending APDU Test\n";
            obver.updateAPDU(txt_apdu);

        }

    }

    public void displayLogSending(){

    	for(Observer obver: lst_obver){


            txt_log="Logging-> Logging Test\n";
            obver.updateLog(txt_log);

        }

    }

    public void clearAll(){

    	for(Observer obver: lst_obver){

            txt_all="";
            obver.clearALL(txt_all);

        }

    }

    public void clearAPDU(){

    	for(Observer obver: lst_obver){

            txt_apdu="";
            obver.clearAPDU(txt_apdu);

        }

    }

    public void clearLogging(){

    	for(Observer obver: lst_obver){

            txt_apdu="";
            obver.clearLog(txt_apdu);

        }

    }

  /*  public void addObserver(java.util.Observer observer) {
        throw new UnsupportedOperationException("Not yet implemented");
    }*/


}