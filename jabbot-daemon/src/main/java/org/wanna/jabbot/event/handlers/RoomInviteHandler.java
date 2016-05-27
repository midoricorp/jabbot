package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.binding.event.JoinRoomEvent;
import org.wanna.jabbot.binding.event.RoomInviteEvent;
import org.wanna.jabbot.event.EventDispatcher;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class RoomInviteHandler implements EventHandler<RoomInviteEvent>{
	@Override
	public boolean process(RoomInviteEvent event, EventDispatcher dispatcher) {
		dispatcher.dispatch(new JoinRoomEvent(event.getBinding(),event.getPayload()));
		return false;
	}
}
