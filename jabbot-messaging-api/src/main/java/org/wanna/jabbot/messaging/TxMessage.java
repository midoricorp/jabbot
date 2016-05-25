package org.wanna.jabbot.messaging;

/**
 * TxMessage are message which are passed from the Core to a binding so that binding can submit it to the protocol
 * it is bound to.
 *
 * In an usual scenario TXMessage will be translated by the binding into a binding specific message such like:
 * XMPP Message, IRC Message and so on. And then passed onto the underlying binding connection for further processing.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public interface TxMessage {
	/**
	 * The content of the message to send.
	 *
	 * @return message content
	 */
	MessageContent getMessageContent();

	/**
	 * The destination resource where the message should be delivered.
	 *
	 * @return resource
	 */
	Resource getDestination();

	/**
	 * The RxMessage which initiated this TxMessage
	 *
	 * @return RxMessage
	 */
	RxMessage getRequest();
}
