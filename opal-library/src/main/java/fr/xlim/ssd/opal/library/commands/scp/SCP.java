/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a Java 6 library that implements Global Platform 2.x card
 * specification. OPAL is able to upload and manage Java Card applet lifecycle
 * on Java Card while dealing of the authentication of the user and encryption
 * of the communication between the user and the card. OPAL is also able
 * to manage different implementations of the specification via a pluggable
 * interface.
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
package fr.xlim.ssd.opal.library.commands.scp;

import fr.xlim.ssd.opal.library.commands.SecLevel;
import fr.xlim.ssd.opal.library.config.SCPMode;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SCP {

    /// Logger used to print messages
    private static final Logger logger = LoggerFactory.getLogger(SCP.class);

    /// Secure Channel Protocol used
    private SCPMode scpMode;

    /// Secure Level used to communicate
    private SecLevel secMode;

    /// Initialized Cypher Vector used to initialized encryption steps
    protected byte[] icv;

    public SCPMode getScpMode() {
        return scpMode;
    }

    public void setScpMode(SCPMode scpMode) {
        this.scpMode = scpMode;
    }

    public SecLevel getSecMode() {
        return secMode;
    }

    public void setSecMode(SecLevel secMode) {
        this.secMode = secMode;
    }

    public byte[] getIcv() {
        return icv;
    }

    public void setIcv(byte[] icv) {
        this.icv = icv;
    }

    /**
     * ICV Initialization. All values set to 0
     */
    public void initIcv() {
        if (getScpMode() == SCPMode.SCP_01_15
                || getScpMode() == SCPMode.SCP_01_05
                || getScpMode() == SCPMode.SCP_UNDEFINED
                || getScpMode() == SCPMode.SCP_02_04
                || getScpMode() == SCPMode.SCP_02_05
                || getScpMode() == SCPMode.SCP_02_0A
                || getScpMode() == SCPMode.SCP_02_0B
                || getScpMode() == SCPMode.SCP_02_14
                || getScpMode() == SCPMode.SCP_02_15
                || getScpMode() == SCPMode.SCP_02_1A
                || getScpMode() == SCPMode.SCP_02_1B
                || getScpMode() == SCPMode.SCP_02_45
                || getScpMode() == SCPMode.SCP_02_54
                || getScpMode() == SCPMode.SCP_02_55) {

            logger.debug("==> Init ICV begin");
            setIcv(new byte[8]);
            for (int i = 0; i < getIcv().length; i++) {
                getIcv()[i] = (byte) 0x00;
            }
            logger.debug("* New ICV is " + Conversion.arrayToHex(getIcv()));
            logger.debug("==> Init ICV end");
        }
        if (getScpMode() == SCPMode.SCP_03_05
                || getScpMode() == SCPMode.SCP_03_0D
                || getScpMode() == SCPMode.SCP_03_25
                || getScpMode() == SCPMode.SCP_03_2D
                || getScpMode() == SCPMode.SCP_03_65
                || getScpMode() == SCPMode.SCP_03_6D) {

            logger.debug("==> Init ICV begin");
            setIcv(new byte[16]);
            for (int i = 0; i < getIcv().length; i++) {
                getIcv()[i] = (byte) 0x00;
            }
            logger.debug("* New ICV is " + Conversion.arrayToHex(getIcv()));
            logger.debug("==> Init ICV end");

        }
    }
}
