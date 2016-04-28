package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.messaging.Message;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class MessageEvent implements BindingEvent<Message>{
	private Message message;
	private Binding binding;

	public MessageEvent(Binding binding, Message message) {
		this.binding = binding;
		this.message = message;
	}

	@Override
	public Message getPayload() {
		return message;
	}

	@Override
	public Binding getBinding() {
		return binding;
	}
}
