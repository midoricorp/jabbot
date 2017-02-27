package org.wanna.jabbot.messaging;

/**
 * MessageContent with extra routing information.
 *
 * @author vmorsiani
 * @since 2017-02-26
 */
public interface RoutableMessageContent extends MessageContent{
	/**
	 * Binding id to which the message content should be delivered
	 * @return String unique identifier of a binding
	 */
	String getBindingId();

	/**
	 * Resource to which the message content should be delivered
	 * @return unique identifier of a resource on a binding
	 */
	String getResourceId();
}
