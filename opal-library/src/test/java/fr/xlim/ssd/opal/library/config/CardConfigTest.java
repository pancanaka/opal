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

import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardConfigTest {

    private CardConfig cardConfig;
    private SCKey scKey;

    @Before
    public void setUp() {
        scKey = mock(SCKey.class);
        cardConfig = new CardConfig
                ("dummy", "dummy", Arrays.asList(new byte[][]{new byte[0]}), new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, new GP2xCommands());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenNameNull() {
        CardConfig cc = new CardConfig
                (null, "dummy", Arrays.asList(new byte[][]{new byte[0]}), new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, new GP2xCommands());
    }

    @Test
    public void checkCardConfigFailedWhenDescriptionNull() {
        CardConfig cc = new CardConfig
                ("dummy", null, Arrays.asList(new byte[][]{new byte[0]}), new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, new GP2xCommands());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenATRsNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", null, new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey},  new GP2xCommands());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenIsdNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", Arrays.asList(new byte[][]{new byte[0]}), null, SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey},  new GP2xCommands());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenScpNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", Arrays.asList(new byte[][]{new byte[0]}), new byte[0], null,
                        "dummy", new SCKey[]{scKey},  new GP2xCommands());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenTpNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", Arrays.asList(new byte[][]{new byte[0]}), new byte[0], SCPMode.SCP_UNDEFINED,
                        null, new SCKey[]{scKey},  new GP2xCommands());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenKeysNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", Arrays.asList(new byte[][]{new byte[0]}), new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", null,  new GP2xCommands());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCardConfigFailedWhenImplNull() {
        CardConfig cc = new CardConfig
                ("dummy", "dummy", Arrays.asList(new byte[][]{new byte[0]}), new byte[0], SCPMode.SCP_UNDEFINED,
                        "dummy", new SCKey[]{scKey}, null);
    }

    @Test
    public void testGetDefaultInitUpdateP1() {
        when(scKey.getVersion()).thenReturn((byte) 255);
        assertEquals(0, cardConfig.getDefaultInitUpdateP1());
        when(scKey.getVersion()).thenReturn((byte) 1);
        assertEquals(1, cardConfig.getDefaultInitUpdateP1());
    }

    @Test
    public void testGetDefaultInitUpdateP2() {
        when(scKey.getId()).thenReturn((byte) 1);
        assertEquals(0, cardConfig.getDefaultInitUpdateP2());
        when(scKey.getId()).thenReturn((byte) 32);
        assertEquals(32, cardConfig.getDefaultInitUpdateP2());
    }
}
