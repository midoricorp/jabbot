package org.wanna.jabbot.messaging;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class DefaultRxMessage implements RxMessage {
	private MessageContent messageContent;
	private Resource sender;

	public DefaultRxMessage(MessageContent messageContent, Resource sender) {
		this.messageContent = messageContent;
		this.sender = sender;
	}

	public MessageContent getMessageContent() {
		return messageContent;
	}

	public Resource getSender() {
		return sender;
	}
}
