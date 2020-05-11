package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingAware;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.DisconnectionRequestEvent;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.behavior.Configurable;
import org.wanna.jabbot.event.EventManager;
import org.wanna.jabbot.extension.ExtensionLoader;

import java.util.*;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-06-24
 */
public class BindingContainer {
	private static final Map<String,BindingContainer> registry = new HashMap<>();
	private BindingConfiguration configuration;
	private Binding binding;
	private CommandFactory commandFactory;
	private final static Logger logger = LoggerFactory.getLogger(BindingContainer.class);
	private ConnectionInfo connectionInfo;

	/**
	 * Create a new BindingContainer
	 *
	 * @param configuration the Binding configuration
	 * @return fully initialized BindingContainer
	 */
	public static BindingContainer create(BindingConfiguration configuration){
		if(registry.containsKey(configuration.getId())){
			return registry.get(configuration.getId());
		}else{
			logger.info("{} - registering new binding with class {}",configuration.getId(),configuration.getType());
			BindingContainer manager = new BindingContainer(configuration);
			registry.put(configuration.getId(),manager);
			manager.loadCommandExtensions(configuration.getExtensions());

			return manager;
		}
	}

	public static BindingContainer getInstance(String id){
		return registry.get(id);
	}

	public static void remove(String id){
		BindingContainer manager = registry.get(id);
		if(manager != null) {
			//send a disconnect event
			final BindingEvent event = new DisconnectionRequestEvent(manager.getBinding());
			EventManager.getInstance().getOutgoingDispatcher().dispatch(event);
			//stop managing the binding
			registry.remove(id);
		}
	}

	public static Collection<BindingContainer> getRegistry(){
		return registry.values();
	}

	private BindingContainer(BindingConfiguration configuration){
		this.configuration = configuration;
		this.connectionInfo = new ConnectionInfo(configuration.getId());
		this.binding = createBinding(configuration);
		this.commandFactory = new JabbotCommandFactory();
	}

	private Binding createBinding(BindingConfiguration configuration) {
		final ExtensionLoader loader = ExtensionLoader.getInstance();
		final Binding conn;
		conn = loader.getExtension(configuration.getType(), Binding.class, configuration);
		if (conn != null) {
			conn.registerListener(event -> EventManager.getInstance().getIncomingDispatcher().dispatch(event));
		}
		return conn;
	}

	public Command addCommand(ExtensionConfiguration configuration){
		final ExtensionLoader loader = ExtensionLoader.getInstance();
		Command command = loader.getExtension(configuration.getClassName(),Command.class,configuration.getName());

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

			logger.info("{} - registered command {} with alias '{}'", binding.getIdentifier(),command, configuration.getName());
		}

		return command;
	}

	private void loadCommandExtensions(Set<ExtensionConfiguration> configurationSet){
		for (ExtensionConfiguration configuration : configurationSet) {
			addCommand(configuration);
		}

		List<CommandFactoryAware> commandAwareList = new ArrayList<CommandFactoryAware>();
		for (Command command : commandFactory.getAvailableCommands().values()) {
			if (command instanceof CommandFactoryAware) {
				commandAwareList.add(((CommandFactoryAware) command));
			}
		}

		// now that we found the commands that need alerting, alert them
		for (CommandFactoryAware command : commandAwareList) {
			command.onCommandsLoaded();
		}
	}

	public Binding getBinding() {
		return binding;
	}

	public CommandFactory getCommandFactory() {
		return commandFactory;
	}

	public BindingConfiguration getConfiguration() {
		return configuration;
	}

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	@Override
	public String toString() {
		return "BindingContainer{" +
				"id=" + configuration.getId()+
				'}';
	}
}
