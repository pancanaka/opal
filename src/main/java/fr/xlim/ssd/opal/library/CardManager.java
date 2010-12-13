package fr.xlim.ssd.opal.library;

import javax.smartcardio.CardChannel;

import fr.xlim.ssd.opal.library.commands.CommandsImplementationNotFound;

/**
 * This class contains methods that could be sent to the Card Manager. <br/>
 * As explained in Global Platform specification, a Card Manager contains : 
 * <ul>
 * <li>The Global Platform Environment</li>
 * <li>The Issuer Security Domain</li>
 * <li>Cardholder Verification Method Services</li>
 * </ul>
 * 
 * @author Damien Arcuset, Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public class CardManager extends SecurityDomain {

	/**
	 * Creates the off-card "Card Manager"
	 * @param CmdImplementation the String representation of the choosen implemetation (i.e. "fr.xlim.ssd.opal.commands.GP2xCommands") <br/>
	 * This designed implementation must override the class {@link fr.xlim.ssd.opal.library.commands.Commands}
	 * @param cc the initialized card channel on which data will be sent to the card
	 * @param aid the byte array containing the aid representation of the card manager
	 * @throws CommandsImplementationNotFound
	 * @throws ClassNotFoundException
	 */
	public CardManager(String CmdImplementation, CardChannel cc, byte[] aid)
			throws CommandsImplementationNotFound, ClassNotFoundException {
		super(CmdImplementation, cc, aid);
	}
}