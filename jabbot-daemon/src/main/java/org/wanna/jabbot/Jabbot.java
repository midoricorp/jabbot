package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.ConnectionCreationException;
import org.wanna.jabbot.binding.ConnectionFactory;
import org.wanna.jabbot.binding.JabbotConnection;
import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.behavior.Configurable;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.config.JabbotConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Jabbot {
	final Logger logger = LoggerFactory.getLogger(Jabbot.class);

	private JabbotConfiguration configuration;
	private List<JabbotConnection> connectionList = new ArrayList<>();
	private ConnectionFactory connectionFactory;

	public Jabbot( JabbotConfiguration configuration ) {
		this.configuration = configuration;
	}

	public boolean connect(){
		for (JabbotConnectionConfiguration connectionConfiguration : configuration.getServerList()) {
			JabbotConnection conn;
			try {
				conn = connectionFactory.create(connectionConfiguration);
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
			} catch (ConnectionCreationException e) {
				logger.error("failed to create binding for {}",connectionConfiguration.getType(),e);
			}
		}
		return true;
	}

	public void disconnect(){
	}

	/**
	 * Create a new Commandractory and populate it with the commands registered for that binding
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

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
