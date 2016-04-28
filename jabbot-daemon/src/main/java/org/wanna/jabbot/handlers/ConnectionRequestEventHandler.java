package org.wanna.jabbot.handlers;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ConnectionRequestEventHandler implements EventHandler<ConnectionRequestEvent>{
	@Override
	public void process(ConnectionRequestEvent event) {
		final Binding binding = event.getBinding();
		if(binding == null) return;
		binding.connect();
	}
}
