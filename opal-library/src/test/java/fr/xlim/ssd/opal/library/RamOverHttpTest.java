/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.xlim.ssd.opal.library;


import java.security.Security;

import fr.xlim.ssd.opal.library.commands.ramoverhttp.RAMOverHTTP;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Anis
 */
public class RamOverHttpTest {

    RAMOverHTTP ramOverHTTP;

    @Test
    public void RAMOverHTTPConstructorTest()
    {
        ramOverHTTP = new RAMOverHTTP("psk-tls.key", "null", "PSK_A", "localhost", "OPALJcop21");
        assertEquals(Security.getProperty("jessie.psk.storage.file"),"psk-tls.key");
        assertEquals(Security.getProperty("jessie.clientSessionContext.codec"),"null");
        assertEquals(Security.getProperty("jessie.psk.identity"),"PSK_A");
    }

    

}
