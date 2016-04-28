package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;

import java.util.List;
import java.util.Queue;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class BindingMonitor implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(BindingMonitor.class);
	private final Queue<BindingEvent> queue;
	private final List<Binding> bindings;

	public BindingMonitor(List<Binding> bindings,Queue<BindingEvent> queue) {
		this.bindings = bindings;
		this.queue = queue;
	}

	@Override
	public void run() {
		logger.trace("checking binding health");
		for (final Binding binding : bindings) {
			try{
				if(!binding.isConnected()){
					logger.info("binding {} is disconnected. queueing for connection...",binding);
					queue.offer(new ConnectionRequestEvent(binding));
				}else{
					logger.trace("binding {} is connected",binding);
				}

			}catch (Throwable t){
				logger.error("unable to check {} health",binding,t);
			}
		}
		logger.trace("health check done");
	}
}
