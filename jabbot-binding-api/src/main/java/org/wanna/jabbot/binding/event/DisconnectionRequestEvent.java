package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class DisconnectionRequestEvent extends AbstractBindingEvent<Binding>{
	public DisconnectionRequestEvent(Binding binding) {
		super(binding, binding);
	}
}
