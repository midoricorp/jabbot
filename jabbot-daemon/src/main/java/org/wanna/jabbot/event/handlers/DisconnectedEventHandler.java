package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.binding.event.DisconnectedEvent;
import org.wanna.jabbot.event.EventDispatcher;
import org.wanna.jabbot.ConnectionInfo;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class DisconnectedEventHandler implements EventHandler<DisconnectedEvent>{
	@Override
	public boolean process(DisconnectedEvent event, EventDispatcher dispatcher) {
		dispatcher.dispatch(new ConnectionRequestEvent(event.getBinding()));
		return true;
	}
}
