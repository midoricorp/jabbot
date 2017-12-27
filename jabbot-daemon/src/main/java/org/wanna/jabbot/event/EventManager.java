package org.wanna.jabbot.event;

import org.wanna.jabbot.binding.event.BindingEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-06-24
 */
public class EventManager {
	private final static EventManager instance = new EventManager();

	private final BlockingQueue<BindingEvent> incomingQueue,outgoingQueue;
	private EventDispatcher incomingDispatcher,outgoingDispatcher;
	private EventQueueProcessor incomingProcessor, outgoingProcessor;
	private AtomicBoolean running = new AtomicBoolean(false);

	public static EventManager getInstance(){
		return instance;
	}

	private EventManager() {
		this.incomingQueue = new LinkedBlockingDeque<>();
		this.outgoingQueue = new LinkedBlockingDeque<>();

		this.incomingDispatcher = new EventDispatcher(incomingQueue);
		this.outgoingDispatcher = new EventDispatcher(outgoingQueue);
		this.incomingProcessor = new EventQueueProcessor(incomingQueue,outgoingDispatcher,"Incoming Event Processor");
		this.outgoingProcessor = new EventQueueProcessor(outgoingQueue,incomingDispatcher,"Outgoing Event Processor");
	}

	public void start(){
		if(running.compareAndSet(false,true)) {
			incomingProcessor.start();
			outgoingProcessor.start();
		}
	}

	public void stop(){
		if(running.compareAndSet(true,false)) {
			incomingProcessor.halt();
			outgoingProcessor.halt();
		}
	}

	public EventDispatcher getIncomingDispatcher() {
		return incomingDispatcher;
	}

	public EventDispatcher getOutgoingDispatcher() {
		return outgoingDispatcher;
	}
}

