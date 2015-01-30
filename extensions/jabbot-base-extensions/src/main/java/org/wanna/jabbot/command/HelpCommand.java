package org.wanna.jabbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.AbstractCommand;

import java.util.Collection;


/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class HelpCommand extends AbstractCommand implements CommandFactoryAware {
	private CommandFactory commandFactory;
	final Logger logger = LoggerFactory.getLogger(HelpCommand.class);

	public HelpCommand(final CommandConfig config){
		super(config);
	}

	@Override
	public void process(MucHolder chatroom, MessageWrapper message){
		StringBuilder sb = new StringBuilder("Below is the list of available commands:\n");

		Collection<Command> availableCommands = commandFactory.getAvailableCommands();
		for (Command availableCommand : availableCommands) {
			sb.append(availableCommand.getCommandName()).append("\n");
		}

		chatroom.sendMessage(sb.toString());
	}

	@Override
	public void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}
}
