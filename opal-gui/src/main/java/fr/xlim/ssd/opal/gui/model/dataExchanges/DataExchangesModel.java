/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a graphical user interface for the OPAL library
 * (http://secinfo.msi.unilim.fr/opal/). It aims to implement most
 * of the procedures needed to manage applets in a Java Card for
 * developers.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.gui.model.dataExchanges;

import java.util.ArrayList;


/*
 * This Singleton class is the model of the JDialog by using the Observer / Observable pattern
 *  
 */

public class DataExchangesModel implements Observable {

    private static DataExchangesModel instance;
    ResponseAPDUState state = new ResponseAPDUState();
    ArrayList<Observer> lst_obver = new ArrayList<Observer>();// Observers list

    String txt_log;//Text for the logging JTextPane
    String txt_all;//Text for the all JTextPane
    //Text for the APDU JTextPane
    String head; //Send APDU:
    String req_apdu;//APDU ->
    String params;//CLA, INS, P1, P2, LC
    String lc;//Used to get the LC
    String le;//Used to verify if LE exists
    String data;//Data Sent to the card
    String resp;//APDU <-
    String res_apdu;//Response from the card

    //This private constructor stops the default one to be called
    private DataExchangesModel() {
    }

    //This method is the constructor of this singleton pattern
    public static DataExchangesModel getInstance() {

        if (instance == null) instance = new DataExchangesModel();
        return instance;

    }

    //Adding observers to the list
    @Override
    public void addObserver(Observer obver) {

        lst_obver.add(obver);

    }

    //This function is used to display loggers in the "ALL" JTextPane
    @Override
    public void displayAllLogSendings(MyMessage message) {

        for (Observer obver : lst_obver) {


            txt_all = message.toString() + "\n";
            obver.updateALL(txt_all, message.getLevel());

        }

    }

    //This function is used to display the APDU sendings and response status in the "APDU" JTextPane
    @Override
    public void displayApduSending(MyMessage message) {

        for (Observer obver : lst_obver) {

            if (message.toString().length() > 39) {

                //If it is a logger token from the ResponseAPDU function int the CummunicationController class
                if (message.toString().substring(19, 38).equals("Response to command")) {

                    //Compute the length of a message
                    int msg_lng = message.toString().length();

                    //Compute the first part of a log to know from which part the two last bytes starts in the logger
                    int apdu_lng = 0;
                    for (; ; ) {

                        if (message.toString().charAt(apdu_lng) == '<') break;
                        apdu_lng++;

                    }

                    //Use the two last bytes to get the response status by using the ResponseAPDUState class
                    String response = state.convertResponse(message.toString().substring(msg_lng - 6, msg_lng - 1));

                    le = "";
                    lc = message.toString().substring(55, 57);//Get LC
                    //Convert the LC value by using the function hexaToInt below in order to compare the LC value with the length of the data
                    // if (LC != Data length ) then le exists
                    // else le doesn't exists
                    int lc_value = LCToInt(lc);

                    head = "Send APDU : \n";
                    req_apdu = "APDU -> ";
                    params = message.toString().substring(42, 57);
                    //if the status has no error
                    if (response.equals("90 00 (SW No Error)")) {

                        //Store in the data String the data field of an APDU command
                        String dt = message.toString().substring(57, (apdu_lng - 11)) + "\n";
                        data = dt.substring(0, ((params.length() + dt.length()) / 2) - params.length()) + " \n";

                        //if the data field is different to the LC value, it means that the LE is used
                        if ((lc_value * 3) != dt.substring(0, ((params.length() + dt.length()) / 2) - params.length()).length()) {


                            le = message.toString().substring(57, 60);//Store the LE

                            //Then store the new Data
                            dt = message.toString().substring(60, (apdu_lng - 11)) + "\n";
                            data = dt.substring(0, ((params.length() + dt.length()) / 2) - params.length()) + " \n";

                        }

                        resp = "APDU <- ";
                        res_apdu = response + "\n \n";
                        //Update the "APDU" JTextPane
                        obver.updateAPDU(head, req_apdu, params, le, data, resp, res_apdu, "INFO");
                    }

                    //If the status has an error
                    else {

                        //Store in the data String the data field of an APDU command
                        data = message.toString().substring(57, apdu_lng - 11) + " \n";

                        //if the data field is different to the LC value, it means that the LE is used
                        if ((lc_value * 3) != message.toString().substring(57, apdu_lng - 11).length()) {

                            le = message.toString().substring(57, 60);//Store the LE

                            //Then store the new Data
                            data = message.toString().substring(60, apdu_lng - 11) + " \n";


                        }
                        resp = "APDU <- ";
                        res_apdu = response + "\n \n";

                        //Update the "APDU" JTextPane
                        obver.updateAPDU(head, req_apdu, params, le, data, resp, res_apdu, "ERROR");
                    }

                }

            }


        }

    }

    //This function is used to display loggers in the "Logging" JTextPane
    @Override
    public void displayLogSending(MyMessage message) {

        for (Observer obver : lst_obver) {

            txt_log = message.toString() + "\n";
            obver.updateLog(txt_log, message.getLevel());

        }

    }


    //Convert the LC to an int
    public int LCToInt(String hexa) {

        int right = 0;
        int left = 0;

        if (hexa.length() > 2) return -1;
        else {

            //Compute the second character
            if (hexa.charAt(1) == '0' || hexa.charAt(1) == '1' || hexa.charAt(1) == '2' || hexa.charAt(1) == '3' || hexa.charAt(1) == '4'
                    || hexa.charAt(1) == '5' || hexa.charAt(1) == '6' || hexa.charAt(1) == '7' || hexa.charAt(1) == '8' || hexa.charAt(1) == '9') {

                String s = String.valueOf(hexa.charAt(1));
                right = Integer.parseInt(s);


            } else if (hexa.charAt(1) == 'A') right = 10;
            else if (hexa.charAt(1) == 'B') right = 11;
            else if (hexa.charAt(1) == 'C') right = 12;
            else if (hexa.charAt(1) == 'D') right = 13;
            else if (hexa.charAt(1) == 'E') right = 14;
            else if (hexa.charAt(1) == 'F') right = 15;

            //Compute the first character
            if (hexa.charAt(0) == '0' || hexa.charAt(0) == '1' || hexa.charAt(0) == '2' || hexa.charAt(0) == '3' || hexa.charAt(0) == '4'
                    || hexa.charAt(0) == '5' || hexa.charAt(0) == '6' || hexa.charAt(0) == '7' || hexa.charAt(0) == '8' || hexa.charAt(0) == '9') {

                String s = String.valueOf(hexa.charAt(0));
                left = Integer.parseInt(s) * 10;

            } else if (hexa.charAt(0) == 'A') right = 10 * 10;
            else if (hexa.charAt(0) == 'B') right = 11 * 10;
            else if (hexa.charAt(0) == 'C') right = 12 * 10;
            else if (hexa.charAt(0) == 'D') right = 13 * 10;
            else if (hexa.charAt(0) == 'E') right = 14 * 10;
            else if (hexa.charAt(0) == 'F') right = 15 * 10;

            int result = right + left;

            return result;

        }
    }

}