package org.wanna.jabbot.command;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public interface Command {
	String getCommandName();
	ParsedCommand getParsedCommand();
	void setParsedCommand(ParsedCommand parsedCommand);
	void process(MucHolder chatroom, Message message) throws XMPPException, SmackException.NotConnectedException;
}

