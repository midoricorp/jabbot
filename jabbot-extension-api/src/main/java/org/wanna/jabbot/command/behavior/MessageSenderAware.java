package org.wanna.jabbot.command.behavior;

import org.wanna.jabbot.command.MessageSender;

/**
 * Interface which indicates that the implementor is MessageSender aware.
 * Any commands implementing this interface will have a MessageSender implementation injected
 * which will allow it to autonomously send message.
 * 
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-24
 */
public interface MessageSenderAware {
	/**
	 * Setter for the MessageSender
	 * @param messageSender the message sender to be injected
	 */
	void setMessageSender(MessageSender messageSender);
}
