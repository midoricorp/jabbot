package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.event.EventDispatcher;
import org.wanna.jabbot.event.handlers.EventHandler;
import org.wanna.jabbot.event.handlers.EventHandlerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Task to load BindingEvent from a queue and process them.
 * The class will create an EventHandler instance using the EventHandlerFactory
 * and then invoke {@link EventHandler#process(BindingEvent, EventDispatcher)}
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class EventQueueProcessor extends Thread{
	private final Logger logger = LoggerFactory.getLogger(EventQueueProcessor.class);
	private final BlockingQueue<BindingEvent> queue;
	private final EventDispatcher dispatcher;
	private boolean running = false;
	/**
	 * Constructor
	 * @param queue Queue from which events will be consume
	 * @param dispatcher Event dispatcher facility
	 */
	public EventQueueProcessor(BlockingQueue<BindingEvent> queue, EventDispatcher dispatcher, String threadName) {
		super(threadName);
		this.queue = queue;
		this.dispatcher = dispatcher;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				BindingEvent event = queue.take();
				logger.debug("{} - loaded {} from event queue", event.getBinding().getIdentifier(),event);
				EventHandler handler = EventHandlerFactory.getInstance().create(event.getClass());
				if (handler != null) {
					try {
						boolean status = handler.process(event, dispatcher);
						logger.info("{} - event {} processed: {}",event.getBinding().getIdentifier(), event, status);
					} catch (Exception e) {
						logger.error("{} - failed to process {}: {}",event.getBinding().getIdentifier(), event, e);
					}
				} else {
					logger.warn("{} - no handler found for {}", event.getBinding().getIdentifier(),event);
				}

			} catch (InterruptedException e) {
				logger.error("interrupted",e);
			}
		}
	}

	public void halt(){
		running = false;
		this.interrupt();
		try {
			this.join();
		} catch (InterruptedException e) {
			logger.error("could not join {}",this.getName(),e);
		}
	}
}

