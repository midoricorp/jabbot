package org.wanna.jabbot;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.DisconnectionRequestEvent;
import org.wanna.jabbot.event.EventManager;
import org.wanna.jabbot.extension.ExtensionLoader;

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
	private Binding binding;

	public static  BindingManager register(BindingConfiguration configuration){
		if(managers.containsKey(configuration.getId())){
			return managers.get(configuration.getId());
		}else{
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
		this.commandManager = CommandManager.register(binding);
		for (ExtensionConfiguration extensionConfiguration : configuration.getExtensions()) {
			commandManager.add(extensionConfiguration);
		}
	}

	private Binding createBinding(BindingConfiguration configuration) {
		final ExtensionLoader loader = ExtensionLoader.getInstance();
		final Binding conn;
		conn = loader.getExtension(configuration.getType(), Binding.class, configuration);
		if (conn != null) {
			conn.registerListener(new BindingListener() {
				@Override
				public void eventReceived(BindingEvent event) {
					EventManager.getInstance().getIncomingDispatcher().dispatch(event);
				}
			});

		}
		return conn;
	}

	public Binding getBinding() {
		return binding;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public BindingConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public String toString() {
		return "BindingManager{" +
				"id=" + configuration.getId()+
				'}';
	}
}
