package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.event.*;
import org.wanna.jabbot.config.JabbotConfiguration;
import org.wanna.jabbot.event.EventDispatcher;
import org.wanna.jabbot.event.handlers.*;
import org.wanna.jabbot.extension.ExtensionLoader;
import org.wanna.jabbot.extension.ExtensionScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Jabbot {
	private final Logger logger = LoggerFactory.getLogger(Jabbot.class);

	private JabbotConfiguration configuration;
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
	//private List<Binding> bindings = new ArrayList<>();
	private Map<String,Binding> bindingMap = new HashMap<>();
	private final BlockingQueue<BindingEvent> incomingQueue, outgoingQueue;
	private EventDispatcher incomingDispatcher,outgoingDispatcher;
	private EventQueueProcessor incomingProcessor, outgoingProcessor;

	public Jabbot( JabbotConfiguration configuration ) {
		//TODO remove this hack and schedule it properly once extension discovery is fully implemented
		if(configuration.getExtensionsFolder() != null){
			new ExtensionScanner(configuration.getExtensionsFolder()).run();
		}

		this.incomingQueue = new LinkedBlockingDeque<>();
		this.outgoingQueue = new LinkedBlockingDeque<>();
		this.incomingDispatcher = new EventDispatcher(incomingQueue);
		this.outgoingDispatcher = new EventDispatcher(outgoingQueue);
		this.configuration = configuration;

		incomingProcessor = new EventQueueProcessor(incomingQueue,outgoingDispatcher,"Incoming Event Processor");
		outgoingProcessor = new EventQueueProcessor(outgoingQueue,incomingDispatcher,"Outgoing Event Processor");
		this.registerEventHandlers();
	}

	public boolean connect(){
		incomingProcessor.start();
		outgoingProcessor.start();

		scheduledExecutorService.scheduleAtFixedRate(new BindingMonitor(bindingMap.values(),outgoingDispatcher), 5L,60L, TimeUnit.SECONDS);
		ExtensionLoader loader = ExtensionLoader.getInstance();
		for (final BindingConfiguration connectionConfiguration : configuration.getServerList()) {
			logger.info("{} - initializing with type {}", connectionConfiguration.getId(),connectionConfiguration.getType());
			final Binding conn;
			conn = loader.getExtension(connectionConfiguration.getType(),Binding.class,connectionConfiguration);
			if(conn != null) {
				logger.info("{} - initializing commands", conn.getIdentifier());
				CommandManager.getInstanceFor(conn).initializeFromConfigSet(connectionConfiguration.getExtensions());
				logger.info("{} - initializing listener",conn.getIdentifier());
				conn.registerListener(new JabbotBindingListener(incomingQueue));
				logger.info("{} - initialization completed",conn.getIdentifier());
				bindingMap.put(connectionConfiguration.getId(), conn);
			}
		}
		return true;
	}

	public void disconnect(){
		scheduledExecutorService.shutdown();
		incomingProcessor.halt();
		outgoingProcessor.halt();
	}

	/**
	 * Feeds EventHandlerFactory.
	 * It maps binding Event to Jabbot events handler
	 */
	private void registerEventHandlers(){
		EventHandlerFactory factory = EventHandlerFactory.getInstance();
		factory.register(ConnectedEvent.class, new ConnectedEventHandler());
		factory.register(MessageEvent.class, new MessageEventHandler(bindingMap));
		factory.register(ConnectionRequestEvent.class,new ConnectionRequestEventHandler());
		factory.register(JoinRoomEvent.class,new JoinRoomEventHandler());
		factory.register(OutgoingMessageEvent.class, new OutgoingMessageEventHandler());
		factory.register(RoomInviteEvent.class, new RoomInviteHandler());
		factory.register(DisconnectedEvent.class,new DisconnectedEventHandler());
	}

}
