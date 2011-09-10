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
 * This is a customised observer interface used to update the DataExchangesView class
 */

package fr.xlim.ssd.opal.gui.model.dataExchanges;


public interface Observer {

    public void updateALL(String change_text, String level);

    public void updateAPDU(String head, String req, String params, String le, String data, String response, String res, String level);

    public void updateLog(String change_text, String level);


}