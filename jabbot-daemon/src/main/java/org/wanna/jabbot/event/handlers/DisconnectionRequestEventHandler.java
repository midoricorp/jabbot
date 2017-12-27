package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.binding.event.DisconnectionRequestEvent;
import org.wanna.jabbot.event.EventDispatcher;

/**
 * Event used to request a binding to disconnect.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class DisconnectionRequestEventHandler implements EventHandler<DisconnectionRequestEvent>{
	@Override
	public boolean process(DisconnectionRequestEvent event, EventDispatcher dispatcher) {
		event.getBinding().disconnect();
		return true;
	}
}
