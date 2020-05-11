package org.wanna.jabbot.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.event.handlers.EventHandler;
import org.wanna.jabbot.event.handlers.EventHandlerFactory;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	/**
	 * Constructor
	 * @param queue Queue from which events will be consume
	 * @param dispatcher Event dispatcher facility
	 */
	EventQueueProcessor(BlockingQueue<BindingEvent> queue, EventDispatcher dispatcher, String threadName) {
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
				executorService.submit(new Runner(event));
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

	class Runner implements Runnable{
		final BindingEvent event;
		final Date since;
		Runner(BindingEvent event){
			this.event = event;
			this.since = new Date();
		}

		@Override
		public void run() {
			EventHandler handler = EventHandlerFactory.getInstance().create(event.getClass());
			if (handler != null) {
				try {
					logger.info("{} - processing {}",event.getBinding().getIdentifier(),event);
					handler.process(event, dispatcher);
				} catch (Exception e) {
					logger.error("{} - failed to process {}",event.getBinding().getIdentifier(), event, e);
				}
			} else {
				logger.warn("{} - no handler found for {}", event.getBinding().getIdentifier(),event);
			}
		}
	}
}

