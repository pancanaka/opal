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
 * This interface is used by the DataExchangesModel class
 */

package fr.xlim.ssd.opal.gui.model.dataExchanges;

public interface Observable {

    public void addObserver(Observer obver);
    public void displayAllLogSendings(MyMessage message);
    public void displayApduSending(MyMessage message);
    public void displayLogSending(MyMessage message);

}