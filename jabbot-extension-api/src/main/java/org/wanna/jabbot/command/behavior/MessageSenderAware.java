package org.wanna.jabbot.command.behavior;

import org.wanna.jabbot.command.MessageSender;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-24
 */
public interface MessageSenderAware {
	void setMessageSender(MessageSender messageSender);
}
