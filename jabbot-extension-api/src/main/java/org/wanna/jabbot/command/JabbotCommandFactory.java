package org.wanna.jabbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class JabbotCommandFactory implements CommandFactory{
	final Logger logger = LoggerFactory.getLogger(JabbotCommandFactory.class);

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

	@Override
	public Map<String, Command> getAvailableCommands() {
		return (registry ==null? new HashMap<String,Command>(): registry);
	}

	private Map<String,Command> registry = new HashMap<>();
	@Override
	public void register(String commandName, Command command) {
		logger.debug("registering command {} with class {}",commandName,command.getClass());
		registry.put(commandName, command);
	}
}
