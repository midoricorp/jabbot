package org.wanna.jabbot.binding;

import org.wanna.jabbot.command.messaging.Message;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-20
 */
public interface BindingListener {
	void onMessage(Binding binding,Message message);
}
