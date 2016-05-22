package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.binding.event.*;

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

		register(ConnectedEvent.class, new ConnectedEventHandler());
		register(MessageEvent.class, new MessageEventHandler());
		register(ConnectionRequestEvent.class,new ConnectionRequestEventHandler());
		register(JoinRoomEvent.class,new JoinRoomEventHandler());
		register(OutgoingMessageEvent.class, new OutgoingMessageEventHandler());
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
