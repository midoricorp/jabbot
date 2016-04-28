package org.wanna.jabbot.handlers;

import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.ConnectedEvent;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.binding.event.MessageEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class EventHandlerFactory {
	private static EventHandlerFactory _instance = new EventHandlerFactory();

	private Map<Class<? extends BindingEvent>,EventHandler> registry;

	private EventHandlerFactory(){
		registry = new HashMap<>();

		register(ConnectedEvent.class, new ConnectecEventHandler());
		register(MessageEvent.class, new MessageEventHandler());
		register(ConnectionRequestEvent.class,new ConnectionRequestEventHandler());
	}

	public static EventHandlerFactory getInstance(){
		return _instance;
	}

	public EventHandler create(Class<? extends BindingEvent> clazz){
		return registry.get(clazz);
	}

	public void register(Class<? extends BindingEvent> clazz,EventHandler handler){
		registry.put(clazz,handler);
	}
}
