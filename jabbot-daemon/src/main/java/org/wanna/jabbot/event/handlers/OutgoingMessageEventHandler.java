package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.binding.event.OutgoingMessageEvent;
import org.wanna.jabbot.messaging.TxMessage;
import org.wanna.jabbot.event.EventDispatcher;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class OutgoingMessageEventHandler implements EventHandler<OutgoingMessageEvent>{

	@Override
	public boolean process(OutgoingMessageEvent event, EventDispatcher dispatcher) {
		TxMessage response = event.getPayload();
		event.getBinding().sendMessage(response);
		return true;
	}
}
