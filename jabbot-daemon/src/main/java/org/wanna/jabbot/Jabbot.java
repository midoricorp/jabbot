package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingCreationException;
import org.wanna.jabbot.binding.BindingFactory;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.BindingDefinition;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.ConnectionRequestEvent;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.config.JabbotConfiguration;
import org.wanna.jabbot.handlers.EventHandler;
import org.wanna.jabbot.handlers.EventHandlerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Jabbot {
	final Logger logger = LoggerFactory.getLogger(Jabbot.class);

	private JabbotConfiguration configuration;
	private BindingFactory bindingFactory;
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

	private List<Binding> bindings = new ArrayList<>();
	private Queue<BindingEvent> eventQueue;

	public Jabbot( JabbotConfiguration configuration ) {
		eventQueue = new ConcurrentLinkedQueue<>();
		this.configuration = configuration;
		this.bindingFactory = newConnectionFactory(configuration.getBindings());
		scheduledExecutorService.scheduleAtFixedRate(new BindingMonitor(bindings,eventQueue), 5L,60L, TimeUnit.SECONDS);
		scheduledExecutorService.scheduleAtFixedRate(new EventQueueProcessor(eventQueue),5L,1L,TimeUnit.SECONDS);
	}

	public boolean connect(){
		BindingListener listener = new JabbotBindingListener(eventQueue);
		for (final BindingConfiguration connectionConfiguration : configuration.getServerList()) {
			final Binding conn;
			try {
				conn = bindingFactory.create(connectionConfiguration);
				CommandManager.getInstanceFor(conn).initializeFromConfigSet(connectionConfiguration.getExtensions());
                conn.registerListener(listener);
				conn.registerListener(new LoggingEventListener());
				bindings.add(conn);
				if(conn.isConnected()){
					logger.debug("connection established to {} as {}",connectionConfiguration.getUrl(),connectionConfiguration.getUsername());
				}
			} catch (BindingCreationException e) {
				logger.error("failed to create binding for {}",connectionConfiguration.getType(),e);
			}
		}
		return true;
	}

	public void disconnect(){
	}

	private BindingFactory newConnectionFactory(Collection<BindingDefinition> bindings){
		BindingFactory factory =  new JabbotBindingFactory();
		if(bindings == null){
			return factory;
		}

		for (BindingDefinition binding : bindings) {
			try {
				Class clazz = Class.<Command>forName(String.valueOf(binding.getClassName()));
                @SuppressWarnings("unchecked")
				Class<? extends Binding> connectionClass = (Class<? extends Binding>)clazz;
				logger.info("registering {} binding with class {}",binding.getName(),binding.getClassName());
				factory.register(binding.getName(),connectionClass);
			} catch (ClassNotFoundException e) {
				logger.error("unable to register {} binding with class {}",binding.getName(),binding.getClassName());
			}
		}
		return factory;
	}

}
