package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.binding.event.JoinRoomEvent;
import org.wanna.jabbot.event.EventDispatcher;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class JoinRoomEventHandler implements EventHandler<JoinRoomEvent>{
	@Override
	public boolean process(JoinRoomEvent event, EventDispatcher dispatcher) {
		event.getBinding().joinRoom(event.getPayload());
		return true;
	}
}