package org.wanna.jabbot;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.statistics.StatisticsManager;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class JabbotCommandFactory implements CommandFactory {
	private Binding binding;

	JabbotCommandFactory(Binding binding) {
		this.binding = binding;
	}

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
		registry.put(commandName, command);
		BindingContainer.getInstance(binding.getIdentifier()).getStatisticsManager().registerCommandStats(commandName);
	}

	@Override
	public void deregister(String commandName) {
		registry.remove(commandName);
		BindingContainer.getInstance(binding.getIdentifier()).getStatisticsManager().removeCommandStats(commandName);
	}
}
