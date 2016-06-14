package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;

/**
 * Event to notify that a binding successfully connected.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ConnectedEvent extends AbstractBindingEvent<Binding>{
	public ConnectedEvent(Binding binding) {
		super(binding,binding);
	}
}
