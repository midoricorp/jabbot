package org.wanna.jabbot.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.JabbotCommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-08
 */
public abstract class AbstractJabbotConnection<T> implements JabbotConnection<T> {
	private final Logger logger = LoggerFactory.getLogger(AbstractJabbotConnection.class);
	protected T connection;
	protected CommandFactory commandFactory;
	private JabbotConnectionConfiguration configuration;

	protected AbstractJabbotConnection(JabbotConnectionConfiguration configuration) {
		this.configuration = configuration;
		logger.debug("initializing command factory for {}",configuration.getUrl());
		this.commandFactory = newCommandFactory(configuration.getCommands());
	}

	public T getWrappedConnection() {
		return connection;
	}

	private CommandFactory newCommandFactory(Set<CommandConfig> commandConfigs){
		CommandFactory commandFactory = new JabbotCommandFactory();
		if(commandConfigs == null){
			return commandFactory;
		}

		for (CommandConfig commandConfig : commandConfigs) {
			try {
				Class<Command> commandClass = (Class<Command>)Class.forName(commandConfig.getClassName());
				Command command = commandClass.getDeclaredConstructor(CommandConfig.class).newInstance(commandConfig);
				if(command instanceof CommandFactoryAware){
					((CommandFactoryAware)command).setCommandFactory(commandFactory);
				}
				commandFactory.register(commandConfig.getName(),command);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
				logger.error("error creating command",e);
			}
		}
		return commandFactory;
	}

	public JabbotConnectionConfiguration getConfiguration(){
		return configuration;
	}
}
