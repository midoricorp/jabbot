package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.BindingContainer;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.ConnectedEvent;
import org.wanna.jabbot.binding.event.JoinRoomEvent;
import org.wanna.jabbot.event.EventDispatcher;
import org.wanna.jabbot.web.services.Status;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ConnectedEventHandler implements EventHandler<ConnectedEvent>{
	@Override
	public boolean process(ConnectedEvent event, EventDispatcher dispatcher) {
		final Binding binding = event.getBinding();
		if(binding == null) return false;
		BindingContainer.getInstance(binding.getIdentifier()).getStatus().setStatus(Status.StatusType.CONNECTED);
		for (RoomConfiguration roomConfiguration : binding.getConfiguration().getRooms()) {
			BindingEvent joinRequest = new JoinRoomEvent(binding,roomConfiguration);
			dispatcher.dispatch(joinRequest);
		}
		return true;
	}
}
