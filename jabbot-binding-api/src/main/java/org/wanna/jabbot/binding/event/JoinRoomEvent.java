package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.config.RoomConfiguration;

/**
 * Event to request a binding to join a room
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class JoinRoomEvent extends AbstractBindingEvent<RoomConfiguration>{
	public JoinRoomEvent(Binding binding, RoomConfiguration payload) {
		super(binding, payload);
	}
}
