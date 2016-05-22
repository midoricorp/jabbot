package org.wanna.jabbot.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.event.BindingEvent;

import java.util.Queue;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class EventDispatcher {
	private final Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

	private final Queue<BindingEvent> queue;

	public EventDispatcher(Queue<BindingEvent> queue) {
		this.queue = queue;
	}

	public void dispatch(BindingEvent event){
		logger.debug("dispatching {}",event);
		queue.offer(event);
	}
}
