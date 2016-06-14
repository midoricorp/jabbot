package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.Room;

/**
 * Event to notify that a binding successfully joined a room
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class RoomJoinedEvent extends AbstractBindingEvent<Room>{
	public RoomJoinedEvent(Binding binding, Room room) {
		super(binding,room);
	}
}
