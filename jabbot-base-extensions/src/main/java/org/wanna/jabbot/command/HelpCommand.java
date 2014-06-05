package org.wanna.jabbot.command;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
class HelpCommand extends AbstractCommand implements CommandFactoryAware{
	private CommandFactory commandFactory;
	final Logger logger = LoggerFactory.getLogger(HelpCommand.class);


	@Override
	public String getCommandName() {
		return "help";
	}

	@Override
	public void process(MucHolder chatroom, Message message) throws XMPPException, SmackException.NotConnectedException {
		logger.debug("{} triggered on {} for message {}",getCommandName(),chatroom.getRoomName(),message.getBody());
		StringBuilder sb = new StringBuilder("Below is the list of available commands:\n");

		Collection<Command> availableCommands = commandFactory.getAvailableCommands();
		for (Command availableCommand : availableCommands) {
			sb.append(availableCommand.getCommandName()).append("\n");
		}

		chatroom.getMuc().sendMessage(sb.toString());
	}

	@Override
	public void process(Message message) {
	}

	@Override
	public void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}
}
