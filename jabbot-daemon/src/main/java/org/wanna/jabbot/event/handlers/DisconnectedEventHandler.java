package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.BindingContainer;
import org.wanna.jabbot.binding.Binding;
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
		Binding binding = event.getBinding();
		if( binding == null ){
			return false;
		}

		BindingContainer.getInstance(binding.getIdentifier()).getConnectionInfo().setStatus(ConnectionInfo.StatusType.STOPPED);
		dispatcher.dispatch(new ConnectionRequestEvent(event.getBinding()));
		return true;
	}
}
