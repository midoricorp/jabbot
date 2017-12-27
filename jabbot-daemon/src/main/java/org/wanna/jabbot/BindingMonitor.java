package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.binding.event.DisconnectionRequestEvent;
import org.wanna.jabbot.event.EventManager;

import java.util.Collection;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class BindingMonitor implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(BindingMonitor.class);
	private final Collection<BindingManager> bindings;

	public BindingMonitor(Collection<BindingManager> bindings) {
		this.bindings = bindings;
	}

	@Override
	public void run() {
		logger.trace("checking binding health");
		for (final BindingManager manager : bindings) {
			try{
				if(!manager.getBinding().isConnected()){
					logger.info("binding {} is disconnected. queueing for connection...",manager);
					EventManager.getInstance().getOutgoingDispatcher().dispatch(new DisconnectionRequestEvent(manager.getBinding()));
					EventManager.getInstance().getOutgoingDispatcher().dispatch(new ConnectionRequestEvent(manager.getBinding()));
				}else{
					logger.trace("binding {} is connected",manager);
				}
			}catch (Throwable t){
				logger.error("unable to check {} health",manager,t);
			}
		}
		logger.trace("health check done");
	}
}
