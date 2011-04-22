 
package fr.xlim.ssd.opal.gui.exceptions;

/**
 *
 * @author Tiana Razafindralambo
 */
public class SecurityDomainNotFoundException extends Exception {

    public SecurityDomainNotFoundException(){};
    @Override
    public String getMessage(){ return "Security domain isn't yet set.";}
} 