package org.wanna.jabbot.command.messaging;

import org.wanna.jabbot.binding.messaging.MessageContent;

/**
 * A MessageSender is a class which enables a command to autonomously send message.
 * This will enable a command to send messages during it's execution and still returns CommandResult
 * at the end of its execution.
 *
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-24
 * @see MessageContent
 * @see org.wanna.jabbot.command.Command
 */
public interface MessageSender {
	/**
	 * Send a messageContent
     *
     * @param messageContent the MessageContent to send
	 */
	void sendMessage(MessageContent messageContent);
}
