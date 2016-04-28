package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.event.BindingEvent;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class LoggingEventListener implements BindingListener{
	Logger logger = LoggerFactory.getLogger(LoggingEventListener.class);

	@Override
	public void eventReceived(BindingEvent event) {
		logger.info("received an event: {}",event);
	}
}
