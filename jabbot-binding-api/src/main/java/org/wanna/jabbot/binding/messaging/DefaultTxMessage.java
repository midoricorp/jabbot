package org.wanna.jabbot.binding.messaging;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class DefaultTxMessage implements TxMessage {
	private MessageContent messageContent;
	private Resource destination;
	private RxMessage request;

	public DefaultTxMessage(MessageContent messageContent, Resource destination, RxMessage request) {
		this.messageContent = messageContent;
		this.destination = destination;
		this.request = request;
	}

	public MessageContent getMessageContent() {
		return messageContent;
	}

	public Resource getDestination() {
		return destination;
	}

	public RxMessage getRequest() {
		return request;
	}
}
