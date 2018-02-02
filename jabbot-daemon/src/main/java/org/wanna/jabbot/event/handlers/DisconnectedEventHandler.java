package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.BindingManager;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.binding.event.DisconnectedEvent;
import org.wanna.jabbot.event.EventDispatcher;
import org.wanna.jabbot.web.services.Status;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class DisconnectedEventHandler implements EventHandler<DisconnectedEvent>{
	@Override
	public boolean process(DisconnectedEvent event, EventDispatcher dispatcher) {
		Binding binding = event.getBinding();
		if( binding == null ){
			return false;
		}

		BindingManager.getInstance(binding.getIdentifier()).getStatus().setStatus(Status.StatusType.STOPPED);
		dispatcher.dispatch(new ConnectionRequestEvent(event.getBinding()));
		return true;
	}
}
