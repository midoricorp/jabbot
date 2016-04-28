package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ConnectionRequestEvent implements BindingEvent<Binding>{
	private Binding binding;

	public ConnectionRequestEvent(Binding binding) {
		this.binding = binding;
	}

	@Override
	public Binding getPayload() {
		return binding;
	}

	@Override
	public Binding getBinding() {
		return binding;
	}
}
