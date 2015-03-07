package org.wanna.jabbot.command;

/**
 * A MessageSender is a class which enables a command to autonomously send message.
 * This will enable a command to send messages during it's execution and still returns CommandResult
 * at the end of its execution.
 *
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-24
 * @see org.wanna.jabbot.command.CommandMessage
 * @see org.wanna.jabbot.command.Command
 */
public interface MessageSender {
	/**
	 * Send a message
	 *
	 */
	void sendMessage(CommandMessage message);
}
