package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.messaging.MessageContent;
import org.wanna.jabbot.messaging.Resource;
import org.wanna.jabbot.messaging.TxMessage;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class OutgoingMessageEvent extends AbstractBindingEvent<TxMessage>{
	private Resource destination;
	private MessageContent requestMessageContent;

	public OutgoingMessageEvent(Binding binding, TxMessage payload) {
		super(binding,payload);
	}

	public Resource getDestination() {
		return destination;
	}

	public void setDestination(Resource destination) {
		this.destination = destination;
	}

	public MessageContent getRequestMessageContent() {
		return requestMessageContent;
	}

	public void setRequestMessageContent(MessageContent requestMessageContent) {
		this.requestMessageContent = requestMessageContent;
	}
}
