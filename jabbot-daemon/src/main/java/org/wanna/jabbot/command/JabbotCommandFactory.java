package org.wanna.jabbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class JabbotCommandFactory implements CommandFactory{
	final Logger logger = LoggerFactory.getLogger(JabbotCommandFactory.class);

	Map<String,Command> registry;

	public void setRegistry(Map<String, Command> registry) {
		this.registry = registry;
	}

	@Override
	public Command create(String commandName) throws CommandNotFoundException{
		Command command = registry.get(commandName);
		if(command == null){
			throw new CommandNotFoundException(commandName);
		}

		if(command instanceof CommandFactoryAware){
			((CommandFactoryAware) command).setCommandFactory(this);
		}

		return command;
	}

	public Command create(ParsedCommand parsedCommand) throws CommandNotFoundException{
		Command command = create(parsedCommand.getCommandName());
		command.setParsedCommand(parsedCommand);
		return command;
	}

	@Override
	public Collection<Command> getAvailableCommands() {
		return (registry==null?null:registry.values());
	}
}
