package org.wanna.jabbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.parser.ParsedCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class JabbotCommandFactory implements CommandFactory{
	final Logger logger = LoggerFactory.getLogger(JabbotCommandFactory.class);

	public Command create(ParsedCommand parsedCommand) throws CommandNotFoundException{
		Command command = registry.get(parsedCommand.getCommandName());
		if(command == null){
			throw new CommandNotFoundException(parsedCommand.getCommandName());
		}

		if(command instanceof CommandFactoryAware){
			((CommandFactoryAware) command).setCommandFactory(this);
		}

		command.setParsedCommand(parsedCommand);
		return command;
	}

	@Override
	public Collection<Command> getAvailableCommands() {
		return (registry ==null?null: registry.values());
	}

	private Map<String,Command> registry = new HashMap<>();
	@Override
	public void register(String commandName, Command command) {
		registry.put(commandName, command);
	}
}
