package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.binding.event.DisconnectionRequestEvent;
import org.wanna.jabbot.event.EventManager;
import org.wanna.jabbot.web.services.Status;

import java.util.Collection;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class BindingMonitor implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(BindingMonitor.class);
	private final Collection<BindingContainer> bindings;

	BindingMonitor(Collection<BindingContainer> bindings) {
		this.bindings = bindings;
	}

	@Override
	public void run() {
		logger.trace("checking binding health");
		for (final BindingContainer manager : bindings) {
			try{
				if(!manager.getBinding().isConnected()){
					logger.info("{} - binding is disconnected. queueing for connection...",manager.getBinding().getIdentifier());
					EventManager.getInstance().getOutgoingDispatcher().dispatch(new DisconnectionRequestEvent(manager.getBinding()));
					EventManager.getInstance().getOutgoingDispatcher().dispatch(new ConnectionRequestEvent(manager.getBinding()));
				}else{
					logger.trace("{} - binding is connected",manager.getBinding().getIdentifier());
					manager.getStatus().setStatus(Status.StatusType.CONNECTED);
				}
			}catch (Throwable t){
				logger.error("{} - unable to check health",manager.getBinding().getIdentifier(),t);
			}
		}
		logger.trace("health check done");
	}
}
