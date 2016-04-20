package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.CommandNotFoundException;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class JabbotCommandFactory implements CommandFactory {
	final Logger logger = LoggerFactory.getLogger(JabbotCommandFactory.class);

	public Command create(String commandName) throws CommandNotFoundException {
		Command command = registry.get(commandName);
		if(command == null){
			throw new CommandNotFoundException(commandName);
		}
		return command;
	}

	@Override
	public Map<String, Command> getAvailableCommands() {
		return (registry ==null? new TreeMap<String,Command>(): registry);
	}

	private Map<String,Command> registry = new TreeMap<>();
	@Override
	public void register(String commandName, Command command) {
		logger.debug("registering command {} with class {}",commandName,command.getClass());
		registry.put(commandName, command);
	}

	@Override
	public void deregister(String commandName) {
		logger.debug("deregistering command {}",commandName);
		Command c = registry.remove(commandName);
		logger.debug("deregistering command {} with class {}",commandName, c==null?"(null)":c.getClass());
	}
}
