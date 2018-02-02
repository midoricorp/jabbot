package org.wanna.jabbot.event.handlers;

import org.wanna.jabbot.binding.event.ServletRegistrationEvent;
import org.wanna.jabbot.event.EventDispatcher;
import org.wanna.jabbot.web.WebServer;

public class ServletRegistrationEventHandler implements EventHandler<ServletRegistrationEvent>{
	@Override
	public boolean process(ServletRegistrationEvent event, EventDispatcher dispatcher) {
		WebServer webServer = WebServer.getInstance();
		if(webServer != null){
			webServer.register(event.getBinding(),event.getPayload());
			return true;
		}else{
			return false;
		}
	}
}
