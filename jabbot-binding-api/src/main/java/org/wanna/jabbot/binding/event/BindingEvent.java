package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;

/**
 * A BindingEvent is an event which could either be submitted by a binding or received by a binding.
 * It serves to notify Jabbot core that the binding is waiting on something from the core, or simply notifies it that
 * an incoming event has been properly processed.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public interface BindingEvent<T> {
	/**
	 * Returns the event payload.
	 *
	 * @return payload
	 */
	T getPayload();

	/**
	 * Depending on the direction of the event it either represent the binding which generated the event
	 * Or the binding to which this event is addressed.
	 *
	 * @return Binding
	 */
	Binding getBinding();
}
