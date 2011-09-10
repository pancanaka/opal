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
 * This class is used to convert the two last bytes of a message to a response status of an ISO 7816 constant value
 */

package fr.xlim.ssd.opal.gui.model.dataExchanges;

public class ResponseAPDUState {


    //Main function
    public String convertResponse(String last_bytes) {

        if (last_bytes.equals("90 00")) return last_bytes + " (SW No Error)";
        else if (last_bytes.equals("69 99")) return last_bytes + " (SW Applet selection failed)";
        else if (last_bytes.equals("61 00")) return last_bytes + " (SW Response bytes remaining)";
        else if (last_bytes.equals("6E 00")) return last_bytes + " (SW CLA value not supported)";
        else if (last_bytes.equals("69 86")) return last_bytes + " (SW Command not allowed (no current EF))";
        else if (last_bytes.equals("69 85")) return last_bytes + " (SW Conditions of use not satisfied)";
        else if (last_bytes.equals("6C 00")) return last_bytes + " (SW Correct Expected Length (Le))";
        else if (last_bytes.equals("69 84")) return last_bytes + " (SW Data invalid)";
        else if (last_bytes.equals("6A 84")) return last_bytes + " (SW Not enough memory space in the file)";
        else if (last_bytes.equals("69 83")) return last_bytes + " (SW File invalid)";
        else if (last_bytes.equals("6A 82")) return last_bytes + " (SW File not found)";
        else if (last_bytes.equals("6A 81")) return last_bytes + " (SW Function not supported)";
        else if (last_bytes.equals("6A 86")) return last_bytes + " (SW Incorrect parameters (P1,P2))";
        else if (last_bytes.equals("6D 00")) return last_bytes + " (SW INS value not supported)";
        else if (last_bytes.equals("68 81")) return last_bytes + " (SW Card does not support logical channels)";
        else if (last_bytes.equals("6A 83")) return last_bytes + " (SW Record not found)";
        else if (last_bytes.equals("68 82")) return last_bytes + " (SW Card does not support secure messaging)";
        else if (last_bytes.equals("69 82")) return last_bytes + " (SW Security condition not satisfied)";
        else if (last_bytes.equals("6F 00")) return last_bytes + " (SW No precise diagnosis)";
        else if (last_bytes.equals("62 00")) return last_bytes + " (SW Warning, card state unchanged)";
        else if (last_bytes.equals("6A 80")) return last_bytes + " (SW Wrong data)";
        else if (last_bytes.equals("67 00")) return last_bytes + " (SW Wrong length)";
        else if (last_bytes.equals("6B 00")) return last_bytes + " (SW Incorrect parameters (P1,P2))";
        else return null;

    }

}
