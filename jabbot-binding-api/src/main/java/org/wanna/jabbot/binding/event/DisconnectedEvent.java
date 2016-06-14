package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;

/**
 * Event to notify that a binding has been disconnected
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class DisconnectedEvent extends AbstractBindingEvent<Binding>{
	public DisconnectedEvent(Binding binding) {
		super(binding, binding);
	}
}
