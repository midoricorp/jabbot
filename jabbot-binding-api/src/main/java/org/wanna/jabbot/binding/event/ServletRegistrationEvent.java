package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.ServletConfiguration;

/**
 * Event used to register a servlet from a binding.
 * This is a way to declare a callback handler for the said binding.
 */
public class ServletRegistrationEvent extends AbstractBindingEvent<ServletConfiguration>{
	public ServletRegistrationEvent(Binding binding, ServletConfiguration payload) {
		super(binding, payload);
	}
}
