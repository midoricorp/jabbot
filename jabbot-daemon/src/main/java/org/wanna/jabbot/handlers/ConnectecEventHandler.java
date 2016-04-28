package org.wanna.jabbot.handlers;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.ConnectedEvent;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ConnectecEventHandler implements EventHandler<ConnectedEvent>{
	@Override
	public void process(ConnectedEvent event) {
		final Binding binding = event.getBinding();
		if(binding == null) return;
		for (RoomConfiguration roomConfiguration : binding.getConfiguration().getRooms()) {
			binding.joinRoom(roomConfiguration);
		}
	}
}
