package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.BindingManager;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.ConnectionException;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.event.EventDispatcher;
import org.wanna.jabbot.web.services.Status;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ConnectionRequestEventHandler implements EventHandler<ConnectionRequestEvent>{
	@Override
	public boolean process(ConnectionRequestEvent event, EventDispatcher dispatcher) {
		final Binding binding = event.getBinding();
		if(binding == null) return false;
		BindingManager.getInstance(binding.getIdentifier()).getStatus().setStatus(Status.StatusType.STARTED);
		try {
			return binding.connect();
		}catch(ConnectionException e){
			BindingManager.getInstance(binding.getIdentifier()).getStatus().setStatus(Status.StatusType.STOPPED);
			throw e;
		}
	}
}
