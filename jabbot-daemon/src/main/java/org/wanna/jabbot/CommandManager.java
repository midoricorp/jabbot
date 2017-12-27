package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingAware;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.behavior.Configurable;
import org.wanna.jabbot.extension.ExtensionLoader;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-24
 */
public class CommandManager {
	private final static Logger logger = LoggerFactory.getLogger(CommandManager.class);
	private static final Map<Binding,CommandManager> instances = new WeakHashMap<>();
	private CommandFactory commandFactory;
	private Binding binding;

	private CommandManager(Binding binding) {
		this.binding = binding;
		commandFactory = new JabbotCommandFactory();
	}

	public static CommandManager register(Binding binding){
		if(!instances.containsKey(binding)){
			CommandManager manager = new CommandManager(binding);
			instances.put(binding,manager);
			return manager;
		}
		return instances.get(binding);
	}

	public static void remove(Binding binding){
		instances.remove(binding);
	}

	/**
	 * Retrieve the command factory associated to a given binding
	 *
	 * @return Command factory
	 */
	public CommandFactory getCommandFactory(){
		return commandFactory;
	}

	/**
	 * Removes a command from the CommandFactory
	 *
	 * @param name command name
	 */
	public void remove(String name){
		commandFactory.deregister(name);
	}

	/**
	 * Create a new Command instance from an ExtensionConfiguration and add it to the CommandFactory
	 * @param configuration Extension configuration
	 *
	 * @return created command
	 */
	public Command add(ExtensionConfiguration configuration){
		Command command = ExtensionLoader.getInstance().getExtension(configuration.getClassName(),Command.class,configuration.getName());
		if(command != null) {
			if (command instanceof CommandFactoryAware) {
				((CommandFactoryAware) command).setCommandFactory(commandFactory);
			}

			if (command instanceof Configurable) {
				((Configurable) command).configure(configuration.getConfiguration());
			}

			if (command instanceof BindingAware) {
				((BindingAware) command).setBinding(binding);
			}
			commandFactory.register(configuration.getName(), command);
			logger.info("registered command {} with alias '{}' in {}", command, configuration.getName(), binding);
		}
		return  command;
	}
}

