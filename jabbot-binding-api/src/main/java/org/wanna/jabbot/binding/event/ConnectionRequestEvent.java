package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;

/**
 * Event to request a binding to initiate a connection.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ConnectionRequestEvent extends AbstractBindingEvent<Binding>{
	public ConnectionRequestEvent(Binding binding) {
		super(binding,binding);
	}
}
