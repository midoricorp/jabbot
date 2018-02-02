package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.DisconnectionRequestEvent;
import org.wanna.jabbot.event.EventManager;
import org.wanna.jabbot.extension.ExtensionLoader;
import org.wanna.jabbot.statistics.StatisticsManager;
import org.wanna.jabbot.web.services.Status;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-06-24
 */
public class BindingManager {
	private static final Map<String,BindingManager> managers = new HashMap<>();
	private BindingConfiguration configuration;
	private CommandManager commandManager;
	private StatisticsManager statisticsManager;
	private Binding binding;
	private final static Logger logger = LoggerFactory.getLogger(BindingManager.class);
	private Status bindingStatus;

	public static  BindingManager register(BindingConfiguration configuration){
		if(managers.containsKey(configuration.getId())){
			return managers.get(configuration.getId());
		}else{
			logger.info("{} - registering new binding with class {}",configuration.getId(),configuration.getType());
			BindingManager manager = new BindingManager(configuration);
			managers.put(configuration.getId(),manager);
			return manager;
		}
	}

	public static BindingManager getInstance(String id){
		return managers.get(id);
	}

	public static void remove(String id){
		BindingManager manager = managers.get(id);
		if(manager != null) {
			//unregister Commands
			CommandManager.remove(manager.getBinding());
			//send a disconnect event
			final BindingEvent event = new DisconnectionRequestEvent(manager.getBinding());
			EventManager.getInstance().getOutgoingDispatcher().dispatch(event);
			//stop managing the binding
			managers.remove(id);
		}
	}

	public static Collection<BindingManager> getManagers(){
		return managers.values();
	}

	private BindingManager(BindingConfiguration configuration){
		this.configuration = configuration;
		this.binding = createBinding(configuration);
		this.commandManager = CommandManager.getInstance(binding);
		this.statisticsManager = StatisticsManager.getInstance(binding);
		this.bindingStatus = new Status();
		bindingStatus.setId(configuration.getId());
		for (ExtensionConfiguration extensionConfiguration : configuration.getExtensions()) {
			commandManager.add(extensionConfiguration);
		}
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

	public Binding getBinding() {
		return binding;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public StatisticsManager getStatisticsManager() {
		return statisticsManager;
	}

	public BindingConfiguration getConfiguration() {
		return configuration;
	}

	public Status getStatus() {
		return bindingStatus;
	}

	@Override
	public String toString() {
		return "BindingManager{" +
				"id=" + configuration.getId()+
				'}';
	}
}
