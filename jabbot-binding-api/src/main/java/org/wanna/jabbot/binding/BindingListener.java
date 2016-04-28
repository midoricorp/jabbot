package org.wanna.jabbot.binding;


import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.messaging.Message;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-20
 */
public interface BindingListener {
	void eventReceived(BindingEvent event);
}
