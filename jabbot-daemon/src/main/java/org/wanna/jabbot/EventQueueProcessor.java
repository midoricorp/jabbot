package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.handlers.EventHandler;
import org.wanna.jabbot.handlers.EventHandlerFactory;

import java.util.Queue;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class EventQueueProcessor implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(EventQueueProcessor.class);
	private final Queue<BindingEvent> queue;

	public EventQueueProcessor(Queue<BindingEvent> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		BindingEvent event = queue.poll();
		if(event == null) return;
		logger.debug("loaded {} from event queue", event);
		EventHandler handler = EventHandlerFactory.getInstance().create(event.getClass());
		if(handler != null){
			try {
				handler.process(event);
			}catch (Exception e){
				logger.error("failed to process {}",event,e);
			}
		}else{
			logger.warn("no handler found for {}",event);
		}
	}
}

