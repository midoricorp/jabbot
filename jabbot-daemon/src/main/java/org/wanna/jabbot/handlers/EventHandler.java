package org.wanna.jabbot.handlers;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public interface EventHandler<T> {
	void process(T event);
}
