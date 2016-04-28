package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.Room;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class RoomJoinedEvent implements BindingEvent<Room>{
	private Room room;
	private Binding binding;

	public RoomJoinedEvent(Binding binding, Room room) {
		this.binding = binding;
		this.room = room;
	}

	@Override
	public Room getPayload() {
		return room;
	}

	@Override
	public Binding getBinding() {
		return binding;
	}
}
