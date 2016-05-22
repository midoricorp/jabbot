package org.wanna.jabbot.event.handlers;


import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.event.EventDispatcher;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public interface EventHandler<T extends BindingEvent> {
	boolean process(T event, EventDispatcher dispatcher);
}
