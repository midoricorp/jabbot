package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingCreationException;
import org.wanna.jabbot.binding.BindingFactory;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.BindingDefinition;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.config.JabbotConfiguration;
import org.wanna.jabbot.event.EventDispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
	private BlockingQueue<BindingEvent> incomingQueue;
	private BlockingQueue<BindingEvent> outgoingQueue;
	private EventDispatcher incomingDispatcher,outgoingDispatcher;
	private EventQueueProcessor incomingProcessor, outgoingProcessor;

	public Jabbot( JabbotConfiguration configuration ) {
		this.incomingQueue = new LinkedBlockingDeque<>();
		this.outgoingQueue = new LinkedBlockingDeque<>();
		this.incomingDispatcher = new EventDispatcher(incomingQueue);
		this.outgoingDispatcher = new EventDispatcher(outgoingQueue);
		this.configuration = configuration;
		this.bindingFactory = newConnectionFactory(configuration.getBindings());

		this.incomingProcessor = new EventQueueProcessor(incomingQueue,outgoingDispatcher);
		this.outgoingProcessor = new EventQueueProcessor(outgoingQueue,incomingDispatcher);
	}

	public boolean connect(){
		scheduledExecutorService.scheduleAtFixedRate(new BindingMonitor(bindings,outgoingDispatcher), 5L,60L, TimeUnit.SECONDS);
		scheduledExecutorService.schedule(incomingProcessor,5L,TimeUnit.SECONDS);
		scheduledExecutorService.schedule(outgoingProcessor,5L,TimeUnit.SECONDS);

		for (final BindingConfiguration connectionConfiguration : configuration.getServerList()) {
			final Binding conn;
			try {
				conn = bindingFactory.create(connectionConfiguration);
				CommandManager.getInstanceFor(conn).initializeFromConfigSet(connectionConfiguration.getExtensions());
                conn.registerListener(new BindingListener() {
					@Override
					public void eventReceived(BindingEvent event) {
						incomingQueue.offer(event);
					}
				});
				bindings.add(conn);
			} catch (BindingCreationException e) {
				logger.error("failed to create binding for {}",connectionConfiguration.getType(),e);
			}
		}
		return true;
	}

	public void disconnect(){
		incomingProcessor.stop();
		outgoingProcessor.stop();
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

				factory.register(binding.getName(),connectionClass);
				logger.info("registered binding {} with alias '{}'",connectionClass,binding.getName());
			} catch (ClassNotFoundException e) {
				logger.error("unable to register {} binding with class {}",binding.getName(),binding.getClassName());
			}
		}
		return factory;
	}

}
