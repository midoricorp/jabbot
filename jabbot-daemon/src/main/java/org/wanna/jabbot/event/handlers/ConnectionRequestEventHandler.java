package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.event.EventDispatcher;
import org.wanna.jabbot.ConnectionInfo;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ConnectionRequestEventHandler implements EventHandler<ConnectionRequestEvent>{
	@Override
	public boolean process(ConnectionRequestEvent event, EventDispatcher dispatcher) {
		final Binding binding = event.getBinding();
		if(binding == null) return false;
		return binding.connect();
	}
}
