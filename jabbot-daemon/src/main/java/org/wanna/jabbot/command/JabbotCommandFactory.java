package org.wanna.jabbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public Command create(ParsedCommand parsedCommand){
		Command command = registry.get(parsedCommand.getCommandName());
		//If matching command is found, initialize it
		if(command != null){
			command.setParsedCommand(parsedCommand);
		}

		return command;
	}

	@Override
	public Map<String, Command> getAvailableCommands() {
		return registry;
	}
}
