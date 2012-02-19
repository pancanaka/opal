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
package fr.xlim.ssd.opal.library.config;

import fr.xlim.ssd.opal.library.commands.Commands;

import java.util.List;

/**
 * Contains card configuration information (instance obtained from CardConfigFactory)
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 * @see fr.xlim.ssd.opal.library.CardConfigFactory
 */

public class CardConfig {

    /// The configuration name
    private String name;

    /// The configuration description
    private String description;

    /// The ATR list linked to this card configuration
    private List<byte[]> atrs;

    /// The Issuer Security Domain (ISD) AID
    private byte[] isd;

    /// The Secure Channel Protocol (SCP) Mode
    private SCPMode scp;

    /// The transmission protocol
    private String tp;

    /// The credentials keys
    private SCKey[] keys;

    /// The implementation class name
    private Commands implementation;

    // a card config is local if not present in the main config file <classpath://config.xml>
    private boolean local;

    /**
     * Constructor for CardConfig object
     *
     * @param name           the card configuration name
     * @param description    the card configuration description
     * @param atrs           the card ATR linked to this configuration
     * @param isd            the issuer security domain AID
     * @param scp            the SCP mode
     * @param tp             the transmission protocol
     * @param keys           the credential keys
     * @param implementation the class name of the implementation
     */
    public CardConfig(String name,
                      String description,
                      List<byte[]> atrs,
                      byte[] isd,
                      SCPMode scp,
                      String tp,
                      SCKey[] keys,
                      Commands implementation) {

        if (name == null) {
            throw new IllegalArgumentException("name must be not null");
        }

        // description may be null

        // At least one ATR
        if (atrs == null) {
            throw new IllegalArgumentException("atrs must be not null");
        }

        if (atrs.size() == 0) {
            throw new IllegalArgumentException("atrs must contain at lesat one ATR");
        }

        if (isd == null) {
            throw new IllegalArgumentException("isd must be not null");
        }

        if (scp == null) {
            throw new IllegalArgumentException("scp must be not null");
        }

        if (tp == null) {
            throw new IllegalArgumentException("tp must be not null");
        }

        if (keys == null) {
            throw new IllegalArgumentException("keys must be not null");
        }

        if (implementation == null) {
            throw new IllegalArgumentException("implementation must be not null");
        }

        this.name = name;
        this.description = description;
        this.atrs = atrs;
        this.isd = isd;
        this.scp = scp;
        this.tp = tp;
        this.keys = keys;
        this.implementation = implementation;
        this.local = false;
    }

    /**
     * Get the name used in the configuration file
     *
     * @return A string with the configuration name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description used in the configuration file
     *
     * @return A string with the configuration description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the card ATRs linked to this configuration
     *
     * @return An ATR array which contains linked card ATR
     */
    public List<byte[]> getAtrs() {
        return atrs;
    }

    /**
     * Get the implementation class used by this card configuration
     *
     * @return A string with the class name
     */
    public Commands getImplementation() {
        return this.implementation;
    }

    /**
     * Get the issuer security domain AID used by this card configuration
     *
     * @return An byte array with the ISD AID
     */
    public byte[] getIsd() {
        return this.isd;
    }

    /**
     * Get the secure channel protocol mode used by this card configuration
     *
     * @return the SCP mode
     */
    public SCPMode getScpMode() {
        return this.scp;
    }

    /**
     * Get the transmission protocol used by this card configuration
     *
     * @return the transmission protocol used
     */
    public String getTransmissionProtocol() {
        return this.tp;
    }

    /**
     * Get the Secure Channel Keys used by this card configuration
     *
     * @return the default SC keys
     */
    public SCKey[] getSCKeys() {
        return this.keys;
    }

    /**
     * Get the value P1 used by an extra-step (typically from proprietary class like Gemalto)
     *
     * @return the P1 param to send to initUpdtate Command according to the KeySetVersion found in configured keys
     */
    public byte getDefaultInitUpdateP1() {
        if (this.keys[0].getVersion() == (byte) 255) {
            return 0;
        } else {
            return this.keys[0].getVersion();
        }
    }

    /**
     * Get the value P2 used by an extra-step (typically from proprietary class like Gemalto)
     *
     * @return the P2 param to send to initUpdtate Command according to the first KeyId found in configured keys
     */
    public byte getDefaultInitUpdateP2() {
        if (this.keys[0].getId() == 1) {
            return 0;
        } else {
            return this.keys[0].getId();
        }

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardConfig that = (CardConfig) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
