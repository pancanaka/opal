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

    /// Encryption session key
    protected byte[] sessEnc;

    /// C-MAC session key
    protected byte[] sessMac;

    /// R-MAC session key
    protected byte[] sessRMac;

    /// Data Encryption session key
    protected byte[] sessKek;

    /// Host challenge used to authenticate host in smartcard
    protected byte[] hostChallenge;

    /// Card challenge used to authenticate smartcard in host
    protected byte[] cardChallenge;

    /// Card response challenge
    protected byte[] cardCrypto;

    /// Derivation data used to calculate session keys
    protected byte[] derivationData;

    /// Host challenge result
    protected byte[] hostCrypto;

    /// Sequence counter used in SCP 02. Its value is the number of previous validate authentication
    protected byte[] sequenceCounter;

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

    public byte[] getSessEnc() {
        return sessEnc;
    }

    public void setSessEnc(byte[] sessEnc) {
        this.sessEnc = sessEnc;
    }

    public byte[] getSessMac() {
        return sessMac;
    }

    public void setSessMac(byte[] sessMac) {
        this.sessMac = sessMac;
    }

    public byte[] getSessRMac() {
        return sessRMac;
    }

    public void setSessRMac(byte[] sessRMac) {
        this.sessRMac = sessRMac;
    }

    public byte[] getSessKek() {
        return sessKek;
    }

    public void setSessKek(byte[] sessKek) {
        this.sessKek = sessKek;
    }

    public byte[] getHostChallenge() {
        return hostChallenge;
    }

    public void setHostChallenge(byte[] hostChallenge) {
        this.hostChallenge = hostChallenge;
    }

    public byte[] getCardChallenge() {
        return cardChallenge;
    }

    public void setCardChallenge(byte[] cardChallenge) {
        this.cardChallenge = cardChallenge;
    }

    public byte[] getCardCrypto() {
        return cardCrypto;
    }

    public void setCardCrypto(byte[] cardCrypto) {
        this.cardCrypto = cardCrypto;
    }

    public byte[] getDerivationData() {
        return derivationData;
    }

    public void setDerivationData(byte[] derivationData) {
        this.derivationData = derivationData;
    }

    public byte[] getHostCrypto() {
        return hostCrypto;
    }

    public void setHostCrypto(byte[] hostCrypto) {
        this.hostCrypto = hostCrypto;
    }

    public byte[] getSequenceCounter() {
        return sequenceCounter;
    }

    public void setSequenceCounter(byte[] sequenceCounter) {
        this.sequenceCounter = sequenceCounter;
    }

    /**
     * Calculate Derivation data. This step depending to the @see{fr.xlim.ssd.opal.library.commands.GP2xCommands.initializeUpdate} card response.
     */
    public void calculateDerivationData() {

        logger.debug("==> Calculate Derivation Data");

        if ((getScpMode() == SCPMode.SCP_UNDEFINED)
                || (getScpMode() == SCPMode.SCP_01_05)
                || (getScpMode() == SCPMode.SCP_01_15)) { // SCP 01_*

            setDerivationData(new byte[16]);

            System.arraycopy(getHostChallenge(), 0, getDerivationData(), 4, 4);
            System.arraycopy(getHostChallenge(), 4, getDerivationData(), 12, 4);
            System.arraycopy(getCardChallenge(), 0, getDerivationData(), 8, 4);
            System.arraycopy(getCardChallenge(), 4, getDerivationData(), 0, 4);

        } else if (getScpMode() == SCPMode.SCP_02_15
                || getScpMode() == SCPMode.SCP_02_04
                || getScpMode() == SCPMode.SCP_02_05
                || getScpMode() == SCPMode.SCP_02_14
                || getScpMode() == SCPMode.SCP_02_0A
                || getScpMode() == SCPMode.SCP_02_45
                || getScpMode() == SCPMode.SCP_02_55) { // SCP 02_*

            setDerivationData(new byte[16]);
            System.arraycopy(getSequenceCounter(), 0, getDerivationData(), 2, 2);

        } else if ((getScpMode() == SCPMode.SCP_03_65)
                || (getScpMode() == SCPMode.SCP_03_6D)
                || (getScpMode() == SCPMode.SCP_03_05)
                || (getScpMode() == SCPMode.SCP_03_0D)
                || (getScpMode() == SCPMode.SCP_03_2D)
                || (getScpMode() == SCPMode.SCP_03_25)) {


            /*
             * Derivation data in SCP 03 mode
             *
             * -0-----------------------10--11---12--13--14--15-
             * | label (11 byte of 00)    | dc | 00 |  L   | i  |
             * -------------------------------------------------
             *
             * --16------------23-24-------------31-
             *  | Host Challenge | Card Challenge |
             * -------------------------------------
             *
             * Definition of the derivation constant (dc):
             * - 00 : derivation data to calculate card cryptogram
             * - 01 : derivation data to calculate host cryptogram
             * - 04 : derivation of S-ENC
             * - 06 : derivation of S-MAC
             * - 07 : derivation of S-RMAC
             */


            setDerivationData(new byte[32]);
            byte[] label = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            System.arraycopy(label, 0, getDerivationData(), 0, label.length);
            System.arraycopy(getHostChallenge(), 0, getDerivationData(), 16, getHostChallenge().length);
            System.arraycopy(getCardChallenge(), 0, getDerivationData(), 24, getHostChallenge().length);
        }

        logger.debug("* Derivation Data is " + Conversion.arrayToHex(getDerivationData()));

        logger.debug("==> Calculate Derivation Data End");

    }
}
