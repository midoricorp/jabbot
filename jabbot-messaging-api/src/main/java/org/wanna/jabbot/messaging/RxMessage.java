package org.wanna.jabbot.messaging;

/**
 * RXMessages are message which are received from a Binding and then passed onto Jabbot core for futher processing
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public interface RxMessage {
	/**
	 * Content of the message
	 *
	 * @return content
	 */
	MessageContent getMessageContent();

	/**
	 * Resource which initiated the message
	 * @return resource
	 */
	Resource getSender();
}
