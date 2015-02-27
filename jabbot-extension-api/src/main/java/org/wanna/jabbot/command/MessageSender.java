package org.wanna.jabbot.command;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-24
 */
public interface MessageSender {
	void sendMessage(MessageWrapper request,CommandResult result);
}
