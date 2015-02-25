package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingCreationException;
import org.wanna.jabbot.binding.BindingFactory;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.BindingDefinition;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.behavior.Configurable;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.config.JabbotConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Jabbot {
	final Logger logger = LoggerFactory.getLogger(Jabbot.class);

	private JabbotConfiguration configuration;
	private List<Binding> connectionList = new ArrayList<>();
	private BindingFactory bindingFactory;

	public Jabbot( JabbotConfiguration configuration ) {
		this.configuration = configuration;
		this.bindingFactory = newConnectionFactory(configuration.getBindings());
	}

	public boolean connect(){
		for (BindingConfiguration connectionConfiguration : configuration.getServerList()) {
			Binding conn;
			try {
				conn = bindingFactory.create(connectionConfiguration);
				if(conn instanceof CommandFactoryAware){
					CommandFactory commandFactory = newCommandFactory(connectionConfiguration.getCommands());
					((CommandFactoryAware)conn).setCommandFactory(commandFactory);
				}

				conn.registerListener(new JabbotBindingListener(connectionConfiguration.getCommandPrefix()));
				conn.connect(connectionConfiguration);
				for (RoomConfiguration roomConfiguration : connectionConfiguration.getRooms()) {
					conn.joinRoom(roomConfiguration);
				}
				connectionList.add(conn);
				if(conn.isConnected()){
					logger.debug("connection established to {} as {}",connectionConfiguration.getUrl(),connectionConfiguration.getUsername());
				}
			} catch (BindingCreationException e) {
				logger.error("failed to create binding for {}",connectionConfiguration.getType(),e);
			}
		}
		return true;
	}

	public void disconnect(){
	}

	private BindingFactory newConnectionFactory(Collection<BindingDefinition> bindings){
		BindingFactory factory =  new JabbotBindingFactory();
		if(bindings == null){
			return factory;
		}

		for (BindingDefinition binding : bindings) {
			try {
				Class clazz = Class.<Command>forName(String.valueOf(binding.getClassName()));
				Class<? extends Binding> connectionClass = (Class<? extends Binding>)clazz;
				logger.info("registering {} binding with class {}",binding.getName(),binding.getClassName());
				factory.register(binding.getName(),connectionClass);
			} catch (ClassNotFoundException e) {
				logger.error("unable to register {} binding with class {}",binding.getName(),binding.getClassName());
			}
		}
		return factory;
	}

	/**
	 * Create a new CommandFactory and populate it with the commands registered for that binding
	 * @param commandConfigs The List of {@link org.wanna.jabbot.command.Command} to register in the factory
	 *
	 * @return populated CommandFactory
	 */
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

				if(command instanceof Configurable){
					((Configurable)command).configure(commandConfig.getConfiguration());
				}

				commandFactory.register(commandConfig.getName(),command);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
				logger.error("error creating command",e);
			}
		}
		return commandFactory;
	}
}
