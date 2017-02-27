package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.event.EventDispatcher;

import java.util.Collection;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
class BindingMonitor implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(BindingMonitor.class);
	private final EventDispatcher dispatcher;
	private final Collection<Binding> bindings;

	BindingMonitor(Collection<Binding> bindings, EventDispatcher dispatcher) {
		this.bindings = bindings;
		this.dispatcher = dispatcher;
	}

	@Override
	public void run() {
		logger.trace("checking binding health");
		for (final Binding binding : bindings) {
			try{
				if(!binding.isConnected()){
					logger.info("binding {} is disconnected. queueing for connection...",binding);
					dispatcher.dispatch(new ConnectionRequestEvent(binding));
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
